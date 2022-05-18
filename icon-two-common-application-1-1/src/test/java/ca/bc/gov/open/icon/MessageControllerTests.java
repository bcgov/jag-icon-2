package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.controllers.MessageController;
import ca.bc.gov.open.icon.ereporting.AppointmentMessage;
import ca.bc.gov.open.icon.ereporting.GetMessage;
import ca.bc.gov.open.icon.ereporting.SetMessageDate;
import ca.bc.gov.open.icon.message.GetMessageDetails;
import ca.bc.gov.open.icon.message.GetMessages;
import ca.bc.gov.open.icon.message.Messages;
import ca.bc.gov.open.icon.message.SetMessageDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class MessageControllerTests {
    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testMessageAccessed() throws JsonProcessingException {
        var req = new MessageAccessed();

        var status = new Status();
        status.setSuccess(true);
        ResponseEntity<Status> responseEntity = new ResponseEntity<>(status, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Status>>any()))
                .thenReturn(responseEntity);

        MessageController messageController = new MessageController(restTemplate, objectMapper);
        var resp = messageController.messageAccessed(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetMessage() throws JsonProcessingException {
        var req = new GetMessage();

        var appointmentMessage = new AppointmentMessage();
        ResponseEntity<AppointmentMessage> responseEntity =
                new ResponseEntity<>(appointmentMessage, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<AppointmentMessage>>any()))
                .thenReturn(responseEntity);

        MessageController messageController = new MessageController(restTemplate, objectMapper);
        var resp = messageController.getMessage(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testSetMessageDate() throws JsonProcessingException {
        var req = new SetMessageDate();

        var appointmentMessage = new AppointmentMessage();
        ResponseEntity<AppointmentMessage> responseEntity =
                new ResponseEntity<>(appointmentMessage, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<AppointmentMessage>>any()))
                .thenReturn(responseEntity);

        MessageController messageController = new MessageController(restTemplate, objectMapper);
        var resp = messageController.setMessageDate(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testSetMessageDetails() throws JsonProcessingException {
        var req = new SetMessageDetails();

        var messages = new Messages();
        ResponseEntity<Messages> responseEntity = new ResponseEntity<>(messages, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Messages>>any()))
                .thenReturn(responseEntity);

        MessageController messageController = new MessageController(restTemplate, objectMapper);
        var resp = messageController.setMessageDetails(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetMessages() throws JsonProcessingException {
        var req = new GetMessages();

        var messages = new Messages();
        ResponseEntity<Messages> responseEntity = new ResponseEntity<>(messages, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Messages>>any()))
                .thenReturn(responseEntity);

        MessageController messageController = new MessageController(restTemplate, objectMapper);
        var resp = messageController.getMessages(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetMessageDetails() throws JsonProcessingException {
        var req = new GetMessageDetails();

        var messages = new Messages();
        ResponseEntity<Messages> responseEntity = new ResponseEntity<>(messages, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Messages>>any()))
                .thenReturn(responseEntity);

        MessageController messageController = new MessageController(restTemplate, objectMapper);
        var resp = messageController.getMessageDetails(req);
        Assertions.assertNotNull(resp);
    }
}
