package kr.co.cntt.scc.app.admin.model;

import lombok.Data;

@Data
public class settingSearch {
	private String version;
	private int updateYn;
	private int pushYn;
	
	@Data
	public static class Response {
		private String version = "1.0.0";
		private boolean updateYn = true;
		private boolean pushYn = true;

	}
	
	@Data
	public static class ResponseSetting {
		private boolean pushYn;
	}
	
	@Data
	public static class ResponseVersion {
		private String version; // = "1.0.0";
		private boolean updateYn; // = true;
	}
	
	@Data
	public static class ResponseLogout {
		private boolean successYn; // = true;
	}
}
