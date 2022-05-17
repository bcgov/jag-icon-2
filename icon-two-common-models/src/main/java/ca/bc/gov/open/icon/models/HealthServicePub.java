package ca.bc.gov.open.icon.models;

import java.io.Serializable;
import java.time.Instant;
import lombok.Data;

@Data
public class HealthServicePub implements Serializable {
    private String csNum;
    private String hsrId;
    private String location;
    private Instant requestDate;
    private String healthRequest;
    private String pacId;
}
