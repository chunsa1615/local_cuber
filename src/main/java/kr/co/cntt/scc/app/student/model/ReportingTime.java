package kr.co.cntt.scc.app.student.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ReportingTime {

	@Data
	public static class Time {
		String date;
		Integer time;
	}
	
//	@Data
//	public static class AllTime {
//		String date;
//		Integer time;
//	}
	
	@Data
	public static class Temp {
		String branchId;
		String memberId;		
	}	
	
	@Data
	public static class TimeTest {
		String businessDt;
		String branchId;
		String branchNm;
		String memberId;
		String memberNm;
		String no;		
		Integer time;
		boolean autoYn;
	}	
	@Data
	public static class ResponseTest {
		List<TimeTest> TimeTestList;
	}
	
	
	@Data
	public static class Response {
		List<Time> myTimeList;
		List<Time> allTimeList;
	}
}
