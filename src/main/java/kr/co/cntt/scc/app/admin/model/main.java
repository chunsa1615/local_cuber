package kr.co.cntt.scc.app.admin.model;

import java.sql.Time;
import java.util.Date;
import java.util.List;

import kr.co.cntt.scc.app.admin.model.notice.Response;
import kr.co.cntt.scc.util.DateUtil;
import kr.co.cntt.scc.model.ApiResponse;
import lombok.Data;

@Data
public class main {

    private String name;
    private Object DeskStatus;
    //private List<Object> notice;
    private Object notice;
    
    @Data
    public static class DeskStatus {
    	private String curDt = DateUtil.getDateStringAppAdmin(("now"));
    	private int totalDesk;
        private int curDesk;
    	private double curDeskRatio;    	
    }
    
    @Data
    public static class Notice {
    	private String noticeId;
    	private String noticeDt;
    	private String title;
    }
    
    @Data
    public static class Response  {

        private String name;
        private DeskStatus deskStatus;
        //private List<notice> noticeList;
        private List<Notice> noticeList;

    }
}
