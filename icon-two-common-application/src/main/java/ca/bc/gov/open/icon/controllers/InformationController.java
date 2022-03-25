package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.auth.GetDeviceInfo;
import ca.bc.gov.open.icon.auth.GetDeviceInfoResponse;
import ca.bc.gov.open.icon.auth.GetUserInfo;
import ca.bc.gov.open.icon.auth.GetUserInfoResponse;
import ca.bc.gov.open.icon.configuration.SoapConfig;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.myinfo.*;
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
import provider.ws.healthservicerequest.source.icon2.hsr.GetHSRCount;
import provider.ws.healthservicerequest.source.icon2.hsr.GetHSRCountResponse;

@Endpoint
@Slf4j
public class InformationController {
  @Value("${icon.host}")
  private String host = "https://127.0.0.1/";

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Autowired
  public InformationController(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.Authorization.ws.provider:AuthAuth",
      localPart = "getUserInfo")
  @ResponsePayload
  public GetUserInfoResponse getUserInfo(@RequestPayload GetUserInfo getUserInfo)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "information/user-info")
                .queryParam("xmlString", getUserInfo.getXMLString())
                .queryParam("UserTokenString", getUserInfo.getUserTokenString());
       try {
      HttpEntity<GetUserInfoResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), GetUserInfoResponse.class);
      log.info(
          objectMapper.writeValueAsString(new RequestSuccessLog("Request Success", "getUserInfo")));
      GetUserInfoResponse out = new GetUserInfoResponse();
      return out;
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS", "getUserInfo", ex.getMessage(), getUserInfo)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.Authorization.ws.provider:AuthAuth",
      localPart = "getDeviceInfo")
  @ResponsePayload
  public GetDeviceInfoResponse getDeviceInfo(@RequestPayload GetDeviceInfo getDeviceInfo)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "information/device-info")
                .queryParam("xmlString", getDeviceInfo.getXMLString())
                .queryParam("userTokenString", getDeviceInfo.getUserTokenString());
        try {
      HttpEntity<GetDeviceInfoResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), GetDeviceInfoResponse.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "getDeviceInfo")));
      GetDeviceInfoResponse out = new GetDeviceInfoResponse();
      return resp.getBody();
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS", "getDeviceInfo", ex.getMessage(), getDeviceInfo)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.MyInfo.ws.provider:MyInfo",
      localPart = "")
  @ResponsePayload
  public GetOrdersResponse getOrders(@RequestPayload GetOrders getOrders)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "information/orders");
    HttpEntity<GetOrders> payload = new HttpEntity<>(getOrders, new HttpHeaders());

    try {
      HttpEntity<GetOrdersResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.POST, payload, GetOrdersResponse.class);
      log.info(
          objectMapper.writeValueAsString(new RequestSuccessLog("Request Success", "getOrders")));
      GetOrdersResponse out = new GetOrdersResponse();
      return out;
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS", "getOrders", ex.getMessage(), getOrders)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.MyInfo.ws.provider:MyInfo",
      localPart = "") // ask Ethan later about  SoapConfig.SOAP_NAMESPACE
  @ResponsePayload
  public GetProgramsResponse getPrograms(@RequestPayload GetPrograms getPrograms)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "information/programs");
    HttpEntity<GetPrograms> payload = new HttpEntity<>(getPrograms, new HttpHeaders());

    try {
      HttpEntity<GetProgramsResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.POST, payload, GetProgramsResponse.class);
      log.info(
          objectMapper.writeValueAsString(new RequestSuccessLog("Request Success", "getPrograms")));
      GetProgramsResponse out = new GetProgramsResponse();
      return out;
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS", "getPrograms", ex.getMessage(), getPrograms)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.MyInfo.ws.provider:MyInfo",
      localPart = "") // ask Ethan later about  SoapConfig.SOAP_NAMESPACE
  @ResponsePayload
  public GetLocationsResponse getLocations(@RequestPayload GetLocations getLocations)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "information/locations");
    HttpEntity<GetLocations> payload = new HttpEntity<>(getLocations, new HttpHeaders());

    try {
      HttpEntity<GetLocationsResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.POST, payload, GetLocationsResponse.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "getLocations")));
      GetLocationsResponse out = new GetLocationsResponse();
      return out;
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS", "getLocations", ex.getMessage(), getLocations)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.MyInfo.ws.provider:MyInfo",
      localPart = "") // ask Ethan later about  SoapConfig.SOAP_NAMESPACE
  @ResponsePayload
  public GetConditionsResponse getConditions(@RequestPayload GetConditions getConditions)
      throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "information/conditions");
    HttpEntity<GetConditions> payload = new HttpEntity<>(getConditions, new HttpHeaders());

    try {
      HttpEntity<GetConditionsResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.POST, payload, GetConditionsResponse.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "getConditions")));
      GetConditionsResponse out = new GetConditionsResponse();
      return out;
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS", "getConditions", ex.getMessage(), getConditions)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.MyInfo.ws.provider:MyInfo",
      localPart = "") // ask Ethan later about  SoapConfig.SOAP_NAMESPACE
  @ResponsePayload
  public GetOrdersConditionsResponse getOrdersConditions(
      @RequestPayload GetOrdersConditions getOrdersConditions) throws JsonProcessingException {

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(host + "information/order-conditions");
    HttpEntity<GetOrdersConditions> payload =
        new HttpEntity<>(getOrdersConditions, new HttpHeaders());

    try {
      HttpEntity<GetOrdersConditionsResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.POST, payload, GetOrdersConditionsResponse.class);
      log.info(
          objectMapper.writeValueAsString(
              new RequestSuccessLog("Request Success", "getOrdersConditions")));
      GetOrdersConditionsResponse out = new GetOrdersConditionsResponse();
      return out;
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog(
                  "Error received from ORDS",
                  "getOrdersConditions",
                  ex.getMessage(),
                  getOrdersConditions)));
      throw new ORDSException();
    }
  }

  @PayloadRoot(
      namespace = "http://reeks.bcgov/ICON2.Source.MyInfo.ws.provider:MyInfo",
      localPart = "") // ask Ethan later about  SoapConfig.SOAP_NAMESPACE
  @ResponsePayload
  public GetDatesResponse getDates(@RequestPayload GetDates getDates)
      throws JsonProcessingException {

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "information/dates");
    HttpEntity<GetDates> payload = new HttpEntity<>(getDates, new HttpHeaders());

    try {
      HttpEntity<GetDatesResponse> resp =
          restTemplate.exchange(
              builder.toUriString(), HttpMethod.POST, payload, GetDatesResponse.class);
      log.info(
          objectMapper.writeValueAsString(new RequestSuccessLog("Request Success", "getDates")));
      GetDatesResponse out = new GetDatesResponse();
      return out;
    } catch (Exception ex) {
      log.error(
          objectMapper.writeValueAsString(
              new OrdsErrorLog("Error received from ORDS", "getDates", ex.getMessage(), getDates)));
      throw new ORDSException();
    }
  }
}
