package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.auth.*;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.utils.*;
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
            namespace = "ICON2.Source.Authorization.ws.provider:AuthAuth",
            localPart = "getUserInfo")
    @ResponsePayload
    public GetUserInfoResponse getUserInfo(@RequestPayload GetUserInfo getUserInfo)
            throws JsonProcessingException {

        var getUserInfoDocument =
                XMLUtilities.convertReq(getUserInfo, new GetUserInfoDocument(), "getUserInfo");

        // fetch the inmost UserInfo layer
        var inner =
                getUserInfoDocument.getXMLString() != null
                                && getUserInfoDocument.getXMLString().getUserInfo() != null
                        ? getUserInfoDocument.getXMLString().getUserInfo()
                        : new UserInfo();

        HttpEntity<UserInfo> payload = new HttpEntity<>(inner, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/user-info");

        try {
            HttpEntity<UserInfo> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, UserInfo.class);

            var getUserInfoResponseDocument = new GetUserInfoResponseDocument();
            var outResp = new UserInfoOut();
            outResp.setUserInfo(resp.getBody());
            getUserInfoResponseDocument.setXMLString(outResp);

            var getUserInfoResponse =
                    XMLUtilities.convertResp(
                            getUserInfoResponseDocument,
                            new GetUserInfoResponse(),
                            "getUserInfoResponse");
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getUserInfo")));
            return getUserInfoResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getUserInfo",
                                    ex.getMessage(),
                                    inner)));
            throw handleError(ex, new ca.bc.gov.open.icon.auth.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.Authorization.ws.provider:AuthAuth",
            localPart = "getDeviceInfo")
    @ResponsePayload
    public GetDeviceInfoResponse getDeviceInfo(@RequestPayload GetDeviceInfo getDeviceInfo)
            throws JsonProcessingException {

        var getDeviceInfoDocument =
                XMLUtilities.convertReq(
                        getDeviceInfo, new GetDeviceInfoDocument(), "getDeviceInfo");

        // fetch the inmost DeviceInfo layer
        DeviceInfo inner =
                (getDeviceInfoDocument.getXMLString() != null
                                && getDeviceInfoDocument.getXMLString().getDeviceInfo() != null
                        ? getDeviceInfoDocument.getXMLString().getDeviceInfo()
                        : new DeviceInfo());

        HttpEntity<DeviceInfo> payload = new HttpEntity<>(inner, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/device-info");

        try {
            HttpEntity<DeviceInfo> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, DeviceInfo.class);

            var getDeviceInfoResponseDocument = new GetDeviceInfoResponseDocument();
            var outResp = new DeviceInfoOut();
            outResp.setDeviceInfo(resp.getBody());
            getDeviceInfoResponseDocument.setXMLString(outResp);

            var getDeviceInfoResponse =
                    XMLUtilities.convertResp(
                            getDeviceInfoResponseDocument,
                            new GetDeviceInfoResponse(),
                            "getDeviceInfoResponse");

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getDeviceInfo")));
            return getDeviceInfoResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getDeviceInfo",
                                    ex.getMessage(),
                                    inner)));

            throw handleError(ex, new ca.bc.gov.open.icon.auth.Error());
        }
    }
}
