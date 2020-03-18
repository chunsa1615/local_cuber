package kr.co.cntt.scc.push;

import java.io.Serializable;

public class ApiResultPush implements Serializable {
	/**
	 * 세션 클러스터링을 하는 경우, 세션 유지를 위한 직렬화
	 */
	private static final long serialVersionUID = -5490028064343285606L;
	private String result_code;
	private String result_message;
	//private Object result = "";
	
	public String getResult_code() {
		return result_code;
	}
	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}
	public String getResult_message() {
		return result_message;
	}
	public void setResult_message(String result_message) {
		this.result_message = result_message;
	}
	/*public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}*/
	
}
