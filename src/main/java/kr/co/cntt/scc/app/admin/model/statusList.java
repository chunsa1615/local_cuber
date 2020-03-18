package kr.co.cntt.scc.app.admin.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class statusList {
	private int totalCount;
	private int paginNum;	
	private int totalLearningTm;
	
	//입실정보
	@Data
	public static class StatusListDeskInfo {
		private String entryType;
		private String name;
		private String gender;
		private String roomName;
		private String deskName;
		private String school;
	}
	//입실정보
	@Data
	public static class StatusListEntry {
		private String entryDt;
		private String entryTm;
		private String entryType;
		@JsonIgnore
		private Date entryDtOg;
		@JsonIgnore
		private String businessDt;
	}
	
	//총시간을 구하기 위한 object
	@Data
	public static class TotalLearning {
		private Date entryDt;
		private String entryType;
		private String businessDt;
	}
	
	@Data
	public static class Response {
		private int totalCount;
		private int pagingNum;
		private int totalLearningTm;
		
		private StatusListDeskInfo statusListDeskInfo;
		
		private List<StatusListEntry> statusListEntryList;
		
	}
}
