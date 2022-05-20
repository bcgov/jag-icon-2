package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.controllers.AuditController;
import ca.bc.gov.open.icon.myinfo.*;
import ca.bc.gov.open.icon.packageinfo.GetPackageInfo;
import ca.bc.gov.open.icon.packageinfo.GetPackageInfoResponse;
import ca.bc.gov.open.icon.session.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
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
public class AuditControllerTests {
    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testeServiceAccessed() throws JsonProcessingException {
        var req = new EServiceAccessed();
        var eService = new EService();
        Base base = new Base();
        base.setCsNumber("A");
        base.setDeviceNO("A");
        base.setSessionID("A");
        eService.setBase(base);
        eService.setEServiceCD("A");

        var status = new Status();
        status.setSuccess(true);
        ResponseEntity<Status> responseEntity = new ResponseEntity<>(status, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<Status>>any()))
                .thenReturn(responseEntity);

        AuditController auditController = new AuditController(restTemplate, objectMapper);
        var resp = auditController.eServiceAccessed(req);

        Assertions.assertNotNull(resp);
    }

    @Test
    public void testeHomeScreenAccessed() throws JsonProcessingException {
        var req = new HomeScreenAccessed();
        var homeScreen = new HomeScreen();
        var base = new Base();
        base.setCsNumber("A");
        base.setDeviceNO("A");
        base.setSessionID("A");
        homeScreen.setBase(base);
        req.setHomeScreen(homeScreen);

        Status status = new Status();
        status.setSuccess(true);
        ResponseEntity<Status> responseEntity = new ResponseEntity<>(status, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<Status>>any()))
                .thenReturn(responseEntity);

        var auditController = new AuditController(restTemplate, objectMapper);
        var resp = auditController.homeScreenAccessed(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testSessionTimeoutExecuted() throws JsonProcessingException {
        var req = new SessionTimeoutExecuted();
        var sessionTimeout = new SessionTimeout();
        var base = new Base();
        base.setCsNumber("A");
        base.setDeviceNO("A");
        base.setSessionID("A");
        sessionTimeout.setBase(base);
        sessionTimeout.setEServiceCD("A");
        sessionTimeout.setEServiceFuntionCD("A");
        req.setSessionTimeout(sessionTimeout);

        var status = new Status();
        status.setSuccess(true);
        ResponseEntity<Status> responseEntity = new ResponseEntity<>(status, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<Status>>any()))
                .thenReturn(responseEntity);

        AuditController auditController = new AuditController(restTemplate, objectMapper);
        var resp = auditController.sessionTimeoutExecuted(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testEServiceFunctionAccessed() throws JsonProcessingException {
        var req = new EServiceFunctionAccessed();
        var eServiceFunction = new EServiceFunction();
        var base = new Base();
        base.setCsNumber("A");
        base.setDeviceNO("A");
        base.setSessionID("A");
        eServiceFunction.setBase(base);
        eServiceFunction.setEServiceCD("A");
        eServiceFunction.setEServiceFunctionCD("A");
        req.setEServiceFunction(eServiceFunction);

        var status = new Status();
        status.setSuccess(true);
        ResponseEntity<Status> responseEntity = new ResponseEntity<>(status, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<Status>>any()))
                .thenReturn(responseEntity);

        AuditController auditController = new AuditController(restTemplate, objectMapper);
        var resp = auditController.eServiceFunctionAccessed(req);
        Assertions.assertNotNull(resp);
    }

    private ClientHistoryOuter Create_ClientHistoryOuter() {
        var clientHistoryOuter = new ClientHistoryOuter();
        var clientHistoryInner = new ClientHistoryInner();
        var clientHistory = Create_ClientHistory();

        clientHistoryInner.setClientHistory(clientHistory);
        clientHistoryOuter.setClientHistory(clientHistoryInner);

        return clientHistoryOuter;
    }

    private ClientHistory Create_ClientHistory() {
        var clientHistory = new ClientHistory();

        clientHistory.setCsNum("A");
        clientHistory.setInstCommStatusFilter("A");

        var row = new Row();
        row.setEnd("A");
        row.setStart("A");
        row.setTotal("A");
        clientHistory.setRow(row);

        List<ClientHistoryDetails> details = new ArrayList<>();
        details.add(Create_ClientHistoryDetails());
        clientHistory.setClientHistoryDetails(details);

        return clientHistory;
    }

    private ClientHistoryDetails Create_ClientHistoryDetails() {
        var detail = new ClientHistoryDetails();
        detail.setDate(Instant.now());
        detail.setInstCommStatus("A");
        detail.setDate(Instant.now());
        detail.setDconsecutive("A");
        detail.setFileNumber("A");

        var movement = new Movement();
        movement.setMovementCode("A");
        movement.setMovementDescription("A");
        detail.setMovement(movement);

        var movementReason = new MovementReason();
        movementReason.setMovementReasonCode("A");
        movementReason.setMovementReasonDescription("A");
        detail.setMovementReason(movementReason);

        var location = new Location();
        var address = new Address();
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        address.setType("A");
        address.setLine1("A");
        address.setLine2("A");
        address.setLine3("A");
        address.setCity("A");
        address.setProvince("A");
        address.setPostalCode("A");
        location.setAddress(addresses);
        location.setCode("A");
        location.setDescription("A");
        location.setFax("A");
        location.setInstCommType("A");
        location.setPhone("A");
        detail.setLocation(location);

        var court = new Court();
        court.setCourtCode("A");
        court.setCourtDescription("A");
        detail.setCourt(court);

        detail.setOffence("A");

        var disposition = new Disposition();
        disposition.setDispositionCode("A");
        disposition.setDispositionDescription("A");
        detail.setDisposition(disposition);

        var sentenceLength = new SentenceLength();
        sentenceLength.setSentenceLengthCode("A");
        sentenceLength.setSentenceLengthDescription("A");
        detail.setSentenceLength(sentenceLength);

        return detail;
    }

    @Test
    public void testGetClientHistory() throws JsonProcessingException {

        var req = new GetClientHistory();
        req.setXMLString(Create_ClientHistoryOuter());

        var userTokenOuter = new UserTokenOuter();
        var userTokenInner = new UserTokenInner();
        var userToken = new UserToken();

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

        var clientHistory = new ClientHistory();

        List<ClientHistoryDetails> details = new ArrayList<>();
        details.add(Create_ClientHistoryDetails());
        clientHistory.setClientHistoryDetails(details);

        ResponseEntity<ClientHistory> responseEntity =
                new ResponseEntity<>(clientHistory, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<ClientHistory>>any()))
                .thenReturn(responseEntity);

        AuditController auditController = new AuditController(restTemplate, objectMapper);
        var resp = auditController.getClientHistory(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetPackageInfo() throws JsonProcessingException {
        var req = new GetPackageInfo();

        var getPackageInfoResponse = new GetPackageInfoResponse();
        getPackageInfoResponse.setXMLString("A");

        ResponseEntity<GetPackageInfoResponse> responseEntity =
                new ResponseEntity<>(getPackageInfoResponse, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<GetPackageInfoResponse>>any()))
                .thenReturn(responseEntity);

        AuditController auditController = new AuditController(restTemplate, objectMapper);
        var resp = auditController.getPackageInfo(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetSessionParameters() throws JsonProcessingException {
        var req = new GetSessionParameters();
        var sessionParameterOutest = new SessionParameterOutest();
        var sessionParameterOuter = new SessionParameterOuter();
        var sessionParameterInner = new SessionParameterInner();
        List<SessionParameter> sessions = new ArrayList<>();
        var sessionParameter = new SessionParameter();
        sessionParameter.setParameterCd("A");
        sessionParameter.setValue("A");
        sessions.add(sessionParameter);
        sessionParameterInner.setSessionParameter(sessions);
        sessionParameterOuter.setSessionParameters(sessionParameterInner);
        sessionParameterOutest.setSessionParameters(sessionParameterOuter);
        req.setXMLString(sessionParameterOutest);

        var out = new SessionParameterInner();
        List<SessionParameter> sessions1 = new ArrayList<>();
        var sessionParameter1 = new SessionParameter();
        sessionParameter1.setParameterCd("A");
        sessionParameter1.setValue("A");
        sessions1.add(sessionParameter1);
        out.setSessionParameter(sessions1);

        ResponseEntity<SessionParameterInner> responseEntity =
                new ResponseEntity<>(out, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<SessionParameterInner>>any()))
                .thenReturn(responseEntity);

        AuditController auditController = new AuditController(restTemplate, objectMapper);
        var resp = auditController.getSessionParameters(req);
        Assertions.assertNotNull(resp);
    }

//    public static class MessageControllerTests {}
}
