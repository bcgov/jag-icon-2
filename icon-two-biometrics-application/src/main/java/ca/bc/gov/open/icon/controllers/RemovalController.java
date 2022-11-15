package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.configuration.SoapConfig.ACCOUNT_TYPE_FIVE;
import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.bcs.*;
import ca.bc.gov.open.icon.biometrics.*;
import ca.bc.gov.open.icon.configuration.SoapConfig;
import ca.bc.gov.open.icon.exceptions.APIThrownException;
import ca.bc.gov.open.icon.iis.*;
import ca.bc.gov.open.icon.iis.BCeIDAccountTypeCode;
import ca.bc.gov.open.icon.iis.IssuanceToken;
import ca.bc.gov.open.icon.iis.ResponseCode;
import ca.bc.gov.open.icon.ips.*;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@Slf4j
public class RemovalController {

    @Value("${icon.iis-host}")
    private String iisHost = "https://127.0.0.1/";

    @Value("${icon.ips-host}")
    private String ipsHost = "https://127.0.0.1/";

    @Value("${icon.bsc-host}")
    private String bcsHost = "https://127.0.0.1/";

    @Value("${icon.ords-host}")
    private String ordsHost = "https://127.0.0.1/";

    @Value("${icon.online-service-id}")
    private String onlineServiceId;

    private final WebServiceTemplate soapTemplate;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public RemovalController(
            WebServiceTemplate soapTemplate,
            ObjectMapper objectMapper,
            ModelMapper modelMapper,
            RestTemplate restTemplate) {
        this.soapTemplate = soapTemplate;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "move")
    @ResponsePayload
    public MoveResponse move(@RequestPayload Move move) throws JsonProcessingException {
        try {
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(ordsHost + "client/did")
                            .queryParam("csNum", move.getCsNumTo());

            HttpEntity<Map<String, String>> andidResp =
                    restTemplate.exchange(
                            builder.build().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            new ParameterizedTypeReference<>() {});

            String did = Objects.requireNonNull(andidResp.getBody()).getOrDefault("andid", "");

            RegisterIndividual registerIndividual = new RegisterIndividual();
            RegisterIndividualRequest registerIndividualRequest = new RegisterIndividualRequest();
            registerIndividualRequest.setOnlineServiceId(onlineServiceId);
            registerIndividualRequest.setRequesterUserId(move.getRequestorUserId());
            registerIndividualRequest.setAccountType(ACCOUNT_TYPE_FIVE);
            registerIndividualRequest.setRequesterAccountTypeCode(
                    BCeIDAccountTypeCode.fromValue(move.getRequestorType()));
            registerIndividual.setRequest(registerIndividualRequest);

            RegisterIndividualResponse registerIndividualResponse =
                    (RegisterIndividualResponse)
                            soapTemplate.marshalSendAndReceive(iisHost, registerIndividual);

            if (!registerIndividualResponse
                    .getRegisterIndividualResult()
                    .getCode()
                    .equals(ResponseCode.SUCCESS)) {
                throw new APIThrownException(
                        "Failed to register individual "
                                + registerIndividualResponse
                                        .getRegisterIndividualResult()
                                        .getMessage());
            }

            String idRef = registerIndividualResponse.getRegisterIndividualResult().getIdRef();

            Link link = new Link();
            LinkRequest linkRequest = new LinkRequest();
            linkRequest.setRequesterUserId(move.getRequestorUserId());
            linkRequest.setDID(did);
            linkRequest.setOnlineServiceId(onlineServiceId);
            linkRequest.setIdRef(idRef);
            linkRequest.setRequesterAccountTypeCode(
                    ca.bc.gov.open.icon.ips.BCeIDAccountTypeCode.fromValue(
                            move.getRequestorType()));
            link.setRequest(linkRequest);

            LinkResponse linkResponse =
                    (LinkResponse) soapTemplate.marshalSendAndReceive(ipsHost, link);

            if (!linkResponse
                    .getLinkResult()
                    .getCode()
                    .equals(ca.bc.gov.open.icon.ips.ResponseCode.SUCCESS)) {
                throw new APIThrownException(
                        "Failed to link ips " + linkResponse.getLinkResult().getMessage());
            }

            idRef = getRefId(move.getRequestorUserId(), did, null);

            RebindCredential rebindCredential = new RebindCredential();
            RebindCredentialRequest rebindCredentialRequest = new RebindCredentialRequest();
            rebindCredentialRequest.setOnlineServiceId(onlineServiceId);
            rebindCredentialRequest.setRequesterUserId(move.getRequestorUserId());
            rebindCredentialRequest.setRequesterAccountTypeCode(
                    ca.bc.gov.open.icon.bcs.BCeIDAccountTypeCode.fromValue(
                            move.getRequestorType()));
            rebindCredentialRequest.setIDRefTo(idRef);
            rebindCredentialRequest.setCredentialReference(move.getCredentialRefFrom());
            rebindCredential.setRequest(rebindCredentialRequest);

            RebindCredentialResponse rebindCredentialResponse =
                    (RebindCredentialResponse)
                            soapTemplate.marshalSendAndReceive(bcsHost, rebindCredential);

            if (!rebindCredentialResponse
                    .getRebindCredentialResult()
                    .getCode()
                    .equals(ca.bc.gov.open.icon.bcs.ResponseCode.SUCCESS)) {
                throw new APIThrownException(
                        "Failed to rebind bcs "
                                + rebindCredentialResponse
                                        .getRebindCredentialResult()
                                        .getMessage());
            }

            // Grab the did with the from number to start clean up
            builder =
                    UriComponentsBuilder.fromHttpUrl(ordsHost + "client/did")
                            .queryParam("csNum", move.getCsNumFrom());

            andidResp =
                    restTemplate.exchange(
                            builder.build().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            new ParameterizedTypeReference<>() {});

            did = Objects.requireNonNull(andidResp.getBody()).getOrDefault("andid", "");

            idRef = getRefId(move.getRequestorUserId(), did, null);

            RemoveIndividual removeIndividual = new RemoveIndividual();
            RemoveIndividualRequest removeIndividualRequest = new RemoveIndividualRequest();
            removeIndividualRequest.setRequesterUserId(move.getRequestorUserId());
            removeIndividualRequest.setRequesterAccountTypeCode(
                    BCeIDAccountTypeCode.fromValue(move.getRequestorType()));
            rebindCredentialRequest.setIDRefTo(idRef);
            removeIndividualRequest.setOnlineServiceId(onlineServiceId);

            IssuanceToken issuanceToken = new IssuanceToken();
            issuanceToken.setIssuanceID(move.getIssuanceID());
            issuanceToken.setExpiry(move.getExpiry());
            issuanceToken.setEnrollmentURL(move.getEnrollmentURL());

            removeIndividual.setRequest(removeIndividualRequest);
            removeIndividual.setIssuance(issuanceToken);

            RemoveIndividualResponse removeIndividualResponse =
                    (RemoveIndividualResponse)
                            soapTemplate.marshalSendAndReceive(iisHost, removeIndividual);

            if (!removeIndividualResponse
                    .getRemoveIndividualResult()
                    .getCode()
                    .equals(ResponseCode.SUCCESS)) {
                throw new APIThrownException(
                        "Failed to remove individual "
                                + removeIndividualResponse
                                        .getRemoveIndividualResult()
                                        .getMessage());
            }

            Unlink unlink = new Unlink();
            UnlinkRequest unlinkRequest = new UnlinkRequest();
            unlinkRequest.setDID(did);
            unlinkRequest.setRequesterUserId(move.getRequestorUserId());
            unlinkRequest.setOnlineServiceId(onlineServiceId);
            unlinkRequest.setRequesterAccountTypeCode(
                    ca.bc.gov.open.icon.ips.BCeIDAccountTypeCode.fromValue(
                            move.getRequestorType()));

            ca.bc.gov.open.icon.ips.IssuanceToken issuanceTokenIPS =
                    new ca.bc.gov.open.icon.ips.IssuanceToken();
            issuanceTokenIPS.setIssuanceID(move.getIssuanceID());
            issuanceTokenIPS.setExpiry(move.getExpiry());
            issuanceTokenIPS.setEnrollmentURL(move.getEnrollmentURL());

            unlink.setIssuance(issuanceTokenIPS);
            unlink.setRequest(unlinkRequest);

            UnlinkResponse unlinkResponse =
                    (UnlinkResponse) soapTemplate.marshalSendAndReceive(ipsHost, unlink);

            if (!unlinkResponse
                    .getUnlinkResult()
                    .getCode()
                    .equals(ca.bc.gov.open.icon.ips.ResponseCode.SUCCESS)) {
                throw new APIThrownException(
                        "Failed to unlink " + unlinkResponse.getUnlinkResult().getMessage());
            }
            return new MoveResponse();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog("Processing failed", "move", ex.getMessage(), move)));
            throw handleError(ex, new ca.bc.gov.open.icon.biometrics.Error());
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "remove")
    @ResponsePayload
    public RemoveResponse remove(@RequestPayload Remove remove) throws JsonProcessingException {
        try {
            DestroyCredential destroyCredential = new DestroyCredential();
            DestroyCredentialRequest destroyCredentialRequest = new DestroyCredentialRequest();
            destroyCredentialRequest.setCredentialReference(remove.getCredentialRef());
            destroyCredentialRequest.setOnlineServiceId(onlineServiceId);
            destroyCredentialRequest.setRequesterAccountTypeCode(
                    ca.bc.gov.open.icon.bcs.BCeIDAccountTypeCode.VOID);

            ca.bc.gov.open.icon.bcs.IssuanceToken issuanceToken =
                    new ca.bc.gov.open.icon.bcs.IssuanceToken();
            issuanceToken.setIssuanceID(remove.getIssuanceID());
            issuanceToken.setExpiry(remove.getExpiry());
            issuanceToken.setEnrollmentURL(remove.getEnrollmentURL());

            destroyCredential.setRequest(destroyCredentialRequest);
            destroyCredential.setIssuance(issuanceToken);

            DestroyCredentialResponse destroyCredentialResponse =
                    (DestroyCredentialResponse)
                            soapTemplate.marshalSendAndReceive(bcsHost, destroyCredential);

            if (!destroyCredentialResponse
                    .getDestroyCredentialResult()
                    .getCode()
                    .equals(ca.bc.gov.open.icon.bcs.ResponseCode.SUCCESS)) {
                throw new APIThrownException(
                        "Failed to destroy credential "
                                + destroyCredentialResponse
                                        .getDestroyCredentialResult()
                                        .getMessage());
            }

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(ordsHost + "client/did")
                            .queryParam("csNum", remove.getCsNum());

            HttpEntity<Map<String, String>> andidResp =
                    restTemplate.exchange(
                            builder.build().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            new ParameterizedTypeReference<>() {});

            String did = Objects.requireNonNull(andidResp.getBody()).getOrDefault("andid", "");

            String refId = getRefId("", did, null);

            RemoveIndividual removeIndividual = new RemoveIndividual();
            RemoveIndividualRequest removeIndividualRequest = new RemoveIndividualRequest();
            removeIndividualRequest.setOnlineServiceId(onlineServiceId);
            removeIndividualRequest.setIdRef(refId);

            IssuanceToken issuanceTokenRemoval = new IssuanceToken();
            issuanceTokenRemoval.setIssuanceID(remove.getIssuanceID());
            issuanceTokenRemoval.setExpiry(remove.getExpiry());
            issuanceTokenRemoval.setEnrollmentURL(remove.getEnrollmentURL());

            removeIndividual.setRequest(removeIndividualRequest);
            removeIndividual.setIssuance(issuanceTokenRemoval);

            RemoveIndividualResponse removeIndividualResponse =
                    (RemoveIndividualResponse)
                            soapTemplate.marshalSendAndReceive(iisHost, removeIndividual);

            if (!removeIndividualResponse
                    .getRemoveIndividualResult()
                    .getCode()
                    .equals(ResponseCode.SUCCESS)) {
                throw new APIThrownException(
                        "Failed to remove individual "
                                + removeIndividualResponse
                                        .getRemoveIndividualResult()
                                        .getMessage());
            }

            Unlink unlink = new Unlink();
            UnlinkRequest unlinkRequest = new UnlinkRequest();
            unlinkRequest.setDID(did);
            unlinkRequest.setOnlineServiceId(onlineServiceId);

            ca.bc.gov.open.icon.ips.IssuanceToken issuanceTokenUnlink =
                    new ca.bc.gov.open.icon.ips.IssuanceToken();
            issuanceTokenUnlink.setIssuanceID(remove.getIssuanceID());
            issuanceTokenUnlink.setExpiry(remove.getExpiry());
            issuanceTokenUnlink.setEnrollmentURL(remove.getEnrollmentURL());

            unlink.setIssuance(issuanceTokenUnlink);
            unlink.setRequest(unlinkRequest);

            UnlinkResponse unlinkResponse =
                    (UnlinkResponse) soapTemplate.marshalSendAndReceive(ipsHost, unlink);

            if (!unlinkResponse
                    .getUnlinkResult()
                    .getCode()
                    .equals(ca.bc.gov.open.icon.ips.ResponseCode.SUCCESS)) {
                throw new RuntimeException(
                        "Failed to unlink ips " + unlinkResponse.getUnlinkResult().getMessage());
            }

            return new RemoveResponse();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Processing failed", "remove", ex.getMessage(), remove)));
            throw handleError(ex, new ca.bc.gov.open.icon.biometrics.Error());
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "removeIdentity")
    @ResponsePayload
    public RemoveIdentityResponse removeIdentity(@RequestPayload RemoveIdentity removeIdentity)
            throws JsonProcessingException {
        try {
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(ordsHost + "client/did")
                            .queryParam("csNum", removeIdentity.getCsNum());

            HttpEntity<Map<String, String>> andidResp =
                    restTemplate.exchange(
                            builder.build().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            new ParameterizedTypeReference<>() {});

            String did = Objects.requireNonNull(andidResp.getBody()).getOrDefault("andid", "");

            String refId =
                    getRefId(
                            removeIdentity.getRequestorUserId(),
                            did,
                            removeIdentity.getRequestorType());

            RemoveIndividual removeIndividual = new RemoveIndividual();
            RemoveIndividualRequest removeIndividualRequest = new RemoveIndividualRequest();
            removeIndividualRequest.setRequesterUserId(removeIdentity.getRequestorUserId());
            removeIndividualRequest.setRequesterAccountTypeCode(
                    ca.bc.gov.open.icon.iis.BCeIDAccountTypeCode.fromValue(
                            removeIdentity.getRequestorType()));
            removeIndividualRequest.setOnlineServiceId(onlineServiceId);
            removeIndividualRequest.setIdRef(refId);

            IssuanceToken issuanceTokenRemoval = new IssuanceToken();
            issuanceTokenRemoval.setIssuanceID(removeIdentity.getIssuanceID());
            issuanceTokenRemoval.setExpiry(removeIdentity.getExpiry());
            issuanceTokenRemoval.setEnrollmentURL(removeIdentity.getEnrollmentURL());

            removeIndividual.setRequest(removeIndividualRequest);
            removeIndividual.setIssuance(issuanceTokenRemoval);

            RemoveIndividualResponse removeIndividualResponse =
                    (RemoveIndividualResponse)
                            soapTemplate.marshalSendAndReceive(iisHost, removeIndividual);

            if (!removeIndividualResponse
                    .getRemoveIndividualResult()
                    .getCode()
                    .equals(ResponseCode.SUCCESS)) {
                throw new APIThrownException(
                        "Failed to remove individual "
                                + removeIndividualResponse
                                        .getRemoveIndividualResult()
                                        .getMessage());
            }

            Unlink unlink = new Unlink();
            UnlinkRequest unlinkRequest = new UnlinkRequest();
            unlinkRequest.setRequesterUserId(removeIdentity.getRequestorUserId());
            unlinkRequest.setRequesterAccountTypeCode(
                    ca.bc.gov.open.icon.ips.BCeIDAccountTypeCode.fromValue(
                            removeIdentity.getRequestorType()));

            unlinkRequest.setDID(did);
            unlinkRequest.setOnlineServiceId(onlineServiceId);

            ca.bc.gov.open.icon.ips.IssuanceToken issuanceTokenUnlink =
                    new ca.bc.gov.open.icon.ips.IssuanceToken();
            issuanceTokenUnlink.setIssuanceID(removeIdentity.getIssuanceID());
            issuanceTokenUnlink.setExpiry(removeIdentity.getExpiry());
            issuanceTokenUnlink.setEnrollmentURL(removeIdentity.getEnrollmentURL());

            unlink.setIssuance(issuanceTokenUnlink);
            unlink.setRequest(unlinkRequest);

            UnlinkResponse unlinkResponse =
                    (UnlinkResponse) soapTemplate.marshalSendAndReceive(ipsHost, unlink);
            if (!unlinkResponse
                    .getUnlinkResult()
                    .getCode()
                    .equals(ca.bc.gov.open.icon.ips.ResponseCode.SUCCESS)) {
                throw new APIThrownException(
                        "Failed to unlink ips " + unlinkResponse.getUnlinkResult().getMessage());
            }

            return new RemoveIdentityResponse();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Processing failed",
                                    "remove",
                                    ex.getMessage(),
                                    removeIdentity)));
            throw handleError(ex, new ca.bc.gov.open.icon.biometrics.Error());
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "removeTemplate")
    @ResponsePayload
    public RemoveTemplateResponse removeTemplate(@RequestPayload RemoveTemplate removeTemplate)
            throws JsonProcessingException {
        try {
            DestroyCredential destroyCredential = new DestroyCredential();
            DestroyCredentialRequest destroyCredentialRequest = new DestroyCredentialRequest();
            destroyCredentialRequest.setCredentialReference(removeTemplate.getCredentialRef());
            destroyCredentialRequest.setOnlineServiceId(onlineServiceId);
            destroyCredentialRequest.setRequesterAccountTypeCode(
                    ca.bc.gov.open.icon.bcs.BCeIDAccountTypeCode.VOID);

            ca.bc.gov.open.icon.bcs.IssuanceToken issuanceToken =
                    new ca.bc.gov.open.icon.bcs.IssuanceToken();
            issuanceToken.setIssuanceID(removeTemplate.getIssuanceID());
            issuanceToken.setExpiry(removeTemplate.getExpiry());
            issuanceToken.setEnrollmentURL(removeTemplate.getEnrollmentURL());

            destroyCredential.setRequest(destroyCredentialRequest);
            destroyCredential.setIssuance(issuanceToken);

            DestroyCredentialResponse destroyCredentialResponse =
                    (DestroyCredentialResponse)
                            soapTemplate.marshalSendAndReceive(bcsHost, destroyCredential);

            if (!destroyCredentialResponse
                    .getDestroyCredentialResult()
                    .getCode()
                    .equals(ca.bc.gov.open.icon.bcs.ResponseCode.SUCCESS)) {
                throw new APIThrownException(
                        "Failed to destroy credential "
                                + destroyCredentialResponse
                                        .getDestroyCredentialResult()
                                        .getMessage());
            }

            return new RemoveTemplateResponse();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Processing failed",
                                    "remove",
                                    ex.getMessage(),
                                    removeTemplate)));
            throw handleError(ex, new ca.bc.gov.open.icon.biometrics.Error());
        }
    }

    private String getRefId(String requesterId, String did, String type) {
        GetIdRef getIdRef = new GetIdRef();
        GetIdRefRequest getIdRefRequest = new GetIdRefRequest();
        getIdRefRequest.setRequesterUserId(requesterId);
        getIdRefRequest.setOnlineServiceId(onlineServiceId);
        getIdRefRequest.setDID(did);
        getIdRefRequest.setRequesterAccountTypeCode(
                StringUtils.hasLength(type)
                        ? ca.bc.gov.open.icon.ips.BCeIDAccountTypeCode.fromValue(type)
                        : ca.bc.gov.open.icon.ips.BCeIDAccountTypeCode.VOID);
        getIdRef.setRequest(getIdRefRequest);

        GetIdRefResponse getIdRefResponse =
                (GetIdRefResponse) soapTemplate.marshalSendAndReceive(ipsHost, getIdRef);

        if (!getIdRefResponse
                .getGetIdRefResult()
                .getCode()
                .equals(ca.bc.gov.open.icon.ips.ResponseCode.SUCCESS)) {
            throw new APIThrownException(
                    "Failed to get RefId " + getIdRefResponse.getGetIdRefResult().getMessage());
        }
        return getIdRefResponse.getGetIdRefResult().getIdRef();
    }
}
