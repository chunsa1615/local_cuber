package kr.co.cntt.scc.app.admin.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"id"})
public class AppAdminVersion {

    @JsonIgnore
    private Long id;
    
    private String version;		//최신 버젼값
    
    private boolean mandatory;	//업데이트 필수 여부
    
    @JsonIgnore
    private int useYn;

    private Date insertDt;

    private Date updateDt;
    
}
