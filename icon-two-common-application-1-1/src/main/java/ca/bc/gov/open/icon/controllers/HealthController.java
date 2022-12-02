package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.audit.HealthServiceRequest;
import ca.bc.gov.open.icon.audit.HealthServiceRequestSubmitted;
import ca.bc.gov.open.icon.audit.HealthServiceRequestSubmittedResponse;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.configuration.QueueConfig;
import ca.bc.gov.open.icon.exceptions.APIThrownException;
import ca.bc.gov.open.icon.hsr.*;
import ca.bc.gov.open.icon.hsrservice.GetHealthServiceRequestSummary;
import ca.bc.gov.open.icon.hsrservice.GetHealthServiceRequestSummaryResponse;
import ca.bc.gov.open.icon.models.*;
import ca.bc.gov.open.icon.utils.XMLUtilities;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
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
                new GetHealthServiceRequestHistoryDocument();
        getHealthServiceRequestHistoryDocument.setHealthService(
                XMLUtilities.deserializeXmlStr(
                        getHealthServiceRequestHistory.getXMLString(), new HealthService()));
        getHealthServiceRequestHistoryDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        getHealthServiceRequestHistory.getUserTokenString(),
                        new ca.bc.gov.open.icon.hsr.UserToken()));

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

            String isAllowed = Objects.requireNonNull(resp.getBody()).getOrDefault("isAllowed", "");

            if (isAllowed.equals("0")) {
                throw new APIThrownException(
                        "The requested CSNumber does not have access to this function.");
            }

            var csNumber = getHealthServiceRequestHistoryDocument.getHealthService().getCsNum();
            var startRecord =
                    getHealthServiceRequestHistoryDocument.getHealthService().getRow().getStart();
            var endRecord =
                    getHealthServiceRequestHistoryDocument.getHealthService().getRow().getEnd();

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

            GetHealthServiceRequestHistoryResponse getHealthServiceRequestHistoryResponse =
                    new GetHealthServiceRequestHistoryResponse();
            UserToken userToken = new UserToken();
            HealthService healthService = new HealthService();

            healthService.setCsNum(
                    getHealthServiceRequestHistoryDocument.getHealthService().getCsNum());
            List<ca.bc.gov.open.icon.hsr.HealthServiceRequest> healthServiceRequests =
                    new LinkedList<ca.bc.gov.open.icon.hsr.HealthServiceRequest>();
            Row row = new Row();

            var totalRequestCount =
                    summaryResponse
                            .getGetHealthServiceRequestSummaryReturn()
                            .getTotalRequestCount();
            var hsrList =
                    summaryResponse
                            .getGetHealthServiceRequestSummaryReturn()
                            .getRequests()
                            .getRequests();

            row.setStart(startRecord);
            row.setEnd(endRecord);
            row.setTotal(String.valueOf(totalRequestCount));

            for (var service : hsrList) {

                ca.bc.gov.open.icon.hsr.HealthServiceRequest request =
                        new ca.bc.gov.open.icon.hsr.HealthServiceRequest();
                request.setRequestDate(service.getSubmittedDtm());
                request.setHealthRequest(service.getDetailsTxt());
                request.setHsrId(String.valueOf(service.getId()));

                healthServiceRequests.add(request);
            }

            healthService.setHealthServiceRequest(healthServiceRequests);
            healthService.setRow(row);

            getHealthServiceRequestHistoryDocument.setHealthService(healthService);
            getHealthServiceRequestHistoryResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(
                            getHealthServiceRequestHistoryDocument.getHealthService()));
            getHealthServiceRequestHistoryResponse.setUserTokenString(
                    XMLUtilities.serializeXmlStr(
                            getHealthServiceRequestHistoryDocument.getUserToken()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog(
                                    "Request Success", "getHealthServiceRequestHistory")));

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

        PublishHSRDocument publishHSRDocument = new PublishHSRDocument();
        publishHSRDocument.setHealthService(
                XMLUtilities.deserializeXmlStr(publishHSR.getXMLString(), new HealthService()));
        publishHSRDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        publishHSR.getUserTokenString(), new ca.bc.gov.open.icon.hsr.UserToken()));

        try {
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(host + "health/publish-hsr");

            // ORDS Call - PublishHSR
            HttpEntity<List<HealthServicePub>> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            new HttpEntity<>(publishHSRDocument, new HttpHeaders()),
                            new ParameterizedTypeReference<>() {});

            // Go through all health service requests
            for (var pub : resp.getBody()) {
                enQueue(pub);
            }

            // Compose response body
            PublishHSRResponse publishHSRResponse = new PublishHSRResponse();
            HealthService healthService = new HealthService();
            healthService.setCsNum(publishHSRDocument.getHealthService().getCsNum());
            List<ca.bc.gov.open.icon.hsr.HealthServiceRequest> healthServiceRequests =
                    new ArrayList<>();

            for (var pub : resp != null ? resp.getBody() : null) {
                ca.bc.gov.open.icon.hsr.HealthServiceRequest healthServiceRequest =
                        new ca.bc.gov.open.icon.hsr.HealthServiceRequest();
                healthServiceRequest.setHsrId(pub.getHsrId());
                healthServiceRequest.setPacID(pub.getPacId());
                healthServiceRequest.setLocation(pub.getLocation());
                healthServiceRequest.setRequestDate(pub.getRequestDate());
                healthServiceRequest.setHealthRequest(pub.getHealthRequest());
                healthServiceRequests.add(healthServiceRequest);
            }
            healthService.setHealthServiceRequest(healthServiceRequests);
            healthService.setRow(publishHSRDocument.getHealthService().getRow());

            publishHSRDocument.setHealthService(healthService);
            publishHSRResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(publishHSRDocument.getHealthService()));
            publishHSRResponse.setUserTokenString(
                    XMLUtilities.serializeXmlStr(publishHSRDocument.getUserToken()));
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

    private void enQueue(HealthServicePub healthServicePub) throws JsonProcessingException {
        log.info("Sending HSR: " + healthServicePub); // might delete later

        try {
            this.rabbitTemplate.convertAndSend(
                    queueConfig.getTopicExchangeName(),
                    queueConfig.getHsrRoutingkey(),
                    healthServicePub);
        } catch (AmqpException ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error sending health service to MQ",
                                    "publishHSR",
                                    ex.getMessage(),
                                    healthServicePub)));
            handleError(ex);
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.HealthServiceRequest.ws.provider:HSR",
            localPart = "getHSRCount")
    @ResponsePayload
    public GetHSRCountResponse getHSRCount(@RequestPayload GetHSRCount getHSRCount)
            throws JsonProcessingException {

        GetHSRCountDocument getHSRCountDocument = new GetHSRCountDocument();
        getHSRCountDocument.setHSRCount(
                XMLUtilities.deserializeXmlStr(getHSRCount.getXMLString(), new HSRCount()));
        getHSRCountDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        getHSRCount.getUserTokenString(), new ca.bc.gov.open.icon.hsr.UserToken()));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "health/hsr-count");
        HttpEntity<GetHSRCountDocument> payload =
                new HttpEntity<>(getHSRCountDocument, new HttpHeaders());

        try {

            HttpEntity<HSRCount> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, HSRCount.class);

            GetHSRCountResponse getHSRCountResponse = new GetHSRCountResponse();
            getHSRCountDocument.setHSRCount(resp.getBody());
            getHSRCountResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(getHSRCountDocument.getHSRCount()));
            getHSRCountResponse.setUserTokenString(
                    XMLUtilities.serializeXmlStr(getHSRCountDocument.getUserToken()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getHSRCount")));

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
