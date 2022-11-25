package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

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

            GetOrdersResponseDocument getOrdersResponseDocument = new GetOrdersResponseDocument();
            OrdersOuter outResp = new OrdersOuter();
            outResp.setOrders(resp.getBody());
            getOrdersResponseDocument.setXMLString(outResp);

            var getOrdersResponse =
                    XMLUtilities.convertResp(
                            getOrdersResponseDocument,
                            new GetOrdersResponse(),
                            "getOrdersResponse");

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getOrders")));

            return getOrdersResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getOrders",
                                    ex.getMessage(),
                                    getOrders)));
            throw handleError(ex, new ca.bc.gov.open.icon.myinfo.Error());
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

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getPrograms")));

            return getProgramsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getPrograms",
                                    ex.getMessage(),
                                    getPrograms)));
            throw handleError(ex, new ca.bc.gov.open.icon.myinfo.Error());
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

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getLocations")));

            return getLocationsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getLocations",
                                    ex.getMessage(),
                                    getLocations)));
            throw handleError(ex, new ca.bc.gov.open.icon.myinfo.Error());
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

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getConditions")));

            return getConditionsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getConditions",
                                    ex.getMessage(),
                                    getConditions)));
            throw handleError(ex, new ca.bc.gov.open.icon.myinfo.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo",
            localPart = "getOrdersConditions")
    @ResponsePayload
    public GetOrdersConditionsResponse getOrdersConditions(
            @RequestPayload GetOrdersConditions getOrdersConditions)
            throws JsonProcessingException {

        var getOrdersConditionsDocument =
                XMLUtilities.convertReq(
                        getOrdersConditions,
                        new GetOrdersConditionsDocument(),
                        "getOrdersConditions");

        HttpEntity<GetOrdersConditionsDocument> payload =
                new HttpEntity<>(getOrdersConditionsDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/order-conditions");

        try {
            HttpEntity<OrdersConditions> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            OrdersConditions.class);

            GetOrdersConditionsResponseDocument getOrdersConditionsResponseDocument =
                    new GetOrdersConditionsResponseDocument();
            OrdersConditionsOuter outResp = new OrdersConditionsOuter();
            outResp.setOrdersConditions(resp.getBody());
            getOrdersConditionsResponseDocument.setXMLString(outResp);

            var getOrdersConditionsResponse =
                    XMLUtilities.convertResp(
                            getOrdersConditionsResponseDocument,
                            new GetOrdersConditionsResponse(),
                            "getOrdersConditionsResponse");

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getOrdersConditions")));

            return getOrdersConditionsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getOrdersConditions",
                                    ex.getMessage(),
                                    getOrdersConditions)));
            throw handleError(ex, new ca.bc.gov.open.icon.myinfo.Error());
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

            GetDatesResponseDocument getDatesResponseDocument = new GetDatesResponseDocument();
            DatesOuter outResp = new DatesOuter();
            outResp.setDates(resp.getBody());
            getDatesResponseDocument.setXMLString(outResp);

            var getDatesResponse =
                    XMLUtilities.convertResp(
                            getDatesResponseDocument, new GetDatesResponse(), "getDatesResponse");

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getDates")));

            return getDatesResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getDates",
                                    ex.getMessage(),
                                    getDates)));
            throw handleError(ex, new ca.bc.gov.open.icon.myinfo.Error());
        }
    }
}
