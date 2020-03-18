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
public class Expense {
    
    @JsonIgnore
    private Long id;

    private String branchId;

    private String expenseId;       

    private Date expenseDt;
    private Time expenseTm;

    private int expenseGroup;
    private int expenseOption;
    
    private int payType;
    private int payInOutType;
    
    private int expenseAmount;
    

    @JsonIgnore
    private int useYn;

    private String expenseNote;

    private Date insertDt;

    private Date updateDt;
    
    private String insertId;

}
