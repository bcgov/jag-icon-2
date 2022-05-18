package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.controllers.AuthenticationController;
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
public class AuthenticationControllerTests {

    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testReauthenticationFailed() throws JsonProcessingException {
        var req = new ReauthenticationFailed();
        var reauthentication = new Reauthentication();
        var base = new Base();
        base.setSessionID("A");
        base.setCsNumber("A");
        base.setSessionID("A");
        reauthentication.setBase(base);
        reauthentication.setEServiceCD("A");
        reauthentication.setEServiceFuntionCD("A");
        reauthentication.setTransactionID("A");
        reauthentication.setBiometricID("A");
        reauthentication.setEReportingEventID("A");
        req.setReauthentication(reauthentication);

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

        AuthenticationController authenticationController =
                new AuthenticationController(restTemplate, objectMapper);
        var resp = authenticationController.reauthenticationFailed(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testReauthenticationSucceeded() throws JsonProcessingException {
        var req = new ReauthenticationSucceeded();
        var reauthentication = new Reauthentication();
        var base = new Base();
        base.setSessionID("A");
        base.setCsNumber("A");
        base.setSessionID("A");
        reauthentication.setBase(base);
        reauthentication.setEServiceCD("A");
        reauthentication.setEServiceFuntionCD("A");
        reauthentication.setTransactionID("A");
        reauthentication.setBiometricID("A");
        reauthentication.setEReportingEventID("A");
        req.setReauthentication(reauthentication);

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

        AuthenticationController authenticationController =
                new AuthenticationController(restTemplate, objectMapper);
        var resp = authenticationController.reauthenticationSucceeded(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testLogoutExecuted() throws JsonProcessingException {
        var req = new LogoutExcecuted();
        var logout = new Logout();
        var base = new Base();
        base.setSessionID("A");
        base.setCsNumber("A");
        base.setSessionID("A");
        logout.setBase(base);
        logout.setEServiceCD("A");
        logout.setEServiceFuntionCD("A");
        req.setLogout(logout);

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

        AuthenticationController authenticationController =
                new AuthenticationController(restTemplate, objectMapper);
        var resp = authenticationController.logoutExecuted(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testIdleTimeoutExecuted() throws JsonProcessingException {
        var req = new IdleTimeoutExecuted();
        var idleTimeout = new IdleTimeout();
        var base = new Base();
        base.setSessionID("A");
        base.setCsNumber("A");
        base.setSessionID("A");
        idleTimeout.setBase(base);
        idleTimeout.setEServiceCD("A");
        idleTimeout.setEServiceFuntionCD("A");
        req.setIdleTimeout(idleTimeout);

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

        AuthenticationController authenticationController =
                new AuthenticationController(restTemplate, objectMapper);
        var resp = authenticationController.idleTimeoutExecuted(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testPrimaryAuthenticationCompleted() throws JsonProcessingException {
        var req = new PrimaryAuthenticationCompleted();
        var primaryAuthentication = new PrimaryAuthentication();
        var base = new Base();
        base.setSessionID("A");
        base.setCsNumber("A");
        base.setSessionID("A");
        primaryAuthentication.setBase(base);
        primaryAuthentication.setBiometricID("A");
        primaryAuthentication.setBiometricID("A");
        req.setPrimaryAuthentication(primaryAuthentication);

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

        AuthenticationController authenticationController =
                new AuthenticationController(restTemplate, objectMapper);
        var resp = authenticationController.primaryAuthenticationCompleted(req);
        Assertions.assertNotNull(resp);
    }
}
