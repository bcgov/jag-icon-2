package ca.bc.gov.open.icon;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.auth.GetDeviceInfo;
import ca.bc.gov.open.icon.auth.GetHasFunctionalAbility;
import ca.bc.gov.open.icon.auth.GetPreAuthorizeClient;
import ca.bc.gov.open.icon.auth.GetUserInfo;
import ca.bc.gov.open.icon.controllers.AuthenticationController;
import ca.bc.gov.open.icon.controllers.InformationController;
import ca.bc.gov.open.icon.ereporting.*;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OrdsErrorTests {
    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate webServiceTemplate;

    @Mock private RestTemplate restTemplate;

    @Test
    public void testGetPreAuthorizeClientFail() {
        var authenticationController = new AuthenticationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> authenticationController.getPreAuthorizeClient(new GetPreAuthorizeClient()));
    }

    @Test
    public void testGetHasFunctionalAbilityFail() {
        var authenticationController = new AuthenticationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        authenticationController.getHasFunctionalAbility(
                                new GetHasFunctionalAbility()));
    }

    @Test
    public void testGetUserInfoFail() {
        var informationController = new InformationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class, () -> informationController.getUserInfo(new GetUserInfo()));
    }

    @Test
    public void testGetDeviceInfoFail() {
        var informationController = new InformationController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> informationController.getDeviceInfo(new GetDeviceInfo()));
    }

    @Test
    public void securityTestFail_Then401() throws Exception {
        var response =
                mockMvc.perform(post("/ws").contentType(MediaType.TEXT_XML))
                        .andExpect(status().is4xxClientError())
                        .andReturn();
        Assertions.assertEquals(
                HttpStatus.UNAUTHORIZED.value(), response.getResponse().getStatus());
    }
}
