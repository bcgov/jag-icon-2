package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.configuration.SoapConfig;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.session.GetSessionParameters;
import ca.bc.gov.open.icon.session.GetSessionParametersResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@Slf4j

public class AuthenticationController {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthenticationControllerController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload
    public ReauthenticationFailedResponse reauthenticationFailed (
            @RequestPayload ReauthenticationFailed reauthenticationFailed )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "auth/reauthentication-failed");
        HttpEntity<ReauthenticationFailed> payload = new HttpEntity<>( reauthenticationFailed, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "reauthenticationFailed")));
            ReauthenticationFailedResponse out = new  ReauthenticationFailedResponse();
            out.setStatus(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "reauthenticationFailed",
                                    ex.getMessage(),
                                    reauthenticationFailed)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload
    public ReauthenticationSucceededResponse reauthenticationSucceeded (
            @RequestPayload ReauthenticationSucceeded reauthenticationSucceeded )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "auth/reauthentication-succeeded");
        HttpEntity<ReauthenticationSucceeded> payload = new HttpEntity<>( reauthenticationSucceeded, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "reauthenticationSucceeded")));
            ReauthenticationSucceededResponse out = new  ReauthenticationSucceededResponse();
            out.setStatus(resp.getBody());
            return out;

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "reauthenticationSucceeded",
                                    ex.getMessage(),
                                    reauthenticationSucceeded)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload
    public LogoutExcecutedResponse logoutExecuted (
            @RequestPayload LogoutExcecuted logoutExecuted )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "auth/logout-executed");
        HttpEntity<LogoutExecuted> payload = new HttpEntity<>( logoutExecuted, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "logoutExecuted")));
            LogoutExcecutedResponse out = new  LogoutExcecutedResponse();
            out.setStatus(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "logoutExecuted",
                                    ex.getMessage(),
                                    logoutExecuted)));
            throw new ORDSException();
        }
    }


}
