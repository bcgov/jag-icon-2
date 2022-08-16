package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.auth.*;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.myinfo.*;
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

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getUserInfo")));

            var getUserInfoResponseDocument = new GetUserInfoResponseDocument();
            var outResp = new UserInfoOut();
            outResp.setUserInfo(resp.getBody());
            getUserInfoResponseDocument.setXMLString(outResp);

            var getUserInfoResponse =
                    XMLUtilities.convertResp(
                            getUserInfoResponseDocument,
                            new GetUserInfoResponse(),
                            "getUserInfoResponse");

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

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getDeviceInfo")));

            var getDeviceInfoResponseDocument = new GetDeviceInfoResponseDocument();
            var outResp = new DeviceInfoOut();
            outResp.setDeviceInfo(resp.getBody());
            getDeviceInfoResponseDocument.setXMLString(outResp);

            var getDeviceInfoResponse =
                    XMLUtilities.convertResp(
                            getDeviceInfoResponseDocument,
                            new GetDeviceInfoResponse(),
                            "getDeviceInfoResponse");

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

        var getOrdersDocument =
                XMLUtilities.convertReq(getOrders, new GetOrdersDocument(), "getOrders");

        HttpEntity<GetOrdersDocument> payload =
                new HttpEntity<>(getOrdersDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/orders");

        try {
            HttpEntity<Orders> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Orders.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getOrders")));

            GetOrdersResponseDocument getOrdersResponseDocument = new GetOrdersResponseDocument();
            OrdersOuter outResp = new OrdersOuter();
            outResp.setOrders(resp.getBody());
            getOrdersResponseDocument.setXMLString(outResp);

            var getOrdersResponse =
                    XMLUtilities.convertResp(
                            getOrdersResponseDocument,
                            new GetOrdersResponse(),
                            "getOrdersResponse");

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

        var getProgramsDocument =
                XMLUtilities.convertReq(getPrograms, new GetProgramsDocument(), "getPrograms");

        HttpEntity<GetProgramsDocument> payload =
                new HttpEntity<>(getProgramsDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/programs");

        try {
            HttpEntity<Programs> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Programs.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getPrograms")));
            GetProgramsResponseDocument getProgramsResponseDocument =
                    new GetProgramsResponseDocument();
            ProgramsOuter outResp = new ProgramsOuter();
            outResp.setPrograms(resp.getBody());
            getProgramsResponseDocument.setXMLString(outResp);

            var getProgramsResponse =
                    XMLUtilities.convertResp(
                            getProgramsResponseDocument,
                            new GetProgramsResponse(),
                            "getProgramsResponse");

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

        var getLocationsDocument =
                XMLUtilities.convertReq(getLocations, new GetLocationsDocument(), "getLocations");

        HttpEntity<GetLocationsDocument> payload =
                new HttpEntity<>(getLocationsDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/locations");

        try {
            HttpEntity<Locations> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Locations.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getLocations")));

            GetLocationsResponseDocument getLocationsResponseDocument =
                    new GetLocationsResponseDocument();

            LocationsOuter outResp = new LocationsOuter();
            outResp.setLocations(resp.getBody());
            getLocationsResponseDocument.setXMLString(outResp);

            var getLocationsResponse =
                    XMLUtilities.convertResp(
                            getLocationsResponseDocument,
                            new GetLocationsResponse(),
                            "getLocationsResponse");

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

        var getConditionsDocument =
                XMLUtilities.convertReq(
                        getConditions, new GetConditionsDocument(), "getConditions");

        HttpEntity<GetConditionsDocument> payload =
                new HttpEntity<>(getConditionsDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/conditions");

        try {
            HttpEntity<Conditions> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Conditions.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getConditions")));
            GetConditionsResponseDocument getConditionsResponseDocument =
                    new GetConditionsResponseDocument();
            ConditionsOuter outResp = new ConditionsOuter();
            outResp.setConditions(resp.getBody());
            getConditionsResponseDocument.setXMLString(outResp);

            var getConditionsResponse =
                    XMLUtilities.convertResp(
                            getConditionsResponseDocument,
                            new GetConditionsResponse(),
                            "getConditionsResponse");

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

        var getDatesDocument =
                XMLUtilities.convertReq(getDates, new GetDatesDocument(), "getDates");

        HttpEntity<GetDatesDocument> payload =
                new HttpEntity<>(getDatesDocument, new HttpHeaders());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "information/dates");

        try {
            HttpEntity<Dates> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Dates.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getDates")));

            GetDatesResponseDocument getDatesResponseDocument = new GetDatesResponseDocument();
            DatesOuter outResp = new DatesOuter();
            outResp.setDates(resp.getBody());
            getDatesResponseDocument.setXMLString(outResp);

            var getDatesResponse =
                    XMLUtilities.convertResp(
                            getDatesResponseDocument, new GetDatesResponse(), "getDatesResponse");

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
