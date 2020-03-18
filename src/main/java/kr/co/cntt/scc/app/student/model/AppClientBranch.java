package kr.co.cntt.scc.app.student.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

public class AppClientBranch {
	
	@Data
	public static class CenterListResponse {
		List<CenterList> centerList;
	}
	
	@Data
	@JsonInclude(Include.NON_NULL)
	public static class CenterList {
		private String branchNm;
		private String branchId;
		private Boolean registYn;
		private Boolean mainBranchYn;
		
		@JsonIgnore
		private String memberId;
	}
	
	@Data
	public static class Branch {
		String branchNm;
		String address1;
		String openDt;
		String tel;
		String weekdayOpen;
		String weekendOpen;
		String branchId;
		
		@JsonIgnore
		private Boolean reservationYn;
		@JsonIgnore		
		private Boolean singleYn;
		@JsonIgnore
		private Boolean multiYn;
		@JsonIgnore
		private Boolean privateYn;
		
	}

	@Data
	public static class Desk {
		String deskName;
		Integer deskType;
		private Boolean registYn;
		
	}
	
	@Data
	public static class DeskThum {
		String thumUrl;
	}
	
	
	@Data
	public static class Response {
		Branch branch;
		List<CenterList> centerList;
		List<Desk> deskList;
		List<DeskThum> deskThumList;
	}
	
}
