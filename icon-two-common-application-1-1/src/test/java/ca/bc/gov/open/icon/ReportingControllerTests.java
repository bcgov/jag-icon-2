package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.Base;
import ca.bc.gov.open.icon.audit.EReportAnswers;
import ca.bc.gov.open.icon.audit.EReportAnswersSubmitted;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.controllers.ReportingController;
import ca.bc.gov.open.icon.ereporting.*;
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
public class ReportingControllerTests {
    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

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

        var reportingController = new ReportingController(restTemplate, objectMapper);
        var resp = reportingController.eReportAnswersSubmitted(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetReportingCmpltInstruction() throws JsonProcessingException {
        var req = new GetReportingCmpltInstruction();
        var ReportingCmpltInstructionOuter = new ReportingCmpltInstructionOuter();
        var ReportingCmpltInstructionInner = new ReportingCmpltInstructionInner();
        var ReportingCmpltInstruction = new ReportingCmpltInstruction();
        ReportingCmpltInstruction.setCsNum("A");
        ReportingCmpltInstruction.setText("A");
        ReportingCmpltInstructionInner.setReportingCmpltInstruction(ReportingCmpltInstruction);
        ReportingCmpltInstructionOuter.setReportingCmpltInstruction(ReportingCmpltInstructionInner);
        req.setXMLString(ReportingCmpltInstructionOuter);

        var userTokenOuter = new UserTokenOuter();
        var userTokenInner = new UserTokenInner();
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

        userTokenInner.setUserToken(userToken);
        userTokenOuter.setUserToken(userTokenInner);
        req.setUserTokenString(userTokenOuter);

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

        var reportingController = new ReportingController(restTemplate, objectMapper);
        var resp = reportingController.getReportingCmpltInstruction(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetLocationsResponse() throws JsonProcessingException {
        var req = new GetLocations();
        var LocationsOuter = new LocationsOuter();
        var LocationsInner = new LocationsInner();
        var Locations = new Locations();
        List<Location> draftl = new ArrayList<>();
        var Location = new Location();
        Location.setLocationCd("A");
        draftl.add(Location);
        Locations.setLocation(draftl);
        LocationsInner.setLocations(Locations);
        LocationsOuter.setLocations(LocationsInner);
        req.setXMLString(LocationsOuter);

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

        var reportingController = new ReportingController(restTemplate, objectMapper);
        var resp = reportingController.getLocationsResponse(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testSubmitAnswers() throws JsonProcessingException {
        var req = new SubmitAnswers();
        var ReportOuter = new ReportOuter();
        var ReportInner = new ReportInner();
        var Report = new Report();
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
        ReportInner.setEReport(Report);
        ReportOuter.setReport(ReportInner);
        req.setXMLString(ReportOuter);

        var userTokenOuter = new UserTokenOuter();
        var userTokenInner = new UserTokenInner();
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

        userTokenInner.setUserToken(userToken);
        userTokenOuter.setUserToken(userTokenInner);
        req.setUserTokenString(userTokenOuter);

        var report1 = new Report();
        report1 = Report;
        ResponseEntity<Report> responseEntity = new ResponseEntity<>(report1, HttpStatus.OK);

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

        var AppointmentOuter = new AppointmentOuter();
        var AppointmentInner = new AppointmentInner();
        var Appointment = new Appointment();

        Appointment.setCsNum("A");
        Appointment.setStartDate("A");
        Appointment.setEndDate("A");
        Appointment.setStartTime("A");
        Appointment.setEndTime("A");

        AppointmentInner.setAppointment(Appointment);
        AppointmentOuter.setAppointment(AppointmentInner);
        req.setXMLString(AppointmentOuter);

        var userTokenOuter = new UserTokenOuter();
        var userTokenInner = new UserTokenInner();
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

        userTokenInner.setUserToken(userToken);
        userTokenOuter.setUserToken(userTokenInner);
        req.setUserTokenString(userTokenOuter);

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

        var StatusOuter = new StatusOuter();
        var StatusInner = new StatusInner();
        var Status = new ca.bc.gov.open.icon.ereporting.Status();

        Status.setEventId("A");
        Status.setCsNum("A");
        Status.setHasNextAppointment("A");
        Status.setIsCurrentAppointment("A");
        Status.setProfileEnabled("A");
        Status.setAnswersSubmitted("A");
        Status.setAnswersCorrect("A");

        StatusInner.setStatus(Status);
        StatusOuter.setStatus(StatusInner);
        req.setXMLString(StatusOuter);

        var userTokenOuter = new UserTokenOuter();
        var userTokenInner = new UserTokenInner();
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

        userTokenInner.setUserToken(userToken);
        userTokenOuter.setUserToken(userTokenInner);
        req.setUserTokenString(userTokenOuter);

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

    //    @Test
    //    public void testInstantSerializer() throws IOException {
    //        ObjectMapper objectMapper = new ObjectMapper();
    //        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    //        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    //        SimpleModule module = new SimpleModule();
    //        module.addDeserializer(Instant.class, new InstantDeserializer());
    //        module.addSerializer(Instant.class, new InstantSerializer());
    //        objectMapper.registerModule(module);
    //
    //        var time = Instant.now();
    //        String out = objectMapper.writeValueAsString(time);
    //
    //        String expected =
    //                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    //                        .withZone(ZoneId.of("UTC"))
    //                        .withLocale(Locale.US)
    //                        .format(time);
    //
    //        out = out.replace("\"", "");
    //        Assertions.assertEquals(expected, out);
    //    }
}
