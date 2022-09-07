package ca.bc.gov.open.icon.exceptions;

public class ServiceFaultException extends RuntimeException {

    private ServiceFault serviceFault;

    public ServiceFaultException(ServiceFault serviceFault) {
        super(
                "An error response was received from ORDS please check that your request is of valid form");
        this.serviceFault = serviceFault;
    }

    public ServiceFaultException(Throwable e, ServiceFault serviceFault) {
        super(
                "An error response was received from ORDS please check that your request is of valid form",
                e);
        this.serviceFault = serviceFault;
    }

    public ServiceFault getServiceFault() {
        return serviceFault;
    }

    public void setServiceFault(ServiceFault serviceFault) {
        this.serviceFault = serviceFault;
    }
}
