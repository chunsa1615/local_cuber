package kr.co.cntt.scc.app.admin.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class statisticsUser {
	private int totalMemberCount;
	

	//통계관리 현황
	@Data
	public static class Statistics {
//		private String type = "세무직";
//		private String count = "60%";
		private String type;
		private String count;
	}
	

	
	//성인/학생별
	@Data
	public static class Student {
		//private String job = "고등학생";
		//private String ratio = "40%";
		private String job;
		private String ratio;
	} 
	
	//지역별 현황
	@Data
	public static class Area {
		
		//private String area = "증산동";
		//private String count = "60%";
		private String area;
		//private String count;
		
		public List<Student> studentList;
		
	}
		
	@Data
	public static class Apart {

		private String name;
		private String ratio;
	} 

	@Data
	public static class Response {		
		
		private int totalMemberCount;
		
		private List<Statistics> statisticsList;		

		
	}

	@Data
	public static class AreaResponse {				
		
		private int totalMemberCount;
		
		private List<Area> areaList;
		
		private List<Apart> apartList;
		
	}
	
	@Data
	public static class JobResponse {				
		
		private int totalMemberCount;
		
		private List<Student> JobList;
		
	}
	
	@Data
	public static class Time {				
		
		private String time;
		private String ratio;
		
	}
	@Data
	public static class TimeResponse {				
		
		private int totalMemberCount;
		
		private List<Time> timeList;
		
	}
}
