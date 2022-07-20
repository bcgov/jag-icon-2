package ca.bc.gov.open.icon;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ca.bc.gov.open.icon.biometrics.*;
import ca.bc.gov.open.icon.controllers.*;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
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

    @Mock private WebServiceTemplate soapTemplate;

    @Mock private RestTemplate restTemplate;

    @Mock private ModelMapper modalMapper;

    @Test
    public void testReactivateFail() {
        var activationController = new ActivationController(webServiceTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class, () -> activationController.reactivate(new Reactivate()));
    }

    @Test
    public void testDeactivateFail() {
        var activationController = new ActivationController(webServiceTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class, () -> activationController.deactivate(new Deactivate()));
    }

    @Test
    public void testGetDidFail() {
        var didController = new DidController(webServiceTemplate, objectMapper);

        Assertions.assertThrows(ORDSException.class, () -> didController.getDid(new GetDID()));
    }

    @Test
    public void testStartEnrollmentFail() {
        var enrollmentController =
                new EnrollmentController(
                        webServiceTemplate, objectMapper, restTemplate, modalMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> enrollmentController.startEnrollment(new StartEnrollment()));
    }

    @Test
    public void testFinishEnrollmentFail() {
        var enrollmentController =
                new EnrollmentController(
                        webServiceTemplate, objectMapper, restTemplate, modalMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> enrollmentController.finishEnrollment(new FinishEnrollment()));
    }

    @Test
    public void testMoveFail() {
        var removalController =
                new RemovalController(webServiceTemplate, objectMapper, modalMapper, restTemplate);

        Assertions.assertThrows(ORDSException.class, () -> removalController.move(new Move()));
    }

    @Test
    public void testRemoveFail() {
        var removalController =
                new RemovalController(webServiceTemplate, objectMapper, modalMapper, restTemplate);

        Assertions.assertThrows(ORDSException.class, () -> removalController.remove(new Remove()));
    }

    @Test
    public void testRemoveIdentityFail() {
        var removalController =
                new RemovalController(webServiceTemplate, objectMapper, modalMapper, restTemplate);

        Assertions.assertThrows(
                ORDSException.class, () -> removalController.removeIdentity(new RemoveIdentity()));
    }

    @Test
    public void testRemoveTemplateFail() {
        var removalController =
                new RemovalController(webServiceTemplate, objectMapper, modalMapper, restTemplate);

        Assertions.assertThrows(
                ORDSException.class, () -> removalController.removeTemplate(new RemoveTemplate()));
    }

    @Test
    public void testStartSearchFail() {
        var searchController = new SearchController(soapTemplate, objectMapper, modalMapper);

        Assertions.assertThrows(
                ORDSException.class, () -> searchController.startSearch(new StartSearch()));
    }

    @Test
    public void testFinishSearchFail() {
        var searchController = new SearchController(soapTemplate, objectMapper, modalMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        searchController.finishSearch(
                                new ca.bc.gov.open.icon.biometrics.FinishSearch()));
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
