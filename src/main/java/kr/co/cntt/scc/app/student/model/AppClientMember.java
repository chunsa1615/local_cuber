package kr.co.cntt.scc.app.student.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class AppClientMember { //학생or부모 공통 model

	String appId;
	
	String no;
	
	String memberId;
	
	String branchId;
	
	String mainBranchId;
	
	String mainChildNo;
	
	String branchNm;
	
	String id;
	
	String pw;
	
	String name;
	
	//String studentName;
	
	//String StudentNickName;
	
	Integer role;
	
	Integer gender;
	
	String birthDt;
	
	String email;
	
	String bfTel;
	
	String tel;
	
	String address1;
	
	String address2;
	
	String addressDetail;
	
	Integer addressType;
	
	String postcode;
	
	String job;
	
	String interest;
	
    @JsonIgnore
    private int useYn;
    
    Integer pushYn;
    
    Date insertDt;
    
    Date updateDt;
    
    String imgUrl;
    
    Boolean smsYes;
    
    Boolean enterexitYes;
    
    Boolean personalYn;
    
    Boolean utilYn;

    Boolean transferYn;
	
}
