package ca.bc.gov.open.jagiconconsumer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.hsrservice.SubmitHealthServiceRequest;
import ca.bc.gov.open.icon.hsrservice.SubmitHealthServiceRequestResponse;
import ca.bc.gov.open.icon.models.HealthServicePub;
import ca.bc.gov.open.jagiconconsumer.services.HSRService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class IconConsumerApplicationTests {

    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

    @Mock private WebServiceTemplate soapTemplate = new WebServiceTemplate();

    @Test
    void testProcessHSR() throws JsonProcessingException, InterruptedException {
        var req = new HealthServicePub();
        req.setHsrId("A");
        req.setCsNum("A");
        req.setPacId("A");
        req.setLocation("A");
        req.setRequestDate(Instant.now());
        req.setHealthRequest("A");

        // Set up to mock soap service response
        var soapResp = new SubmitHealthServiceRequestResponse();
        when(soapTemplate.marshalSendAndReceive(
                        anyString(), Mockito.any(SubmitHealthServiceRequest.class)))
                .thenReturn(soapResp);

        Map<String, String> out = new HashMap<>();
        ResponseEntity<Map<String, String>> responseEntity =
                new ResponseEntity<>(out, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenReturn(responseEntity);

        HSRService hsrService = new HSRService(restTemplate, objectMapper, webServiceTemplate);
        hsrService.processHSR(req);
    }
}
