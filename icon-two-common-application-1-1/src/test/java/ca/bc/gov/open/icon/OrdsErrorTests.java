package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.configuration.QueueConfig;
import ca.bc.gov.open.icon.controllers.*;
import ca.bc.gov.open.icon.ereporting.*;
import ca.bc.gov.open.icon.error.SetErrorMessage;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.exceptions.ServiceFaultException;
import ca.bc.gov.open.icon.hsr.GetHSRCount;
import ca.bc.gov.open.icon.hsr.GetHealthServiceRequestHistory;
import ca.bc.gov.open.icon.hsr.PublishHSR;
import ca.bc.gov.open.icon.message.GetMessageDetails;
import ca.bc.gov.open.icon.message.GetMessages;
import ca.bc.gov.open.icon.message.SetMessageDetails;
import ca.bc.gov.open.icon.myinfo.*;
import ca.bc.gov.open.icon.myinfo.GetLocations;
import ca.bc.gov.open.icon.packageinfo.GetPackageInfo;
import ca.bc.gov.open.icon.session.GetSessionParameters;
import ca.bc.gov.open.icon.tombstone.GetTombStoneInfo;
import ca.bc.gov.open.icon.tombstone.GetTombStoneInfo2;
import ca.bc.gov.open.icon.tombstone.GetTombStoneInfoRequest;
import ca.bc.gov.open.icon.trustaccount.GetTrustAccount;
import ca.bc.gov.open.icon.visitschedule.GetVisitSchedule;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class OrdsErrorTests {
    @Mock private WebServiceTemplate webServiceTemplate;
    @Mock private RestTemplate restTemplate;
    @Mock private ObjectMapper objectMapper;
    @Mock private QueueConfig queueConfig;
    @Mock private RabbitTemplate rabbitTemplate;
    @Mock private AmqpAdmin amqpAdmin;

    @Qualifier("hsr-queue")
    @Mock
    private org.springframework.amqp.core.Queue hsrQueue;

    @Qualifier("ping-queue")
    @Mock
    private org.springframework.amqp.core.Queue pingQueue;

    @Mock private InformationController informationController;
    @Mock private AuditController auditController;
    @Mock private AuthenticationController authenticationController;
    @Mock private MessageController messageController;
    @Mock private RecordController recordController;
    @Mock private ReportingController reportingController;
    @Mock private HealthController healthController;
    @Mock private ClientController clientController;
    @Mock private ErrorHandlingController errorHandlingController;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        informationController = Mockito.spy(new InformationController(restTemplate, objectMapper));
        auditController = Mockito.spy(new AuditController(restTemplate, objectMapper));
        authenticationController =
                Mockito.spy(new AuthenticationController(restTemplate, objectMapper));
        messageController = Mockito.spy(new MessageController(restTemplate, objectMapper));
        recordController = Mockito.spy(new RecordController(restTemplate, objectMapper));
        reportingController = Mockito.spy(new ReportingController(restTemplate, objectMapper));
        healthController =
                Mockito.spy(
                        new HealthController(
                                webServiceTemplate,
                                restTemplate,
                                objectMapper,
                                hsrQueue,
                                pingQueue,
                                rabbitTemplate,
                                amqpAdmin,
                                queueConfig));
        clientController = Mockito.spy(new ClientController(restTemplate, objectMapper));
        errorHandlingController =
                Mockito.spy(new ErrorHandlingController(restTemplate, objectMapper));
    }

    /*
        AuditController
    */
    @Test
    public void testEServiceAccessedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.eServiceAccessed(new EServiceAccessed()));
    }

    @Test
    public void testHomeScreenAccessedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.homeScreenAccessed(new HomeScreenAccessed()));
    }

    @Test
    public void testSessionTimeoutExecutedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.sessionTimeoutExecuted(new SessionTimeoutExecuted()));
    }

    @Test
    public void testEServiceFunctionAccessedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.eServiceFunctionAccessed(new EServiceFunctionAccessed()));
    }

    @Test
    public void testGetPackageInfoFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> auditController.getPackageInfo(new GetPackageInfo()));
    }

    @Test
    public void testGetSessionParametersFail() {
        GetSessionParameters getSessionParameters = new GetSessionParameters();
        getSessionParameters.setXMLString("A");
        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.getSessionParameters(getSessionParameters));
    }

    /*
        AuthenticationController
    */
    @Test
    public void testReauthenticationFailedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        authenticationController.reauthenticationFailed(
                                new ReauthenticationFailed()));
    }

    @Test
    public void testReauthenticationSucceededFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        authenticationController.reauthenticationSucceeded(
                                new ReauthenticationSucceeded()));
    }

    @Test
    public void testLogoutExecutedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> authenticationController.logoutExecuted(new LogoutExcecuted()));
    }

    @Test
    public void testIdleTimeoutExecutedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> authenticationController.idleTimeoutExecuted(new IdleTimeoutExecuted()));
    }

    @Test
    public void testPrimaryAuthenticationCompletedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        authenticationController.primaryAuthenticationCompleted(
                                new PrimaryAuthenticationCompleted()));
    }
    /*
        ClientController
    */

    @Test
    public void testGetTombStoneInfoFail() {
        var getTombStoneInfo = new GetTombStoneInfo();
        var getTombStoneInfo2 = new GetTombStoneInfo2();
        var getTombStoneInfoRequest = new GetTombStoneInfoRequest();

        getTombStoneInfo.setXMLString("A");
        getTombStoneInfo2.setTombStoneInfo(getTombStoneInfoRequest);

        Assertions.assertThrows(
                ORDSException.class, () -> clientController.getTombStoneInfo(getTombStoneInfo));
    }

    @Test
    public void testGetTrustAccountFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> clientController.getTrustAccount(new GetTrustAccount()));
    }

    @Test
    public void testGetVisitScheduleFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> clientController.getVisitSchedule(new GetVisitSchedule()));
    }

    /*
        ErrorHandlingController
    */
    @Test
    public void testSetErrorMessageFail() {
        when(restTemplate.exchange(
                        Mockito.anyString(),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenThrow(new RestClientException("BAD"));

        Assertions.assertThrows(
                ServiceFaultException.class,
                () -> errorHandlingController.setErrorMessage(new SetErrorMessage()));
    }

    /*
        HealthController
    */
    @Test
    public void testHealthServiceRequestSubmittedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        healthController.healthServiceRequestSubmitted(
                                new HealthServiceRequestSubmitted()));
    }

    @Test
    public void testGetHealthServiceRequestHistoryFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        healthController.getHealthServiceRequestHistory(
                                new GetHealthServiceRequestHistory()));
    }

    @Test
    public void testPublishHSROrdsFail() {
        PublishHSR publishHSR = new PublishHSR();
        publishHSR.setXMLString("A");
        publishHSR.setUserTokenString("A");
        Assertions.assertThrows(ORDSException.class, () -> healthController.publishHSR(publishHSR));
    }

    @Test
    public void testPublishHSRFail() {
        Assertions.assertThrows(
                ServiceFaultException.class, () -> healthController.publishHSR(new PublishHSR()));
    }

    @Test
    public void testGetHSRCountFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> healthController.getHSRCount(new GetHSRCount()));
    }

    /*
        InformationController
    */

    @Test
    public void testGetOrdersFail() {
        GetOrders getOrders = new GetOrders();
        getOrders.setXMLString("A");
        getOrders.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> informationController.getOrders(getOrders));
    }

    @Test
    public void testGetProgramsFail() {
        GetPrograms getPrograms = new GetPrograms();
        getPrograms.setXMLString("A");
        getPrograms.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> informationController.getPrograms(getPrograms));
    }

    @Test
    public void testGetLocationsFail() {
        GetLocations getLocations = new GetLocations();
        getLocations.setXMLString("A");
        getLocations.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> informationController.getLocations(getLocations));
    }

    @Test
    public void testGetConditionsFail() {
        GetConditions getConditions = new GetConditions();
        getConditions.setXMLString("A");
        getConditions.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> informationController.getConditions(getConditions));
    }

    @Test
    public void testGetOrdersConditionsFail() {
        GetOrdersConditions getOrdersConditions = new GetOrdersConditions();
        getOrdersConditions.setXMLString("A");
        getOrdersConditions.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class,
                () -> informationController.getOrdersConditions(getOrdersConditions));
    }

    @Test
    public void testGetDatesFail() {
        GetDates getDates = new GetDates();
        getDates.setXMLString("A");
        getDates.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> informationController.getDates(getDates));
    }

    @Test
    public void testGetClientHistoryFail() {
        GetClientHistory getClientHistory = new GetClientHistory();
        getClientHistory.setXMLString("A");
        getClientHistory.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class,
                () -> informationController.getClientHistory(getClientHistory));
    }

    /*
        MessageController
    */

    @Test
    public void testMessageAccessedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> messageController.messageAccessed(new MessageAccessed()));
    }

    @Test
    public void testGetMessageFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> messageController.getMessage(new GetMessage()));
    }

    @Test
    public void testSetMessageDateFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> messageController.setMessageDate(new SetMessageDate()));
    }

    @Test
    public void testSetMessageDetailsFail() {
        SetMessageDetails setMessageDetails = new SetMessageDetails();
        setMessageDetails.setXMLString("A");
        setMessageDetails.setUserTokenString("A");

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenThrow(new ORDSException());

        Assertions.assertThrows(
                ORDSException.class, () -> messageController.setMessageDetails(setMessageDetails));
    }

    @Test
    public void testGetMessagesFail() {
        GetMessages getMessages = new GetMessages();
        getMessages.setXMLString("A");
        getMessages.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> messageController.getMessages(getMessages));
    }

    @Test
    public void testGetMessageDetailsFail() {
        GetMessageDetails getMessageDetails = new GetMessageDetails();
        getMessageDetails.setXMLString("A");
        getMessageDetails.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> messageController.getMessageDetails(getMessageDetails));
    }

    /*
        RecordController
    */
    @Test
    public void testRecordCompletedFail() {
        when(restTemplate.exchange(
                        Mockito.anyString(),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenThrow(new RestClientException("BAD"));

        Assertions.assertThrows(
                ServiceFaultException.class,
                () -> recordController.recordCompleted(new RecordCompleted()));
    }

    @Test
    public void testRecordExceptionFail() {
        when(restTemplate.exchange(
                        Mockito.anyString(),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenThrow(new RestClientException("BAD"));

        Assertions.assertThrows(
                ServiceFaultException.class,
                () -> recordController.recordException(new RecordException()));
    }

    /*
        ReportingController
    */
    @Test
    public void testEReportAnswersSubmittedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> reportingController.eReportAnswersSubmitted(new EReportAnswersSubmitted()));
    }

    @Test
    public void testGetReportingCmpltInstructionFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        reportingController.getReportingCmpltInstruction(
                                new GetReportingCmpltInstruction()));
    }

    @Test
    public void testGetLocationsResponseFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        reportingController.getLocationsResponse(
                                new ca.bc.gov.open.icon.ereporting.GetLocations()));
    }

    @Test
    public void testSubmitAnswersFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> reportingController.submitAnswers(new SubmitAnswers()));
    }

    @Test
    public void testGetAppointmentFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> reportingController.getAppointment(new GetAppointment()));
    }

    @Test
    public void testGetQuestionsFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> reportingController.getQuestions(new GetQuestions()));
    }

    @Test
    public void testGetStatusFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> reportingController.getStatus(new GetStatus()));
    }
}
