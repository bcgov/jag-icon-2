package icon;

import ca.bc.gov.open.icon.audit.Base;
import ca.bc.gov.open.icon.audit.HealthServiceRequest;
import ca.bc.gov.open.icon.audit.HealthServiceRequestSubmitted;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.error.SetErrorMessage;
import ca.bc.gov.open.icon.hsr.*;
import ca.bc.gov.open.icon.hsrservice.ArrayOfHealthServiceRequest;
import ca.bc.gov.open.icon.hsrservice.GetHealthServiceRequestSummary;
import ca.bc.gov.open.icon.hsrservice.GetHealthServiceRequestSummaryResponse;
import ca.bc.gov.open.icon.hsrservice.HealthServiceRequestBundle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import icon.configuration.QueueConfig;
import icon.controllers.AuthenticationController;
import icon.controllers.ErrorHandlingController;
import icon.controllers.HealthController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
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

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class HealthControllerTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

    @Qualifier("hsr-queue") private org.springframework.amqp.core.Queue hsrQueue;

    @Qualifier("ping-queue") private org.springframework.amqp.core.Queue pingQueue;

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
                        queueConfig
                        );
        var resp = healthController.healthServiceRequestSubmitted(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetHealthServiceRequestHistory() throws JsonProcessingException {
        var req = new GetHealthServiceRequestHistory();
        var healServiceOuter = new HealServiceOuter();
        var healServiceInner = new HealServiceInner();
        var healService = new HealService();
        List<ca.bc.gov.open.icon.hsr.HealthServiceRequest> hsrs = new ArrayList<>();
        var hsr = new ca.bc.gov.open.icon.hsr.HealthServiceRequest();
        hsr.setHsrId("A");
        hsr.setPacID("A");
        hsr.setLocation("A");
        hsr.setRequestDate("A");
        hsr.setHealthRequest("A");
        hsrs.add(hsr);
        req.setXMLString(healServiceOuter);
        healServiceOuter.setHealthService(healServiceInner);
        healService.setHealthServiceRequest(hsrs);
        var row = new Row();
        row.setStart("1");
        row.setEnd("3");
        row.setTotal("3");
        healService.setRow(row);
        healService.setCsNum("1");
        healServiceInner.setHealthService(healService);
        healServiceOuter.setHealthService(healServiceInner);
        req.setXMLString(healServiceOuter);

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
                        queueConfig
                );

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
                anyString(),
                Mockito.any(GetHealthServiceRequestSummary.class)))
                .thenReturn(soapResp);

        var resp = healthController.getHealthServiceRequestHistory(req);
        Assertions.assertNotNull(resp);
    }


    @Test
    public void testGetHSRCount() throws JsonProcessingException {
        var req = new GetHSRCount();
        var hsrCountOuter = new HSRCountOuter();
        var hsrCountInner = new HSRCountInner();
        var hsrCount = new HSRCount();
        req.setXMLString(hsrCountOuter);
        hsrCountOuter.setHealthServiceCount(hsrCountInner);
        hsrCountInner.setHealthServiceCount(hsrCount);
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
                        queueConfig
                );
        var resp = healthController.getHSRCount(req);
        Assertions.assertNotNull(resp);
    }

}
