package kr.co.cntt.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Reservation 예약
 *
 * Created by jslivane on 2016. 8. 12..
 *
 */
@Data
@ToString(exclude = {"id", "useYn"})
public class EntryRank {
    
    @JsonIgnore
    private Long id;

    private String branchId;
    
    private String branchNm;
   
    private String rank;
    
    private String cnt;
    
    private String no;

    private String memberId;
    
    private String name;

    private String businessDt;    
    
    private String hour;
    
    private String min;
    
    private String allHour;
    
    private String allMin;
    
    private String allTime;
    
    private String allRank;
    
    private String avgHour;
    
    private String avgMin;

    private int autoYn;

    @JsonIgnore
    private int useYn;

    private Date insertDt;

    private Date updateDt;

}
