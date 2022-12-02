package ca.bc.gov.open.sender;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.models.HealthServicePub;
import ca.bc.gov.open.sender.services.HSRService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
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
        req.setHealthRequest("A");
        req.setCsNum("A");
        req.setHsrId("A");
        req.setPacId("A");
        req.setRequestDate("A");
        req.setLocation("A");

        List<HealthServicePub> healthServicePubs = new ArrayList();
        var healthServicePub = new HealthServicePub();
        healthServicePub.setCsNum("A");
        healthServicePub.setHsrId("A");
        healthServicePub.setLocation("A");
        healthServicePub.setRequestDate("A");
        healthServicePub.setHealthRequest("A");
        healthServicePub.setPacId("A");
        healthServicePub.setCsNum("A");
        healthServicePub.setHsrId("A");
        healthServicePub.setLocation("A");
        healthServicePub.setRequestDate("A");
        healthServicePub.setHealthRequest("A");
        healthServicePub.setPacId("A");
        healthServicePubs.add(healthServicePub);
        ResponseEntity<List<HealthServicePub>> responseEntity =
                new ResponseEntity<>(healthServicePubs, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<List<HealthServicePub>>>any()))
                .thenReturn(responseEntity);

        // Set up to mock ords response
        ResponseEntity<HealthServicePub> responseEntity1 =
                new ResponseEntity<>(healthServicePub, HttpStatus.OK);
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<HealthServicePub>>any()))
                .thenReturn(responseEntity1);

        HSRService hsrService = new HSRService(restTemplate, objectMapper, webServiceTemplate);
        hsrService.processHSR(req);
    }
}
