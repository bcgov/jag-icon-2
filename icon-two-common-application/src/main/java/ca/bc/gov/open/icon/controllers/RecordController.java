package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.configuration.SoapConfig;
import ca.bc.gov.open.icon.ereporting.RecordCompleted;
import ca.bc.gov.open.icon.ereporting.RecordCompletedResponse;
import ca.bc.gov.open.icon.ereporting.RecordException;
import ca.bc.gov.open.icon.ereporting.RecordExceptionResponse;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.message.GetMessageDetails;
import ca.bc.gov.open.icon.message.GetMessageDetailsResponse;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
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

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload

    public RecordCompletedResponse recordCompleted (
            @RequestPayload RecordCompleted recordCompleted
    )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "record/record-completed");
        HttpEntity<RecordCompleted> payload = new HttpEntity<>( recordCompleted, new HttpHeaders());

        try {
            HttpEntity<RecordCompletedResponse resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            RecordCompletedResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "recordCompleted")));
            RecordCompletedResponse out = new RecordCompletedResponse();
            return out;
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

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload

    public RecordExceptionResponse recordException (
            @RequestPayload RecordException recordException
    )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "record/record-exception");
        HttpEntity<RecordException> payload = new HttpEntity<>( recordException, new HttpHeaders());

        try {
            HttpEntity<RecordExceptionResponse resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            RecordExceptionResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "recordException")));
            RecordExceptionResponse out = new RecordExceptionResponse();
            return out;
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
