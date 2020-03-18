package kr.co.cntt.scc.app.student.model;

import java.util.List;

import lombok.Data;

@Data
public class ReservationBranch {
	
	String branchId;
	String branchNm;
	Boolean reservationYn;
	Boolean multiYn;
	Boolean singleYn;
	Boolean privateYn;

	
	@Data
	public static class Branch {
		String branchId;
		String branchNm;
		String startDt;
		String deskName;
		Integer roomType;
	}
	
	@Data
	public static class Response {
		List<Branch> branchRsvYnList;
	}
}
