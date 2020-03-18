package kr.co.cntt.scc.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"id", "useYn"})
public class SeatReservation {
	
	@JsonIgnore
	
	
	//시퀀스
	private int id;
	
	//무료체험 Id
	private String applicationId;
	//학생ID
	private String appId;
	//memberId
	private String memberId;
	//매장ID
	private String branchId;
	//룸 형태
	private int roomType;
	//일반/학부모 구분
	private int role;
	//신청자 이름
	private String name;
	//신청자 성별
	private int gender;
	//신청자 생일
	private String birthDt;
	//신청자 폰번호
	private String tel;
	//신청자 email
	private String email;
	//예약상태
	private int status;
	//무료신청/좌석예약 구분
	private int type;
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
