package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.bcs.*;
import ca.bc.gov.open.icon.biometrics.Deactivate;
import ca.bc.gov.open.icon.biometrics.DeactivateResponse;
import ca.bc.gov.open.icon.biometrics.Reactivate;
import ca.bc.gov.open.icon.biometrics.ReactivateResponse;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@Slf4j
public class ActivationController {
    @Value("${icon.bsc-host}")
    private String bcsHost = "https://127.0.0.1/";

    @Value("${icon.online-service-id}")
    private String onlineServiceId;

    private final WebServiceTemplate soapTemplate;
    private final ObjectMapper objectMapper;

    public ActivationController(WebServiceTemplate soapTemplate, ObjectMapper objectMapper) {
        this.soapTemplate = soapTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(
            namespace = "ICON2_Biometrics.Source.Biometrics.ws.provider:Biometrics",
            localPart = "reactivate")
    @ResponsePayload
    public ReactivateResponse reactivate(@RequestPayload Reactivate reactivate)
            throws JsonProcessingException {
        try {
            ReactivateCredential reactivateCredential = new ReactivateCredential();
            ReactivateCredentialRequest reactivateCredentialRequest =
                    new ReactivateCredentialRequest();
            reactivateCredentialRequest.setRequesterUserId(reactivate.getRequestorUserId());
            reactivateCredentialRequest.setCredentialReference(reactivate.getCredentialRef());
            reactivateCredentialRequest.setRequesterAccountTypeCode(
                    BCeIDAccountTypeCode.fromValue(reactivate.getRequestorType()));
            reactivateCredential.setRequest(reactivateCredentialRequest);

            ca.bc.gov.open.icon.bcs.ReactivateCredentialResponse reactivateCredentialResponse =
                    (ca.bc.gov.open.icon.bcs.ReactivateCredentialResponse)
                            soapTemplate.marshalSendAndReceive(bcsHost, reactivateCredential);

            if (!reactivateCredentialResponse
                    .getReactivateCredentialResult()
                    .getCode()
                    .equals(ResponseCode.SUCCESS)) {
                throw new RuntimeException(
                        "Failed to reactivate credential "
                                + reactivateCredentialResponse
                                        .getReactivateCredentialResult()
                                        .getMessage());
            }

            return new ReactivateResponse();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Processing failed",
                                    "reactivate",
                                    ex.getMessage(),
                                    reactivate)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(
            namespace = "ICON2_Biometrics.Source.Biometrics.ws.provider:Biometrics",
            localPart = "deactivate")
    @ResponsePayload
    public DeactivateResponse deactivate(@RequestPayload Deactivate deactivate)
            throws JsonProcessingException {

        try {
            DeactivateCredential deactivateCredential = new DeactivateCredential();
            DeactivateCredentialRequest deactivateCredentialRequest =
                    new DeactivateCredentialRequest();
            deactivateCredentialRequest.setOnlineServiceId(onlineServiceId);
            deactivateCredentialRequest.setRequesterUserId(deactivate.getRequestorUserId());
            deactivateCredentialRequest.setRequesterAccountTypeCode(
                    BCeIDAccountTypeCode.fromValue(deactivate.getRequestorType()));
            deactivateCredentialRequest.setCredentialReference(deactivate.getCredentialRef());
            deactivateCredential.setRequest(deactivateCredentialRequest);

            DestroyCredentialResponse destroyCredentialResponse =
                    (DestroyCredentialResponse)
                            soapTemplate.marshalSendAndReceive(bcsHost, deactivateCredential);

            if (!destroyCredentialResponse
                    .getDestroyCredentialResult()
                    .getCode()
                    .equals(ResponseCode.SUCCESS)) {
                throw new RuntimeException(
                        "Failed to destroy credential "
                                + destroyCredentialResponse
                                        .getDestroyCredentialResult()
                                        .getMessage());
            }
            return new DeactivateResponse();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Processing failed",
                                    "deactivate",
                                    ex.getMessage(),
                                    deactivate)));
            throw new ORDSException();
        }
    }
}
