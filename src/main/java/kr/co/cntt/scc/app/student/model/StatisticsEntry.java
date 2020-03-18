package kr.co.cntt.scc.app.student.model;

import kr.co.cntt.scc.app.student.model.Result.Response;
import lombok.Data;

@Data
public class StatisticsEntry {

	@Data
	public static class Entry {
		String branchId;
		String branchNm;
		String no;
		String memberId;
		String memberNm;
		String businessDt;
		
		Integer totalTime;
		Integer totalCount;
	}
	
	@Data
	public static class Response {
		Boolean resultYn;
	}
	
}

