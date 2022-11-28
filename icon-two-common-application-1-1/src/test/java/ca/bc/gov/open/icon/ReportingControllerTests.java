package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.Base;
import ca.bc.gov.open.icon.audit.EReportAnswers;
import ca.bc.gov.open.icon.audit.EReportAnswersSubmitted;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.controllers.*;
import ca.bc.gov.open.icon.ereporting.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReportingControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private WebServiceTemplate webServiceTemplate;
    @Mock private RestTemplate restTemplate;
    @Mock private ReportingController reportingController;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        reportingController = Mockito.spy(new ReportingController(restTemplate, objectMapper));
    }

    @Test
    public void testEReportAnswersSubmitted() throws JsonProcessingException {
        var req = new EReportAnswersSubmitted();
        var eReportAnswers = new EReportAnswers();
        var base = new Base();
        base.setSessionID("A");
        base.setCsNumber("A");
        base.setSessionID("A");
        eReportAnswers.setBase(base);
        eReportAnswers.setServiceCD("A");
        eReportAnswers.setFunctionCD("A");
        eReportAnswers.setEReportingEventID("A");

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

        var resp = reportingController.eReportAnswersSubmitted(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetReportingCmpltInstruction() throws JsonProcessingException {
        var req = new GetReportingCmpltInstruction();
        var ReportingCmpltInstruction = new ReportingCmpltInstruction();
        ReportingCmpltInstruction.setCsNum("A");
        ReportingCmpltInstruction.setText("A");
        req.setXMLString("A");

        var userToken = new ca.bc.gov.open.icon.ereporting.UserToken();

        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        req.setUserTokenString("A");

        var reportingCmpltInstruction = new ReportingCmpltInstruction();
        reportingCmpltInstruction.setText("A");
        reportingCmpltInstruction.setCsNum("A");
        ResponseEntity<ReportingCmpltInstruction> responseEntity =
                new ResponseEntity<>(reportingCmpltInstruction, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<ReportingCmpltInstruction>>any()))
                .thenReturn(responseEntity);

        var resp = reportingController.getReportingCmpltInstruction(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetLocationsResponse()
            throws JsonProcessingException, JAXBException, UnsupportedEncodingException {
        var req = new GetLocations();
        var Locations = new Locations();
        List<Location> draftl = new ArrayList<>();
        var Location = new Location();
        Location.setLocationCd("A");
        draftl.add(Location);
        Locations.setLocation(draftl);
        req.setXMLString("A");

        req.setUserTokenString("A");

        var locations1 = new Locations();
        locations1 = Locations;
        ResponseEntity<Locations> responseEntity = new ResponseEntity<>(locations1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Locations>>any()))
                .thenReturn(responseEntity);

        var resp = reportingController.getLocationsResponse(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testSubmitAnswers() throws JsonProcessingException {
        var req = new SubmitAnswers();
        var Report = new Ereport();
        Report.setCsNum("A");
        Report.setDeviceNo("A");
        Report.setEventID("A");
        Report.setState("A");
        List<Question> Questions = new ArrayList<>();
        var Question = new Question();
        Question.setStandardQuestionID("A");
        Question.setStandardText("A");
        Question.setAdditionalText("A");
        List<Answer> Answers = new ArrayList<>();
        var Answer = new Answer();
        Answer.setCode("A");
        Answer.setDescription("A");
        Answers.add(Answer);
        Question.setAnswer(Answers);
        Questions.add(Question);
        Report.setQuestion(Questions);
        req.setXMLString("A");

        var userToken = new ca.bc.gov.open.icon.ereporting.UserToken();

        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        req.setUserTokenString("A");

        var report1 = new Ereport();
        report1 = Report;
        ResponseEntity<Ereport> responseEntity = new ResponseEntity<>(report1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Ereport>>any()))
                .thenReturn(responseEntity);

        var resp = reportingController.submitAnswers(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetAppointment()
            throws JsonProcessingException, JAXBException, UnsupportedEncodingException {
        var req = new GetAppointment();

        var Appointment = new Appointment();

        Appointment.setCsNum("A");
        Appointment.setStartDate("A");
        Appointment.setEndDate("A");
        Appointment.setStartTime("A");
        Appointment.setEndTime("A");

        req.setXMLString("A");

        var userToken = new ca.bc.gov.open.icon.ereporting.UserToken();

        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        req.setUserTokenString("A");

        var appointment1 = new Appointment();
        ResponseEntity<Appointment> responseEntity =
                new ResponseEntity<>(appointment1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Appointment>>any()))
                .thenReturn(responseEntity);

        var resp = reportingController.getAppointment(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetQuestions() throws JsonProcessingException {
        var req = new GetQuestions();
        var report = new Ereport();
        report.setCsNum("A");
        report.setDeviceNo("A");
        report.setEventID("A");
        report.setState("A");
        List<Question> Questions = new ArrayList<>();
        var Question = new Question();
        Question.setStandardQuestionID("A");
        Question.setStandardText("A");
        Question.setAdditionalText("A");
        List<Answer> Answers = new ArrayList<>();
        var Answer = new Answer();
        Answer.setCode("A");
        Answer.setDescription("A");
        Answers.add(Answer);
        Question.setAnswer(Answers);
        Questions.add(Question);
        report.setQuestion(Questions);
        req.setXMLString("A");

        req.setUserTokenString("A");

        ResponseEntity<Ereport> responseEntity = new ResponseEntity<>(report, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Ereport>>any()))
                .thenReturn(responseEntity);

        var resp = reportingController.getQuestions(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetStatus() throws JsonProcessingException {
        var req = new GetStatus();

        var Status = new ca.bc.gov.open.icon.ereporting.Status();

        Status.setEventId("A");
        Status.setCsNum("A");
        Status.setHasNextAppointment("A");
        Status.setIsCurrentAppointment("A");
        Status.setProfileEnabled("A");
        Status.setAnswersSubmitted("A");
        Status.setAnswersCorrect("A");

        req.setXMLString("A");

        var userToken = new ca.bc.gov.open.icon.ereporting.UserToken();

        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        req.setUserTokenString("A");

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

        var resp = reportingController.getStatus(req);
        Assertions.assertNotNull(resp);
    }
}
