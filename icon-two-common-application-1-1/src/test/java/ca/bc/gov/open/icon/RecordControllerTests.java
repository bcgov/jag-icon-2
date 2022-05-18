package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.controllers.RecordController;
import ca.bc.gov.open.icon.ereporting.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RecordControllerTests {
    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testRecordCompleted() throws JsonProcessingException {
        var req = new RecordCompleted();
        var clientLogNotificationOuter = new ClientLogNotificationOuter();
        var clientLogNotificationInner = new ClientLogNotificationInner();
        var clientLogNotification = new ClientLogNotification();
        req.setXMLString(clientLogNotificationOuter);
        clientLogNotificationOuter.setClientLogNotification(clientLogNotificationInner);
        clientLogNotificationInner.setClientLogNotification(clientLogNotification);

        var out = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(out, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Map>>any()))
                .thenReturn(responseEntity);

        RecordController recordController = new RecordController(restTemplate, objectMapper);
        var resp = recordController.recordCompleted(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testRecordException() throws JsonProcessingException {
        var req = new RecordException();
        var clientLogNotificationOuter = new ClientLogNotificationOuter();
        var clientLogNotificationInner = new ClientLogNotificationInner();
        var clientLogNotification = new ClientLogNotification();
        req.setXMLString(clientLogNotificationOuter);
        clientLogNotificationOuter.setClientLogNotification(clientLogNotificationInner);
        clientLogNotificationInner.setClientLogNotification(clientLogNotification);

        var out = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(out, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Map>>any()))
                .thenReturn(responseEntity);

        RecordController recordController = new RecordController(restTemplate, objectMapper);
        var resp = recordController.recordException(req);
        Assertions.assertNotNull(resp);
    }
}
