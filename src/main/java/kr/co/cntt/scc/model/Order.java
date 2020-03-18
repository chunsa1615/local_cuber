package kr.co.cntt.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.sql.Time;
import java.util.Date;

/**
 * Order 주문
 *
 * Created by jslivane on 2016. 9. 26..
 *
 */
@Data
@ToString(exclude = {"id", "useYn"})
public class Order {
    
    @JsonIgnore
    private Long id;

    private String branchId;

    private String orderId;

    private String memberId;

    private Date orderDt;

    private Time orderTm;

    private String orderNote;

    private int orderStatus;

    @JsonIgnore
    private int useYn;

    private Date insertDt;

    private Date updateDt;

}
