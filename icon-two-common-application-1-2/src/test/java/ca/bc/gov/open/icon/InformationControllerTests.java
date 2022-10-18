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
}
