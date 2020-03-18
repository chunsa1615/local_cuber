package kr.co.cntt.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.sql.Time;
import java.util.Date;

/**
 * Reservation 예약
 *
 * Created by jslivane on 2016. 8. 12..
 *
 */
@Data
@ToString(exclude = {"id", "useYn"})
public class Reservation {
    
    @JsonIgnore
    private Long id;

    private String branchId;

    private String orderId;

    private String reservationId;

    private String memberId;
    
    private String memberNo;

    private String deskId;
    
    private int reservationNum;

    private Date deskStartDt;

    private Time deskStartTm;

    private Date deskEndDt;
    
    private int deskStartDtTimeStamp;
    
    private int deskEndDtTimeStamp;

    private Time deskEndTm;

    @JsonIgnore
    private int useYn;

    private Date reservationDt;

    private Time reservationTm;

    private String reservationNote;

    private int reservationStatus;

    private Date insertDt;

    private Date updateDt;
    
    private int checkoutYn;
    
    private int dayGap;

}
