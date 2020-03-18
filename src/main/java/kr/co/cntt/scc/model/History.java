package kr.co.cntt.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.cntt.scc.Constants;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Created by jslivane on 2016. 7. 7..
 */
@Data
@ToString(exclude = {"id"})
public class History {
    @JsonIgnore
    private Long id;

    private String branchId;
    private Date insertDt;
    private String insertId;

    private Constants.HistoryType type;

    private String memberId;

    private String orderId;
    private String reservationId;

    private String payId;
    private String rentalId;
    
    private String roomId;
    private String deskId;

    private String userId;

    private String d;

    public History(String branchId, Constants.HistoryType type, String d) {
        this.branchId = branchId;
        this.type = type;
        this.d = d;
    }

}

