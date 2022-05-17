package icon;

import ca.bc.gov.open.icon.audit.Base;
import ca.bc.gov.open.icon.audit.Reauthentication;
import ca.bc.gov.open.icon.audit.ReauthenticationFailed;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.auth.*;
import ca.bc.gov.open.icon.myinfo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import icon.controllers.AuthenticationController;
import icon.controllers.InformationController;
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

import javax.xml.bind.annotation.XmlElement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class InformationControllerTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();


    @Test
    public void testGetUserInfo() throws JsonProcessingException {
        var req = new GetUserInfo();
        var userInfoOut = new UserInfoOut();
        var userInfoInner = new UserInfoInner();
        var userInfo = new UserInfo();

        req.setXMLString(userInfoOut);
        userInfoOut.setUserInfo(userInfoInner);
        userInfoInner.setUserInfo(userInfo);

        userInfo.setCsNum("A");
        userInfo.setLastName("A");
        userInfo.setFirstName("A");
        userInfo.setLatestPhoto("A");
        userInfo.setLocationCD("A");
        userInfo.setBusinessRole("A");
        var sessionInfo = new SessionInfo();
        sessionInfo.setSessionLimit("A");
        sessionInfo.setIdleTimeout("A");
        userInfo.setSessionInfo(sessionInfo);
        List<ServiceInfo> servies = new ArrayList<>();
        ServiceInfo service = new ServiceInfo();
        servies.add(service);
        service.setName("A");
        service.setDescription("A");
        service.setUrn("A");
        service.setSessionInfo(sessionInfo);
        List<FunctionalAbility> functions = new ArrayList<>();
        FunctionalAbility function = new FunctionalAbility();
        function.setFunctionCd("A");
        function.setFunctionCd("A");
        functions.add(function);
        service.setFunctionalAbility(functions);
        userInfo.setServiceInfo(servies);

        var userInfo1 = new UserInfo();
        List<ServiceInfo> servies1 = new ArrayList<>();
        ServiceInfo service1 = new ServiceInfo();
        servies1.add(service);
        service1.setName("A");
        service1.setDescription("A");
        service1.setUrn("A");
        service1.setSessionInfo(sessionInfo);
        List<FunctionalAbility> functions1 = new ArrayList<>();
        FunctionalAbility function1 = new FunctionalAbility();
        function1.setFunctionCd("A");
        function1.setFunctionCd("A");
        functions1.add(function);
        service1.setFunctionalAbility(functions1);
        userInfo1.setServiceInfo(servies);

        ResponseEntity<UserInfo> responseEntity = new ResponseEntity<>(userInfo1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<UserInfo>>any()))
                .thenReturn(responseEntity);

        InformationController informationController =
                new InformationController(restTemplate, objectMapper);
        var resp = informationController.getUserInfo(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetDeviceInfo() throws JsonProcessingException {
        var req = new GetDeviceInfo();
        var deviceInfoOut = new DeviceInfoOut();
        var deviceInfoInner = new DeviceInfoInner();
        var deviceInfo = new DeviceInfo();

        deviceInfo.setDeviceNo("A");
        deviceInfo.setLocationCd("A");
        deviceInfo.setBusinessRole("A");
        deviceInfo.setIsEnabled("A");
        deviceInfo.setSystemMessage("A");
        deviceInfo.setPollActiveInterval("A");
        deviceInfo.setPollSleepInterval("A");
        deviceInfo.setCertificateName("A");
        List<ServiceCodes> codes = new ArrayList<>();
        ServiceCodes code = new ServiceCodes();
        code.setServiceCd("A");
        codes.add(code);
        deviceInfo.setServiceCodes(codes);
        deviceInfoInner.setDeviceInfo(deviceInfo);
        deviceInfoOut.setDeviceInfo(deviceInfoInner);
        req.setXMLString(deviceInfoOut);

        var deviceInfo1 = new DeviceInfo();
        deviceInfo1.setDeviceNo("A");
        deviceInfo1.setLocationCd("A");
        deviceInfo1.setBusinessRole("A");
        deviceInfo1.setIsEnabled("A");
        deviceInfo1.setSystemMessage("A");
        deviceInfo1.setPollActiveInterval("A");
        deviceInfo1.setPollSleepInterval("A");
        deviceInfo1.setCertificateName("A");
        List<ServiceCodes> codes1 = new ArrayList<>();
        ServiceCodes code1 = new ServiceCodes();
        code1.setServiceCd("A");
        codes1.add(code);
        deviceInfo1.setServiceCodes(codes1);

        ResponseEntity<DeviceInfo> responseEntity = new ResponseEntity<>(deviceInfo1, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                Mockito.any(String.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.<HttpEntity<String>>any(),
                Mockito.<Class<DeviceInfo>>any()))
                .thenReturn(responseEntity);

        InformationController informationController =
                new InformationController(restTemplate, objectMapper);
        var resp = informationController.getDeviceInfo(req);
        Assertions.assertNotNull(resp);
    }


    @Test
    public void testGetOrders() throws JsonProcessingException {
        var req = new GetOrders();
        var OrdersOuter = new OrdersOuter();
        var OrdersInner = new OrdersInner();
        var orders = new Orders();
        List<OrdersInfo> ordersInfos = new ArrayList<>();
        var ordersInfo = new OrdersInfo();

        req.setXMLString(OrdersOuter);
        OrdersOuter.setOrders(OrdersInner);
        OrdersInner.setOrders(orders);
        orders.setOrdersInfo(ordersInfos);
        ordersInfos.add(ordersInfo);

        ordersInfo.setAudoId("A");
        ordersInfo.setDescription("A");
        ordersInfo.setAdultYouth("A");
        ordersInfo.setOrderNum("A");
        ordersInfo.setStartDate(Instant.now());
        ordersInfo.setEndDate(Instant.now());

        var orders1 = new Orders();
        List<OrdersInfo> ordersInfos1 = new ArrayList<>();
        var ordersInfo1 = new OrdersInfo();
        ordersInfo1.setAudoId("A");
        ordersInfo1.setDescription("A");
        ordersInfo1.setAdultYouth("A");
        ordersInfo1.setOrderNum("A");
        ordersInfo1.setStartDate(Instant.now());
        ordersInfo1.setEndDate(Instant.now());

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
        var programsInner = new ProgramsInner();

        var programs = new Programs();
        req.setXMLString(programsOuter);
        programsOuter.setPrograms(programsInner);
        programsInner.setPrograms(programs);

        programs.setCsNum("A");
        programs.setInstCommStatusFilter("A");
        var row = new Row();
        row.setStart("1");
        row.setEnd("3");
        row.setTotal("3");
        programs.setRow(row);
        List<ProgramInfo> programInfos = new ArrayList<>();
        var programInfo = new ProgramInfo();
        programInfos.add(programInfo);
        programs.setProgramInfo(programInfos);


        var programs1 = new Programs();
        programs1.setCsNum("A");
        programs1.setInstCommStatusFilter("A");
        var row1 = new Row();
        row1.setStart("1");
        row1.setEnd("3");
        row1.setTotal("3");
        programs1.setRow(row);
        List<ProgramInfo> programInfos1 = new ArrayList<>();
        var programInfo1 = new ProgramInfo();
        programInfos1.add(programInfo1);
        programs1.setProgramInfo(programInfos);

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
        var locationsInner = new LocationsInner();

        var locations = new Locations();
        req.setXMLString(locationsOuter);
        locationsOuter.setLocations(locationsInner);
        locationsInner.setLocations(locations);

        locations.setCsNum("A");
        var paroleOfficer = new ParoleOfficer();
        paroleOfficer.setFirstname("A");
        paroleOfficer.setLastname("A");
        paroleOfficer.setLastname("A");
        locations.setParoleOfficer(paroleOfficer);
        List<Location> locs = new ArrayList<>();
        var loc = new Location();
        loc.setPhone("A");
        loc.setInstCommType("A");
        loc.setFax("A");
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
        loc.setAddress(addresses);
        loc.setDescription("A");
        loc.setInstCommType("A");
        locations.setLocation(locs);

        var locations1 = new Locations();

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

//    @Test
//    public void testGetConditions() throws JsonProcessingException {
//        var req = new GetConditions();
//        var ConditionsOuter = new ConditionsOuter();
//        var ConditionsInner = new ConditionsInner();
//
//        var conditions = new Conditions();
//        req.setXMLString(locationsOuter);
//        locationsOuter.setLocations(locationsInner);
//        locationsInner.setLocations(locations);
//
//        conditions.setCsNum("A");
//        conditions.setAudoId("A");
//        conditions.setOrderNum("A");
//        conditions.setStartDate("A");
//        conditions.setEndDate("A");
//        var row = new Row();
//        row.setStart("1");
//        row.setEnd("3");
//        row.setTotal("3");
//        conditions.setRow(row);
//        List<ConditionsDetails> details = new ArrayList<>();
//        var conditionsDetails = new ConditionsDetails();
//        details.add(conditionsDetails);
//        details.add();
//        conditions.setConditionsDetails("A");
//
//        List<Location> locs = new ArrayList<>();
//        var loc = new Location();
//        loc.setPhone("A");
//        loc.setInstCommType("A");
//        loc.setFax("A");
//        var address = new Address();
//        List<Address> addresses = new ArrayList<>();
//        addresses.add(address);
//        address.setType("A");
//        address.setLine1("A");
//        address.setLine2("A");
//        address.setLine3("A");
//        address.setCity("A");
//        address.setProvince("A");
//        address.setPostalCode("A");
//        loc.setAddress(addresses);
//        loc.setDescription("A");
//        loc.setInstCommType("A");
//        locations.setLocation(locs);
//
//        var locations1 = new Locations();
//
//        ResponseEntity<Locations> responseEntity = new ResponseEntity<>(locations1, HttpStatus.OK);
//
//        // Set up to mock ords response
//        when(restTemplate.exchange(
//                Mockito.any(String.class),
//                Mockito.eq(HttpMethod.POST),
//                Mockito.<HttpEntity<String>>any(),
//                Mockito.<Class<Locations>>any()))
//                .thenReturn(responseEntity);
//
//        InformationController informationController =
//                new InformationController(restTemplate, objectMapper);
//        var resp = informationController.getLocations(req);
//        Assertions.assertNotNull(resp);
//    }
}
