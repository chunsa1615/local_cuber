package kr.co.cntt.scc.app.admin.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class accounting {
	private int totalCount;
	private int pagingNum;
	
	private String curAmount;
	private String monthAmount;		
	private String receivableAmount;
	
	//매출현황
	@Data
	public static class AccountingPay {
		private String payDt;
		private String payTm; 
		private String name;
		private String payInOutType;
		private String payAmount;
		private String totalAmount;
		private String payNote;
		private String expenseType;
		@JsonIgnore
		private String expenseGroup;
		@JsonIgnore
		private String expenseOption;
	}
	
	@Data
	public static class Response {
//		private int totalCount = 1;
//		private int pagingNum = 1;
//		
//		private String curAmount = "41340000";
//		private String monthAmount = "1125000";		
//		private String receivableAmount = "150000";
		
		private int totalCount;
		private int pagingNum;
		
		private String curAmount;
		private String monthAmount;		
		private String receivableAmount;
		private String cashAmount;
		private String cardAmount;
		
		private List<AccountingPay> accountingPayList;
	}
	
}
