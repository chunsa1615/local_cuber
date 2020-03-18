package kr.co.cntt.scc.app.admin.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class AppClientStudy {
	
	private Boolean succesYn;
	//private List<StudyPlan> studyPlan;
	private StudyPlan studyPlan;
	
	@Data
	public static class StudyPlan {
		
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String no;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private Integer role;
		
		@JsonIgnore
		@JsonInclude(JsonInclude.Include.NON_NULL)
		// private Boolean successYn;
		private Integer studyStatus;
		private String goalTime;
		private String startDt;
		private String endDt;
		
	}
	
	@Data
	public static class StudyHistory {
		
		private Integer studyStatus;
		private List<StudyList> studyPlanList;
		
	}
	
	@Data
	public static class StudyList {
		
		private String studyStatus;
		private String startDt;
		private String endDt;
		//private String studyPeriod;
		private String goalPerDay;
		private String timeRate;
		
	}
	
	@Data
	public static class GoalStatus {
		
		private String no;
		private Integer role;
		private String branchNm;
		private String name;
		//private Integer goalTime;
		private String goalTime;
		private String learningTime;
		private String startDt;
		private String endDt;
		private String timeRate;
		private String remainTime;
		private Integer studyStatus;
		
		private List<StudyList> studyPlanList;
		
		@JsonIgnore
		private String id;
		@JsonIgnore
		private Integer unConvlTime;
		@JsonIgnore
		private Integer unConvGtime;
		@JsonIgnore
		private Integer diffDays;
		@JsonIgnore
		private Integer diffHours;
		@JsonIgnore
		private String convDiff;
		@JsonIgnore
		private Integer progYn;
		
		
	}
	
	@Data
	public static class learnUpd {
		private Boolean succesYn;
	}

	
	
}
