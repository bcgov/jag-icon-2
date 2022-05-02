package icon.controllers;

import ca.bc.gov.open.icon.audit.HealthServiceRequest;
import ca.bc.gov.open.icon.audit.HealthServiceRequestSubmitted;
import ca.bc.gov.open.icon.audit.HealthServiceRequestSubmittedResponse;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.hsr.*;
import ca.bc.gov.open.icon.hsrservice.GetHealthServiceRequestSummary;
import ca.bc.gov.open.icon.hsrservice.GetHealthServiceRequestSummaryResponse;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Endpoint
@Slf4j
public class HealthController {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    @Value("${icon.hsr-service-url}")
    private String hsrServiceUrl;

    private final WebServiceTemplate soapTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public HealthController(WebServiceTemplate soapTemplate, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.soapTemplate = soapTemplate;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(
            namespace = "ICON2.Source.Audit.ws:Record",
            localPart = "HealthServiceRequestSubmitted")
    @ResponsePayload
    public HealthServiceRequestSubmittedResponse healthServiceRequestSubmitted(
            @RequestPayload HealthServiceRequestSubmitted healthServiceRequestSubmitted)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "health/service-rqst-submitted");

        HealthServiceRequest inner =
                healthServiceRequestSubmitted != null
                                && healthServiceRequestSubmitted.getHealthServiceRequest() != null
                        ? healthServiceRequestSubmitted.getHealthServiceRequest()
                        : new HealthServiceRequest();
        HttpEntity<HealthServiceRequest> payload = new HttpEntity<>(inner, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog(
                                    "Request Success", "healthServiceRequestSubmitted")));
            HealthServiceRequestSubmittedResponse out = new HealthServiceRequestSubmittedResponse();
            out.setStatus(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "healthServiceRequestSubmitted",
                                    ex.getMessage(),
                                    healthServiceRequestSubmitted)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.HealthServiceRequest.ws.provider:HSR",
            localPart = "getHealthServiceRequestHistory")
    @ResponsePayload
    public GetHealthServiceRequestHistoryResponse getHealthServiceRequestHistory(
            @RequestPayload GetHealthServiceRequestHistory getHealthServiceRequestHistory)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "health/service-rqst-history");
        HttpEntity<GetHealthServiceRequestHistory> payload =
                new HttpEntity<>(getHealthServiceRequestHistory, new HttpHeaders());

        try {

            HttpEntity<Map<String, String>> resp =
                    restTemplate.exchange(
                            builder.build().toUri(),
                            HttpMethod.POST,
                            payload,
                            new ParameterizedTypeReference<>() {});
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getHealthServiceRequestHistory")));

            String isAllowed = Objects.requireNonNull(resp.getBody()).getOrDefault("isAllowed", "");

//            if (isAllowed.equals("0")){
//                throw new Exception("The requested CSNumber does not have access to this function.");  // TODO the error is not populated in response and need new ORDSException()
//            }

            var csNumber = getHealthServiceRequestHistory.getXMLString().getHealthService().getHealthService().getCsNum();
            var startRecord =  getHealthServiceRequestHistory.getXMLString().getHealthService().getHealthService().getRow().getStart();
            var endRecord =  getHealthServiceRequestHistory.getXMLString().getHealthService().getHealthService().getRow().getEnd();


            ca.bc.gov.open.icon.hsrservice.GetHealthServiceRequestSummary healthServiceRequestSummary =
                    new GetHealthServiceRequestSummary();

            healthServiceRequestSummary.setCsNumber(csNumber);
            healthServiceRequestSummary.setStartRecord(startRecord);
            healthServiceRequestSummary.setEndRecord(endRecord);
            healthServiceRequestSummary.setNumCharacters("4000");

            GetHealthServiceRequestSummaryResponse summaryResponse = (GetHealthServiceRequestSummaryResponse)
                            soapTemplate.marshalSendAndReceive(hsrServiceUrl, healthServiceRequestSummary);

            GetHealthServiceRequestHistoryResponse out = new GetHealthServiceRequestHistoryResponse();
            UserTokenOuter userTokenOuter = new UserTokenOuter();
            HealServiceOuter healServiceOuter = new HealServiceOuter();
            HealServiceInner healServiceInner = new HealServiceInner();
            HealService healService = new HealService();
            List<ca.bc.gov.open.icon.hsr.HealthServiceRequest>  healthServiceRequest= new LinkedList<ca.bc.gov.open.icon.hsr.HealthServiceRequest>();
            Row row = new Row();

            healService.setHealthServiceRequest(healthServiceRequest);
            healServiceInner.setHealthService(healService);
            healServiceOuter.setHealthService(healServiceInner);
            out.setXMLString(healServiceOuter);

            var totalRequestCount = summaryResponse.getGetHealthServiceRequestSummaryReturn().getTotalRequestCount();
            var hsrList = summaryResponse.getGetHealthServiceRequestSummaryReturn().getRequests().getRequests();
            for ( var service : hsrList) {

                ca.bc.gov.open.icon.hsr.HealthServiceRequest request = new ca.bc.gov.open.icon.hsr.HealthServiceRequest();
                String date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneId.of("GMT-7"))
                        .withLocale(Locale.US)
                        .format(service.getSubmittedDtm());
                request.setRequestDate(date);

                request.setHealthRequest(service.getDetailsTxt());
                request.setHsrId(String.valueOf(service.getId()));
            }

            row.setStart(startRecord);
            row.setStart(endRecord);
            row.setTotal(String.valueOf(totalRequestCount));

            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getHealthServiceRequestHistory",
                                    ex.getMessage(),
                                    getHealthServiceRequestHistory)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "http://reeks.bcgov/ICON2.Source.HealthServiceRequest.ws.provider:HSR",
            localPart = "publishHSR")
    @ResponsePayload
    public PublishHSRResponse publishHSR(@RequestPayload PublishHSR publishHSR)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "health/publish-hsr");
        HttpEntity<PublishHSR> payload = new HttpEntity<>(publishHSR, new HttpHeaders());

        try {
            HttpEntity<PublishHSRResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            PublishHSRResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "publishHSR")));
            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "publishHSR",
                                    ex.getMessage(),
                                    publishHSR)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "http://reeks.bcgov/ICON2.Source.HealthServiceRequest.ws.provider:HSR",
            localPart = "getHSRCount")
    @ResponsePayload
    public GetHSRCountResponse getHSRCount(@RequestPayload GetHSRCount getHSRCount)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "health/hsr-count")
                        .queryParam("xmlString", getHSRCount.getXMLString())
                        .queryParam("userTokenString", getHSRCount.getUserTokenString());

        try {
            HttpEntity<GetHSRCountResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            GetHSRCountResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getHSRCount")));
            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getHSRCount",
                                    ex.getMessage(),
                                    getHSRCount)));
            throw new ORDSException();
        }
    }
}
