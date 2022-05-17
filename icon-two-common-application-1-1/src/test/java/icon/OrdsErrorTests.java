package icon;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.auth.GetDeviceInfo;
import ca.bc.gov.open.icon.auth.GetUserInfo;
import ca.bc.gov.open.icon.error.SetErrorMessage;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.hsr.GetHSRCount;
import ca.bc.gov.open.icon.hsr.GetHealthServiceRequestHistory;
import ca.bc.gov.open.icon.hsr.PublishHSR;
import ca.bc.gov.open.icon.myinfo.*;
import ca.bc.gov.open.icon.packageinfo.GetPackageInfo;
import ca.bc.gov.open.icon.session.GetSessionParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import icon.configuration.QueueConfig;
import icon.controllers.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OrdsErrorTests {
    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Mock
    private WebServiceTemplate webServiceTemplate;

    @Mock private RestTemplate restTemplate;

    @Qualifier("hsr-queue") private org.springframework.amqp.core.Queue hsrQueue;

    @Qualifier("ping-queue") private org.springframework.amqp.core.Queue pingQueue;

    @MockBean
    private RabbitTemplate rabbitTemplate;
    @MockBean private AmqpAdmin amqpAdmin;

    private QueueConfig queueConfig;


    @Test
    public void testEServiceAccessedFail() {
        var auditController = new AuditController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.eServiceAccessed(new EServiceAccessed()));
    }

    @Test
    public void testHomeScreenAccessedFail() {
        var auditController = new AuditController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.homeScreenAccessed(new HomeScreenAccessed()));
    }

    @Test
    public void testSessionTimeoutExecutedFail() {
        var auditController = new AuditController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.sessionTimeoutExecuted(new SessionTimeoutExecuted()));
    }

    @Test
    public void testEServiceFunctionAccessedFail() {
        var auditController = new AuditController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.eServiceFunctionAccessed(new EServiceFunctionAccessed()));
    }

    @Test
    public void testGetClientHistoryFail() {
        var auditController = new AuditController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.getClientHistory(new GetClientHistory()));
    }

    @Test
    public void testGetPackageInfoFail() {
        var auditController = new AuditController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class, () -> auditController.getPackageInfo(new GetPackageInfo()));
    }

    @Test
    public void testGetSessionParametersFail() {
        var auditController = new AuditController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.getSessionParameters(new GetSessionParameters()));
    }

    /*
        AuthenticationController
    */
    @Test
    public void testReauthenticationFailedFail() {
        var authenticationController = new AuthenticationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        authenticationController.reauthenticationFailed(
                                new ReauthenticationFailed()));
    }

    @Test
    public void testReauthenticationSucceededFail() {
        var authenticationController = new AuthenticationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        authenticationController.reauthenticationSucceeded(
                                new ReauthenticationSucceeded()));
    }

    @Test
    public void testLogoutExecutedFail() {
        var authenticationController = new AuthenticationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> authenticationController.logoutExecuted(new LogoutExcecuted()));
    }

    @Test
    public void testIdleTimeoutExecutedFail() {
        var authenticationController = new AuthenticationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> authenticationController.idleTimeoutExecuted(new IdleTimeoutExecuted()));
    }

    @Test
    public void testPrimaryAuthenticationCompletedFail() {
        var authenticationController = new AuthenticationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        authenticationController.primaryAuthenticationCompleted(
                                new PrimaryAuthenticationCompleted()));
    }

    /*
        ErrorHandlingController
    */
    @Test
    public void testSetErrorMessageFail() throws Exception {
        var errorHandlingController = new ErrorHandlingController(restTemplate, objectMapper);

        when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.POST),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<ParameterizedTypeReference<Map<String,String>>>any()))
                .thenThrow(new RestClientException("BAD"));

        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        errorHandlingController.setErrorMessage(
                                new SetErrorMessage()));
    }

    /*
        HealthController
    */
    @Test
    public void testHealthServiceRequestSubmittedFail() throws Exception {
        var healthController = new HealthController(
                webServiceTemplate,
                restTemplate,
                objectMapper,
                hsrQueue,
                pingQueue,
                rabbitTemplate,
                amqpAdmin,
                queueConfig
                );

        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        healthController.healthServiceRequestSubmitted(
                                new HealthServiceRequestSubmitted()));    }

    @Test
    public void testGetHealthServiceRequestHistoryFail() throws Exception {
        var healthController = new HealthController(
                webServiceTemplate,
                restTemplate,
                objectMapper,
                hsrQueue,
                pingQueue,
                rabbitTemplate,
                amqpAdmin,
                queueConfig
        );

        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        healthController.getHealthServiceRequestHistory(
                                new GetHealthServiceRequestHistory()));    }

    @Test
    public void testPublishHSRFail() throws Exception {
        var healthController = new HealthController(
                webServiceTemplate,
                restTemplate,
                objectMapper,
                hsrQueue,
                pingQueue,
                rabbitTemplate,
                amqpAdmin,
                queueConfig
        );

        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        healthController.publishHSR(
                                new PublishHSR()));    }

    @Test
    public void testGetHSRCountFail() throws Exception {
        var healthController = new HealthController(
                webServiceTemplate,
                restTemplate,
                objectMapper,
                hsrQueue,
                pingQueue,
                rabbitTemplate,
                amqpAdmin,
                queueConfig
        );

        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        healthController.getHSRCount(
                                new GetHSRCount()));    }

    /*
        InformationController
    */
    @Test
    public void testGetUserInfoFail() {
        var informationController = new InformationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> informationController.getUserInfo(new GetUserInfo()));
    }


    @Test
    public void testGetDeviceInfoFail() {
        var informationController = new InformationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> informationController.getDeviceInfo(new GetDeviceInfo()));
    }

    @Test
    public void testGetOrdersFail() {
        var informationController = new InformationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> informationController.getOrders(new GetOrders()));
    }

    @Test
    public void testGetProgramsFail() {
        var informationController = new InformationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> informationController.getPrograms(new GetPrograms()));
    }

    @Test
    public void testGetLocationsFail() {
        var informationController = new InformationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> informationController.getLocations(new GetLocations()));
    }

    @Test
    public void testGetConditionsFail() {
        var informationController = new InformationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> informationController.getConditions(new GetConditions()));
    }

    @Test
    public void testGetOrdersConditionsFail() {
        var informationController = new InformationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> informationController.getOrdersConditions(new GetOrdersConditions()));
    }

    @Test
    public void testGetDatesFail() {
        var informationController = new InformationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> informationController.getDates(new GetDates()));
    }

    @Test
    public void securityTestFail_Then401() throws Exception {
        var response =
                mockMvc.perform(post("/ws").contentType(MediaType.TEXT_XML))
                        .andExpect(status().is4xxClientError())
                        .andReturn();
        Assertions.assertEquals(
                HttpStatus.UNAUTHORIZED.value(), response.getResponse().getStatus());
    }

}
