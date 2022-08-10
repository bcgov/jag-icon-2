package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.tombstone.*;
import ca.bc.gov.open.icon.trustaccount.*;
import ca.bc.gov.open.icon.utils.XMLUtilities;
import ca.bc.gov.open.icon.visitschedule.*;
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
public class ClientController {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ClientController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(
            namespace = "ICON2.Source.TombStoneInfo.ws.provider:TombStoneInfo",
            localPart = "getTombStoneInfo")
    @ResponsePayload
    public GetTombStoneInfoResponse getTombStoneInfo(
            @RequestPayload GetTombStoneInfo getTombStoneInfo) throws JsonProcessingException {

        var getTombStoneInfoDocument =
                XMLUtilities.convertReq(
                        getTombStoneInfo, new GetTombStoneInfoDocument(), "getTombStoneInfo");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "tombstone-info");

        HttpEntity<GetTombStoneInfoDocument> payload =
                new HttpEntity<>(getTombStoneInfoDocument, new HttpHeaders());

        try {
            HttpEntity<GetTombStoneInfoRequest> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            GetTombStoneInfoRequest.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getTombStoneInfo")));
            GetTombStoneInfoResponseDocument getTombStoneInfoResponseDocument =
                    new GetTombStoneInfoResponseDocument();
            GetTombStoneInfo2 getTombStoneInfoOut = new GetTombStoneInfo2();
            getTombStoneInfoOut.setTombStoneInfo(resp.getBody());
            getTombStoneInfoResponseDocument.setXMLString(getTombStoneInfoOut);

            var getTombStoneInfoResponse =
                    XMLUtilities.convertResp(
                            getTombStoneInfoResponseDocument,
                            new GetTombStoneInfoResponse(),
                            "getTombStoneInfoResponse");

            return getTombStoneInfoResponse;

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getTombStoneInfo",
                                    ex.getMessage(),
                                    getTombStoneInfo)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.TrustAccount.ws.provider:TrustAccount",
            localPart = "getTrustAccount")
    @ResponsePayload
    public GetTrustAccountResponse getTrustAccount(@RequestPayload GetTrustAccount getTrustAccount)
            throws JsonProcessingException {

        var getTrustAccountDocument =
                XMLUtilities.convertReq(
                        getTrustAccount, new GetTrustAccountDocument(), "getTrustAccount");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "trust-account");

        HttpEntity<GetTrustAccountDocument> payload =
                new HttpEntity<>(getTrustAccountDocument, new HttpHeaders());
        try {
            HttpEntity<GetTrustAccountRequest> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            GetTrustAccountRequest.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getTrustAccount")));

            GetTrustAccountResponseDocument getTrustAccountResponseDocument =
                    new GetTrustAccountResponseDocument();
            GetTrustAccount2 getTrustAccountInner = new GetTrustAccount2();
            getTrustAccountInner.setTrustAccount(resp.getBody());
            getTrustAccountResponseDocument.setXMLString(getTrustAccountInner);

            var getTrustAccountResponse =
                    XMLUtilities.convertResp(
                            getTrustAccountResponseDocument,
                            new GetTrustAccountResponse(),
                            "getTrustAccountResponse");

            return getTrustAccountResponse;

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getTrustAccount",
                                    ex.getMessage(),
                                    getTrustAccount)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.VisitSchedule.ws.provider:VisitSchedule",
            localPart = "getVisitSchedule")
    @ResponsePayload
    public GetVisitScheduleResponse getVisitSchedule(
            @RequestPayload GetVisitSchedule getVisitSchedule) throws JsonProcessingException {

        var getVisitScheduleDocument =
                XMLUtilities.convertReq(
                        getVisitSchedule, new GetVisitScheduleDocument(), "getVisitSchedule");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "visit-schedule");

        HttpEntity<GetVisitScheduleDocument> payload =
                new HttpEntity<>(getVisitScheduleDocument, new HttpHeaders());
        try {
            HttpEntity<GetVisitScheduleRequest> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            GetVisitScheduleRequest.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getVisitSchedule")));

            GetVisitScheduleResponseDocument getVisitScheduleResponseDocument =
                    new GetVisitScheduleResponseDocument();
            GetVisitSchedule2 getVisitScheduleInner = new GetVisitSchedule2();
            getVisitScheduleInner.setVisitSchedule(resp.getBody());
            getVisitScheduleResponseDocument.setXMLString(getVisitScheduleInner);

            var getVisitScheduleResponse =
                    XMLUtilities.convertResp(
                            getVisitScheduleResponseDocument,
                            new GetVisitScheduleResponse(),
                            "GetVisitScheduleResponse");

            return getVisitScheduleResponse;

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getVisitSchedule",
                                    ex.getMessage(),
                                    getVisitSchedule)));
            throw new ORDSException();
        }
    }
}
