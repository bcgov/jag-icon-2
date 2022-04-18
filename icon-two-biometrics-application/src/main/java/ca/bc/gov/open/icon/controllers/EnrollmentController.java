package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.biometrics.StartEnrollment;
import ca.bc.gov.open.icon.biometrics.StartEnrollmentResponse;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Map;

@Endpoint
@Slf4j
public class EnrollmentController {

  @Value("${icon.bio-host}")
  private String host = "https://127.0.0.1/";

  @Value("${icon.ords-host}")
  private String ordsHost = "https://127.0.0.1/";

  private final WebServiceTemplate soapTemplate;
  private final ObjectMapper objectMapper;
  private final RestTemplate restTemplate;

  public EnrollmentController(
      WebServiceTemplate soapTemplate, ObjectMapper objectMapper, RestTemplate restTemplate) {
    this.soapTemplate = soapTemplate;
    this.objectMapper = objectMapper;
    this.restTemplate = restTemplate;
  }

  @PayloadRoot(
      namespace = "ICON2_Biometrics.Source.Biometrics.ws.provider:Biometrics",
      localPart = "startEnrollment")
  @ResponsePayload
  public StartEnrollmentResponse startEnrollment(@RequestPayload StartEnrollment startEnrollment)
      throws JsonProcessingException {

    try {
      UriComponentsBuilder builder =
          UriComponentsBuilder.fromHttpUrl(host + "biometrics/client/did")
              .queryParam("csnum", startEnrollment.getCsNum());

      HttpEntity<Map<String, String>> andid =
          restTemplate.exchange(
              builder.build().toUri(),
              HttpMethod.GET,
              new HttpEntity<>(new HttpHeaders()),
                  new ParameterizedTypeReference<>() {
                  });

    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Processing failed", "startEnrollment", ex.getMessage(), startEnrollment)));
      throw new ORDSException();
    }
  }
}
