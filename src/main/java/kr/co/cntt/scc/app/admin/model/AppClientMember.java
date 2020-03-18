package kr.co.cntt.scc.app.admin.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class AppClientMember {


	private String no;
	private Integer role;
	private String name;
	private String id;
	private int result_count;
	private String userPw;
	private String userId;
	@JsonIgnore
	private String branchId;
	@JsonIgnore
	private String memberId;
	@JsonIgnore
	private String mainChildNo;
	@JsonIgnore
	private Integer job;
	private String mainBranchId;
	private String branchNm;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String imgUrl;
	
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String parentsName;
	
	
	@Data
	public static class IdResponse {
		
		private String idResult;
		
	}
	
	@Data
	public static class  PwResponse {

		private Boolean pwResult;
		//private String name;
		//private String tel;
		//private String id;
		//private Integer role;
		
	}
	
	@Data
	public static class LoginResponse {

		private String no;
		private Integer role;
		private String mainChildNo;
	}

	@Data
	public static class DupResponse {

		private Boolean idDupYn;
		
	}
	
	@Data
	public static class PwChResponse {

		private Boolean pwChYn;
		
	}
	
}
