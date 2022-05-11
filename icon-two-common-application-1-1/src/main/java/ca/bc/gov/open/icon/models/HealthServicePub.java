package ca.bc.gov.open.icon.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HealthServicePub {
    private String csNum;
    private String hsrId;
    private String location;
    private String requestDate;
    private String healthRequest;
    private String pacId;
}
