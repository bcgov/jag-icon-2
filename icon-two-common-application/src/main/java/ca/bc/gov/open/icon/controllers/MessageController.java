package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.audit.MessageAccessed;
import ca.bc.gov.open.icon.audit.MessageAccessedResponse;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.configuration.SoapConfig;
import ca.bc.gov.open.icon.ereporting.GetMessage;
import ca.bc.gov.open.icon.ereporting.GetMessageResponse;
import ca.bc.gov.open.icon.ereporting.SetMessageDate;
import ca.bc.gov.open.icon.ereporting.SetMessageDateResponse;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.message.*;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.myfiles.SetMessageResponse;
import ca.bc.gov.open.icon.myinfo.GetDates;
import ca.bc.gov.open.icon.myinfo.GetDatesResponse;
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

import java.util.Set;

@Endpoint
@Slf4j
public class MessageController {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public MessageController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload

    public MessageAccessedResponse messageAccessed (
            @RequestPayload MessageAccessed messageAccessed
    )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "message/message-accessed");
        HttpEntity<MessageAccessed> payload = new HttpEntity<>( messageAccessed, new HttpHeaders());

        try {
            HttpEntity<Status resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "messageAccessed")));
            MessageAccessedResponse out = new MessageAccessedResponse();
            out.setStatus(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "messageAccessed",
                                    ex.getMessage(),
                                    messageAccessed)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload

    public GetMessageResponse getMessage (
            @RequestPayload GetMessage getMessage
    )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "message/get-message");
        HttpEntity<GetMessage> payload = new HttpEntity<>( getMessage, new HttpHeaders());

        try {
            HttpEntity<GetMessageResponse resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                           GetMessageResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getMessage")));
            GetMessageResponse out = new GetMessageResponse();
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getMessage",
                                    ex.getMessage(),
                                    getMessage)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload

    public SetMessageDateResponse setMessageDate (
            @RequestPayload SetMessageDate setMessageDate
    )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "message/set-message-date");
        HttpEntity<SetMessageDate> payload = new HttpEntity<>( setMessageDate, new HttpHeaders());

        try {
            HttpEntity<SetMessageDateResponse resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            SetMessageDateResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "setMessageDate")));
            SetMessageDateResponse out = new SetMessageDateResponse();
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "setMessageDate",
                                    ex.getMessage(),
                                    setMessageDate)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload

    public SetMessageDetailsResponse setMessageDetails (
            @RequestPayload SetMessageDetails setMessageDetails
    )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "message/set-message-details");
        HttpEntity<SetMessageDetails> payload = new HttpEntity<>( setMessageDetails, new HttpHeaders());

        try {
            HttpEntity<SetMessageDetailsResponse resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            SetMessageDetailsResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "setMessageDetails")));
            SetMessageDetailsResponse out = new SetMessageDetailsResponse();
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "setMessageDetails",
                                    ex.getMessage(),
                                    setMessageDetails)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload

    public GetMessagesResponse getMessages (
            @RequestPayload GetMessages getMessages
    )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "message/get-messages");
        HttpEntity<GetMessages> payload = new HttpEntity<>( getMessages, new HttpHeaders());

        try {
            HttpEntity<GetMessagesResponse resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            GetMessagesResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getMessages")));
            GetMessagesResponse out = new GetMessagesResponse();
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getMessages",
                                    ex.getMessage(),
                                    getMessages)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload

    public GetMessageDetailsResponse getMessageDetails (
            @RequestPayload GetMessageDetails getMessageDetails
    )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "message/get-messages-details");
        HttpEntity<GetMessageDetails> payload = new HttpEntity<>( getMessageDetails, new HttpHeaders());

        try {
            HttpEntity<GetMessageDetailsResponse resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            GetMessageDetailsResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getMessageDetails")));
            GetMessageDetailsResponse out = new GetMessageDetailsResponse();
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getMessageDetails",
                                    ex.getMessage(),
                                    getMessageDetails)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload

    public GetMessageDetailsResponse getMessageDetails (
            @RequestPayload GetMessageDetails getMessageDetails
    )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "message/get-messages-details");
        HttpEntity<GetMessageDetails> payload = new HttpEntity<>( getMessageDetails, new HttpHeaders());

        try {
            HttpEntity<GetMessageDetailsResponse resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            GetMessageDetailsResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getMessageDetails")));
            GetMessageDetailsResponse out = new GetMessageDetailsResponse();
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getMessageDetails",
                                    ex.getMessage(),
                                    getMessageDetails)));
            throw new ORDSException();
        }
    }
}
