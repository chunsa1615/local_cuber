package kr.co.cntt.scc.app.admin.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class accountingMonth {
	private String totalAmount;
	
	//매출현황
	@Data
	public static class AccountingMonthPay {
		private String month;
		private String amount;
		@JsonIgnore
		private String year;
		
	}
	
	@Data
	public static class Year {
		private String year;				
				
		private List<AccountingMonthPay> accountingMonthPayList;
	}
	
	@Data
	public static class Response {
		private String totalAmount;
		
		private List<Year> yearList;
	}
}
