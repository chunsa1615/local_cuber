package kr.co.cntt.scc.app.student.model;

import java.util.List;

import lombok.Data;

@Data
public class CommonCode {

	List<Code> codeList;
	
	@Data
	public static class Code {
		String text;
		Integer value;
	}
}
