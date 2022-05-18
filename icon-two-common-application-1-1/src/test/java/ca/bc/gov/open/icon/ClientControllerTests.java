package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.controllers.ClientController;
import ca.bc.gov.open.icon.tombstone.GetTombStoneInfo;
import ca.bc.gov.open.icon.tombstone.GetTombStoneInfo2;
import ca.bc.gov.open.icon.tombstone.GetTombStoneInfoRequest;
import ca.bc.gov.open.icon.trustaccount.GetTrustAccount;
import ca.bc.gov.open.icon.trustaccount.GetTrustAccount2;
import ca.bc.gov.open.icon.trustaccount.GetTrustAccountRequest;
import ca.bc.gov.open.icon.visitschedule.GetVisitSchedule;
import ca.bc.gov.open.icon.visitschedule.GetVisitSchedule2;
import ca.bc.gov.open.icon.visitschedule.GetVisitScheduleRequest;
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
public class ClientControllerTests {
    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testGetTombStoneInfo() throws JsonProcessingException {
        var req = new GetTombStoneInfo();
        var getTombStoneInfo2 = new GetTombStoneInfo2();
        var getTombStoneInfoRequest = new GetTombStoneInfoRequest();

        req.setXMLString(getTombStoneInfo2);
        getTombStoneInfo2.setTombStoneInfo(getTombStoneInfoRequest);

        var getTombStoneInfoRequest1 = new GetTombStoneInfoRequest();
        ResponseEntity<GetTombStoneInfoRequest> responseEntity =
                new ResponseEntity<>(getTombStoneInfoRequest1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetTombStoneInfoRequest>>any()))
                .thenReturn(responseEntity);

        ClientController clientController = new ClientController(restTemplate, objectMapper);
        var resp = clientController.getTombStoneInfo(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetTrustAccount() throws JsonProcessingException {
        var req = new GetTrustAccount();
        var getTrustAccount2 = new GetTrustAccount2();
        var getTrustAccountRequest = new GetTrustAccountRequest();

        req.setXMLString(getTrustAccount2);
        getTrustAccount2.setTrustAccount(getTrustAccountRequest);

        var getTrustAccountRequest1 = new GetTrustAccountRequest();
        ResponseEntity<GetTrustAccountRequest> responseEntity =
                new ResponseEntity<>(getTrustAccountRequest1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetTrustAccountRequest>>any()))
                .thenReturn(responseEntity);

        ClientController clientController = new ClientController(restTemplate, objectMapper);
        var resp = clientController.getTrustAccount(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetVisitSchedule() throws JsonProcessingException {
        var req = new GetVisitSchedule();
        var getVisitSchedule2 = new GetVisitSchedule2();
        var getVisitScheduleRequest = new GetVisitScheduleRequest();

        req.setXMLString(getVisitSchedule2);
        getVisitSchedule2.setVisitSchedule(getVisitScheduleRequest);

        var getVisitScheduleRequest1 = new GetVisitScheduleRequest();
        ResponseEntity<GetVisitScheduleRequest> responseEntity =
                new ResponseEntity<>(getVisitScheduleRequest1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetVisitScheduleRequest>>any()))
                .thenReturn(responseEntity);

        ClientController clientController = new ClientController(restTemplate, objectMapper);
        var resp = clientController.getVisitSchedule(req);
        Assertions.assertNotNull(resp);
    }
}
