package kr.co.cntt.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.qos.logback.core.net.SyslogOutputStream;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Created by jslivane on 2016. 7. 7..
 */
@Data
@ToString(exclude = {"id"})
public class Notification {
	
    @JsonIgnore
    private Long id;

    private String branchId;
    private Date smsDt;

    //private String memberId;
    private String userId;    
    
    private String fromNumber;
    private String toNumber;

    private String msg;
    private String resultCode;
    private String cmid;  
   
    
}

