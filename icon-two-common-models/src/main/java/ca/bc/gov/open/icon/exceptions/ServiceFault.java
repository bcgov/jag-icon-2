package ca.bc.gov.open.icon.exceptions;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Error",
        propOrder = {"reason"})
@XmlRootElement(name = "Error")
public class ServiceFault {
    @XmlElement(name = "Reason")
    private String reason;

    public ServiceFault() {}

    public ServiceFault(String reason) {
        this.reason = reason;
    }
}
