package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.auth.*;
import ca.bc.gov.open.icon.exceptions.ORDSException;
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
public class AuthenticationController {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthenticationController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(
            namespace = "ICON2.Source.Authorization.ws.provider:AuthAuth",
            localPart = "getPreAuthorizeClient")
    @ResponsePayload
    public GetPreAuthorizeClientResponse getPreAuthorizeClient(
            @RequestPayload GetPreAuthorizeClient getPreAuthorizeClient)
            throws JsonProcessingException {

        var getPreAuthorizeClientDocument =
                XMLUtilities.convertReq(
                        getPreAuthorizeClient,
                        new GetPreAuthorizeClientDocument(),
                        "getPreAuthorizeClient");

        // fetch the inmost DeviceInfo layer
        var inner =
                getPreAuthorizeClientDocument.getXMLString() != null
                                && getPreAuthorizeClientDocument
                                                .getXMLString()
                                                .getPreAuthorizeClient()
                                        != null
                        ? getPreAuthorizeClientDocument.getXMLString().getPreAuthorizeClient()
                        : new PreAuthorizeClient();

        HttpEntity<PreAuthorizeClient> payload = new HttpEntity<>(inner, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "auth/pre-auth-client");

        try {
            HttpEntity<PreAuthorizeClient> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            PreAuthorizeClient.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getPreAuthorizeClient")));

            var getPreAuthorizeClientResponseDocument = new GetPreAuthorizeClientResponseDocument();
            var outResp = new PreAuthorizeClientOut();
            outResp.setPreAuthorizeClient(resp.getBody());
            getPreAuthorizeClientResponseDocument.setXMLString(outResp);

            var getPreAuthorizeClientResponse =
                    XMLUtilities.convertResp(
                            getPreAuthorizeClientResponseDocument,
                            new GetPreAuthorizeClientResponse(),
                            "getPreAuthorizeClientResponse");

            return getPreAuthorizeClientResponse;

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getHasFunctionalAbility",
                                    ex.getMessage(),
                                    inner)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.Authorization.ws.provider:AuthAuth",
            localPart = "getHasFunctionalAbility")
    @ResponsePayload
    public GetHasFunctionalAbilityResponse getHasFunctionalAbility(
            @RequestPayload GetHasFunctionalAbility getHasFunctionalAbility)
            throws JsonProcessingException {

        var getHasFunctionalAbilityDocument =
                XMLUtilities.convertReq(
                        getHasFunctionalAbility,
                        new GetHasFunctionalAbilityDocument(),
                        "getHasFunctionalAbility");

        HttpEntity<GetHasFunctionalAbilityDocument> payload =
                new HttpEntity<>(getHasFunctionalAbilityDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "auth/has-functional-ability");

        try {
            HttpEntity<HasFunctionalAbility> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            HasFunctionalAbility.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getHasFunctionalAbility")));

            var getHasFunctionalAbilityResponseDocument =
                    new GetHasFunctionalAbilityResponseDocument();
            var outResp = new HasFunctionalAbilityOut();
            outResp.setHasFunctionalAbility(resp.getBody());
            getHasFunctionalAbilityResponseDocument.setXMLString(outResp);

            var getHasFunctionalAbilityResponse =
                    XMLUtilities.convertResp(
                            getHasFunctionalAbilityResponseDocument,
                            new GetHasFunctionalAbilityResponse(),
                            "getHasFunctionalAbilityResponse");

            return getHasFunctionalAbilityResponse;

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getHasFunctionalAbility",
                                    ex.getMessage(),
                                    getHasFunctionalAbility)));
            throw new ORDSException();
        }
    }
}
