package kr.co.cntt.scc.app.admin.model;

import java.sql.Time;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class notice {

	@JsonIgnore
    private Long id;

	private String noticeId;
	private String branchId;
    private String title;

    private String content;

    private String noticeDt;
    private Time noticeTm;
    
    @JsonIgnore
    private int useYn;
    
    private Date insertDt;

    private Date updateDt;
        
    //공지사항 detail list
	@Data
    public static class NoticeDetail {
    	private String noticeId;
    	private String title;
        private String noticeDt;

    }
	
	@Data
	public static class NoticeDetailResponse {
		private List<NoticeDetail> noticeList;
	}
	
    
    @Data
    public static class Response {
    	private String title;    	
        private String noticeDt;
        private String content;

        @JsonIgnore
        private String branchId;
        
    }
    

}
