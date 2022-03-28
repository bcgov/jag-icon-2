package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.audit.EReportAnswersSubmitted;
import ca.bc.gov.open.icon.audit.EReportAnswersSubmittedResponse;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.configuration.SoapConfig;
import ca.bc.gov.open.icon.ereporting.*;
import ca.bc.gov.open.icon.exceptions.ORDSException;
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
public class ReportingController {
  @Value("${icon.host}")
  private String host = "https://127.0.0.1/";

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Autowired
  public ReportingController(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.Audit.ws.provider:Audit",
      localPart = "eReportAnswersSubmitted")
  @ResponsePayload
  public EReportAnswersSubmittedResponse eReportAnswersSubmitted(
      @RequestPayload EReportAnswersSubmitted eReportAnswersSubmitted)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "reporting/ereport-answers-submitted");
    HttpEntity<EReportAnswersSubmitted> payload =
        new HttpEntity<>(eReportAnswersSubmitted, new HttpHeaders());

    try {
      HttpEntity<Status> resp =
          restTemplate.exchange(builder.toUriString(), HttpMethod.POST, payload, Status.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "eReportAnswersSubmitted")));
      EReportAnswersSubmittedResponse out = new EReportAnswersSubmittedResponse();
      out.setStatus(resp.getBody());
      return out;
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS",
                  "eReportAnswersSubmitted",
                  ex.getMessage(),
                  eReportAnswersSubmitted)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.EReporting.ws.provider:EReporting",
      localPart = "getReportingCmpltInstruction")
  @ResponsePayload
  public GetReportingCmpltInstructionResponse getReportingCmpltInstruction(
      @RequestPayload GetReportingCmpltInstruction getReportingCmpltInstruction)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "reporting/complete-instruction")
            .queryParam("xmlString", getReportingCmpltInstruction.getXMLString())
            .queryParam("userTokenString", getReportingCmpltInstruction.getUserTokenString());

    try {
      HttpEntity<GetReportingCmpltInstructionResponse> resp =
          restTemplate.exchange(
              builder.toUriString(),
              HttpMethod.GET,
              new HttpEntity<>(new HttpHeaders()),
              GetReportingCmpltInstructionResponse.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "getReportingCmpltInstruction")));
      return resp.getBody();
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS",
                  "getReportingCmpltInstruction",
                  ex.getMessage(),
                  getReportingCmpltInstruction)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.EReporting.ws.provider:EReporting",
      localPart = "")
  @ResponsePayload
  public GetLocationsResponse getLocationsResponse(
      @RequestPayload GetLocations getLocationsResponse) throws JsonProcessingException {

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "reporting/locations");
    HttpEntity<GetLocations> payload = new HttpEntity<>(getLocationsResponse, new HttpHeaders());

    try {
      HttpEntity<GetLocationsResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.POST, payload, GetLocationsResponse.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "getLocationsResponse")));
      GetLocationsResponse out = new GetLocationsResponse();
      return resp.getBody();
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS",
                  "getLocationsResponse",
                  ex.getMessage(),
                  getLocationsResponse)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.EReporting.ws.provider:EReporting",
      localPart = "submitAnswers")
  @ResponsePayload
  public SubmitAnswersResponse submitAnswers(@RequestPayload SubmitAnswers submitAnswers)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "reporting/submit-answers");
    HttpEntity<SubmitAnswers> payload = new HttpEntity<>(submitAnswers, new HttpHeaders());

    try {
      HttpEntity<SubmitAnswersResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.POST, payload, SubmitAnswersResponse.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "submitAnswers")));
      return resp.getBody();
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS", "submitAnswers", ex.getMessage(), submitAnswers)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.EReporting.ws.provider:EReporting",
      localPart = "getAppointment")
  @ResponsePayload
  public GetAppointmentResponse getAppointment(@RequestPayload GetAppointment getAppointment)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "reporting/appointment")
            .queryParam("xmlString", getAppointment.getXMLString())
            .queryParam("userTokenString", getAppointment.getUserTokenString());

    try {
      HttpEntity<GetAppointmentResponse> resp =
          restTemplate.exchange(
              builder.toUriString(),
              HttpMethod.GET,
              new HttpEntity<>(new HttpHeaders()),
              GetAppointmentResponse.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "getAppointment")));

      return resp.getBody();
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS", "getAppointment", ex.getMessage(), getAppointment)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.EReporting.ws.provider:EReporting",
      localPart = "getQuestions")
  @ResponsePayload
  public GetQuestionsResponse getQuestions(@RequestPayload GetQuestions getQuestions)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "reporting/questions")
            .queryParam("xmlString", getQuestions.getXMLString())
            .queryParam("userTokenString", getQuestions.getUserTokenString());

    try {
      HttpEntity<GetQuestionsResponse> resp =
          restTemplate.exchange(
              builder.toUriString(),
              HttpMethod.GET,
              new HttpEntity<>(new HttpHeaders()),
              GetQuestionsResponse.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "getQuestions")));

      return resp.getBody();
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS", "getQuestions", ex.getMessage(), getQuestions)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.EReporting.ws.provider:EReporting",
      localPart = "getStatus")
  @ResponsePayload
  public GetStatusResponse getStatus(@RequestPayload GetStatus getStatus)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "reporting/status")
            .queryParam("xmlString", getStatus.getXMLString())
            .queryParam("userTokenString", getStatus.getUserTokenString());

    try {
      HttpEntity<GetStatusResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), GetStatusResponse.class);
      log.info(
          objectMapper.writeValueAsString(new RequestSuccessLog("Request Success", "getStatus")));
      return resp.getBody();
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS", "getStatus", ex.getMessage(), getStatus)));
      throw new ORDSException();
    }
  }
}
