package ca.bc.gov.open.icon.models;

import java.io.Serializable;
import lombok.Data;

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
    private String pacLocationCd;
    private String outReason;
    private String newerSequence;
    private String computerSystemCd;
    private String isActive;
    private String sysDate;
    private String fromCsNum;
    private String userId;
    private String mergeUserId;
    private String icsLocationCd;
    private String isIn;
    private String custodyCenter;
    private String livingUnit;
    // to accept the status if update process cancels
    private String status;
}
