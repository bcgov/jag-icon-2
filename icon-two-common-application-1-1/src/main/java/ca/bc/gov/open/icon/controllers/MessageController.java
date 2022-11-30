package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.audit.MessageAccessed;
import ca.bc.gov.open.icon.audit.MessageAccessedResponse;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.ereporting.*;
import ca.bc.gov.open.icon.message.*;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.utils.XMLUtilities;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            throw handleError(ex, new ca.bc.gov.open.icon.audit.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "getMessage")
    @ResponsePayload
    public GetMessageResponse getMessage(@RequestPayload GetMessage getMessage)
            throws JsonProcessingException {

        GetMessageDocument getMessageDocument = new GetMessageDocument();
        getMessageDocument.setAppointmentMessage(
                XMLUtilities.deserializeXmlStr(
                        getMessage.getXMLString(), new AppointmentMessage()));
        getMessageDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        getMessage.getUserTokenString(),
                        new ca.bc.gov.open.icon.ereporting.UserToken()));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "message/response");
        HttpEntity<GetMessageDocument> payload =
                new HttpEntity<>(getMessageDocument, new HttpHeaders());

        try {
            HttpEntity<AppointmentMessage> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            AppointmentMessage.class);

            GetMessageResponse getMessageResponse = new GetMessageResponse();
            getMessageResponse.setXMLString(XMLUtilities.serializeXmlStr(resp.getBody()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getMessage")));

            return getMessageResponse;

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getMessage",
                                    ex.getMessage(),
                                    getMessage)));
            throw handleError(ex, new ca.bc.gov.open.icon.ereporting.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "setMessageDate")
    @ResponsePayload
    public SetMessageDateResponse setMessageDate(@RequestPayload SetMessageDate setMessageDate)
            throws JsonProcessingException {

        SetMessageDateDocument setMessageDateDocument = new SetMessageDateDocument();
        setMessageDateDocument.setAppointmentMessage(
                XMLUtilities.deserializeXmlStr(
                        setMessageDate.getXMLString(), new AppointmentMessage()));
        setMessageDateDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        setMessageDate.getUserTokenString(),
                        new ca.bc.gov.open.icon.ereporting.UserToken()));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "message/date");
        HttpEntity<SetMessageDateDocument> payload =
                new HttpEntity<>(setMessageDateDocument, new HttpHeaders());

        try {

            restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    payload,
                    new ParameterizedTypeReference<>() {});

            SetMessageDateResponse setMessageDateResponse = new SetMessageDateResponse();
            setMessageDateResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(setMessageDateDocument.getAppointmentMessage()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "setMessageDate")));

            return setMessageDateResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "setMessageDate",
                                    ex.getMessage(),
                                    setMessageDate)));

            throw handleError(ex, new ca.bc.gov.open.icon.ereporting.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.Message.ws.provider:Message",
            localPart = "setMessageDetails")
    @ResponsePayload
    public SetMessageDetailsResponse setMessageDetails(
            @RequestPayload SetMessageDetails setMessageDetails) throws JsonProcessingException {

        var setMessageDetailsDocument =
                XMLUtilities.convertReq(
                        setMessageDetails, new SetMessageDetailsDocument(), "setMessageDetails");

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "message/details/set");
        HttpEntity<SetMessageDetailsDocument> payload =
                new HttpEntity<>(setMessageDetailsDocument, new HttpHeaders());

        try {
            HttpEntity<Messages> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Messages.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "setMessageDetails")));
            SetMessageDetailsResponseDocument setMessageDetailsResponseDocument =
                    new SetMessageDetailsResponseDocument();
            MessagesOuter outResp = new MessagesOuter();
            outResp.setMessages(resp.getBody());
            setMessageDetailsResponseDocument.setXMLString(outResp);

            var setMessageDetailsResponse =
                    XMLUtilities.convertResp(
                            setMessageDetailsResponseDocument,
                            new SetMessageDetailsResponse(),
                            "setMessageDetailsResponse");

            return setMessageDetailsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "setMessageDetails",
                                    ex.getMessage(),
                                    setMessageDetails)));
            throw handleError(ex, new ca.bc.gov.open.icon.message.Error());
        }
    }

    @PayloadRoot(namespace = "ICON2.Source.Message.ws.provider:Message", localPart = "getMessages")
    @ResponsePayload
    public GetMessagesResponse getMessages(@RequestPayload GetMessages getMessages)
            throws JsonProcessingException {

        var getMessagesDocument =
                XMLUtilities.convertReq(getMessages, new GetMessagesDocument(), "getMessages");

        HttpEntity<GetMessagesDocument> payload =
                new HttpEntity<>(getMessagesDocument, new HttpHeaders());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "message/responses");

        try {
            HttpEntity<Messages> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Messages.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getMessages")));

            GetMessagesResponseDocument getMessagesResponseDocument =
                    new GetMessagesResponseDocument();
            MessagesOuter outResp = new MessagesOuter();
            outResp.setMessages(resp.getBody());
            getMessagesResponseDocument.setXMLString(outResp);

            var getMessagesResponse =
                    XMLUtilities.convertResp(
                            getMessagesResponseDocument,
                            new GetMessagesResponse(),
                            "getMessagesResponse");

            return getMessagesResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getMessages",
                                    ex.getMessage(),
                                    getMessages)));
            throw handleError(ex, new ca.bc.gov.open.icon.message.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.Message.ws.provider:Message",
            localPart = "getMessageDetails")
    @ResponsePayload
    public GetMessageDetailsResponse getMessageDetails(
            @RequestPayload GetMessageDetails getMessageDetails) throws JsonProcessingException {

        var getMessageDetailsDocument =
                XMLUtilities.convertReq(
                        getMessageDetails, new GetMessageDetailsDocument(), "getMessageDetails");

        HttpEntity<GetMessageDetailsDocument> payload =
                new HttpEntity<>(getMessageDetailsDocument, new HttpHeaders());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "message/details");

        try {
            HttpEntity<Messages> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Messages.class);

            GetMessageDetailsResponseDocument getMessageDetailsResponseDocument =
                    new GetMessageDetailsResponseDocument();
            MessagesOuter outResp = new MessagesOuter();
            outResp.setMessages(resp.getBody());
            getMessageDetailsResponseDocument.setXMLString(outResp);

            var getMessageDetailsResponse =
                    XMLUtilities.convertResp(
                            getMessageDetailsResponseDocument,
                            new GetMessageDetailsResponse(),
                            "getMessageDetailsResponse");

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getMessageDetails")));

            return getMessageDetailsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getMessageDetails",
                                    ex.getMessage(),
                                    getMessageDetails)));
            throw handleError(ex, new ca.bc.gov.open.icon.message.Error());
        }
    }
}
