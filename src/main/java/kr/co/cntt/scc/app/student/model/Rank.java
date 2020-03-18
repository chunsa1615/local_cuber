package kr.co.cntt.scc.app.student.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class Rank {

	@Data
	public static class RankInfo {
		String rank;
		String name;
		String branchNm;
		Integer time;
		
		@JsonIgnore
		String memberId;
	}

	@Data
	public static class Response {
		String name;
		String area;
		String myRank;
		List<RankInfo> rankList;
	}

	
}

