package ca.bc.gov.open.icon;

import ca.bc.gov.open.icon.bcs.*;
import ca.bc.gov.open.icon.biometrics.Deactivate;
import ca.bc.gov.open.icon.biometrics.Reactivate;
import ca.bc.gov.open.icon.controllers.ActivationController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ActivationControllerTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private WebServiceTemplate soapTemplate;

    @Autowired
    @Mock private RestTemplate restTemplate ;

    @Test
    public void testReactivate() throws Exception {
        var req = new Reactivate();
        req.setCredentialRef("A");
        req.setRequestorUserId("A");
        req.setRequestorType("LDB");

        var activationController =
                new ActivationController(
                        soapTemplate,
                        objectMapper);

        // Set up to mock soap service response
        var soapResp =  new ReactivateCredentialResponse();
        var reactivateCredentialResponse2 = new ReactivateCredentialResponse2();
        reactivateCredentialResponse2.setCode(ResponseCode.SUCCESS);
        soapResp.setReactivateCredentialResult(reactivateCredentialResponse2);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(ReactivateCredential.class)))
                .thenReturn(soapResp);

        var resp = activationController.reactivate(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testDeactivate() throws JsonProcessingException {
        var req = new Deactivate();
        req.setCredentialRef("A");
        req.setRequestorUserId("A");
        req.setRequestorType("LDB");

        var activationController =
                new ActivationController(
                        soapTemplate,
                        objectMapper);

        // Set up to mock soap service response
        var soapResp =  new DestroyCredentialResponse();
        var reactivateCredentialResponse2 = new DestroyCredentialResponse2();
        reactivateCredentialResponse2.setCode(ResponseCode.SUCCESS);
        soapResp.setDestroyCredentialResult(reactivateCredentialResponse2);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(DeactivateCredential.class)))
                .thenReturn(soapResp);

        var resp = activationController.deactivate(req);
        Assertions.assertNotNull(resp);
    }
}
