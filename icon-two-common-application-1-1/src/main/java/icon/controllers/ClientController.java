package icon.controllers;

import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.tombstone.GetTombStoneInfo;
import ca.bc.gov.open.icon.tombstone.GetTombStoneInfo2;
import ca.bc.gov.open.icon.tombstone.GetTombStoneInfoRequest;
import ca.bc.gov.open.icon.tombstone.GetTombStoneInfoResponse;
import ca.bc.gov.open.icon.trustaccount.GetTrustAccount;
import ca.bc.gov.open.icon.trustaccount.GetTrustAccount2;
import ca.bc.gov.open.icon.trustaccount.GetTrustAccountRequest;
import ca.bc.gov.open.icon.trustaccount.GetTrustAccountResponse;
import ca.bc.gov.open.icon.visitschedule.GetVisitSchedule;
import ca.bc.gov.open.icon.visitschedule.GetVisitSchedule2;
import ca.bc.gov.open.icon.visitschedule.GetVisitScheduleRequest;
import ca.bc.gov.open.icon.visitschedule.GetVisitScheduleResponse;
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
            namespace = "http://reeks.bcgov/ICON2.Source.TombStoneInfo.ws.provider:TombStoneInfo",
            localPart = "getTombStoneInfo")
    @ResponsePayload
    public GetTombStoneInfoResponse getTombStoneInfo(
            @RequestPayload GetTombStoneInfo getTombStoneInfo) throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "client/tombstone-info");
        HttpEntity<GetTombStoneInfoRequest> payload = new HttpEntity<>(getTombStoneInfo.getXMLString().getTombStoneInfo(), new HttpHeaders());

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
               GetTombStoneInfoResponse getTombStoneInfoResponseOut = new GetTombStoneInfoResponse();
               GetTombStoneInfo2 getTombStoneInfoOut = new GetTombStoneInfo2();
               getTombStoneInfoOut.setTombStoneInfo(resp.getBody());
               getTombStoneInfoResponseOut.setXMLString(getTombStoneInfoOut);
               return getTombStoneInfoResponseOut;

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
            namespace = "http://reeks.bcgov/ICON2.Source.TrustAccount.ws.provider:TrustAccount",
            localPart = "getTrustAccount")
    @ResponsePayload
    public GetTrustAccountResponse getTrustAccount(@RequestPayload GetTrustAccount getTrustAccount)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "client/trust-account");

        HttpEntity<GetTrustAccount> payload = new HttpEntity<>(getTrustAccount, new HttpHeaders());
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

            GetTrustAccountResponse getTrustAccountResponseOut = new GetTrustAccountResponse();
            GetTrustAccount2 getTrustAccountInner = new GetTrustAccount2();
            getTrustAccountInner.setTrustAccount(resp.getBody());
            getTrustAccountResponseOut.setXMLString(getTrustAccountInner);
            return getTrustAccountResponseOut;

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
            namespace = "http://reeks.bcgov/ICON2.Source.VisitSchedule.ws.provider:VisitSchedule",
            localPart = "getVisitScheduleResponse")
    @ResponsePayload
    public GetVisitScheduleResponse getVisitSchedule(
            @RequestPayload GetVisitSchedule getVisitSchedule) throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "client/visit-schedule");
        HttpEntity<GetVisitSchedule> payload = new HttpEntity<>(getVisitSchedule, new HttpHeaders());
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

            GetVisitScheduleResponse getVisitScheduleResponseOut = new GetVisitScheduleResponse();
            GetVisitSchedule2 getVisitScheduleInner = new GetVisitSchedule2();
            getVisitScheduleInner.setVisitSchedule(resp.getBody());
            getVisitScheduleResponseOut.setXMLString(getVisitScheduleInner);
            return getVisitScheduleResponseOut;

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
