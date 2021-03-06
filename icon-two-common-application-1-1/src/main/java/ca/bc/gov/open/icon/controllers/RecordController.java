package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.ereporting.*;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
public class RecordController {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RecordController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "recordCompleted")
    @ResponsePayload
    public RecordCompletedResponse recordCompleted(@RequestPayload RecordCompleted recordCompleted)
            throws JsonProcessingException {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "record/completed");

        var inner =
                recordCompleted.getXMLString() != null
                                && recordCompleted.getXMLString().getClientLogNotification() != null
                                && recordCompleted
                                                .getXMLString()
                                                .getClientLogNotification()
                                                .getClientLogNotification()
                                        != null
                        ? recordCompleted
                                .getXMLString()
                                .getClientLogNotification()
                                .getClientLogNotification()
                        : new ClientLogNotification();

        HttpEntity<ClientLogNotification> payload = new HttpEntity<>(inner, new HttpHeaders());

        try {
            HttpEntity<Map<String, String>> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            new ParameterizedTypeReference<>() {});
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "recordCompleted")));
            return new RecordCompletedResponse();

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "recordCompleted",
                                    ex.getMessage(),
                                    recordCompleted)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "recordException")
    @ResponsePayload
    public RecordExceptionResponse recordException(@RequestPayload RecordException recordException)
            throws JsonProcessingException {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "record/exception");

        var inner =
                recordException.getXMLString() != null
                                && recordException.getXMLString() != null
                                && recordException
                                                .getXMLString()
                                                .getClientLogNotification()
                                                .getClientLogNotification()
                                        != null
                        ? recordException
                                .getXMLString()
                                .getClientLogNotification()
                                .getClientLogNotification()
                        : new ClientLogNotification();

        HttpEntity<RecordException> payload = new HttpEntity<>(recordException, new HttpHeaders());

        try {
            HttpEntity<Map<String, String>> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            new ParameterizedTypeReference<>() {});
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "recordException")));
            return new RecordExceptionResponse();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "recordException",
                                    ex.getMessage(),
                                    recordException)));
            throw new ORDSException();
        }
    }
}
