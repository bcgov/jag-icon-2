package ca.bc.gov.open.icon.exceptions;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Error",
        propOrder = {"reason"})
@XmlRootElement(name = "Error")
public class ServiceFault {
    @XmlElement(name = "Reason")
    private String reason = "";

    public ServiceFault() {
        this.reason = "";
    }

    public ServiceFault(org.springframework.web.client.HttpServerErrorException ex) {
        this.reason = "";
        String msg = ex.getMessage();
        var STATUS_MESSAGE = "status_message\":\"";
        var start = msg.indexOf(STATUS_MESSAGE);
        if (start != -1) {
            var end = msg.indexOf("\" }", start + STATUS_MESSAGE.length());
            msg = msg.substring(start + STATUS_MESSAGE.length(), end);
        }

        this.reason = msg;
    }

    public ServiceFault(String reason) {
        this.reason = reason;
        int start = reason.indexOf("\"cause\": \"", 0);
        if (start != -1) {
            int end = reason.indexOf(",<EOL>", start + 1);
            if (end != -1) {
                this.reason = reason.substring(start + 10, end - 1);
            }
        }
    }
}
