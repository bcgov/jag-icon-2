package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.auth.*;
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
    public void getPreAuthorizeClient() throws JsonProcessingException {
        var req = new GetPreAuthorizeClient();
        var preAuthorizeClientOut = new PreAuthorizeClientOut();
        var preAuthorizeClientInner = new PreAuthorizeClientInner();
        var preAuthorizeClient = new PreAuthorizeClient();
        preAuthorizeClient.setCsNum("A");
        preAuthorizeClient.setIsAllowed("A");

        req.setXMLString(preAuthorizeClientOut);
        preAuthorizeClientOut.setPreAuthorizeClient(preAuthorizeClientInner);
        preAuthorizeClientInner.setPreAuthorizeClient(preAuthorizeClient);

        var userInfo1 = new PreAuthorizeClient();
        ResponseEntity<PreAuthorizeClient> responseEntity =
                new ResponseEntity<>(userInfo1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<PreAuthorizeClient>>any()))
                .thenReturn(responseEntity);

        AuthenticationController authenticationController =
                new AuthenticationController(restTemplate, objectMapper);
        var resp = authenticationController.getPreAuthorizeClient(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void getHasFunctionalAbility() throws JsonProcessingException {
        var req = new GetHasFunctionalAbility();

        var hasFunctionalAbilityOut = new HasFunctionalAbilityOut();
        var hasFunctionalAbilityInner = new HasFunctionalAbilityInner();
        var hasFunctionalAbility = new HasFunctionalAbility();
        var functionalAbility = new FunctionalAbility();
        functionalAbility.setFunctionCd("A");
        functionalAbility.setServiceCd("A");
        hasFunctionalAbility.setFunctionalAbility(functionalAbility);
        hasFunctionalAbilityInner.setHasFunctionalAbility(hasFunctionalAbility);
        hasFunctionalAbilityOut.setHasFunctionalAbility(hasFunctionalAbilityInner);
        req.setXMLString(hasFunctionalAbilityOut);

        var UserTokenOut = new UserTokenOut();
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
        UserTokenOut.setUserToken(userTokenInner);
        req.setUserTokenString(UserTokenOut);

        var userInfo1 = new HasFunctionalAbility();
        ResponseEntity<HasFunctionalAbility> responseEntity =
                new ResponseEntity<>(userInfo1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<HasFunctionalAbility>>any()))
                .thenReturn(responseEntity);

        AuthenticationController authenticationController =
                new AuthenticationController(restTemplate, objectMapper);
        var resp = authenticationController.getHasFunctionalAbility(req);
        Assertions.assertNotNull(resp);
    }
}
