package kr.co.cntt.scc.app.admin.common;

import lombok.Data;

@Data
public class ApiResult {
	

	//private String result_code; // 
	//private String result_message; //

	private Object header;
	private Object body;
	
	
	@Data
	public static class header {
		private String result_code; // 
		private String result_message; //	
	}
	
	/*
	public ApiResult() {
		this.result_code = ApiResultConsts.resultCode.SUCCESS.getCode();
		this.result_message = ApiResultConsts.resultCode.SUCCESS.getMessage();		
		
	}
	
	public ApiResult(ApiResultConsts.resultCode resultCode) {
		this.result_code = resultCode.getCode();
		this.result_message = resultCode.getMessage();
	}
	*/
	public ApiResult(Object result_data, header resultCode) {
		this.body = result_data;
		
		this.header = resultCode;
	}



	
}
