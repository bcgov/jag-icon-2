package ca.bc.gov.open.icon.models;

import java.io.Serializable;
import lombok.Data;

@Data
public class Fault implements Serializable {
    private String csNumber;
    private String surname;
    private String givenName1;
    private String givenName2;
    private String birthDate;
    private String gender;
    private String photoGuid;
    private String probableDischargeDate;
    private String outLocation;
    private String outReason;
    private String centre;
    private String livingUnit;
}
