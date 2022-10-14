package ca.bc.gov.open.icon;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.Base;
import ca.bc.gov.open.icon.audit.HealthServiceRequest;
import ca.bc.gov.open.icon.audit.HealthServiceRequestSubmitted;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.configuration.QueueConfig;
import ca.bc.gov.open.icon.controllers.HealthController;
import ca.bc.gov.open.icon.hsr.*;
import ca.bc.gov.open.icon.hsrservice.ArrayOfHealthServiceRequest;
import ca.bc.gov.open.icon.hsrservice.GetHealthServiceRequestSummary;
import ca.bc.gov.open.icon.hsrservice.GetHealthServiceRequestSummaryResponse;
import ca.bc.gov.open.icon.hsrservice.HealthServiceRequestBundle;
import ca.bc.gov.open.icon.models.HealthServicePub;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class HealthControllerTests {
    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

    @Qualifier("hsr-queue")
    private org.springframework.amqp.core.Queue hsrQueue;

    @Qualifier("ping-queue")
    private org.springframework.amqp.core.Queue pingQueue;

    @MockBean private RabbitTemplate rabbitTemplate;
    @MockBean private AmqpAdmin amqpAdmin;

    private QueueConfig queueConfig;

    @Test
    public void testHealthServiceRequestSubmitted() throws JsonProcessingException {
        var req = new HealthServiceRequestSubmitted();
        var HealthServiceRequest = new HealthServiceRequest();
        var base = new Base();
        base.setSessionID("A");
        base.setCsNumber("A");
        base.setSessionID("A");
        HealthServiceRequest.setBase(base);
        HealthServiceRequest.setHealthServiceRequestID("A");
        HealthServiceRequest.setServiceCD("A");
        HealthServiceRequest.setFunctionCD("A");

        Status status = new Status();
        status.setSuccess(true);
        ResponseEntity<Status> responseEntity = new ResponseEntity<>(status, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Status>>any()))
                .thenReturn(responseEntity);

        var healthController =
                new HealthController(
                        webServiceTemplate,
                        restTemplate,
                        objectMapper,
                        hsrQueue,
                        pingQueue,
                        rabbitTemplate,
                        amqpAdmin,
                        queueConfig);
        var resp = healthController.healthServiceRequestSubmitted(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetHealthServiceRequestHistory() throws JsonProcessingException {
        var req = new GetHealthServiceRequestHistory();
        req.setXMLString(
                "<HealthService>\n"
                        + "    <csNum>1</csNum>\n"
                        + "    <Row>\n"
                        + "        <start>0</start>\n"
                        + "        <end>1</end>\n"
                        + "    </Row>\n"
                        + "</HealthService>");

        Map<String, String> out = new HashMap<>();
        out.put("isAllowed", "1");
        ResponseEntity<Map<String, String>> responseEntity =
                new ResponseEntity<>(out, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenReturn(responseEntity);

        var healthController =
                new HealthController(
                        webServiceTemplate,
                        restTemplate,
                        objectMapper,
                        hsrQueue,
                        pingQueue,
                        rabbitTemplate,
                        amqpAdmin,
                        queueConfig);

        // Set up to mock soap service response
        GetHealthServiceRequestSummaryResponse soapResp =
                new GetHealthServiceRequestSummaryResponse();
        var healthServiceRequestBundle = new HealthServiceRequestBundle();
        var arrayOfHealthServiceRequest = new ArrayOfHealthServiceRequest();
        List<ca.bc.gov.open.icon.hsrservice.HealthServiceRequest> hsrs1 = new ArrayList<>();
        var hsr1 = new ca.bc.gov.open.icon.hsrservice.HealthServiceRequest();
        hsr1.setDetailsTxt("A");
        hsr1.setSubmittedDtm(Instant.now());
        hsr1.setId(1);
        hsrs1.add(hsr1);
        arrayOfHealthServiceRequest.setRequests(hsrs1);
        healthServiceRequestBundle.setTotalRequestCount(1);
        healthServiceRequestBundle.setRequests(arrayOfHealthServiceRequest);
        soapResp.setGetHealthServiceRequestSummaryReturn(healthServiceRequestBundle);

        when(webServiceTemplate.marshalSendAndReceive(
                        anyString(), Mockito.any(GetHealthServiceRequestSummary.class)))
                .thenReturn(soapResp);

        var resp = healthController.getHealthServiceRequestHistory(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testPublishHSR() throws JsonProcessingException {
        var req = new PublishHSR();
        req.setXMLString(
                "<HealthService>\n"
                        + "    <csNum>1</csNum>\n"
                        + "    <HealthServiceRequest>\n"
                        + "        <hsrId>1</hsrId>\n"
                        + "        <pacID>1</pacID>\n"
                        + "        <location>1</location>\n"
                        + "        <requestDate></requestDate>\n"
                        + "        <healthRequest>Test!</healthRequest>\n"
                        + "    </HealthServiceRequest>\n"
                        + "</HealthService> ");

        List<HealthServicePub> healthServicePubs = new ArrayList();
        var healthServicePub = new HealthServicePub();
        healthServicePub.setCsNum("A");
        healthServicePub.setHsrId("A");
        healthServicePub.setLocation("A");
        healthServicePub.setRequestDate(Instant.now());
        healthServicePub.setHealthRequest("A");
        healthServicePub.setPacId("A");
        healthServicePub.setCsNum("A");
        healthServicePub.setHsrId("A");
        healthServicePub.setLocation("A");
        healthServicePub.setRequestDate(Instant.now());
        healthServicePub.setHealthRequest("A");
        healthServicePub.setPacId("A");
        healthServicePubs.add(healthServicePub);
        ResponseEntity<List<HealthServicePub>> responseEntity =
                new ResponseEntity<>(healthServicePubs, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<List<HealthServicePub>>>any()))
                .thenReturn(responseEntity);

        // Set up to mock ords response
        ResponseEntity<HealthServicePub> responseEntity1 =
                new ResponseEntity<>(healthServicePub, HttpStatus.OK);
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<HealthServicePub>>any()))
                .thenReturn(responseEntity1);

        var healthController =
                new HealthController(
                        webServiceTemplate,
                        restTemplate,
                        objectMapper,
                        hsrQueue,
                        pingQueue,
                        rabbitTemplate,
                        amqpAdmin,
                        queueConfig);
        var resp = healthController.publishHSR(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetHSRCount() throws JsonProcessingException {
        var req = new GetHSRCount();
        req.setXMLString(
                "<HealthServiceCount>\n" + "    <csNum>1</csNum>\n" + "</HealthServiceCount>");

        var hsrCount = new HSRCount();
        hsrCount.setCount("1");
        hsrCount.setMax("1");
        hsrCount.setHsrId("A");
        hsrCount.setCsNum("1");
        hsrCount.setXmitOkay("A");
        ResponseEntity<HSRCount> responseEntity = new ResponseEntity<>(hsrCount, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<HSRCount>>any()))
                .thenReturn(responseEntity);

        var healthController =
                new HealthController(
                        webServiceTemplate,
                        restTemplate,
                        objectMapper,
                        hsrQueue,
                        pingQueue,
                        rabbitTemplate,
                        amqpAdmin,
                        queueConfig);
        var resp = healthController.getHSRCount(req);
        Assertions.assertNotNull(resp);
    }
}
