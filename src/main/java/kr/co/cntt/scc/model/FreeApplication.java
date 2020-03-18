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
public class FreeApplication {
    
    @JsonIgnore
    private Long id;

    private String branchId;

    private Date startDt;

    private int roomType;
    
    private String name;

    private int gender;

    private String birthDt;
    
    private String tel;

    private String school;
    
    private String email;
    
    private String cmpRoute;

    @JsonIgnore
    private int useYn;

    private Date insertDt;

    private Date updateDt;

    private String freeApplicationId;
}
