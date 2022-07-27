package ca.bc.gov.open.jagiconpoller;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.models.PACModel;
import ca.bc.gov.open.jagiconpoller.config.QueueConfig;
import ca.bc.gov.open.jagiconpoller.services.PACPollerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class IconPollerApplicationTests {

    @Qualifier("pac-queue")
    private org.springframework.amqp.core.Queue pacQueue;

    @Qualifier("ping-queue")
    private org.springframework.amqp.core.Queue pingQueue;

    @MockBean private RabbitTemplate rabbitTemplate;
    @MockBean private AmqpAdmin amqpAdmin;

    private QueueConfig queueConfig;

    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate soapTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

    @Test
    void testPollOrdsForNewRecords() throws JsonProcessingException {

        List<PACModel> out = new ArrayList<>();
        var PACModel = new PACModel();
        out.add(PACModel);
        ResponseEntity<List<PACModel>> responseEntity = new ResponseEntity<>(out, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<List<PACModel>>>any()))
                .thenReturn(responseEntity);

        PACPollerService pacPollerService =
                new PACPollerService(
                        pacQueue, pingQueue, restTemplate, rabbitTemplate, amqpAdmin, queueConfig);
    }
}
