package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.audit.ReauthenticationFailed;
import ca.bc.gov.open.icon.audit.ReauthenticationFailedResponse;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.configuration.SoapConfig;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.trustaccount.GetTrustAccount;
import ca.bc.gov.open.icon.trustaccount.GetTrustAccountResponse;
import ca.bc.gov.open.icon.visitschedule.GetVisitSchedule;
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
import provider.ws.tombstoneinfo.source.icon2.tombstoneinfo.GetTombStoneInfo;
import provider.ws.tombstoneinfo.source.icon2.tombstoneinfo.GetTombStoneInfoResponse;

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
      namespace = SoapConfig.SOAP_NAMESPACE,
      localPart = "") // ask Ethan later about  SoapConfig.SOAP_NAMESPACE
  @ResponsePayload
  public GetTombStoneInfoResponse getTombStoneInfo(
      @RequestPayload GetTombStoneInfo getTombStoneInfo) throws JsonProcessingException {

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "client/tombstone-info");
    HttpEntity<GetTombStoneInfo> payload = new HttpEntity<>(getTombStoneInfo, new HttpHeaders());

    try {
      HttpEntity<GetTombStoneInfoResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.POST, payload, GetTombStoneInfoResponse.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "getTombStoneInfo")));
      GetTombStoneInfoResponse out = new GetTombStoneInfoResponse();
      return out;
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
      namespace = SoapConfig.SOAP_NAMESPACE,
      localPart = "") // ask Ethan later about  SoapConfig.SOAP_NAMESPACE
  @ResponsePayload
  public GetTrustAccountResponse getTrustAccount(@RequestPayload GetTrustAccount getTrustAccount)
      throws JsonProcessingException {

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "client/trust-account");
    HttpEntity<GetTrustAccount> payload = new HttpEntity<>(getTrustAccount, new HttpHeaders());

    try {
      HttpEntity<GetTrustAccountResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.POST, payload, GetTrustAccountResponse.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "getTrustAccount")));
      GetTrustAccountResponse out = new GetTrustAccountResponse();
      return out;
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
      namespace = SoapConfig.SOAP_NAMESPACE,
      localPart = "") // ask Ethan later about  SoapConfig.SOAP_NAMESPACE
  @ResponsePayload
  public GetVisitScheduleResponse getVisitSchedule(
      @RequestPayload GetVisitSchedule getVisitSchedule) throws JsonProcessingException {

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "client/visit-schedule");
    HttpEntity<GetVisitSchedule> payload = new HttpEntity<>(getVisitSchedule, new HttpHeaders());

    try {
      HttpEntity<GetVisitScheduleResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.POST, payload, GetVisitScheduleResponse.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "getVisitSchedule")));
      GetVisitScheduleResponse out = new GetVisitScheduleResponse();
      return out;
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
