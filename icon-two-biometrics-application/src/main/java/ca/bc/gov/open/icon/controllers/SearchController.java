package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.bcs.*;
import ca.bc.gov.open.icon.biometrics.Search;
import ca.bc.gov.open.icon.biometrics.StartSearch;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@Slf4j
public class SearchController {

    @Value("${icon.bsc-host}")
    private String bcsHost = "https://127.0.0.1/";

    @Value("${icon.online-service-id}")
    private String onlineServiceId;

    private final WebServiceTemplate soapTemplate;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    public SearchController(
            WebServiceTemplate soapTemplate, ObjectMapper objectMapper, ModelMapper modelMapper) {
        this.soapTemplate = soapTemplate;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
    }

    @PayloadRoot(
            namespace = "ICON2_Biometrics.Source.Biometrics.ws.provider:Biometrics",
            localPart = "startSearch")
    @ResponsePayload
    public ca.bc.gov.open.icon.biometrics.StartSearchResponse startSearch(
            @RequestPayload StartSearch startSearch) throws JsonProcessingException {
        try {
            ca.bc.gov.open.icon.bcs.StartSearch startSearchBCS =
                    new ca.bc.gov.open.icon.bcs.StartSearch();
            StartSearchRequest startSearchRequest = new StartSearchRequest();
            startSearchRequest.setRequesterUserId(startSearch.getRequestorUserId());
            startSearchRequest.setOnlineServiceId(onlineServiceId);
            startSearchRequest.setActiveOnly(
                    ActiveCodeRequest.fromValue(startSearch.getActiveOnly()));
            startSearchRequest.setRequesterAccountTypeCode(
                    BCeIDAccountTypeCode.fromValue(startSearch.getRequestorType()));

            startSearchBCS.setRequest(startSearchRequest);

            ca.bc.gov.open.icon.bcs.StartSearchResponse bcsResp =
                    (ca.bc.gov.open.icon.bcs.StartSearchResponse)
                            soapTemplate.marshalSendAndReceive(bcsHost, startSearchBCS);

            if (!bcsResp.getStartSearchResult().getCode().equals(ResponseCode.SUCCESS)) {
                throw new RuntimeException(
                        "Failed to start BCS search "
                                + bcsResp.getStartSearchResult().getMessage());
            }

            ca.bc.gov.open.icon.biometrics.StartSearchResponse out =
                    new ca.bc.gov.open.icon.biometrics.StartSearchResponse();

            Search search =
                    modelMapper.map(bcsResp.getStartSearchResult().getSearch(), Search.class);
            out.setSearch(search);

            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Processing failed",
                                    "startSearch",
                                    ex.getMessage(),
                                    startSearch)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2_Biometrics.Source.Biometrics.ws.provider:Biometrics",
            localPart = "finishSearch")
    @ResponsePayload
    public ca.bc.gov.open.icon.biometrics.FinishSearchResponse finishSearch(
            @RequestPayload ca.bc.gov.open.icon.biometrics.FinishSearch finishSearch)
            throws JsonProcessingException {
        try {

            FinishSearch finishSearchBCS = new FinishSearch();
            FinishSearchRequest finishSearchRequest = new FinishSearchRequest();
            finishSearchRequest.setSearchID(finishSearch.getSearchId());
            finishSearchRequest.setRequesterUserId(finishSearch.getRequestorUserId());
            finishSearchRequest.setRequesterAccountTypeCode(
                    BCeIDAccountTypeCode.fromValue(finishSearch.getRequestorType()));
            finishSearchRequest.setOnlineServiceId(onlineServiceId);

            finishSearchBCS.setRequest(finishSearchRequest);

            FinishSearchResponse bcsResp =
                    (FinishSearchResponse)
                            soapTemplate.marshalSendAndReceive(bcsHost, finishSearchBCS);

            if (!bcsResp.getFinishSearchResult().getCode().equals(ResponseCode.SUCCESS)) {
                throw new RuntimeException(
                        "Failed to finish search " + bcsResp.getFinishSearchResult().getMessage());
            }

            ca.bc.gov.open.icon.biometrics.FinishSearchResponse out =
                    new ca.bc.gov.open.icon.biometrics.FinishSearchResponse();

            out.setActiveFlag(bcsResp.getFinishSearchResult().getActive().value());
            out.setClientId(bcsResp.getFinishSearchResult().getDID());
            out.setCredentialRef(bcsResp.getFinishSearchResult().getCredentialReference());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Processing failed",
                                    "finishSearch",
                                    ex.getMessage(),
                                    finishSearch)));
            throw new ORDSException();
        }
    }
}
