package kr.co.cntt.scc.app.admin.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class AppClientReport {
	
	private String no;
	private Integer role;
	private String name;
	private String branchNm;
	private String reservationDt;
	private String roomName;
	private String seatNo;
	private String attendanceRate;
	private String cntrRank; 
	private String totalRank;
	private String entryInfo;
	private String imgUrl;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String job;
	
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String parentsName;
	
	@JsonIgnore
	private String deskStartDt;
	@JsonIgnore
	private String deskEndDt;
	@JsonIgnore
	private Integer regPeriod;
	
	@Data
	public static class CntrRank {
		
		private String cntrRank; 
		private String cntrMember;
	}
	
}
