package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.EReportAnswers;
import ca.bc.gov.open.icon.audit.EReportAnswersSubmitted;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.controllers.ReportingController;
import ca.bc.gov.open.icon.ereporting.*;
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
public class ReportingControllerTests {
    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testEReportAnswersSubmitted() throws JsonProcessingException {
        var req = new EReportAnswersSubmitted();
        var eReportAnswers = new EReportAnswers();
        req.setEReportAnswers(eReportAnswers);

        var status = new Status();
        ResponseEntity<Status> responseEntity = new ResponseEntity<>(status, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Status>>any()))
                .thenReturn(responseEntity);

        var reportingController = new ReportingController(restTemplate, objectMapper);
        var resp = reportingController.eReportAnswersSubmitted(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetReportingCmpltInstruction() throws JsonProcessingException {
        var req = new GetReportingCmpltInstruction();

        var reportingCmpltInstruction = new ReportingCmpltInstruction();
        ResponseEntity<ReportingCmpltInstruction> responseEntity =
                new ResponseEntity<>(reportingCmpltInstruction, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<ReportingCmpltInstruction>>any()))
                .thenReturn(responseEntity);

        var reportingController = new ReportingController(restTemplate, objectMapper);
        var resp = reportingController.getReportingCmpltInstruction(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetLocationsResponse() throws JsonProcessingException {
        var req = new GetLocations();

        var locations = new Locations();
        ResponseEntity<Locations> responseEntity = new ResponseEntity<>(locations, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Locations>>any()))
                .thenReturn(responseEntity);

        var reportingController = new ReportingController(restTemplate, objectMapper);
        var resp = reportingController.getLocationsResponse(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testSubmitAnswers() throws JsonProcessingException {
        var req = new SubmitAnswers();

        var report = new Report();
        ResponseEntity<Report> responseEntity = new ResponseEntity<>(report, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Report>>any()))
                .thenReturn(responseEntity);

        var reportingController = new ReportingController(restTemplate, objectMapper);
        var resp = reportingController.submitAnswers(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetAppointment() throws JsonProcessingException {
        var req = new GetAppointment();

        var appointment = new Appointment();
        ResponseEntity<Appointment> responseEntity =
                new ResponseEntity<>(appointment, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Appointment>>any()))
                .thenReturn(responseEntity);

        var reportingController = new ReportingController(restTemplate, objectMapper);
        var resp = reportingController.getAppointment(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetQuestions() throws JsonProcessingException {
        var req = new GetQuestions();

        var report = new Report();
        ResponseEntity<Report> responseEntity = new ResponseEntity<>(report, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Report>>any()))
                .thenReturn(responseEntity);

        var reportingController = new ReportingController(restTemplate, objectMapper);
        var resp = reportingController.getQuestions(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetStatus() throws JsonProcessingException {
        var req = new GetStatus();

        var status = new ca.bc.gov.open.icon.ereporting.Status();
        ResponseEntity<ca.bc.gov.open.icon.ereporting.Status> responseEntity =
                new ResponseEntity<>(status, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<ca.bc.gov.open.icon.ereporting.Status>>any()))
                .thenReturn(responseEntity);

        var reportingController = new ReportingController(restTemplate, objectMapper);
        var resp = reportingController.getStatus(req);
        Assertions.assertNotNull(resp);
    }
}
