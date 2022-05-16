package ca.bc.gov.open.icon.models;

import java.time.Instant;
import lombok.Data;

@Data
public class HealthServicePub {
    private String csNum;
    private String hsrId;
    private String location;
    private Instant requestDate;
    private String healthRequest;
    private String pacId;
}
