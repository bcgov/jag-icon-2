package ca.bc.gov.open.icon.controllers;

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

    @PayloadRoot(namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo", localPart = "getOrders")
    @ResponsePayload
    public GetOrdersResponse getOrders(@RequestPayload GetOrders getOrders)
            throws JsonProcessingException {

        HttpEntity<GetOrders> payload = new HttpEntity<>(getOrders, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/orders");

        try {
            HttpEntity<Orders> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Orders.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getOrders")));

            GetOrdersResponse getOrdersResponse = new GetOrdersResponse();
            OrdersOuter outResp = new OrdersOuter();
            OrdersInner inResp = new OrdersInner();
            inResp.setOrders(resp.getBody());
            outResp.setOrders(inResp);
            getOrdersResponse.setXMLString(outResp);
            return getOrdersResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getOrders",
                                    ex.getMessage(),
                                    getOrders)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo", localPart = "getPrograms")
    @ResponsePayload
    public GetProgramsResponse getPrograms(@RequestPayload GetPrograms getPrograms)
            throws JsonProcessingException {

        HttpEntity<GetPrograms> payload = new HttpEntity<>(getPrograms, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/programs");

        try {
            HttpEntity<Programs> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Programs.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getPrograms")));
            GetProgramsResponse getProgramsResponse = new GetProgramsResponse();
            ProgramsOuter outResp = new ProgramsOuter();
            ProgramsInner inResp = new ProgramsInner();
            inResp.setPrograms(resp.getBody());
            outResp.setPrograms(inResp);
            getProgramsResponse.setXMLString(outResp);
            return getProgramsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getPrograms",
                                    ex.getMessage(),
                                    getPrograms)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo", localPart = "getLocations")
    @ResponsePayload
    public GetLocationsResponse getLocations(@RequestPayload GetLocations getLocations)
            throws JsonProcessingException {

        HttpEntity<GetLocations> payload = new HttpEntity<>(getLocations, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/locations");

        try {
            HttpEntity<Locations> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Locations.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getLocations")));

            GetLocationsResponse getLocationsResponse = new GetLocationsResponse();
            LocationsOuter outResp = new LocationsOuter();
            LocationsInner inResp = new LocationsInner();
            inResp.setLocations(resp.getBody());
            outResp.setLocations(inResp);
            getLocationsResponse.setXMLString(outResp);
            return getLocationsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getLocations",
                                    ex.getMessage(),
                                    getLocations)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo", localPart = "getConditions")
    @ResponsePayload
    public GetConditionsResponse getConditions(@RequestPayload GetConditions getConditions)
            throws JsonProcessingException {

        HttpEntity<GetConditions> payload = new HttpEntity<>(getConditions, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/conditions");

        try {
            HttpEntity<Conditions> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Conditions.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getConditions")));
            GetConditionsResponse getConditionsResponse = new GetConditionsResponse();
            ConditionsOuter outResp = new ConditionsOuter();
            ConditionsInner inResp = new ConditionsInner();
            inResp.setConditions(resp.getBody());
            outResp.setConditions(inResp);
            getConditionsResponse.setXMLString(outResp);
            return getConditionsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getConditions",
                                    ex.getMessage(),
                                    getConditions)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo",
            localPart = "getOrdersConditions")
    @ResponsePayload
    public GetOrdersConditionsResponse getOrdersConditions(
            @RequestPayload GetOrdersConditions getOrdersConditions)
            throws JsonProcessingException {

        HttpEntity<GetOrdersConditions> payload =
                new HttpEntity<>(getOrdersConditions, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/order-conditions");

        try {
            HttpEntity<OrdersConditions> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            OrdersConditions.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getOrdersConditions")));
            GetOrdersConditionsResponse getOrdersConditionsResponse =
                    new GetOrdersConditionsResponse();
            OrdersConditionsOuter outResp = new OrdersConditionsOuter();
            OrdersConditionsInner inResp = new OrdersConditionsInner();
            inResp.setOrdersConditions(resp.getBody());
            outResp.setOrdersConditions(inResp);
            getOrdersConditionsResponse.setXMLString(outResp);
            return getOrdersConditionsResponse;
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

    @PayloadRoot(namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo", localPart = "getDates")
    @ResponsePayload
    public GetDatesResponse getDates(@RequestPayload GetDates getDates)
            throws JsonProcessingException {

        HttpEntity<GetDates> payload = new HttpEntity<>(getDates, new HttpHeaders());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "information/dates");

        try {
            HttpEntity<Dates> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Dates.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getDates")));

            GetDatesResponse getDatesResponse = new GetDatesResponse();
            DatesOuter outResp = new DatesOuter();
            DatesInner inResp = new DatesInner();
            inResp.setDates(resp.getBody());
            outResp.setDates(inResp);
            getDatesResponse.setXMLString(outResp);
            return getDatesResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getDates",
                                    ex.getMessage(),
                                    getDates)));
            throw new ORDSException();
        }
    }
}
