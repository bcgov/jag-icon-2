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

    public ServiceFault(String reason) {
        int start = reason.indexOf("\"cause\": \"", 0);
        if (start != -1) {
            int end = reason.indexOf(",<EOL>", start + 1);
            if (end != -1) {
                this.reason = reason.substring(start + 10, end - 1);
            }
        }
    }
}
