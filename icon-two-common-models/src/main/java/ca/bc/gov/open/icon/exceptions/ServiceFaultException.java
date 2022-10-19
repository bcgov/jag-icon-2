package ca.bc.gov.open.icon.exceptions;

public class ServiceFaultException extends RuntimeException {

    private Object error;

    public ServiceFaultException(Object error) {
        super(
                "An error response was received from ORDS please check that your request is of valid form");
        this.error = error;
    }

    public ServiceFaultException(Throwable e, Object error) {
        super(
                "An error response was received from ORDS please check that your request is of valid form",
                e);
        this.error = error;
    }

    public Object getServiceFault() {
        return error;
    }

    public void setServiceFault(Object error) {
        this.error = error;
    }

    public String getMessage(org.springframework.web.client.HttpServerErrorException ex) {
        String msg = ex.getMessage();
        var STATUS_MESSAGE = "status_message\":\"";
        var start = msg.indexOf(STATUS_MESSAGE);
        if (start != -1) {
            var end = msg.indexOf("\" }", start + STATUS_MESSAGE.length());
            msg = msg.substring(start + STATUS_MESSAGE.length(), end);
        }

        return msg;
    }

    public String getMessage(String reason) {
        int start = reason.indexOf("\"cause\": \"", 0);
        if (start != -1) {
            int end = reason.indexOf(",<EOL>", start + 1);
            if (end != -1) {
                reason = reason.substring(start + 10, end - 1);
            }
        }

        return reason;
    }
}
