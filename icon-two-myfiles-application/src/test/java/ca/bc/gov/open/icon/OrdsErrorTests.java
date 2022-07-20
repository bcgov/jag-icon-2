package ca.bc.gov.open.icon;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ca.bc.gov.open.icon.controllers.FileController;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.myfiles.*;
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
    public void testGetStatusFail() {
        var fileController = new FileController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class, () -> fileController.getClientClaims(new GetClientClaims()));
    }

    @Test
    public void testGetCsNumsByDateFail() {
        var fileController = new FileController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class, () -> fileController.getCsNumsByDate(new GetCsNumsByDate()));
    }

    @Test
    public void testGetAgencyFileFail() {
        var fileController = new FileController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class, () -> fileController.getAgencyFile(new GetAgencyFile()));
    }

    @Test
    public void testSetMessageFail() {
        var fileController = new FileController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class, () -> fileController.setMessage(new SetMessage()));
    }

    @Test
    public void testSetDisclosureFail() {
        var fileController = new FileController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class, () -> fileController.setDisclosure(new SetDisclosure()));
    }

    @Test
    public void testGetClientInfoFail() {
        var fileController = new FileController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class, () -> fileController.getClientInfo(new GetClientInfo()));
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
