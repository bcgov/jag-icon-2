package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.auth.*;
import ca.bc.gov.open.icon.controllers.InformationController;
import ca.bc.gov.open.icon.myinfo.*;
import ca.bc.gov.open.icon.myinfo.UserToken;
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
public class InformationControllerTests {
    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testGetOrders() throws JsonProcessingException {
        var req = new GetOrders();
        var OrdersOuter = new OrdersOuter();
        var orders = new Orders();
        List<OrdersInfo> draftl = new ArrayList<>();
        var OrdersInfo = new OrdersInfo();
        OrdersInfo.setAudoId("A");
        OrdersInfo.setDescription("A");
        OrdersInfo.setAdultYouth("A");
        OrdersInfo.setOrderNum("A");
        OrdersInfo.setStartDate(Instant.now());
        OrdersInfo.setEndDate(Instant.now());
        draftl.add(OrdersInfo);
        orders.setOrdersInfo(draftl);
        orders.setCsNum("A");

        req.setXMLString("A");
        OrdersOuter.setOrders(orders);

        var userTokenOuter = new UserTokenOuter();
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

        userTokenOuter.setUserToken(userToken);
        req.setUserTokenString("A");

        var orders1 = new Orders();
        ResponseEntity<Orders> responseEntity = new ResponseEntity<>(orders1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Orders>>any()))
                .thenReturn(responseEntity);

        InformationController informationController =
                new InformationController(restTemplate, objectMapper);
        var resp = informationController.getOrders(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetPrograms() throws JsonProcessingException {
        var req = new GetPrograms();
        var programsOuter = new ProgramsOuter();

        var programs = new Programs();
        req.setXMLString("A");
        programsOuter.setPrograms(programs);
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
        List<ProgramInfo> draftl = new ArrayList<>();
        var ProgramInfo = new ProgramInfo();

        ProgramInfo.setInstCommStatus("A");
        ProgramInfo.setProgramName("A");

        ProgramInfo.setLocation(Location);
        ProgramInfo.setEndDate(Instant.now());
        ProgramInfo.setOutcome("A");
        draftl.add(ProgramInfo);
        programs.setProgramInfo(draftl);

        var userTokenOuter = new UserTokenOuter();
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

        userTokenOuter.setUserToken(userToken);
        req.setUserTokenString("A");

        var programs1 = new Programs();
        programs1 = programs;
        ResponseEntity<Programs> responseEntity = new ResponseEntity<>(programs1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Programs>>any()))
                .thenReturn(responseEntity);

        InformationController informationController =
                new InformationController(restTemplate, objectMapper);
        var resp = informationController.getPrograms(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetLocations() throws JsonProcessingException {
        var req = new GetLocations();
        var locationsOuter = new LocationsOuter();

        var locations = new Locations();
        req.setXMLString("A");
        locationsOuter.setLocations(locations);

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
        List<Location> draftl = new ArrayList<>();
        draftl.add(location);
        locations.setLocation(draftl);
        locations.setCsNum("A");
        var ParoleOfficer = new ParoleOfficer();
        ParoleOfficer.setLastname("A");
        ParoleOfficer.setLastname("A");
        ParoleOfficer.setBusinessHrs("A");
        locations.setParoleOfficer(ParoleOfficer);

        var userTokenOuter = new UserTokenOuter();
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

        userTokenOuter.setUserToken(userToken);
        req.setUserTokenString("A");

        var locations1 = new Locations();
        locations1 = locations;
        ResponseEntity<Locations> responseEntity = new ResponseEntity<>(locations1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Locations>>any()))
                .thenReturn(responseEntity);

        InformationController informationController =
                new InformationController(restTemplate, objectMapper);
        var resp = informationController.getLocations(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetConditions() throws JsonProcessingException {
        var req = new GetConditions();
        var conditionsOuter = new ConditionsOuter();
        var conditions = new Conditions();

        conditions.setCsNum("A");
        conditions.setAudoId("A");
        conditions.setOrderNum("A");
        conditions.setStartDate(Instant.now());
        conditions.setEndDate(Instant.now());
        var row = new Row();
        row.setStart("1");
        row.setEnd("3");
        row.setTotal("3");
        conditions.setRow(row);
        List<ConditionsDetails> draftl = new ArrayList<>();
        var ConditionsDetails = new ConditionsDetails();
        ConditionsDetails.setCondition("A");
        ConditionsDetails.setDetails("A");
        draftl.add(ConditionsDetails);
        conditions.setConditionsDetails(draftl);

        req.setXMLString("A");
        conditionsOuter.setConditions(conditions);

        var userTokenOuter = new UserTokenOuter();
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

        userTokenOuter.setUserToken(userToken);
        req.setUserTokenString("A");

        var conditions1 = new Conditions();
        conditions1 = conditions;
        ResponseEntity<Conditions> responseEntity =
                new ResponseEntity<>(conditions1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Conditions>>any()))
                .thenReturn(responseEntity);

        InformationController informationController =
                new InformationController(restTemplate, objectMapper);
        var resp = informationController.getConditions(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testOrdersConditions() throws JsonProcessingException {
        var req = new GetOrdersConditions();
        var ordersConditionsOuter = new OrdersConditionsOuter();
        var ordersConditionsInner = new OrdersConditionsInner();
        var ordersConditions = new OrdersConditions();

        List<OrdersConditionsDetails> draftl = new ArrayList<>();
        var OrdersConditionsDetails = new OrdersConditionsDetails();
        OrdersConditionsDetails.setAudoId("A");
        OrdersConditionsDetails.setDescription("A");
        OrdersConditionsDetails.setAdultYouth("A");
        OrdersConditionsDetails.setOrderNum("A");
        OrdersConditionsDetails.setStartDate(Instant.now());
        OrdersConditionsDetails.setEndDate(Instant.now());
        List<ConditionDetails> ConditionDetails = new ArrayList<>();
        var ConditionDetail = new ConditionDetails();
        ConditionDetail.setCondition("A");
        ConditionDetail.setDetails("A");
        ConditionDetails.add(ConditionDetail);
        OrdersConditionsDetails.setConditionDetails(ConditionDetails);
        draftl.add(OrdersConditionsDetails);
        ordersConditions.setOrdersConditionsDetails(draftl);
        ordersConditions.setCsNum("A");

        req.setXMLString(ordersConditionsOuter);
        ordersConditionsOuter.setOrdersConditions(ordersConditionsInner);
        ordersConditionsInner.setOrdersConditions(ordersConditions);

        List<OrdersConditionsDetails> detailsList = new ArrayList<>();
        ordersConditions.setOrdersConditionsDetails(detailsList);

        var userTokenOuter = new UserTokenOuter();
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

        userTokenOuter.setUserToken(userToken);
        req.setUserTokenString(userTokenOuter);

        var ordersConditions1 = new OrdersConditions();

        var ordersConditionsDetails = new OrdersConditionsDetails();
        detailsList.add(ordersConditionsDetails);

        ordersConditionsDetails.setAudoId("A");
        ordersConditionsDetails.setDescription("A");
        ordersConditionsDetails.setAdultYouth("A");
        ordersConditionsDetails.setOrderNum("A");
        ordersConditionsDetails.setStartDate(Instant.now());
        ordersConditionsDetails.setEndDate(Instant.now());

        List<ConditionDetails> conditionDetailList = new ArrayList<>();
        var conditionDetails = new ConditionDetails();
        conditionDetails.setCondition("A");
        conditionDetails.setCondition("A");
        conditionDetailList.add(conditionDetails);
        ordersConditionsDetails.setConditionDetails(conditionDetailList);

        ResponseEntity<OrdersConditions> responseEntity =
                new ResponseEntity<>(ordersConditions1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<OrdersConditions>>any()))
                .thenReturn(responseEntity);

        InformationController informationController =
                new InformationController(restTemplate, objectMapper);
        var resp = informationController.getOrdersConditions(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetDates() throws JsonProcessingException {
        var req = new GetDates();
        var DatesOuter = new DatesOuter();

        req.setXMLString("A");

        var datesOuter = new DatesOuter();
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

        req.setXMLString("A");
        datesOuter.setDates(dates);

        var userTokenOuter = new UserTokenOuter();
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

        userTokenOuter.setUserToken(userToken);
        req.setUserTokenString("A");

        var dates1 = new Dates();
        dates1 = dates;
        ResponseEntity<Dates> responseEntity = new ResponseEntity<>(dates1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Dates>>any()))
                .thenReturn(responseEntity);

        InformationController informationController =
                new InformationController(restTemplate, objectMapper);
        var resp = informationController.getDates(req);
        Assertions.assertNotNull(resp);
    }
}
