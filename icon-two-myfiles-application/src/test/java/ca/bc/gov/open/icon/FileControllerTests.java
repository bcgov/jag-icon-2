package ca.bc.gov.open.icon;

import ca.bc.gov.open.icon.audit.MessageAccessed;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.controllers.FileController;
import ca.bc.gov.open.icon.myfiles.*;
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

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FileControllerTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testGetClientClaims() throws JsonProcessingException {
        var req = new GetClientClaims();

        var claims = new Claims();
        ResponseEntity<Claims> responseEntity = new ResponseEntity<>(claims, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<Claims>>any()))
                .thenReturn(responseEntity);

        FileController fileController = new FileController(restTemplate, objectMapper);
        var resp = fileController.getClientClaims(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetCsNumsByDate() throws JsonProcessingException {
        var req = new GetCsNumsByDate();

        var getCsNumsByDateResponse = new GetCsNumsByDateResponse();
        ResponseEntity<GetCsNumsByDateResponse> responseEntity = new ResponseEntity<>(getCsNumsByDateResponse, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<GetCsNumsByDateResponse>>any()))
                .thenReturn(responseEntity);

        FileController fileController = new FileController(restTemplate, objectMapper);
        var resp = fileController.getCsNumsByDate(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetAgencyFile() throws JsonProcessingException {
        var req = new GetAgencyFile();

        var agencyFile = new AgencyFile();
        ResponseEntity<AgencyFile> responseEntity = new ResponseEntity<>(agencyFile, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<AgencyFile>>any()))
                .thenReturn(responseEntity);

        FileController fileController = new FileController(restTemplate, objectMapper);
        var resp = fileController.getAgencyFile(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetClientInfo() throws JsonProcessingException {
        var req = new GetClientInfo();

        var getClientInfoResponse = new GetClientInfoResponse();
        ResponseEntity<GetClientInfoResponse> responseEntity = new ResponseEntity<>(getClientInfoResponse, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<GetClientInfoResponse>>any()))
                .thenReturn(responseEntity);

        FileController fileController = new FileController(restTemplate, objectMapper);
        var resp = fileController.getClientInfo(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testSetMessage() throws JsonProcessingException {
        var req = new SetMessage();

        var setMessageResponse = new SetMessageResponse();
        ResponseEntity<SetMessageResponse> responseEntity = new ResponseEntity<>(setMessageResponse, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<SetMessageResponse>>any()))
                .thenReturn(responseEntity);

        FileController fileController = new FileController(restTemplate, objectMapper);
        var resp = fileController.setMessage(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testSetDisclosure() throws JsonProcessingException {
        var req = new SetDisclosure();

        var aetDisclosureResponse = new SetDisclosureResponse();
        ResponseEntity<SetDisclosureResponse> responseEntity = new ResponseEntity<>(aetDisclosureResponse, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<SetDisclosureResponse>>any()))
                .thenReturn(responseEntity);

        FileController fileController = new FileController(restTemplate, objectMapper);
        var resp = fileController.setDisclosure(req);
        Assertions.assertNotNull(resp);
    }
}
