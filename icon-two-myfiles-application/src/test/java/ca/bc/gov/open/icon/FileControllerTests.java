package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.controllers.FileController;
import ca.bc.gov.open.icon.myfiles.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
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
public class FileControllerTests {
    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testGetClientClaims() throws JsonProcessingException {
        var req = new GetClientClaims();
        req.setDirectedIdentifier("A");

        var claims = new Claims();
        claims.setBusinessRole("A");
        claims.setCsNumber("A");
        claims.setLocationId("A");
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
        req.setStartDate("A");
        req.setEndDate("A");

        var getCsNumsByDateResponse = new GetCsNumsByDateResponse();
        List<String> draftl = new ArrayList<>();
        draftl.add("A");
        getCsNumsByDateResponse.setCsNums(draftl);
        ResponseEntity<GetCsNumsByDateResponse> responseEntity =
                new ResponseEntity<>(getCsNumsByDateResponse, HttpStatus.OK);

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
        req.setAgencyIdCd("A");
        req.setAgencyFileNo("A");

        var agencyFile = new AgencyFile();
        agencyFile.setAgencyIdCd("A");
        agencyFile.setAgencyFileNo("A");
        List<CourtFile> draftl = new ArrayList<>();

        var CourtFile = new CourtFile();
        var FileNumber = new FileNumber();
        FileNumber.setAgencyCd("A");
        FileNumber.setPrefix("A");
        FileNumber.setFolderNo("A");
        FileNumber.setSequenceNo("A");
        FileNumber.setRefType("A");
        CourtFile.setFileNumber(FileNumber);

        List<Participant> draftl1 = new ArrayList<>();
        var Participant = new Participant();
        Participant.setParticipantId("A");
        Participant.setParticipantSeqNo("A");
        Participant.setCsNum("A");
        Participant.setLastName("A");
        Participant.setFirstName("A");
        Participant.setYouth("A");
        Participant.setHro("A");
        Participant.setInCustody("A");
        Participant.setActiveSupervision("A");
        Participant.setFacilityLocation("A");
        draftl1.add(Participant);
        CourtFile.setParticipants(draftl1);
        draftl.add(CourtFile);
        agencyFile.setCourtFiles(draftl);
        agencyFile.setRccDecisionDate("A");
        agencyFile.setRccDecisionCode("A");

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
        req.setCsNum("A");

        var getClientInfoResponse = new GetClientInfoResponse();
        var Client = new Client();
        Client.setCsNum("A");
        Client.setLastName("A");
        Client.setFirstName("A");
        Client.setMiddleName("A");
        Client.setGender("A");
        Client.setYouth("A");
        Client.setBirthDate("A");
        Client.setInCustody("A");
        Client.setActiveSupervision("A");
        Client.setFacilityLocation("A");
        Client.setActive("A");
        Client.setPrimaryCaseManager("A");
        getClientInfoResponse.setClient(Client);
        getClientInfoResponse.setStatus("A");
        ResponseEntity<GetClientInfoResponse> responseEntity =
                new ResponseEntity<>(getClientInfoResponse, HttpStatus.OK);

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
        req.setMessage("A");
        req.setCsNum("A");

        var setMessageResponse = new SetMessageResponse();
        setMessageResponse.setStatus("A");
        ResponseEntity<SetMessageResponse> responseEntity =
                new ResponseEntity<>(setMessageResponse, HttpStatus.OK);

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
        req.setContentMigrator("A");
        req.setCourtFileNo("A");
        req.setSetNo("A");
        req.setLastName("A");
        req.setFirstName("A");
        req.setMiddleName("A");
        req.setParticipantId("A");
        req.setCsNo("A");
        req.setSharepointError("A");

        var aetDisclosureResponse = new SetDisclosureResponse();
        ResponseEntity<SetDisclosureResponse> responseEntity =
                new ResponseEntity<>(aetDisclosureResponse, HttpStatus.OK);

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
