package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.configuration.SoapConfig;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.myfiles.*;
import ca.bc.gov.open.icon.myinfo.GetClientHistory;
import ca.bc.gov.open.icon.myinfo.GetClientHistoryResponse;
import ca.bc.gov.open.icon.packageinfo.GetPackageInfo;
import ca.bc.gov.open.icon.packageinfo.GetPackageInfoResponse;
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

@Endpoint
@Slf4j
public class AuditController {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuditController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload
    public EServiceAccessedResponse  eServiceAccessed (
            @RequestPayload EServiceAccessed eServiceAccessed)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/eservice-accessed");
        HttpEntity<EServiceAccessed> payload = new HttpEntity<>( eServiceAccessed, new HttpHeaders());

        try {
            HttpEntity<EServiceAccessedResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            EServiceAccessedResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "eServiceAccessed")));
            EServiceAccessedResponse out = new EServiceAccessedResponse();
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "eServiceAccessed",
                                    ex.getMessage(),
                                    eServiceAccessed)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload
    public HomeScreenAccessedResponse homeScreenAccessed (
            @RequestPayload HomeScreenAccessed homeScreenAccessed )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/home-service-accessed");
        HttpEntity<EServiceAccessed> payload = new HttpEntity<>( homeScreenAccessed , new HttpHeaders());

        try {
            HttpEntity<HomeScreenAccessedResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            HomeScreenAccessedResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "homeScreenAccessed")));
            HomeScreenAccessedResponse out = new HomeScreenAccessedResponse();
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "homeScreenAccessed",
                                    ex.getMessage(),
                                    homeScreenAccessed)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload
    public SessionTimeoutExecutedResponse sessionTimeoutExecuted (
            @RequestPayload SessionTimeoutExecuted sessionTimeoutExecuted )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/session-timeout");
        HttpEntity<SessionTimeoutExecuted> payload = new HttpEntity<>( sessionTimeoutExecuted , new HttpHeaders());

        try {
            HttpEntity<SessionTimeoutExecutedResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            SessionTimeoutExecutedResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "sessionTimeoutExecuted")));
            SessionTimeoutExecutedResponse out = new SessionTimeoutExecutedResponse();
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "sessionTimeoutExecuted",
                                    ex.getMessage(),
                                    sessionTimeoutExecuted)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload
    public EServiceFunctionAccessedResponse eServiceFunctionAccessed (
            @RequestPayload EServiceFunctionAccessed eServiceFunctionAccessed )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/eservice-function-access");
        HttpEntity<EServiceFunctionAccessed> payload = new HttpEntity<>( eServiceFunctionAccessed , new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "eServiceFunctionAccessed")));
            EServiceFunctionAccessedResponse out = new EServiceFunctionAccessedResponse();
            out.setStatus(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "eServiceFunctionAccessed",
                                    ex.getMessage(),
                                    eServiceFunctionAccessed)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload
    public GetClientHistoryResponse getClientHistory (
            @RequestPayload GetClientHistory getClientHistory )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/eservice-function-access");
        HttpEntity<GetClientHistory> payload = new HttpEntity<>( getClientHistory, new HttpHeaders());

        try {
            HttpEntity<GetClientHistoryResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            GetClientHistoryResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getClientHistory")));
            GetClientHistoryResponse out = new GetClientHistoryResponse();
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getClientHistory",
                                    ex.getMessage(),
                                    getClientHistory)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload
    public GetPackageInfoResponse getPackageInfo (
            @RequestPayload GetPackageInfo getPackageInfo )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/package-info");
        HttpEntity<GetPackageInfo> payload = new HttpEntity<>( getPackageInfo, new HttpHeaders());

        try {
            HttpEntity< GetPackageInfoResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            GetPackageInfoResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getPackageInfo")));
            GetPackageInfoResponse out = new  GetPackageInfoResponse();
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getPackageInfo",
                                    ex.getMessage(),
                                    getPackageInfo)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload
    public GetSessionParametersResponse getSessionParameters (
            @RequestPayload GetSessionParameters getSessionParameters )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/session-parameters");
        HttpEntity<GetSessionParameters> payload = new HttpEntity<>( getSessionParameters, new HttpHeaders());

        try {
            HttpEntity< GetSessionParametersResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            GetSessionParametersResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getSessionParameters")));
            GetSessionParametersResponse out = new  GetSessionParametersResponse();
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getSessionParameters",
                                    ex.getMessage(),
                                    getSessionParameters)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "") //ask Ethan later about  SoapConfig.SOAP_NAMESPACE
    @ResponsePayload
    public HealthServiceRequestSubmittedResponse healthServiceRequestSubmitted (
            @RequestPayload HealthServiceRequest healthServiceRequestSubmitted
    )
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/session-parameters");
        HttpEntity<GetSessionParameters> payload = new HttpEntity<>( getSessionParameters, new HttpHeaders());

        try {
            HttpEntity< GetSessionParametersResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            GetSessionParametersResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getSessionParameters")));
            GetSessionParametersResponse out = new  GetSessionParametersResponse();
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getSessionParameters",
                                    ex.getMessage(),
                                    getSessionParameters)));
            throw new ORDSException();
        }
    }

}
