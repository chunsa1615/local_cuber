package kr.co.cntt.scc.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"id", "useYn"})
public class SafeService {
	
	
	//시퀀스		
	private int id;
	
	//인증타입
	private String authType;
	//인증번호
	private String authNum;
	//학부모 app Id
	private String parentsAppId;
	//학부모 이름
	private String parentsName;
	//학부모 no
	private String parentsNo;
	//학부모 id
	private String parentsId;	
	//학생 id
	private String studentId;
	//학생 app Id
	private String studentAppId;	
	//학생 이름
	private String studentName;
	//학생 no
	private String studentNo;
	//매장 Id
	private String branchId;
	//부모 폰번호
	private String parentsTel;
	//학생 폰번호
	private String studentTel;
	//예약상태
	private int status;

	//useYn
	private int useYn;
	
	//신청일자
	private Date startDt;
	//private String startDt;
	//입력일자
	private Date insertDt;
	//private String insertDt;
	//수정일자
	private Date updateDt;
	//private String updateDt;
	
}
