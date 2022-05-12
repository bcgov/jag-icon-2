package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.audit.HealthServiceRequest;
import ca.bc.gov.open.icon.audit.HealthServiceRequestSubmitted;
import ca.bc.gov.open.icon.audit.HealthServiceRequestSubmittedResponse;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.configuration.QueueConfig;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.hsr.*;
import ca.bc.gov.open.icon.hsrservice.GetHealthServiceRequestSummary;
import ca.bc.gov.open.icon.hsrservice.GetHealthServiceRequestSummaryResponse;
import ca.bc.gov.open.icon.hsrservice.SubmitHealthServiceRequest;
import ca.bc.gov.open.icon.hsrservice.SubmitHealthServiceRequestResponse;
import ca.bc.gov.open.icon.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
                            new RequestSuccessLog(
                                    "Request Success", "getHealthServiceRequestHistory")));

            String isAllowed = Objects.requireNonNull(resp.getBody()).getOrDefault("isAllowed", "");

            if (isAllowed.equals("0")) {
                throw new Exception(
                        "The requested CSNumber does not have access to this function.");
            }

            var csNumber =
                    getHealthServiceRequestHistory
                            .getXMLString()
                            .getHealthService()
                            .getHealthService()
                            .getCsNum();
            var startRecord =
                    getHealthServiceRequestHistory
                            .getXMLString()
                            .getHealthService()
                            .getHealthService()
                            .getRow()
                            .getStart();
            var endRecord =
                    getHealthServiceRequestHistory
                            .getXMLString()
                            .getHealthService()
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

            GetHealthServiceRequestHistoryResponse out =
                    new GetHealthServiceRequestHistoryResponse();
            UserTokenOuter userTokenOuter = new UserTokenOuter();
            HealServiceOuter healServiceOuter = new HealServiceOuter();
            HealServiceInner healServiceInner = new HealServiceInner();
            HealService healService = new HealService();
            List<ca.bc.gov.open.icon.hsr.HealthServiceRequest> healthServiceRequest =
                    new LinkedList<ca.bc.gov.open.icon.hsr.HealthServiceRequest>();
            Row row = new Row();

            healService.setHealthServiceRequest(healthServiceRequest);
            healServiceInner.setHealthService(healService);
            healServiceOuter.setHealthService(healServiceInner);
            out.setXMLString(healServiceOuter);

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
                request.setRequestDate(service.getSubmittedDtm());

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
            namespace = "ICON2.Source.HealthServiceRequest.ws.provider:HSR",
            localPart = "publishHSR")
    @ResponsePayload
    public PublishHSRResponse publishHSR(@RequestPayload PublishHSR publishHSR)
            throws JsonProcessingException {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "health/publish-hsr");

        // ORDS Call - PublishHSR
        HttpEntity<HealthServicePub> resp = null;
        try {
            resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            new HttpEntity<>(publishHSR, new HttpHeaders()),
                            HealthServicePub.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "publishHSR")));

            // Invoke SOAP Service - SubmitHealthServiceRequest (SendHSR)
            try {
                SubmitHealthServiceRequest submitHealthServiceRequest =
                        new SubmitHealthServiceRequest();
                submitHealthServiceRequest.setCsNumber(resp.getBody().getCsNum());
                submitHealthServiceRequest.setSubmissionDate(
                        resp.getBody().getRequestDate().toString());
                submitHealthServiceRequest.setCentre(resp.getBody().getLocation());
                submitHealthServiceRequest.setDetails(resp.getBody().getHealthRequest());
                SubmitHealthServiceRequestResponse submitHealthServiceRequestResponse =
                        (SubmitHealthServiceRequestResponse)
                                soapTemplate.marshalSendAndReceive(
                                        hsrServiceUrl, submitHealthServiceRequest);

                // ORDS Call - UpdateHSR
                UriComponentsBuilder builder2 =
                        UriComponentsBuilder.fromHttpUrl(host + "health/update-hsr");
                HttpEntity<HealthServicePub> resp2 = null;
                try {
                    resp2 =
                            restTemplate.exchange(
                                    builder2.toUriString(),
                                    HttpMethod.POST,
                                    resp,
                                    HealthServicePub.class);
                    resp.getBody().setHsrId(resp2.getBody().getHsrId());
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
            } catch (Exception ex) {
                log.error(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from SOAP SERVICE - SubmitHealthServiceRequest",
                                        "publishHSR",
                                        ex.getMessage(),
                                        publishHSR)));
            }
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

        // Publish HSR only if pacId is empty or null
        if (resp.getBody().getPacId().equals("-")) {
            log.warn(
                    objectMapper.writeValueAsString(
                            new HsrStatusLog("publishHSR... failure '-' doing BPM", "publishHSR")));
            enQueueHealthServicePub(resp.getBody());
        } else if (resp.getBody().getPacId() == null) {
            log.warn(
                    objectMapper.writeValueAsString(
                            new HsrStatusLog(
                                    "publishHSR... failure 'null' doing BPM", "publishHSR")));
            enQueueHealthServicePub(resp.getBody());
        } else {
            log.info(
                    objectMapper.writeValueAsString(
                            new HsrStatusLog("publishHSR... success no BPM", "publishHSR")));
        }

        // Compose response body
        PublishHSRResponse out = new PublishHSRResponse();
        HealthServiceOuter outResp = new HealthServiceOuter();
        HealthServiceInner inResp = new HealthServiceInner();
        HealthService healthService = new HealthService();
        healthService.setCsNum(resp.getBody().getCsNum());
        ca.bc.gov.open.icon.hsr.HealthServiceRequest healthServiceRequest =
                new ca.bc.gov.open.icon.hsr.HealthServiceRequest();
        healthServiceRequest.setHsrId(resp.getBody().getHsrId());
        healthServiceRequest.setPacID(resp.getBody().getPacId());
        healthServiceRequest.setLocation(resp.getBody().getLocation());
        healthServiceRequest.setRequestDate(resp.getBody().getRequestDate());
        healthServiceRequest.setHealthRequest(resp.getBody().getHealthRequest());
        healthService.setHealthServiceRequest(healthServiceRequest);
        inResp.setHealthService(healthService);
        outResp.setHealthService(inResp);
        out.setXMLString(outResp);

        log.info(
                objectMapper.writeValueAsString(
                        new RequestSuccessLog("Request Success", "publishHSR")));
        return out;
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

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "health/hsr-count");
        HttpEntity<GetHSRCount> payload = new HttpEntity<>(getHSRCount, new HttpHeaders());

        try {

            HttpEntity<HSRCount> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, HSRCount.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getHSRCount")));

            GetHSRCountResponse getHSRCountResponse = new GetHSRCountResponse();
            HSRCountOuter outResp = new HSRCountOuter();
            HSRCountInner inResp = new HSRCountInner();
            inResp.setHealthServiceCount(resp.getBody());
            outResp.setHealthServiceCount(inResp);
            getHSRCountResponse.setXMLString(outResp);
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
            throw new ORDSException();
        }
    }
}
