package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.auth.*;
import ca.bc.gov.open.icon.controllers.InformationController;
import ca.bc.gov.open.icon.myinfo.*;
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
public class InformationControllerTests {
    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

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

        var userInfo1 = new UserInfo();
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
        deviceInfoInner.setDeviceInfo(deviceInfo);
        deviceInfoOut.setDeviceInfo(deviceInfoInner);
        req.setXMLString(deviceInfoOut);

        var deviceInfo1 = new DeviceInfo();
        ResponseEntity<DeviceInfo> responseEntity =
                new ResponseEntity<>(deviceInfo1, HttpStatus.OK);

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

        req.setXMLString(OrdersOuter);
        OrdersOuter.setOrders(OrdersInner);
        OrdersInner.setOrders(orders);

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
        var programsInner = new ProgramsInner();

        var programs = new Programs();
        req.setXMLString(programsOuter);
        programsOuter.setPrograms(programsInner);
        programsInner.setPrograms(programs);

        var programs1 = new Programs();
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

    @Test
    public void testGetConditions() throws JsonProcessingException {
        var req = new GetConditions();
        var conditionsOuter = new ConditionsOuter();
        var conditionsInner = new ConditionsInner();
        var conditions = new Conditions();

        req.setXMLString(conditionsOuter);
        conditionsOuter.setConditions(conditionsInner);
        conditionsInner.setConditions(conditions);

        var conditions1 = new Conditions();
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

        req.setXMLString(ordersConditionsOuter);
        ordersConditionsOuter.setOrdersConditions(ordersConditionsInner);
        ordersConditionsInner.setOrdersConditions(ordersConditions);
        var ordersConditions1 = new OrdersConditions();
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
        var datesOuter = new DatesOuter();
        var datesInner = new DatesInner();
        var dates = new Dates();

        req.setXMLString(datesOuter);
        datesOuter.setDates(datesInner);
        datesInner.setDates(dates);
        var dates1 = new Dates();
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
