package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.controllers.InformationController;
import ca.bc.gov.open.icon.myinfo.*;
import ca.bc.gov.open.icon.myinfo.UserToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InformationControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private RestTemplate restTemplate;
    @Mock private InformationController controller;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = Mockito.spy(new InformationController(restTemplate, objectMapper));
    }

    @Test
    public void testGetOrders() throws JsonProcessingException {
        var req = new GetOrders();
        var orders = new Orders();
        GetOrdersDocument ordersDocument = new GetOrdersDocument();
        List<OrdersInfo> ordersInfoList = new ArrayList<>();
        var OrdersInfo = new OrdersInfo();
        OrdersInfo.setAudoId("A");
        OrdersInfo.setDescription("A");
        OrdersInfo.setAdultYouth("A");
        OrdersInfo.setOrderNum("A");
        OrdersInfo.setStartDate("A");
        OrdersInfo.setEndDate("A");
        ordersInfoList.add(OrdersInfo);
        orders.setOrdersInfo(ordersInfoList);
        orders.setCsNum("A");

        var userToken = new UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCsNumber("A");
        userToken.setSiteMinderSessionId("A");
        userToken.setSiteMinderTransactionId("A");

        req.setXMLString("A");
        req.setUserTokenString("A");
        ordersDocument.setOrders(orders);
        ordersDocument.setUserToken(userToken);

        ResponseEntity<Orders> responseEntity = new ResponseEntity<>(orders, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Orders>>any()))
                .thenReturn(responseEntity);

        var resp = controller.getOrders(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetPrograms() throws JsonProcessingException {
        var req = new GetPrograms();
        var programs = new Programs();
        req.setXMLString("A");
        programs.setCsNum("A");
        programs.setInstCommStatusFilter("A");
        var row = new Row();
        row.setStart("1");
        row.setEnd("3");
        row.setTotal("3");
        programs.setRow(row);

        var Location = new Location();
        Location.setCode("A");
        Location.setDescription("A");
        Location.setInstCommType("A");
        Location.setPhone("A");
        Location.setFax("A");
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
        Location.setAddress(addresses);
        List<ProgramInfo> programInfoList = new ArrayList<>();
        var ProgramInfo = new ProgramInfo();
        ProgramInfo.setInstCommStatus("A");
        ProgramInfo.setProgramName("A");
        ProgramInfo.setLocation(Location);
        ProgramInfo.setEndDate("A");
        ProgramInfo.setOutcome("A");
        programInfoList.add(ProgramInfo);
        programs.setProgramInfo(programInfoList);

        var userToken = new UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCsNumber("A");
        userToken.setSiteMinderSessionId("A");
        userToken.setSiteMinderTransactionId("A");

        req.setXMLString("A");
        req.setUserTokenString("A");
        GetProgramsDocument getProgramsDocument = new GetProgramsDocument();
        getProgramsDocument.setPrograms(programs);
        getProgramsDocument.setUserToken(userToken);

        ResponseEntity<Programs> responseEntity = new ResponseEntity<>(programs, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Programs>>any()))
                .thenReturn(responseEntity);

        var resp = controller.getPrograms(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetLocations() throws JsonProcessingException {
        var req = new GetLocations();
        var locations = new Locations();
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
        var location = new Location();
        location.setAddress(addresses);
        location.setCode("A");
        location.setDescription("A");
        location.setFax("A");
        location.setInstCommType("A");
        location.setPhone("A");
        List<Location> locationList = new ArrayList<>();
        locationList.add(location);
        locations.setLocation(locationList);
        locations.setCsNum("A");
        var ParoleOfficer = new ParoleOfficer();
        ParoleOfficer.setLastname("A");
        ParoleOfficer.setLastname("A");
        ParoleOfficer.setBusinessHrs("A");
        locations.setParoleOfficer(ParoleOfficer);

        var userToken = new UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCsNumber("A");
        userToken.setSiteMinderSessionId("A");
        userToken.setSiteMinderTransactionId("A");

        GetLocationsDocument getLocationsDocument = new GetLocationsDocument();
        getLocationsDocument.setLocations(locations);
        getLocationsDocument.setUserToken(userToken);
        req.setXMLString("A");
        req.setUserTokenString("A");

        ResponseEntity<Locations> responseEntity = new ResponseEntity<>(locations, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Locations>>any()))
                .thenReturn(responseEntity);

        var resp = controller.getLocations(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetConditions() throws JsonProcessingException {
        var req = new GetConditions();
        var conditions = new Conditions();
        conditions.setCsNum("A");
        conditions.setAudoId("A");
        conditions.setOrderNum("A");
        conditions.setStartDate("A");
        conditions.setEndDate("A");
        var row = new Row();
        row.setStart("1");
        row.setEnd("3");
        row.setTotal("3");
        conditions.setRow(row);
        List<ConditionsDetails> conditionsDetailsList = new ArrayList<>();
        var ConditionsDetails = new ConditionsDetails();
        ConditionsDetails.setCondition("A");
        ConditionsDetails.setDetails("A");
        conditionsDetailsList.add(ConditionsDetails);
        conditions.setConditionsDetails(conditionsDetailsList);

        var userToken = new UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCsNumber("A");
        userToken.setSiteMinderSessionId("A");
        userToken.setSiteMinderTransactionId("A");

        GetConditionsDocument getConditionsDocument = new GetConditionsDocument();
        getConditionsDocument.setConditions(conditions);
        getConditionsDocument.setUserToken(userToken);
        req.setXMLString("A");
        req.setUserTokenString("A");

        ResponseEntity<Conditions> responseEntity = new ResponseEntity<>(conditions, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Conditions>>any()))
                .thenReturn(responseEntity);

        var resp = controller.getConditions(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testOrdersConditions() throws JsonProcessingException {
        var req = new GetOrdersConditions();
        var ordersConditions = new OrdersConditions();
        List<OrdersConditionsDetails> ordersConditionsDetailsList = new ArrayList<>();
        var OrdersConditionsDetails = new OrdersConditionsDetails();
        OrdersConditionsDetails.setAudoId("A");
        OrdersConditionsDetails.setDescription("A");
        OrdersConditionsDetails.setAdultYouth("A");
        OrdersConditionsDetails.setOrderNum("A");
        OrdersConditionsDetails.setStartDate("A");
        OrdersConditionsDetails.setEndDate("A");
        List<ConditionDetails> ConditionDetails = new ArrayList<>();
        var ConditionDetail = new ConditionDetails();
        ConditionDetail.setCondition("A");
        ConditionDetail.setDetails("A");
        ConditionDetails.add(ConditionDetail);
        OrdersConditionsDetails.setConditionDetails(ConditionDetails);
        ordersConditionsDetailsList.add(OrdersConditionsDetails);
        ordersConditions.setOrdersConditionsDetails(ordersConditionsDetailsList);
        ordersConditions.setCsNum("A");

        List<OrdersConditionsDetails> detailsList = new ArrayList<>();
        ordersConditions.setOrdersConditionsDetails(detailsList);

        var userToken = new UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCsNumber("A");
        userToken.setSiteMinderSessionId("A");
        userToken.setSiteMinderTransactionId("A");

        var ordersConditionsDetails = new OrdersConditionsDetails();
        detailsList.add(ordersConditionsDetails);

        ordersConditionsDetails.setAudoId("A");
        ordersConditionsDetails.setDescription("A");
        ordersConditionsDetails.setAdultYouth("A");
        ordersConditionsDetails.setOrderNum("A");
        ordersConditionsDetails.setStartDate("A");
        ordersConditionsDetails.setEndDate("A");

        List<ConditionDetails> conditionDetailList = new ArrayList<>();
        var conditionDetails = new ConditionDetails();
        conditionDetails.setCondition("A");
        conditionDetails.setCondition("A");
        conditionDetailList.add(conditionDetails);
        ordersConditionsDetails.setConditionDetails(conditionDetailList);

        GetOrdersConditionsDocument getOrdersConditionsDocument = new GetOrdersConditionsDocument();
        getOrdersConditionsDocument.setOrdersConditions(ordersConditions);
        getOrdersConditionsDocument.setUserToken(userToken);
        req.setXMLString("A");
        req.setUserTokenString("A");

        ResponseEntity<OrdersConditions> responseEntity =
                new ResponseEntity<>(ordersConditions, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<OrdersConditions>>any()))
                .thenReturn(responseEntity);

        var resp = controller.getOrdersConditions(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetDates() throws JsonProcessingException {
        var req = new GetDates();
        var dates = new Dates();
        dates.setCsNum("A");
        dates.setCustodyEndDate("A");
        dates.setCommunitySupervisionEndDate("A");
        dates.setProbableDischargeReturnDate("A");
        var row = new Row();
        row.setStart("1");
        row.setEnd("3");
        row.setTotal("3");
        dates.setRow(row);

        List<FutureCourtDates> futureCourtDates = new ArrayList<>();
        var futureCourtDate = new FutureCourtDates();
        futureCourtDates.add(futureCourtDate);
        futureCourtDate.setCourtAppearanceDate("A");
        futureCourtDate.setAppearanceReason("A");
        futureCourtDate.setVideoCourt("A");
        futureCourtDate.setCourtFileNumber("A");

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
        futureCourtDate.setLocation(location);
        dates.setFutureCourtDates(futureCourtDates);

        var userToken = new UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCsNumber("A");
        userToken.setSiteMinderSessionId("A");
        userToken.setSiteMinderTransactionId("A");

        GetDatesDocument getDatesDocument = new GetDatesDocument();
        getDatesDocument.setDates(dates);
        getDatesDocument.setUserToken(userToken);
        req.setXMLString("A");
        req.setUserTokenString("A");

        ResponseEntity<Dates> responseEntity = new ResponseEntity<>(dates, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Dates>>any()))
                .thenReturn(responseEntity);

        var resp = controller.getDates(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetClientHistory() throws JsonProcessingException {
        var req = new GetClientHistory();
        var clientHistory = new ClientHistory();
        clientHistory.setCsNum("A");
        clientHistory.setInstCommStatusFilter("A");
        Row row = new Row();
        row.setStart("A");
        row.setEnd("A");
        row.setTotal("A");
        clientHistory.setRow(row);
        List<ClientHistoryDetails> clientHistoryDetailsList = new ArrayList<>();
        ClientHistoryDetails clientHistoryDetails = new ClientHistoryDetails();
        clientHistoryDetails.setDate("A");
        Court court = new Court();
        court.setCourtCode("A");
        court.setCourtDescription("A");
        clientHistoryDetails.setCourt(court);
        Disposition disposition = new Disposition();
        disposition.setDispositionCode("A");
        disposition.setDispositionDescription("A");
        clientHistoryDetails.setDisposition(disposition);
        clientHistoryDetails.setFileNumber("A");
        Location location = new Location();
        location.setCode("A");
        clientHistoryDetails.setLocation(location);
        clientHistoryDetails.setDconsecutive("A");
        clientHistoryDetails.setInstCommStatus("A");
        clientHistoryDetails.setOffence("A");
        clientHistoryDetails.setDconsecutive("A");
        SentenceLength sentenceLength = new SentenceLength();
        sentenceLength.setSentenceLengthCode("A");
        sentenceLength.setSentenceLengthDescription("A");
        clientHistoryDetails.setSentenceLength(sentenceLength);
        Movement movement = new Movement();
        movement.setMovementCode("A");
        movement.setMovementDescription("A");
        clientHistoryDetails.setMovement(movement);
        MovementReason movementReason = new MovementReason();
        movementReason.setMovementReasonCode("A");
        movementReason.setMovementReasonDescription("A");
        clientHistoryDetails.setMovementReason(movementReason);
        clientHistoryDetailsList.add(clientHistoryDetails);
        clientHistory.setClientHistoryDetails(clientHistoryDetailsList);

        var userToken = new UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCsNumber("A");
        userToken.setSiteMinderSessionId("A");
        userToken.setSiteMinderTransactionId("A");

        GetClientHistoryDocument getClientHistoryDocument = new GetClientHistoryDocument();
        getClientHistoryDocument.setClientHistory(clientHistory);
        getClientHistoryDocument.setUserToken(userToken);
        req.setXMLString("A");
        req.setUserTokenString("A");

        ResponseEntity<ClientHistory> responseEntity =
                new ResponseEntity<>(clientHistory, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<ClientHistory>>any()))
                .thenReturn(responseEntity);

        var resp = controller.getClientHistory(req);
        Assertions.assertNotNull(resp);
    }
}
