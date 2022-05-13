package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.audit.MessageAccessed;
import ca.bc.gov.open.icon.audit.MessageAccessedResponse;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.ereporting.*;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.message.*;
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

    @PayloadRoot(namespace = "ICON2.Source.Audit.ws:Record", localPart = "MessageAccessed")
    @ResponsePayload
    public MessageAccessedResponse messageAccessed(@RequestPayload MessageAccessed messageAccessed)
            throws JsonProcessingException {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "message/accessed");
        HttpEntity<MessageAccessed> payload = new HttpEntity<>(messageAccessed, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Status.class);
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

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "getMessage")
    @ResponsePayload
    public GetMessageResponse getMessage(@RequestPayload GetMessage getMessage)
            throws JsonProcessingException {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "message/response");
        HttpEntity<GetMessage> payload = new HttpEntity<>(getMessage, new HttpHeaders());

        try {
            HttpEntity<AppointmentMessage> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            AppointmentMessage.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getMessage")));

            GetMessageResponse getMessageResponse = new GetMessageResponse();
            AppointmentMessageOuter outResp = new AppointmentMessageOuter();
            AppointmentMessageInner inResp = new AppointmentMessageInner();
            inResp.setAppointmentMessage(resp.getBody());
            outResp.setAppointmentMessage(inResp);
            getMessageResponse.setXMLString(outResp);
            return getMessageResponse;

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

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "setMessageDate")
    @ResponsePayload
    public SetMessageDateResponse setMessageDate(@RequestPayload SetMessageDate setMessageDate)
            throws JsonProcessingException {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "message/date");
        HttpEntity<SetMessageDate> payload = new HttpEntity<>(setMessageDate, new HttpHeaders());

        try {
            HttpEntity<AppointmentMessage> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            AppointmentMessage.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "setMessageDate")));

            SetMessageDateResponse getMessageDateResponse = new SetMessageDateResponse();
            AppointmentMessageOuter outResp = new AppointmentMessageOuter();
            AppointmentMessageInner inResp = new AppointmentMessageInner();
            inResp.setAppointmentMessage(resp.getBody());
            outResp.setAppointmentMessage(inResp);
            getMessageDateResponse.setXMLString(outResp);
            return getMessageDateResponse;
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

    @PayloadRoot(
            namespace = "ICON2.Source.Message.ws.provider:Message",
            localPart = "setMessageDetails")
    @ResponsePayload
    public SetMessageDetailsResponse setMessageDetails(
            @RequestPayload SetMessageDetails setMessageDetails) throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "message/details/set");
        HttpEntity<SetMessageDetails> payload =
                new HttpEntity<>(setMessageDetails, new HttpHeaders());

        try {
            HttpEntity<Messages> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Messages.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "setMessageDetails")));
            SetMessageDetailsResponse setMessageDetailsResponse = new SetMessageDetailsResponse();
            MessagesOuter outResp = new MessagesOuter();
            MessagesInner inResp = new MessagesInner();
            inResp.setMessages(resp.getBody());
            outResp.setMessages(inResp);
            setMessageDetailsResponse.setXMLString(outResp);
            return setMessageDetailsResponse;
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

    @PayloadRoot(namespace = "ICON2.Source.Message.ws.provider:Message", localPart = "getMessages")
    @ResponsePayload
    public GetMessagesResponse getMessages(@RequestPayload GetMessages getMessages)
            throws JsonProcessingException {

        HttpEntity<GetMessages> payload = new HttpEntity<>(getMessages, new HttpHeaders());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "message/responses");

        try {
            HttpEntity<Messages> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Messages.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getMessages")));
            GetMessagesResponse getMessagesResponse = new GetMessagesResponse();
            MessagesOuter outResp = new MessagesOuter();
            MessagesInner inResp = new MessagesInner();
            inResp.setMessages(resp.getBody());
            outResp.setMessages(inResp);
            getMessagesResponse.setXMLString(outResp);
            return getMessagesResponse;
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

    @PayloadRoot(
            namespace = "ICON2.Source.Message.ws.provider:Message",
            localPart = "getMessageDetails")
    @ResponsePayload
    public GetMessageDetailsResponse getMessageDetails(
            @RequestPayload GetMessageDetails getMessageDetails) throws JsonProcessingException {

        HttpEntity<GetMessageDetails> payload =
                new HttpEntity<>(getMessageDetails, new HttpHeaders());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "message/details");

        try {
            HttpEntity<Messages> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Messages.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getMessageDetails")));
            GetMessageDetailsResponse getMessageDetailsResponse = new GetMessageDetailsResponse();
            MessagesOuter outResp = new MessagesOuter();
            MessagesInner inResp = new MessagesInner();
            inResp.setMessages(resp.getBody());
            outResp.setMessages(inResp);
            getMessageDetailsResponse.setXMLString(outResp);
            return getMessageDetailsResponse;
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
