package ca.bc.gov.open.icon;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.biometrics.GetDID;
import ca.bc.gov.open.icon.controllers.DidController;
import ca.bc.gov.open.icon.ips.GetDIDResponse2;
import ca.bc.gov.open.icon.ips.ResponseCode;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DidControllerTests {
    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate soapTemplate;

    @Autowired @Mock private RestTemplate restTemplate;

    @Test
    public void testGetDid() throws JsonProcessingException {
        var req = new GetDID();
        req.setRequestorType("Individual");
        req.setRequestorUserId("A");
        req.setIdRef("A");

        var didController = new DidController(soapTemplate, objectMapper);

        // Set up to mock soap service response
        var soapResp = new ca.bc.gov.open.icon.ips.GetDIDResponse();
        var getDIDResponse2 = new GetDIDResponse2();
        getDIDResponse2.setCode(ResponseCode.SUCCESS);
        soapResp.setGetDIDResult(getDIDResponse2);
        when(soapTemplate.marshalSendAndReceive(
                        anyString(), Mockito.any(ca.bc.gov.open.icon.ips.GetDID.class)))
                .thenReturn(soapResp);

        var resp = didController.getDid(req);
        Assertions.assertNotNull(resp);
    }
}
