package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.auth.*;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
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

        // fetch the inmost DeviceInfo layer
        var inner =
                getPreAuthorizeClient.getXMLString() != null
                                && getPreAuthorizeClient.getXMLString().getPreAuthorizeClient()
                                        != null
                                && getPreAuthorizeClient
                                                .getXMLString()
                                                .getPreAuthorizeClient()
                                                .getPreAuthorizeClient()
                                        != null
                        ? getPreAuthorizeClient
                                .getXMLString()
                                .getPreAuthorizeClient()
                                .getPreAuthorizeClient()
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
                            new RequestSuccessLog(
                                    "Request Success", objectMapper.writeValueAsString(inner))));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getPreAuthorizeClient")));

            var getPreAuthorizeClientResponse = new GetPreAuthorizeClientResponse();
            var outResp = new PreAuthorizeClientOut();
            var inResp = new PreAuthorizeClientInner();

            inResp.setPreAuthorizeClient(resp.getBody());
            outResp.setPreAuthorizeClient(inResp);
            getPreAuthorizeClientResponse.setXMLString(outResp);
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

        HttpEntity<GetHasFunctionalAbility> payload =
                new HttpEntity<>(getHasFunctionalAbility, new HttpHeaders());

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
                            new RequestSuccessLog(
                                    "Request Success",
                                    objectMapper.writeValueAsString(getHasFunctionalAbility))));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getHasFunctionalAbility")));

            var getHasFunctionalAbilityResponse = new GetHasFunctionalAbilityResponse();
            var outResp = new HasFunctionalAbilityOut();
            var inResp = new HasFunctionalAbilityInner();

            inResp.setHasFunctionalAbility(resp.getBody());
            outResp.setHasFunctionalAbility(inResp);
            getHasFunctionalAbilityResponse.setXMLString(outResp);
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
