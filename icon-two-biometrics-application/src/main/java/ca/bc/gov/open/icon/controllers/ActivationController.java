package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.bcs.*;
import ca.bc.gov.open.icon.biometrics.Deactivate;
import ca.bc.gov.open.icon.biometrics.DeactivateResponse;
import ca.bc.gov.open.icon.biometrics.Reactivate;
import ca.bc.gov.open.icon.biometrics.ReactivateResponse;
import ca.bc.gov.open.icon.configuration.SoapConfig;
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

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "reactivate")
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

            ReactivateCredentialResponse reactivateCredentialResponse =
                    (ReactivateCredentialResponse)
                            soapTemplate.marshalSendAndReceive(bcsHost, reactivateCredential);

            if (!reactivateCredentialResponse
                    .getReactivateCredentialResult()
                    .getCode()
                    .equals(ResponseCode.SUCCESS)) {
                throw new org.springframework.web.client.RestClientException(
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
            throw handleError(ex, new ca.bc.gov.open.icon.biometrics.Error());
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "deactivate")
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

            DeactivateCredentialResponse deactivateCredentialResponse =
                    (DeactivateCredentialResponse)
                            soapTemplate.marshalSendAndReceive(bcsHost, deactivateCredential);

            if (!deactivateCredentialResponse
                    .getDeactivateCredentialResult()
                    .getCode()
                    .equals(ResponseCode.SUCCESS)) {
                throw new org.springframework.web.client.RestClientException(
                        "Failed to destroy credential "
                                + deactivateCredentialResponse
                                        .getDeactivateCredentialResult()
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
            throw handleError(ex, new ca.bc.gov.open.icon.biometrics.Error());
        }
    }
}
