package kr.co.cntt.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.sql.Time;
import java.util.Date;

/**
 * Pay 지불(지출), 매입/매출
 *
 * Created by jslivane on 2016. 6. 2..
 */
@Data
@ToString(exclude = {"id", "useYn"})
public class Rental {
    
    //@JsonIgnore
    private Long id;

    private String branchId;

    private String goodsId;
    private String goodsIds;
    
    private String rentalTagId;
    private String rentalId;

    private String memberId;

    private String rentalType;
    private String rentalTypeNames;
    
    private int rentalStateType;


    
    @JsonIgnore
    private int useYn;

    private String rentalNote;

    private Date insertDt;

    private Date updateDt;

    private Date rentalDt;

    private Time rentalTm;
}
