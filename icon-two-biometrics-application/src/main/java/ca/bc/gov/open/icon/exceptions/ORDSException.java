package ca.bc.gov.open.icon.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(
        faultCode = FaultCode.CLIENT,
        faultStringOrReason =
                "An error response was received from the ICON2, please check that your request is of valid form")
public class ORDSException extends RuntimeException {
    public ORDSException() {
        super();
    }
}
