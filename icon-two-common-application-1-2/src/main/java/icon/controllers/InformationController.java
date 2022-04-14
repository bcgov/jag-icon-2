package icon.controllers;

import ca.bc.gov.open.icon.auth.*;
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

        // fetch the inmost UserInfo layer
        var inner =
                getUserInfo.getXMLString() != null
                                && getUserInfo.getXMLString().getUserInfo() != null
                                && getUserInfo.getXMLString().getUserInfo().getUserInfo() != null
                        ? getUserInfo.getXMLString().getUserInfo().getUserInfo()
                        : new UserInfo();

        HttpEntity<UserInfo> payload = new HttpEntity<>(inner, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/user-info");

        try {

            HttpEntity<UserInfo> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, UserInfo.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog(
                                    "Request Success", objectMapper.writeValueAsString(inner))));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getUserInfo")));

            var getUserInfoResponse = new GetUserInfoResponse();
            var outResp = new UserInfoOut();
            var inResp = new UserInfoInner();
            var innerResp = resp.getBody();

            inResp.setUserInfo(innerResp);
            outResp.setUserInfo(inResp);
            getUserInfoResponse.setXMLString(outResp);

            return getUserInfoResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getUserInfo",
                                    ex.getMessage(),
                                    inner)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.Authorization.ws.provider:AuthAuth",
            localPart = "getDeviceInfo")
    @ResponsePayload
    public GetDeviceInfoResponse getDeviceInfo(@RequestPayload GetDeviceInfo getDeviceInfo)
            throws JsonProcessingException {

        // fetch the inmost DeviceInfo layer
        DeviceInfo inner =
                (getDeviceInfo.getXMLString() != null
                                && getDeviceInfo.getXMLString().getDeviceInfo() != null
                                && getDeviceInfo.getXMLString().getDeviceInfo().getDeviceInfo()
                                        != null
                        ? getDeviceInfo.getXMLString().getDeviceInfo().getDeviceInfo()
                        : new DeviceInfo());

        HttpEntity<DeviceInfo> payload = new HttpEntity<>(inner, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/device-info");

        try {
            HttpEntity<DeviceInfo> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, DeviceInfo.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog(
                                    "Request Success", objectMapper.writeValueAsString(inner))));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getDeviceInfo")));

            var getDeviceInfoResponse = new GetDeviceInfoResponse();
            var outResp = new DeviceInfoOut();
            var inResp = new DeviceInfoInner();

            inResp.setDeviceInfo(resp.getBody());
            outResp.setDeviceInfo(inResp);
            getDeviceInfoResponse.setXMLString(outResp);
            return getDeviceInfoResponse;

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getDeviceInfo",
                                    ex.getMessage(),
                                    inner)));
            throw new ORDSException();
        }
    }

}
