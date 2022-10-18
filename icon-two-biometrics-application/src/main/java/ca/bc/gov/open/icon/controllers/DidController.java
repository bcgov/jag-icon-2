package ca.bc.gov.open.icon.controllers;

import ca.bc.gov.open.icon.biometrics.GetDID;
import ca.bc.gov.open.icon.biometrics.GetDIDResponse;
import ca.bc.gov.open.icon.configuration.SoapConfig;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.ips.BCeIDAccountTypeCode;
import ca.bc.gov.open.icon.ips.GetDIDRequest;
import ca.bc.gov.open.icon.ips.ResponseCode;
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
public class DidController {

    @Value("${icon.ips-host}")
    private String ipsHost = "https://127.0.0.1/";

    @Value("${icon.online-service-id}")
    private String onlineServiceId;

    private final WebServiceTemplate soapTemplate;
    private final ObjectMapper objectMapper;

    public DidController(WebServiceTemplate soapTemplate, ObjectMapper objectMapper) {
        this.soapTemplate = soapTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getDID")
    @ResponsePayload
    public GetDIDResponse getDid(@RequestPayload GetDID getDID) throws JsonProcessingException {
        try {
            ca.bc.gov.open.icon.ips.GetDID getDIDIPS = new ca.bc.gov.open.icon.ips.GetDID();
            GetDIDRequest getDIDRequest = new GetDIDRequest();
            getDIDRequest.setRequesterUserId(getDID.getRequestorUserId());
            getDIDRequest.setIdRef(getDID.getIdRef());
            getDIDRequest.setRequesterAccountTypeCode(
                    BCeIDAccountTypeCode.fromValue(getDID.getRequestorType()));
            getDIDRequest.setOnlineServiceId(onlineServiceId);

            getDIDIPS.setRequest(getDIDRequest);

            ca.bc.gov.open.icon.ips.GetDIDResponse getDIDResponse =
                    (ca.bc.gov.open.icon.ips.GetDIDResponse)
                            soapTemplate.marshalSendAndReceive(ipsHost, getDIDIPS);

            if (!getDIDResponse.getGetDIDResult().getCode().equals(ResponseCode.SUCCESS)) {
                throw new RuntimeException(
                        "Failed to get did " + getDIDResponse.getGetDIDResult().getMessage());
            }

            GetDIDResponse out = new GetDIDResponse();
            out.setClientDID(getDIDResponse.getGetDIDResult().getDID());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Processing failed", "remove", ex.getMessage(), getDID)));
            throw new ORDSException();
        }
    }
}
