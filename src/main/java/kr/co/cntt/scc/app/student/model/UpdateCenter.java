package kr.co.cntt.scc.app.student.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.cntt.scc.app.student.model.join.RegistYnResponse;
import lombok.Data;

public class UpdateCenter {

	@Data
    public static class Response  {
    	
    	Boolean successYn;
    	String branchNm;
    	String no;
    	//String no;
    	//String role;
    	
    }
}
