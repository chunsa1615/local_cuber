package kr.co.cntt.scc.app.admin.model;

import java.util.List;

import lombok.Data;

@Data
public class AppClientReservation {

	private List<ReservationHistory> reservationHistory;
	
	@Data
	public static class ReservationHistory {
		
		private String no;
		private Integer role;
		private String branchName;
		private String startDt;
		private String status;
		private String statusDesc;
		
	}
	
	@Data
	public static class UserBranchInfo {
		
		private String mainBranchId;
		private String name;
		private String memberId;
		private Integer transferYn;
		
	}
}
