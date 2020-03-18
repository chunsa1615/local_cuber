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
public class Pay {
    
    @JsonIgnore
    private Long id;

    private String branchId;

    private String orderId;       

    private String reservationId;
    
    private String payId;

    private String memberId;

    private Date payDt;
    private Time payTm;

    private int payType;
    private int PayStateType;
    private int payInOutType;
    
    private int payAmount;

    @JsonIgnore
    private int useYn;

    private String payNote;

    private Date insertDt;

    private Date updateDt;

}
