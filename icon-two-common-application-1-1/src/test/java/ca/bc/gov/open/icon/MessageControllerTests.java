package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.Base;
import ca.bc.gov.open.icon.audit.Message;
import ca.bc.gov.open.icon.audit.MessageAccessed;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.controllers.MessageController;
import ca.bc.gov.open.icon.ereporting.*;
import ca.bc.gov.open.icon.message.*;
import ca.bc.gov.open.icon.message.UserToken;
import ca.bc.gov.open.icon.message.UserTokenInner;
import ca.bc.gov.open.icon.message.UserTokenOuter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
        var Message = new Message();
        var base = new Base();
        base.setSessionID("A");
        base.setCsNumber("A");
        base.setSessionID("A");
        Message.setBase(base);
        Message.setEServiceCD("A");
        Message.setEServiceFuntionCD("A");

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

        var AppointmentMessageOuter = new AppointmentMessageOuter();
        var AppointmentMessage = new AppointmentMessage();
        AppointmentMessage.setText("A");
        AppointmentMessage.setCsNum("A");
        AppointmentMessageOuter.setAppointmentMessage(AppointmentMessage);
        req.setXMLString("A");

        var userTokenOuter = new ca.bc.gov.open.icon.ereporting.UserTokenOuter();
        var userToken = new ca.bc.gov.open.icon.ereporting.UserToken();

        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        userTokenOuter.setUserToken(userToken);
        req.setUserTokenString("A");

        var appointmentMessage = new AppointmentMessage();
        appointmentMessage.setCsNum("A");
        appointmentMessage.setText("A");
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

        var AppointmentMessageOuter = new AppointmentMessageOuter();
        var AppointmentMessage = new AppointmentMessage();
        AppointmentMessage.setText("A");
        AppointmentMessage.setCsNum("A");
        AppointmentMessageOuter.setAppointmentMessage(AppointmentMessage);
        req.setXMLString("A");

        var userTokenOuter = new ca.bc.gov.open.icon.ereporting.UserTokenOuter();
        var userToken = new ca.bc.gov.open.icon.ereporting.UserToken();

        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        userTokenOuter.setUserToken(userToken);
        req.setUserTokenString("A");

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

        var MessagesOuter = new MessagesOuter();
        var MessagesInner = new MessagesInner();
        var Messages = new Messages();

        req.setXMLString(MessagesOuter);
        MessagesOuter.setMessages(MessagesInner);
        MessagesInner.setMessages(Messages);

        Messages.setCsNum("A");
        Messages.setUnreadMessageCount("A");
        var row = new Row();
        row.setStart("1");
        row.setEnd("3");
        row.setTotal("3");
        Messages.setRow(row);
        List<MessageDetails> MessageDetails = new ArrayList<>();

        var MessageDetail = new MessageDetails();
        MessageDetail.setId("A");
        MessageDetail.setUnread("A");
        MessageDetail.setTimestamp(Instant.now());
        var MessageType = new MessageType();
        MessageType.setCode("A");
        MessageType.setDescription("A");
        MessageDetail.setMessageType(MessageType);
        MessageDetail.setText("A");
        var Sender = new Sender();
        List<Relationships> Relationships = new ArrayList<>();
        var Relationship = new Relationships();
        Relationship.setCode("A");
        Relationship.setDescription("A");
        Relationships.add(Relationship);
        var Application = new Application();
        Application.setCode("Application");
        Application.setDescription("A");
        Sender.setApplication(Application);
        var Individual = new Individual();
        Individual.setFirstName("A");
        Individual.setLastName("A");
        Individual.setRelationships(Relationships);
        Sender.setIndividual(Individual);
        MessageDetail.setSender(Sender);
        MessageDetail.setHasDisclosureSet("A");
        MessageDetails.add(MessageDetail);
        Messages.setMessageDetails(MessageDetails);

        var userTokenOuter = new UserTokenOuter();
        var userTokenInner = new UserTokenInner();
        var userToken = new UserToken();

        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        userTokenInner.setUserToken(userToken);
        userTokenOuter.setUserToken(userTokenInner);
        req.setUserTokenString(userTokenOuter);

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
        var MessagesOuter = new MessagesOuter();
        var MessagesInner = new MessagesInner();
        var Messages = new Messages();

        req.setXMLString(MessagesOuter);
        MessagesOuter.setMessages(MessagesInner);
        MessagesInner.setMessages(Messages);

        Messages.setCsNum("A");
        Messages.setUnreadMessageCount("A");
        var row = new Row();
        row.setStart("1");
        row.setEnd("3");
        row.setTotal("3");
        Messages.setRow(row);
        List<MessageDetails> MessageDetails = new ArrayList<>();
        var MessageDetail = new MessageDetails();
        MessageDetail.setId("A");
        MessageDetail.setUnread("A");
        MessageDetail.setTimestamp(Instant.now());
        var MessageType = new MessageType();
        MessageType.setCode("A");
        MessageType.setDescription("A");
        MessageDetail.setMessageType(MessageType);
        MessageDetail.setText("A");
        var Sender = new Sender();
        List<Relationships> Relationships = new ArrayList<>();
        var Relationship = new Relationships();
        Relationship.setCode("A");
        Relationship.setDescription("A");
        Relationships.add(Relationship);
        var Application = new Application();
        Application.setCode("Application");
        Application.setDescription("A");
        Sender.setApplication(Application);
        var Individual = new Individual();
        Individual.setFirstName("A");
        Individual.setLastName("A");
        Individual.setRelationships(Relationships);
        Sender.setIndividual(Individual);
        MessageDetail.setSender(Sender);
        MessageDetail.setHasDisclosureSet("A");
        MessageDetails.add(MessageDetail);
        Messages.setMessageDetails(MessageDetails);

        var userTokenOuter = new UserTokenOuter();
        var userTokenInner = new UserTokenInner();
        var userToken = new UserToken();

        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        userTokenInner.setUserToken(userToken);
        userTokenOuter.setUserToken(userTokenInner);
        req.setUserTokenString(userTokenOuter);

        var messages1 = new Messages();
        messages1 = Messages;
        ResponseEntity<Messages> responseEntity = new ResponseEntity<>(messages1, HttpStatus.OK);

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
        var MessagesOuter = new MessagesOuter();
        var MessagesInner = new MessagesInner();
        var messages = new Messages();

        req.setXMLString(MessagesOuter);
        MessagesOuter.setMessages(MessagesInner);
        MessagesInner.setMessages(messages);

        messages.setCsNum("A");
        messages.setUnreadMessageCount("A");
        var row = new Row();
        row.setStart("1");
        row.setEnd("3");
        row.setTotal("3");
        List<MessageDetails> MessageDetails = new ArrayList<>();
        messages.setRow(row);
        var MessageDetail = new MessageDetails();
        MessageDetail.setId("A");
        MessageDetail.setUnread("A");
        MessageDetail.setTimestamp(Instant.now());
        var MessageType = new MessageType();
        MessageType.setCode("A");
        MessageType.setDescription("A");
        MessageDetail.setMessageType(MessageType);
        MessageDetail.setText("A");
        var Sender = new Sender();
        List<Relationships> Relationships = new ArrayList<>();
        var Relationship = new Relationships();
        Relationship.setCode("A");
        Relationship.setDescription("A");
        Relationships.add(Relationship);
        var Application = new Application();
        Application.setCode("Application");
        Application.setDescription("A");
        Sender.setApplication(Application);
        var Individual = new Individual();
        Individual.setFirstName("A");
        Individual.setLastName("A");
        Individual.setRelationships(Relationships);
        Sender.setIndividual(Individual);
        MessageDetail.setSender(Sender);
        MessageDetail.setHasDisclosureSet("A");
        MessageDetails.add(MessageDetail);
        messages.setMessageDetails(MessageDetails);

        var userTokenOuter = new UserTokenOuter();
        var userTokenInner = new UserTokenInner();
        var userToken = new UserToken();

        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        userTokenInner.setUserToken(userToken);
        userTokenOuter.setUserToken(userTokenInner);
        req.setUserTokenString(userTokenOuter);

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
