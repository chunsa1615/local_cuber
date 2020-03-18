package kr.co.cntt.scc.app.admin.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.cntt.scc.app.admin.service.UserInfoService;
import kr.co.cntt.scc.model.ApiResponse;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"id", "useYn"})
public class AppAdminUserInfo {
    @JsonIgnore
    private Long id;
    
    private String userId;
    
    private String branchId;    
    
    private String uuid;	//단말기 UUID
    
    private String os;		//운영체제 구분
    
    private String version;		//운영체제 버젼
    
    private String device;		//단말기 모델명
            
    private String pushId;		//푸시 ID
    
    private boolean pushYn;
    
    @JsonIgnore
    private int useYn;

    private Date insertDt;

    private Date updateDt;
    
    @Data
    public static class Response {

        private String userId;
        private String branchId;
        private boolean pushYn;
    }
	
}
