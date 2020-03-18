package kr.co.cntt.scc.model;

import lombok.Data;

@Data
public class SalesData {

	//화면에서 날짜 받는 값
	private String from_date;
	private String to_date;
	private String name;
	private String insertdt;
	private String branchId;
	
	private String cate_sort;
	private String cate_nm;
	private String sub_cate_cd;
	// private String div_mons;
	
	private int jan;
	private int feb;
	private int mar;
	private int apr;
	private int may;
	private int jun;
	private int jul;
	private int aug;
	private int sep;
	private int oct;
	private int nov;
	private int dec;
	private int total;
	
	//좌석수
	private int seat_count;
	private String openDt;
	private String cur_year;
}
