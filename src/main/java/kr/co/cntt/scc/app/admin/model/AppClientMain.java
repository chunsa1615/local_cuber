package kr.co.cntt.scc.app.admin.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class AppClientMain {


	private String branchName;
	private String job;
	private String entryInfo;
	private String attendanceRate;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String name;	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String parentsName;
	private CenterInfo centerInfo;
	private MainRank mainRank;
	private String imgUrl;
	private List<NoticeList> noticeList;
	
	@Data
	public static class CenterInfo {
		private String branchName;
		private String seatName;
		private String roomName;
	}
	
	@Data
	public static class MainRank {
		private String myRank;
		private String totalMember;
		
	}
	
	@Data
	public static class NoticeList {
		private String title;
		private String noticeId;
	}
	
	@Data
	public static class EntryInfo {
		private String entryDt;
		private String entryTm;
		private String entryType;
		private String entryDtOg;
	}

	
}