package ca.bc.gov.open.jagiconconsumer.services;

import ca.bc.gov.open.icon.hsrservice.SubmitHealthServiceRequest;
import ca.bc.gov.open.icon.hsrservice.SubmitHealthServiceRequestResponse;
import ca.bc.gov.open.icon.models.HealthServicePub;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.io.IOException;
import java.net.SocketException;

@Service
@Slf4j
public class HSRService {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    @Value("${icon.hsr-service-url}")
    private String hsrServiceUrl = "https://127.0.0.1/";

    private static int retries;
    private static int MAX_RETRIES = 5;
    private boolean appErr = false;
    private boolean hsrFail = false;

    private final WebServiceTemplate webServiceTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public HSRService(RestTemplate restTemplate, ObjectMapper objectMapper, WebServiceTemplate webServiceTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.webServiceTemplate = webServiceTemplate;
    }

    public void processHSR(HealthServicePub hsr) throws SocketException, InterruptedException {
        retries = 0;
        // Submit HSR (Invoke SOAP Service)
        SubmitHealthServiceRequest submitHealthServiceRequest = new SubmitHealthServiceRequest();
        submitHealthServiceRequest.setCsNumber(hsr.getCsNum());
        submitHealthServiceRequest.setSubmissionDate(hsr.getRequestDate().toString());
        submitHealthServiceRequest.setCentre(hsr.getLocation());
        submitHealthServiceRequest.setDetails(hsr.getHealthRequest());
        while (retries < MAX_RETRIES) {
            try {
                SubmitHealthServiceRequestResponse submitHealthServiceRequestResponse =
                        (SubmitHealthServiceRequestResponse)
                                webServiceTemplate.marshalSendAndReceive(
                                        hsrServiceUrl, submitHealthServiceRequest);
            } catch (WebServiceIOException ex) {
                // Connection Error
                appErr = false;
                hsrFail = true;
                break;
            } catch (Exception ex) {
                if (++retries == 5) {
                    appErr = true;
                    hsrFail = true;
                    break;
                }
                Thread.sleep(5000);
            }
        }

        // Record HSR

    }

}
