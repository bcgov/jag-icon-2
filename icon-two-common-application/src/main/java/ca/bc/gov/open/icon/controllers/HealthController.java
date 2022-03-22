package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.audit.HealthServiceRequest;
import ca.bc.gov.open.icon.audit.HealthServiceRequestSubmittedResponse;
import ca.bc.gov.open.icon.audit.Status;
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
import provider.ws.healthservicerequest.source.icon2.hsr.*;

@Endpoint
@Slf4j
public class HealthController {
  @Value("${icon.host}")
  private String host = "https://127.0.0.1/";

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Autowired
  public HealthController(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  @PayloadRoot(
      namespace = SoapConfig.SOAP_NAMESPACE,
      localPart = "") // ask Ethan later about  SoapConfig.SOAP_NAMESPACE
  @ResponsePayload
  public HealthServiceRequestSubmittedResponse healthServiceRequestSubmitted(
      @RequestPayload HealthServiceRequest healthServiceRequestSubmitted)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "health/service-rqst-submitted");
    HttpEntity<HealthServiceRequest> payload =
        new HttpEntity<>(healthServiceRequestSubmitted, new HttpHeaders());

    try {
      HttpEntity<Status> resp =
          restTemplate.exchange(builder.toUriString(), HttpMethod.POST, payload, Status.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "healthServiceRequestSubmitted")));
      HealthServiceRequestSubmittedResponse out = new HealthServiceRequestSubmittedResponse();
      out.setStatus(resp.getBody());
      return out;
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS",
                  "healthServiceRequestSubmitted",
                  ex.getMessage(),
                  healthServiceRequestSubmitted)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = SoapConfig.SOAP_NAMESPACE,
      localPart = "") // ask Ethan later about  SoapConfig.SOAP_NAMESPACE
  @ResponsePayload
  public GetHealthServiceRequestHistoryResponse getHealthServiceRequestHistory(
      @RequestPayload GetHealthServiceRequestHistory getHealthServiceRequestHistory)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "health/service-rqst-history");
    HttpEntity<GetHealthServiceRequestHistory> payload =
        new HttpEntity<>(getHealthServiceRequestHistory, new HttpHeaders());

    try {
      HttpEntity<GetHealthServiceRequestHistoryResponse> resp =
          restTemplate.exchange(
              builder.toUriString(),
              HttpMethod.POST,
              payload,
              GetHealthServiceRequestHistoryResponse.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "getHealthServiceRequestHistory")));
      GetHealthServiceRequestHistoryResponse out = new GetHealthServiceRequestHistoryResponse();
      return out;
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS",
                  "getHealthServiceRequestHistory",
                  ex.getMessage(),
                  getHealthServiceRequestHistory)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = SoapConfig.SOAP_NAMESPACE,
      localPart = "") // ask Ethan later about  SoapConfig.SOAP_NAMESPACE
  @ResponsePayload
  public PublishHSRResponse publishHSR(@RequestPayload PublishHSR publishHSR)
      throws JsonProcessingException {

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "health/publish-hsr");
    HttpEntity<PublishHSR> payload = new HttpEntity<>(publishHSR, new HttpHeaders());

    try {
      HttpEntity<PublishHSRResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.POST, payload, PublishHSRResponse.class);
      log.info(
          objectMapper.writeValueAsString(new RequestSuccessLog("Request Success", "publishHSR")));
      PublishHSRResponse out = new PublishHSRResponse();
      return out;
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS", "publishHSR", ex.getMessage(), publishHSR)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = SoapConfig.SOAP_NAMESPACE,
      localPart = "") // ask Ethan later about  SoapConfig.SOAP_NAMESPACE
  @ResponsePayload
  public GetHSRCountResponse getHSRCount(@RequestPayload GetHSRCount getHSRCount)
      throws JsonProcessingException {

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "health/hsr-count");
    HttpEntity<GetHSRCount> payload = new HttpEntity<>(getHSRCount, new HttpHeaders());

    try {
      HttpEntity<GetHSRCountResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.POST, payload, GetHSRCountResponse.class);
      log.info(
          objectMapper.writeValueAsString(new RequestSuccessLog("Request Success", "getHSRCount")));
      GetHSRCountResponse out = new GetHSRCountResponse();
      return out;
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS", "getHSRCount", ex.getMessage(), getHSRCount)));
      throw new ORDSException();
    }
  }
}
