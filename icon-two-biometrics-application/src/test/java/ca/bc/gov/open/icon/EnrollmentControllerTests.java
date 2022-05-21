package ca.bc.gov.open.icon;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.bcs.StartEnrollmentResponse2;
import ca.bc.gov.open.icon.biometrics.StartEnrollment;
import ca.bc.gov.open.icon.controllers.EnrollmentController;
import ca.bc.gov.open.icon.iis.RegisterIndividual;
import ca.bc.gov.open.icon.iis.RegisterIndividualResponse;
import ca.bc.gov.open.icon.iis.RegisterIndividualResponse2;
import ca.bc.gov.open.icon.ips.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EnrollmentControllerTests {

    @Autowired private ObjectMapper objectMapper;

    @Mock private WebServiceTemplate soapTemplate = new WebServiceTemplate();

    @Mock private RestTemplate restTemplate = new RestTemplate();

    @Mock private ModelMapper modalMapper = new ModelMapper();

    @Test
    public void testStartEnrollment() throws JsonProcessingException {
        var req = new StartEnrollment();
        req.setCsNum("A");
        req.setRequestorUserId("A");
        req.setRequestorType("LDB");

        Map<String, String> out = new HashMap<>();
        out.put("andid", "1");
        ResponseEntity<Map<String, String>> responseEntity =
                new ResponseEntity<>(out, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenReturn(responseEntity);

        // Set up to mock soap service response
        var soapResp = new RegisterIndividualResponse();
        var registerIndividualResponse2 = new RegisterIndividualResponse2();
        soapResp.setRegisterIndividualResult(registerIndividualResponse2);
        registerIndividualResponse2.setCode(ca.bc.gov.open.icon.iis.ResponseCode.SUCCESS);
        registerIndividualResponse2.setIdRef("A");

        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(RegisterIndividual.class)))
                .thenReturn(soapResp);

        // Set up to mock soap service response
        var soapResp1 = new LinkResponse();
        var LinkResponse2 = new LinkResponse2();
        soapResp1.setLinkResult(LinkResponse2);
        LinkResponse2.setCode(ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(Link.class)))
                .thenReturn(soapResp1);

        // Set up to mock soap service response
        var soapResp2 = new GetIdRefResponse();
        var GetIdRefResponse2 = new GetIdRefResponse2();
        soapResp2.setGetIdRefResult(GetIdRefResponse2);
        GetIdRefResponse2.setCode(ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(GetIdRef.class)))
                .thenReturn(soapResp2);

        // Set up to mock soap service response
        var soapResp3 = new ca.bc.gov.open.icon.bcs.StartEnrollmentResponse();
        var StartEnrollmentResponse2 = new StartEnrollmentResponse2();
        soapResp3.setStartEnrollmentResult(StartEnrollmentResponse2);
        StartEnrollmentResponse2.setCode(ca.bc.gov.open.icon.bcs.ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(
                        anyString(), Mockito.any(ca.bc.gov.open.icon.bcs.StartEnrollment.class)))
                .thenReturn(soapResp3);

        EnrollmentController enrollmentController =
                new EnrollmentController(soapTemplate, objectMapper, restTemplate, modalMapper);
        var resp = enrollmentController.startEnrollment(req);
        Assertions.assertNotNull(resp);
    }
}
