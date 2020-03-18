package kr.co.cntt.scc.app.admin.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class smsCertify {
	
    @Data
    public static class Response  {
    	
    	private String tel;
    	@JsonInclude(JsonInclude.Include.NON_NULL)
    	private Boolean memberYn;
    	@JsonInclude(JsonInclude.Include.NON_NULL)
    	private Boolean authYn;
    	
    	@JsonIgnore
    	private String authNum;
    }
	
}
