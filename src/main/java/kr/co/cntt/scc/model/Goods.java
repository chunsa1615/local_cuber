package kr.co.cntt.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.sql.Time;
import java.util.Date;

/**
 * Goods (대여 물품)
 *
 * Created by jslivane on 2017. 1. 18..
 */
@Data
@ToString(exclude = {"id"})
public class Goods {
    
    @JsonIgnore
    private Long id;
   
    private String branchId;

    private String goodsId;
    
    private String rentalType;
    
    private String rentalTypeNames;
    
    private String goodsNote;
        

    private Date insertDt;

    private Date updateDt;

}
