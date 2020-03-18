package kr.co.cntt.scc.app.student.model;

import java.util.List;

import lombok.Data;

public class Student {

	@Data
	public static class StudentObject {
		String name;
		String branchNm;
		String no;
		String deskName;
		String roomName;
		String studentId;
		Boolean mainChildYn;
	}
	
	@Data
	public static class Response {
		List<StudentObject> studentList;
	}
}
