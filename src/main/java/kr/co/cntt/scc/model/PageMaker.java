package kr.co.cntt.scc.model;

import lombok.Data;

@Data
public class PageMaker {

	private int totalCount;
	private int startPage;
	private int endPage;
	private boolean prev;
	private boolean next;
	
	private int displayPageNum = 10;
	
	private Page page;
	
	public void setPage(Page page) {
		this.page = page;
	}
	
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	
		calcData();
	}

	private void calcData() {
		
		endPage = (int) (Math.ceil(page.getPage() / (double) displayPageNum) * displayPageNum);
		
		startPage = (endPage - displayPageNum) +1;
		
		int tempEndPage = (int) (Math.ceil(totalCount / (double) page.getPerPageNum()));
		
		if (endPage > tempEndPage) {
			endPage = tempEndPage;
		}
		
		prev = startPage == 1 ? false : true;
		
		if (endPage > 0) {
			next = endPage * page.getPerPageNum() >= totalCount ? false : true;
		}
	}
	
}
