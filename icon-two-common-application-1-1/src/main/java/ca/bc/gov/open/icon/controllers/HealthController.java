package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.audit.HealthServiceRequest;
import ca.bc.gov.open.icon.audit.HealthServiceRequestSubmitted;
import ca.bc.gov.open.icon.audit.HealthServiceRequestSubmittedResponse;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.configuration.QueueConfig;
import ca.bc.gov.open.icon.hsr.*;
import ca.bc.gov.open.icon.hsrservice.GetHealthServiceRequestSummary;
import ca.bc.gov.open.icon.hsrservice.GetHealthServiceRequestSummaryResponse;
import ca.bc.gov.open.icon.hsrservice.SubmitHealthServiceRequest;
import ca.bc.gov.open.icon.hsrservice.SubmitHealthServiceRequestResponse;
import ca.bc.gov.open.icon.models.*;
import ca.bc.gov.open.icon.utils.XMLUtilities;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
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

@Profile("!test")
@Endpoint
@Slf4j
public class HealthController {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    @Value("${icon.hsr-service-url}")
    private String hsrServiceUrl = "https://127.0.0.1/";

    private final WebServiceTemplate soapTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final org.springframework.amqp.core.Queue hsrQueue;
    private final Queue pingQueue;
    private final RabbitTemplate rabbitTemplate;
    private final AmqpAdmin amqpAdmin;
    private final QueueConfig queueConfig;

    @Autowired
    public HealthController(
            WebServiceTemplate soapTemplate,
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Qualifier("hsr-queue") org.springframework.amqp.core.Queue hsrQueue,
            @Qualifier("ping-queue") org.springframework.amqp.core.Queue pingQueue,
            RabbitTemplate rabbitTemplate,
            AmqpAdmin amqpAdmin,
            QueueConfig queueConfig) {
        this.soapTemplate = soapTemplate;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.hsrQueue = hsrQueue;
        this.pingQueue = pingQueue;
        this.rabbitTemplate = rabbitTemplate;
        this.amqpAdmin = amqpAdmin;
        this.queueConfig = queueConfig;
    }

    @PostConstruct
    public void createQueues() {
        amqpAdmin.declareQueue(hsrQueue);
        amqpAdmin.declareQueue(pingQueue);
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
            throw handleError(ex, new ca.bc.gov.open.icon.audit.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.HealthServiceRequest.ws.provider:HSR",
            localPart = "getHealthServiceRequestHistory")
    @ResponsePayload
    public GetHealthServiceRequestHistoryResponse getHealthServiceRequestHistory(
            @RequestPayload GetHealthServiceRequestHistory getHealthServiceRequestHistory)
            throws JsonProcessingException {

        GetHealthServiceRequestHistoryDocument getHealthServiceRequestHistoryDocument =
                XMLUtilities.convertReq(
                        getHealthServiceRequestHistory,
                        new GetHealthServiceRequestHistoryDocument(),
                        "getHealthServiceRequestHistory");

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "health/service-rqst-history");
        HttpEntity<GetHealthServiceRequestHistoryDocument> payload =
                new HttpEntity<>(getHealthServiceRequestHistoryDocument, new HttpHeaders());

        try {

            HttpEntity<Map<String, String>> resp =
                    restTemplate.exchange(
                            builder.build().toUri(),
                            HttpMethod.POST,
                            payload,
                            new ParameterizedTypeReference<>() {});
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog(
                                    "Request Success", "getHealthServiceRequestHistory")));

            String isAllowed = Objects.requireNonNull(resp.getBody()).getOrDefault("isAllowed", "");

            if (isAllowed.equals("0")) {
                throw new Exception(
                        "The requested CSNumber does not have access to this function.");
            }

            var csNumber =
                    getHealthServiceRequestHistoryDocument
                            .getXMLString()
                            .getHealthService()
                            .getCsNum();
            var startRecord =
                    getHealthServiceRequestHistoryDocument
                            .getXMLString()
                            .getHealthService()
                            .getRow()
                            .getStart();
            var endRecord =
                    getHealthServiceRequestHistoryDocument
                            .getXMLString()
                            .getHealthService()
                            .getRow()
                            .getEnd();

            ca.bc.gov.open.icon.hsrservice.GetHealthServiceRequestSummary
                    healthServiceRequestSummary = new GetHealthServiceRequestSummary();

            healthServiceRequestSummary.setCsNumber(csNumber);
            healthServiceRequestSummary.setStartRecord(Integer.valueOf(startRecord));
            healthServiceRequestSummary.setEndRecord(Integer.valueOf(endRecord));
            healthServiceRequestSummary.setNumCharacters(4000);

            GetHealthServiceRequestSummaryResponse summaryResponse =
                    (GetHealthServiceRequestSummaryResponse)
                            soapTemplate.marshalSendAndReceive(
                                    hsrServiceUrl, healthServiceRequestSummary);

            GetHealthServiceRequestHistoryResponseDocument
                    getHealthServiceRequestHistoryResponseDocument =
                            new GetHealthServiceRequestHistoryResponseDocument();
            UserTokenOuter userTokenOuter = new UserTokenOuter();
            HealthServiceOuter healthServiceOuter = new HealthServiceOuter();
            HealthService healthService = new HealthService();

            healthService.setCsNum(
                    getHealthServiceRequestHistoryDocument
                            .getXMLString()
                            .getHealthService()
                            .getCsNum());
            List<ca.bc.gov.open.icon.hsr.HealthServiceRequest> healthServiceRequest =
                    new LinkedList<ca.bc.gov.open.icon.hsr.HealthServiceRequest>();
            Row row = new Row();

            healthService.setHealthServiceRequest(healthServiceRequest);
            healthServiceOuter.setHealthService(healthService);
            getHealthServiceRequestHistoryResponseDocument.setXMLString(healthServiceOuter);

            var totalRequestCount =
                    summaryResponse
                            .getGetHealthServiceRequestSummaryReturn()
                            .getTotalRequestCount();
            var hsrList =
                    summaryResponse
                            .getGetHealthServiceRequestSummaryReturn()
                            .getRequests()
                            .getRequests();
            for (var service : hsrList) {

                ca.bc.gov.open.icon.hsr.HealthServiceRequest request =
                        new ca.bc.gov.open.icon.hsr.HealthServiceRequest();

                var submittedDtm =
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                .withZone(ZoneId.of("GMT-7"))
                                .withLocale(Locale.US)
                                .format(service.getSubmittedDtm());
                request.setRequestDate(submittedDtm);
                request.setHealthRequest(service.getDetailsTxt());
                request.setHsrId(String.valueOf(service.getId()));
            }

            row.setStart(startRecord);
            row.setEnd(endRecord);
            row.setTotal(String.valueOf(totalRequestCount));

            healthServiceOuter.getHealthService().setRow(row);

            var getHealthServiceRequestHistoryResponse =
                    XMLUtilities.convertResp(
                            getHealthServiceRequestHistoryResponseDocument,
                            new GetHealthServiceRequestHistoryResponse(),
                            "getHealthServiceRequestHistoryResponse");

            getHealthServiceRequestHistoryResponse.setUserTokenString(
                    getHealthServiceRequestHistory.getUserTokenString());

            return getHealthServiceRequestHistoryResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getHealthServiceRequestHistory",
                                    ex.getMessage(),
                                    getHealthServiceRequestHistory)));
            throw handleError(ex, new ca.bc.gov.open.icon.hsr.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.HealthServiceRequest.ws.provider:HSR",
            localPart = "publishHSR")
    @ResponsePayload
    public PublishHSRResponse publishHSR(@RequestPayload PublishHSR publishHSR)
            throws JsonProcessingException {

        var publishHSRDocument =
                XMLUtilities.convertReq(publishHSR, new PublishHSRDocument(), "publishHSR");

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "health/publish-hsr");

        // ORDS Call - PublishHSR
        HttpEntity<List<HealthServicePub>> resp = null;
        try {
            resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            new HttpEntity<>(publishHSRDocument, new HttpHeaders()),
                            new ParameterizedTypeReference<>() {});

            // Invoke SOAP Service - SubmitHealthServiceRequest (SendHSR)
            try {
                SubmitHealthServiceRequest submitHealthServiceRequest =
                        new SubmitHealthServiceRequest();

                // Go through all health service requests
                for (var pub : resp.getBody()) {
                    submitHealthServiceRequest.setCsNumber(pub.getCsNum());
                    submitHealthServiceRequest.setSubmissionDate(pub.getRequestDate());
                    submitHealthServiceRequest.setCentre(pub.getLocation());
                    submitHealthServiceRequest.setDetails(pub.getHealthRequest());
                    SubmitHealthServiceRequestResponse submitHealthServiceRequestResponse =
                            (SubmitHealthServiceRequestResponse)
                                    soapTemplate.marshalSendAndReceive(
                                            hsrServiceUrl, submitHealthServiceRequest);
                    pub.setPacId(
                            String.valueOf(
                                    submitHealthServiceRequestResponse
                                            .getSubmitHealthServiceRequestReturn()));
                }
            } catch (Exception ex) {
                log.error(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from SOAP SERVICE - SubmitHealthServiceRequest",
                                        "publishHSR",
                                        ex.getMessage(),
                                        publishHSR)));
                // wM does not throw after receiving error from web service so comment out the next
                // line
                // throw new ORDSException();
            }

            // ORDS Call - UpdateHSR
            UriComponentsBuilder builder2 =
                    UriComponentsBuilder.fromHttpUrl(host + "health/update-hsr");
            HttpEntity<HealthServicePub> resp2 = null;

            for (var pub : resp.getBody()) {
                resp2 =
                        restTemplate.exchange(
                                builder2.toUriString(),
                                HttpMethod.POST,
                                new HttpEntity<>(pub, new HttpHeaders()),
                                HealthServicePub.class);
                pub.setHsrId(resp2.getBody().getHsrId());
            }

            for (var pub : resp.getBody()) {
                // Publish HSR only if pacId is empty or null
                if (pub.getPacId() == null) {
                    log.warn(
                            objectMapper.writeValueAsString(
                                    new HsrStatusLog(
                                            "publishHSR... failure 'null' doing BPM",
                                            "publishHSR")));
                    enQueueHealthServicePub(pub);
                } else if (pub.getPacId().equals("-")) {
                    log.warn(
                            objectMapper.writeValueAsString(
                                    new HsrStatusLog(
                                            "publishHSR... failure '-' doing BPM", "publishHSR")));
                    enQueueHealthServicePub(pub);
                } else {
                    log.info(
                            objectMapper.writeValueAsString(
                                    new HsrStatusLog(
                                            "publishHSR... success no BPM", "publishHSR")));
                }
            }

            // Compose response body
            PublishHSRResponseDocument publishHSRResponseDocument =
                    new PublishHSRResponseDocument();
            HealthServiceOuter outResp = new HealthServiceOuter();
            HealthService healthService = new HealthService();
            healthService.setCsNum(publishHSRDocument.getXMLString().getHealthService().getCsNum());
            List<ca.bc.gov.open.icon.hsr.HealthServiceRequest> requestList = new ArrayList<>();
            for (var pub : resp != null ? resp.getBody() : null) {
                ca.bc.gov.open.icon.hsr.HealthServiceRequest healthServiceRequest =
                        new ca.bc.gov.open.icon.hsr.HealthServiceRequest();
                healthServiceRequest.setHsrId(pub.getHsrId());
                healthServiceRequest.setPacID(pub.getPacId());
                healthServiceRequest.setLocation(pub.getLocation());
                var requestDate =
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                                .withZone(ZoneId.of("GMT-7"))
                                .withLocale(Locale.US)
                                .format(pub.getRequestDate());
                healthServiceRequest.setRequestDate(requestDate);
                healthServiceRequest.setHealthRequest(pub.getHealthRequest());
                requestList.add(healthServiceRequest);
            }
            healthService.setHealthServiceRequest(requestList);
            outResp.setHealthService(healthService);
            publishHSRResponseDocument.setXMLString(outResp);

            var publishHSRResponse =
                    XMLUtilities.convertResp(
                            publishHSRResponseDocument,
                            new PublishHSRResponse(),
                            "publishHSRResponse");

            publishHSRResponse.setUserTokenString(publishHSR.getUserTokenString());

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "publishHSR")));
            return publishHSRResponse;

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "publishHSR",
                                    ex.getMessage(),
                                    publishHSR)));
            throw handleError(ex, new ca.bc.gov.open.icon.hsr.Error());
        }
    }

    private void enQueueHealthServicePub(HealthServicePub p) {
        log.info("Sending HSR: " + p); // might delete later
        this.rabbitTemplate.convertAndSend(
                queueConfig.getTopicExchangeName(), queueConfig.getHsrRoutingkey(), p);
    }

    @PayloadRoot(
            namespace = "ICON2.Source.HealthServiceRequest.ws.provider:HSR",
            localPart = "getHSRCount")
    @ResponsePayload
    public GetHSRCountResponse getHSRCount(@RequestPayload GetHSRCount getHSRCount)
            throws JsonProcessingException {

        var getHSRCountDocument =
                XMLUtilities.convertReq(getHSRCount, new GetHSRCountDocument(), "getHSRCount");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "health/hsr-count");
        HttpEntity<GetHSRCountDocument> payload =
                new HttpEntity<>(getHSRCountDocument, new HttpHeaders());

        try {

            HttpEntity<HSRCount> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, HSRCount.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getHSRCount")));

            GetHSRCountResponseDocument getHSRCountResponseDocument =
                    new GetHSRCountResponseDocument();
            HSRCountOuter outResp = new HSRCountOuter();
            outResp.setHealthServiceCount(resp.getBody());
            getHSRCountResponseDocument.setXMLString(outResp);

            var getHSRCountResponse =
                    XMLUtilities.convertResp(
                            getHSRCountResponseDocument,
                            new GetHSRCountResponse(),
                            "getHSRCountResponse");

            getHSRCountResponse.setUserTokenString(getHSRCount.getUserTokenString());

            return getHSRCountResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getHSRCount",
                                    ex.getMessage(),
                                    getHSRCount)));
            throw handleError(ex, new ca.bc.gov.open.icon.hsr.Error());
        }
    }
}
