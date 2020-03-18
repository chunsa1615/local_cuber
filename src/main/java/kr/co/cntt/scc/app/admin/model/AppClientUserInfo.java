package kr.co.cntt.scc.app.admin.model;

import java.util.List;

import lombok.Data;

@Data
//@JsonInclude(Include.NON_NULL)
public class AppClientUserInfo {

	private String no;
	private Integer role;
	private String id;
	private String name;
	private Integer gender;
	private String branchNm;
	private String imgUrl;
	private String tel;
	//private String smsTel;
	private Boolean smsYes;
	private String telParent ;
	private Boolean enterexitYes;
	private String address1;
	private String address2;
	private String postCode;
	private String addressDetail;
	//사물함 번호
	private String locker;
	private String job;
	private String interest;
	private String email;
	private String birthDt;
	private String mainChildNo;
	private List<StudentList> childList;
	
	
	@Data
	public static class StudentList {
		
		private String studentNo;
		private String studentName;
		
	}
	
	@Data
	public static class UpdateResult {
		private Boolean updResult;
	}

	@Data
	public static class StudentsUserInfo {
		
		private String name;
		private String no;
		private Integer role;
		private String id;
		private String branchNm;
		private String imgUrl;
		private String tel;
		private String telParent ;
		private Boolean smsYes;
		private Boolean enterexitYes;
		private String address1;
		private String address2;
		private String addressDetail;
		private String addressType;
		private String postcode;
		private Integer gender;
		private String locker;
		private String job;
		private String interest;
		private String email;
		private String birthDt;
		
	}
	
	@Data
	public static class ParentsUserInfo {
		
		private String name;
		private String no;
		private Integer role;
		private String id;
		private String branchNm;
		private String imgUrl;
		private String tel;
		private String telParent ;
		private Boolean smsYes;
		private Boolean enterexitYes;
		private String address1;
		private String address2;
		private String addressDetail;
		private String addressType;
		private String postcode;
		private Integer gender;
		private String locker;
		private String job;
		private String interest;
		private String email;
		private String birthDt;
		private String mainChildNo;
		private List<StudentList> childList;
	}
	
	@Data
	public static class SettingInfo {
		
		//private String no;
		//private Integer role;
		private String id;
		private String version;
		private Integer auto;
		private Integer pushYn;
	
	}
	
	@Data
	public static class AfterSet {
		
		private Integer auto;
		private Integer pushYn;
	
	}
	
}
