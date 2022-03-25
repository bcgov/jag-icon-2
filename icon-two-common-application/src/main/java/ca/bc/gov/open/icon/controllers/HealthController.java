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
      namespace = "http://reeks.bcgov/ICON2.Source.Audit.ws.provider:Audit",
      localPart = "HealthServiceRequestSubmitted")
  @ResponsePayload
  public HealthServiceRequestSubmittedResponse healthServiceRequestSubmitted(
      @RequestPayload HealthServiceRequestSubmitted healthServiceRequestSubmitted)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "health/service-rqst-submitted");

    HealthServiceRequest inner =
            healthServiceRequestSubmitted != null && healthServiceRequestSubmitted.getHealthServiceRequest() != null
                    ? healthServiceRequestSubmitted.getHealthServiceRequest()
                    : new HealthServiceRequest();
    HttpEntity<HealthServiceRequest> payload = new HttpEntity<>(inner, new HttpHeaders());
    HttpEntity<HealthServiceRequestSubmitted> payload =
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
      namespace = "http://reeks.bcgov/ICON2.Source.HealthServiceRequest.ws.provider:HSR",
      localPart = "getHealthServiceRequestHistory") // ask Ethan later about  SoapConfig.SOAP_NAMESPACE
  @ResponsePayload
  public GetHealthServiceRequestHistoryResponse getHealthServiceRequestHistory(
      @RequestPayload GetHealthServiceRequestHistory getHealthServiceRequestHistory)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "health/service-rqst-history")
                .queryParam("xmlString", getHealthServiceRequestHistory.getXMLString())
                .queryParam("userTokenString", getHealthServiceRequestHistory.getUserTokenString());

    try {
      HttpEntity<GetHealthServiceRequestHistoryResponse> resp =
          restTemplate.exchange(
              builder.toUriString(),
              HttpMethod.GET,
              new HttpEntity<>(new HttpHeaders()),
              GetHealthServiceRequestHistoryResponse.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "getHealthServiceRequestHistory")));
      GetHealthServiceRequestHistoryResponse out = new GetHealthServiceRequestHistoryResponse();
      return resp.getBody();
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
      namespace = "http://reeks.bcgov/ICON2.Source.HealthServiceRequest.ws.provider:HSR",
      localPart = "publishHSR")
  @ResponsePayload
  public PublishHSRResponse publishHSR(@RequestPayload PublishHSR publishHSR)
      throws JsonProcessingException {

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "health/publish-hsr")
            .queryParam( "xmlString", publishHSR.getXMLString())
            .queryParam("UserTokenString", publishHSR.setUserTokenString());

    try {
      HttpEntity<PublishHSRResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), PublishHSRResponse.class);
      log.info(
          objectMapper.writeValueAsString(new RequestSuccessLog("Request Success", "publishHSR")));
      PublishHSRResponse out = new PublishHSRResponse();
      return resp.getBody();
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS", "publishHSR", ex.getMessage(), publishHSR)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.HealthServiceRequest.ws.provider:HSR",
      localPart = "getHSRCount")
  @ResponsePayload
  public GetHSRCountResponse getHSRCount(@RequestPayload GetHSRCount getHSRCount)
      throws JsonProcessingException {

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "health/hsr-count")
            .queryParam("xmlString", getHSRCount.getXMLString())
            .queryParam("userTokenString", getHSRCount.getUserTokenString());

    try {
      HttpEntity<GetHSRCountResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), GetHSRCountResponse.class);
      log.info(
          objectMapper.writeValueAsString(new RequestSuccessLog("Request Success", "getHSRCount")));
      GetHSRCountResponse out = new GetHSRCountResponse();
      return resp.getBody();
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS", "getHSRCount", ex.getMessage(), getHSRCount)));
      throw new ORDSException();
    }
  }
}
