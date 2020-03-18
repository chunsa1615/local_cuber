package kr.co.cntt.scc.app.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class AppAdminMember {

	 private String no;
	 private String name;
	
	 private String roomName;
	 private String deskName;
	 
	 private String tel;
	 private String telParent;
	 private String telEtc;
	
	 private int enterexitYes;    
	 private int smsYes;
	 
	 private String gender;
	 
	 
	 private String cabinet;
	 private String school;
	 
	 private String schoolGrade;
	 
	
	 private String address1;
	 private String memberNote;
	 	
     private String examType;
    

    @Data
    public static class Response {
        private String no;
        private String name;

        private String roomName;
        private String deskName;
        
        private String tel;
        private String telParent;
        private String telEtc;

        private int enterexitYes;    
        private int smsYes;

   	 	private String gender;

        
        private String cabinet;
        private String school;
	   	
	   	private String schoolGrade;
	   	

        private String address1;
        private String memberNote;
                
        private String examType;
        
    }
    
    

	    
	    //private String branchId;
}
