package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.ereporting.Error;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.myinfo.*;
import ca.bc.gov.open.icon.packageinfo.GetPackageInfo;
import ca.bc.gov.open.icon.packageinfo.GetPackageInfoResponse;
import ca.bc.gov.open.icon.session.*;
import ca.bc.gov.open.icon.utils.XMLUtilities;
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

    @PayloadRoot(namespace = "ICON2.Source.Audit.ws:Record", localPart = "eServiceAccessed")
    @ResponsePayload
    public EServiceAccessedResponse eServiceAccessed(
            @RequestPayload EServiceAccessed eServiceAccessed) throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/eservice-accessed");
        HttpEntity<EServiceAccessed> payload =
                new HttpEntity<>(eServiceAccessed, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "eServiceAccessed")));
            EServiceAccessedResponse out = new EServiceAccessedResponse();
            out.setStatus(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "eServiceAccessed",
                                    ex.getMessage(),
                                    eServiceAccessed)));
            throw handleError(ex, new Error());
        }
    }

    @PayloadRoot(namespace = "ICON2.Source.Audit.ws:Record", localPart = "HomeScreenAccessed")
    @ResponsePayload
    public HomeScreenAccessedResponse homeScreenAccessed(
            @RequestPayload HomeScreenAccessed homeScreenAccessed) throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/home-screen-accessed");
        HttpEntity<HomeScreenAccessed> payload =
                new HttpEntity<>(homeScreenAccessed, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "homeScreenAccessed")));
            HomeScreenAccessedResponse out = new HomeScreenAccessedResponse();
            out.setStatus(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "homeScreenAccessed",
                                    ex.getMessage(),
                                    homeScreenAccessed)));
            throw handleError(ex, new Error());
        }
    }

    @PayloadRoot(namespace = "ICON2.Source.Audit.ws:Record", localPart = "SessionTimeoutExecuted")
    @ResponsePayload
    public SessionTimeoutExecutedResponse sessionTimeoutExecuted(
            @RequestPayload SessionTimeoutExecuted sessionTimeoutExecuted)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/session-timeout");
        HttpEntity<SessionTimeoutExecuted> payload =
                new HttpEntity<>(sessionTimeoutExecuted, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "sessionTimeoutExecuted")));
            SessionTimeoutExecutedResponse out = new SessionTimeoutExecutedResponse();
            out.setStatus(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "sessionTimeoutExecuted",
                                    ex.getMessage(),
                                    sessionTimeoutExecuted)));
            throw handleError(ex, new Error());
        }
    }

    @PayloadRoot(namespace = "ICON2.Source.Audit.ws:Record", localPart = "eServiceFunctionAccessed")
    @ResponsePayload
    public EServiceFunctionAccessedResponse eServiceFunctionAccessed(
            @RequestPayload EServiceFunctionAccessed eServiceFunctionAccessed)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/eservice-function-accessed");
        HttpEntity<EServiceFunctionAccessed> payload =
                new HttpEntity<>(eServiceFunctionAccessed, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Status.class);
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
            throw handleError(ex, new Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo",
            localPart = "getClientHistory")
    @ResponsePayload
    public GetClientHistoryResponse getClientHistory(
            @RequestPayload GetClientHistory getClientHistory) throws JsonProcessingException {

        var getClientHistoryDocument =
                XMLUtilities.convertReq(
                        getClientHistory, new GetClientHistoryDocument(), "getClientHistory");

        HttpEntity<GetClientHistoryDocument> payload =
                new HttpEntity<>(getClientHistoryDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/client-history");

        try {
            HttpEntity<ClientHistory> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, ClientHistory.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getClientHistory")));

            GetClientHistoryResponseDocument getClientHistoryResponseDocument =
                    new GetClientHistoryResponseDocument();
            ClientHistoryOuter outResp = new ClientHistoryOuter();

            outResp.setClientHistory(resp.getBody());
            getClientHistoryResponseDocument.setXMLString(outResp);

            var getClientHistoryResponse =
                    XMLUtilities.convertResp(
                            getClientHistoryResponseDocument,
                            new GetClientHistoryResponse(),
                            "getClientHistoryResponse");

            return getClientHistoryResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getClientHistory",
                                    ex.getMessage(),
                                    getClientHistory)));
            throw handleError(ex, new Error());
        }
    }

    @PayloadRoot(
            namespace = "http://reeks.bcgov/ICON2.Source.Version.ws.provider:PackageInfo",
            localPart = "getPackageInfo")
    @ResponsePayload
    public GetPackageInfoResponse getPackageInfo(@RequestPayload GetPackageInfo getPackageInfo)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/package-info");

        try {
            HttpEntity<GetPackageInfoResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            GetPackageInfoResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getPackageInfo")));
            return resp.getBody();
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

    @PayloadRoot(
            namespace = "ICON2.Source.Common.ws.provider:SessionParameter",
            localPart = "getSessionParameters")
    @ResponsePayload
    public GetSessionParametersResponse getSessionParameters(
            @RequestPayload GetSessionParameters getSessionParameters)
            throws JsonProcessingException {

        var getSessionParametersDocument =
                XMLUtilities.convertReq(
                        getSessionParameters,
                        new GetSessionParametersDocument(),
                        "getSessionParameters");

        SessionParameterOuter inner =
                getSessionParametersDocument.getXMLString() != null
                                && getSessionParametersDocument.getXMLString() != null
                        ? getSessionParametersDocument.getXMLString().getSessionParameters()
                        : new SessionParameterOuter();

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/session-parameters");
        HttpEntity<SessionParameterOuter> payload = new HttpEntity<>(inner, new HttpHeaders());

        try {
            HttpEntity<SessionParameterOuter> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            SessionParameterOuter.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getSessionParameters")));

            GetSessionParametersResponseDocument getSessionParametersResponseDocument =
                    new GetSessionParametersResponseDocument();
            SessionParameterOutest sessionParameterOutest = new SessionParameterOutest();
            SessionParameterOuter sessionParameterOuter = new SessionParameterOuter();

            getSessionParametersResponseDocument.setXMLString(sessionParameterOutest);
            sessionParameterOutest.setSessionParameters(sessionParameterOuter);
            sessionParameterOuter.setSessionParameter(resp.getBody().getSessionParameter());

            var getSessionParametersResponse =
                    XMLUtilities.convertResp(
                            getSessionParametersResponseDocument,
                            new GetSessionParametersResponse(),
                            "getSessionParametersResponse");

            return getSessionParametersResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getSessionParameters",
                                    ex.getMessage(),
                                    getSessionParameters)));
            throw handleError(ex);
        }
    }
}
