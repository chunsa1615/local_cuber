package kr.co.cntt.scc.app.admin.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class register {
	private int totalCount;
	private int pagingNum;
	
	private String payDt;
	private String userName;
	private String payType;
	private String payAmount;
	private String deskName;
	private String RoomName;
	
	private String payStateType;
	//private String reservationStatus;
	//private String reservationStartDt;
	//private String reservationEndDt;
	//private String reservationNote;
	
	@Data
	public static class RegisterPay {
//		private String payDt = "2016.06.23";
//		private String name = "홍길동";
//		private String payType = "현금";
//		private String payAmount = "150000";
//		private String RoomName = "프라이빗큐브 2(남)";
//		private String deskName = "123";			
//		private String payStateType = "정상";
		private String payDt;
		private String name;
		private String payType;
		private String payAmount;
		private String roomName;
		private String deskName;			
		private String payStateType;
		@JsonIgnore
		private String orderId;
		
		private List<RegisterReservation> registerReservationList;
	}
	
	@Data
	public static class RegisterReservation {
		@JsonIgnore
		private String payDt;
		@JsonIgnore
		private String name;
		@JsonIgnore
		private String payType;
		@JsonIgnore
		private String payAmount;
		@JsonIgnore
		private String roomName;
		@JsonIgnore
		private String deskName;			
		@JsonIgnore
		private String payStateType;
		
		@JsonIgnore
		private String orderId;
		
		private String reservationStatus;
		private String deskStartDt;
		private String deskEndDt;
		private String reservationNote;	
	}
	

	
	@Data
	public static class Response {
		private int totalCount;
		private int pagingNum;
		
//		private String reservationDt;
//		private String userName;
//		private String payType;
//		private String payAmount;
//		private String deskName;
//		private String RoomName;		
//		private String payStateType;
		
		private List<RegisterPay> registerPayList;
		
		
	}
}

