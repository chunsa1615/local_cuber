package kr.co.cntt.scc.app.admin.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.cntt.scc.util.DateUtil;
import lombok.Data;

@Data
public class administer {
	
	
	//금일 좌석현황
    @Data
    public static class CurDeskStatus {
//    	private String curDt = "2016.06.22";
//    	private int totalDesk = 100;
//      private int curDesk = 80;	
//    	private String curDeskRatio = "80%";    	
    	private String curDt = DateUtil.getDateStringAppAdmin(("now"));
    	private int totalDesk;
        private int curDesk;	
    	private Double curDeskRatio;
    }
    
    //전일 좌석현황
    @Data
    public static class BeforeDeskStatus {
//    	private String beforeDt = "2016.06.22";
//    	private String beforeDeskRatio = "78%";
    	private String beforeDt = DateUtil.getDateStringAppAdmin(("yesterday"));;
    	private Double beforeDeskRatio;
    }
    
    
    //입실율 현황
    @Data
    public static class DeskRatio {
    	private String deskType;
    	private Double DeskRatio;
    	
    	@JsonIgnore
    	private int count;
    }
    
    @Data
    public static class Response {
    	private CurDeskStatus curDeskStatus;
    	private BeforeDeskStatus beforeDeskStatus;
    	private List<DeskRatio> deskRatio;
    }
}