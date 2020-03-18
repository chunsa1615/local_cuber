package kr.co.cntt.scc.app.admin.model;

import java.sql.Time;
import java.util.List;

import kr.co.cntt.scc.util.DateUtil;
import lombok.Data;

@Data
public class search {
	//private int total;
	//private int pagingNum;
    
	//private int num;

    //private String memberName;
    //private String roomName;
    
    //private Time noticeTm;
    @Data
    public static class SearchMember {
    	private String memberId;
    	private String no;
    	private String name;
        private String roomName;
        private String deskName;        
        private String reservationDt;
    }
	
    @Data
    public static class Response {

    	private int totalCount;
    	private int pagingNum;
    	
        private List<SearchMember> memberList;

    }
}
