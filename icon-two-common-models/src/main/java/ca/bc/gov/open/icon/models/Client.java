package ca.bc.gov.open.icon.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class Client implements Serializable {
    private String clientNumber;
    private String csNum;
    private String eventSeqNum;
    private String eventTypeCode;
    private String surname;
    private String givenName1;
    private String givenName2;
    private String birthDate;
    private String gender;
    private String photoGUID;
    private String probableDischargeDate;
    private String outLocation;
    private String outReason;
    private String NewerUpdate;
    private String LocaCd;
}
