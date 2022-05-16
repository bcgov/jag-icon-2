package icon;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.error.SetErrorMessage;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.myinfo.GetClientHistory;
import ca.bc.gov.open.icon.packageinfo.GetPackageInfo;
import ca.bc.gov.open.icon.session.GetSessionParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import icon.controllers.AuditController;
import icon.controllers.AuthenticationController;
import icon.controllers.ErrorHandlingController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OrdsErrorTests {
    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Mock private RestTemplate restTemplate;

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

        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        errorHandlingController.setErrorMessage(
                                new SetErrorMessage()));    }

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
