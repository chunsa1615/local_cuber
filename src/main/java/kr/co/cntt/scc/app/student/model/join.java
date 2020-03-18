package kr.co.cntt.scc.app.student.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.cntt.scc.app.student.model.AppClientBranch.CenterList;
import lombok.Data;

@Data
public class join {	
	
	String name;	
	String tel;
	
    @Data
    public static class RegistYnResponse  {
    	
    	@JsonIgnore
    	String autoIncrement;
    	
    	Boolean registYn;
    	String id;
    	String joinDt;
    	//String no;
    	//String role;
    	
    }
    
    @Data
    public static class Join02Response  {
    	Boolean registYn;
    }
    
    
    @Data
    public static class JoinResponse  {
    	Boolean registYn;
    	String name;
    	String no;
    	
    	List<CenterList> centerList;
    	
    }
    

}
