package kr.co.cntt.scc.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.cntt.scc.model.SalesData;
import kr.co.cntt.scc.service.SalesDataService;
import lombok.extern.slf4j.Slf4j;
 
/**
 * 메인 컨트롤러 Created by jslivane on 2016. 4. 4..
 */
@Controller
@Slf4j
@PreAuthorize("hasAuthority('admin') OR hasAuthority('user')")
public class ExcelController {
	
	@Autowired
	private SalesDataService sds;

	@RequestMapping("/admin/excelExtract")
	public void downloadExcelTest(
							@RequestParam(required = false) String branchId,
				            @RequestParam(required = false) String sPayStartDt,
				            @RequestParam(required = false) String sPayEndDt,
							HttpServletRequest request,
							HttpServletResponse response) throws Exception, ParseException ,IOException {
	
		String from_date = sPayStartDt;
		String to_date = sPayEndDt;
		
		List<SalesData> sdList = null;
		SalesData sd = new SalesData();
		Date date = new Date();
		
		if (from_date != null && to_date != null) {

			sdList = sds.select_account_data(from_date, to_date, branchId);

		} else {
			
		}
		//Workbook xlsWb = new HSSFWorkbook(); // Excel 2007 이전 버전
		XSSFWorkbook xlsWb = new XSSFWorkbook(); // Excel 2007 이상
		
		/*Sheet sheet1 = xlsWb.createSheet(cur_datetime+data);*/
		XSSFSheet sheet1 = xlsWb.createSheet("StudyCentreStatistics");
		sheet1.setDefaultColumnWidth((short) 12.50);
		sheet1.createFreezePane(2, 0);
		DataFormat hdf = xlsWb.createDataFormat();
		XSSFFont hf = xlsWb.createFont();
		hf.setFontHeightInPoints((short)10.5);
		hf.setBoldweight((short)org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);
		
		XSSFCellStyle cellStyle = null; CellStyle cellStyle2 = null; CellStyle cellStyle3 = null; CellStyle cellStyle4 = null; CellStyle cellStyle5 = null; CellStyle cellStyle6 = null;

		// Cellstyle DEFAULT
		cellStyle = xlsWb.createCellStyle();
		cellStyle.setBorderRight(cellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(cellStyle.BORDER_THIN);
		cellStyle.setBorderTop(cellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(cellStyle.BORDER_THIN);
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setDataFormat(hdf.getFormat("#,##0"));
		
		// Cellstyle YELLOW
		cellStyle2 = xlsWb.createCellStyle();
		cellStyle2.setBorderRight(cellStyle2.BORDER_THIN);
		cellStyle2.setBorderLeft(cellStyle2.BORDER_THIN);
		cellStyle2.setBorderTop(cellStyle2.BORDER_THIN);
		cellStyle2.setBorderBottom(cellStyle2.BORDER_THIN);
		cellStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle2.setFillForegroundColor(HSSFColor.YELLOW.index);
		cellStyle2.setFillPattern(cellStyle2.SOLID_FOREGROUND);

		// Cellstyle PALE_BLUE
		cellStyle3 = xlsWb.createCellStyle();
		cellStyle3.setBorderRight(cellStyle3.BORDER_THIN);
		cellStyle3.setBorderLeft(cellStyle3.BORDER_THIN);
		cellStyle3.setBorderTop(cellStyle3.BORDER_THIN);
		cellStyle3.setBorderBottom(cellStyle3.BORDER_THIN);
		cellStyle3.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle3.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		cellStyle3.setFillPattern(cellStyle3.SOLID_FOREGROUND);

		// Cellstyle GREY_25_PERCENT
		cellStyle4 = xlsWb.createCellStyle();
		cellStyle4.setBorderRight(cellStyle4.BORDER_THIN);
		cellStyle4.setBorderLeft(cellStyle4.BORDER_THIN);
		cellStyle4.setBorderTop(cellStyle4.BORDER_THIN);
		cellStyle4.setBorderBottom(cellStyle4.BORDER_THIN);
		cellStyle4.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle4.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle4.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		cellStyle4.setFillPattern(cellStyle4.SOLID_FOREGROUND);

		// Cellstyle LEMON_CHIFFON
		cellStyle5 = xlsWb.createCellStyle();
		cellStyle5.setBorderRight(cellStyle5.BORDER_THIN);
		cellStyle5.setBorderLeft(cellStyle5.BORDER_THIN);
		cellStyle5.setBorderTop(cellStyle5.BORDER_THIN);
		cellStyle5.setBorderBottom(cellStyle5.BORDER_THIN);
		cellStyle5.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle5.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle5.setFillForegroundColor(HSSFColor.LEMON_CHIFFON.index);
		cellStyle5.setFillPattern(cellStyle5.SOLID_FOREGROUND);
		
		// Cellstyle LIGHT_GREEN
		cellStyle6 = xlsWb.createCellStyle();
		cellStyle6.setBorderRight(cellStyle5.BORDER_MEDIUM);
		cellStyle6.setBorderLeft(cellStyle5.BORDER_MEDIUM);
		cellStyle6.setBorderTop(cellStyle5.BORDER_MEDIUM);
		cellStyle6.setBorderBottom(cellStyle5.BORDER_MEDIUM);
		cellStyle6.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle6.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle6.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		cellStyle6.setFillPattern(cellStyle5.SOLID_FOREGROUND);
		cellStyle6.setFont(hf);
		cellStyle6.setDataFormat(hdf.getFormat("#,##0"));
		
		XSSFRow row = null;
		XSSFCell cell = null;
		
		List<SalesData> branch_info = sds.select_branch_info(branchId);
		String branch_name = branch_info.get(0).getName();
		
		// 1
		row = sheet1.createRow(0);
		cell = row.createCell(0);
		cell = row.createCell(1);
		cell = row.createCell(2);
		// 2
		row = sheet1.createRow(1);
		cell = row.createCell(0); cell.setCellStyle(cellStyle2); cell.setCellValue("S/V");
		cell = row.createCell(1); cell.setCellValue(""); cell.setCellStyle(cellStyle);
		cell = row.createCell(3); cell.setCellStyle(cellStyle2); cell.setCellValue("OPEN");
		cell = row.createCell(4); cell.setCellValue(branch_info.get(0).getOpenDt()); cell.setCellStyle(cellStyle);
		cell = row.createCell(6); cell.setCellStyle(cellStyle2); cell.setCellValue("공사비");
		cell = row.createCell(7); cell.setCellStyle(cellStyle); cell.setCellValue("");
		// 3
		row = sheet1.createRow(2);
		cell = row.createCell(0); cell.setCellStyle(cellStyle2); cell.setCellValue("센터명");
		cell = row.createCell(1); cell.setCellValue(branch_info.get(0).getName()); cell.setCellStyle(cellStyle);
		cell = row.createCell(3); cell.setCellStyle(cellStyle2); cell.setCellValue("좌석수"); 
		cell = row.createCell(4); cell.setCellValue(branch_info.get(0).getSeat_count()); cell.setCellStyle(cellStyle);
		cell = row.createCell(6); cell.setCellStyle(cellStyle2); cell.setCellValue("Max매출");
		cell = row.createCell(7); cell.setCellStyle(cellStyle); cell.setCellValue("");
		// 4
		row = sheet1.createRow(3);
		cell = row.createCell(0); cell.setCellStyle(cellStyle2); cell.setCellValue("년도");
		cell = row.createCell(1); cell.setCellValue(branch_info.get(0).getCur_year()); cell.setCellStyle(cellStyle);
		cell = row.createCell(3); cell.setCellStyle(cellStyle2); cell.setCellValue("임대보증금"); 
		cell = row.createCell(4); cell.setCellValue(""); cell.setCellStyle(cellStyle);
		cell = row.createCell(6); cell.setCellStyle(cellStyle2); cell.setCellValue("");
		cell = row.createCell(7); cell.setCellStyle(cellStyle); cell.setCellValue("");
		//5
		row = sheet1.createRow(4);
		cell = row.createCell(0); cell.setCellStyle(cellStyle2); cell.setCellValue("형태");
		cell = row.createCell(1); cell.setCellValue(""); cell.setCellStyle(cellStyle);
		cell = row.createCell(3); cell.setCellStyle(cellStyle2); cell.setCellValue("로열티형태"); 
		cell = row.createCell(4); cell.setCellValue(""); cell.setCellStyle(cellStyle);
		//7 빈셀
		row = sheet1.createRow(5);
		cell = row.createCell(0);
		//8
		String[] month = { "항목","구분", "Jan", "Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec","Total" };
		int sales_total = 0; 
		int loyalty_total = 0;
		int rental_total = 0;
		int mgt_total = 0;
		int op_total = 0;
		int lab_total = 0;
		
		row = sheet1.createRow(6);
		for(int m=0; m<month.length; m++) {
			cell = row.createCell(m); cell.setCellStyle(cellStyle5); cell.setCellValue(month[m]);
		}
			
			sheet1.addMergedRegion(new CellRangeAddress(7, 11, 0, 0));
		
			for(int i=0; i <= 3 ; i++) {
				
				row = sheet1.createRow(i+7);
				sales_total = sdList.get(i).getJan() + sdList.get(i).getFeb() + sdList.get(i).getMar() + sdList.get(i).getApr() + sdList.get(i).getMay() + sdList.get(i).getJun()
							+ sdList.get(i).getJul() + sdList.get(i).getAug() + sdList.get(i).getSep() + sdList.get(i).getOct() + sdList.get(i).getNov() + sdList.get(i).getDec();
				
				if(sdList.get(i).getCate_sort().equals("0")) { cell = row.createCell(0); cell.setCellValue("매출"); cell.setCellStyle(cellStyle);
				if(sdList.get(i).getCate_sort().equals("0") && sdList.get(i).getSub_cate_cd().equals("10")) { cell = row.createCell(1); cell.setCellValue("멀티"); cell.setCellStyle(cellStyle); }
				if(sdList.get(i).getCate_sort().equals("0") && sdList.get(i).getSub_cate_cd().equals("20")) { cell = row.createCell(1); cell.setCellValue("싱글"); cell.setCellStyle(cellStyle); }
				if(sdList.get(i).getCate_sort().equals("0") && sdList.get(i).getSub_cate_cd().equals("30")) { cell = row.createCell(1); cell.setCellValue("프라이빗"); cell.setCellStyle(cellStyle); }
				if(sdList.get(i).getCate_sort().equals("0") && sdList.get(i).getSub_cate_cd().equals("99")) { cell = row.createCell(1); cell.setCellValue("기타"); cell.setCellStyle(cellStyle); }
				
				cell = row.createCell(2); cell.setCellValue(sdList.get(i).getJan()); cell.setCellStyle(cellStyle);
				cell = row.createCell(3); cell.setCellValue(sdList.get(i).getFeb()); cell.setCellStyle(cellStyle);
				cell = row.createCell(4); cell.setCellValue(sdList.get(i).getMar()); cell.setCellStyle(cellStyle);
				cell = row.createCell(5); cell.setCellValue(sdList.get(i).getApr()); cell.setCellStyle(cellStyle);
				cell = row.createCell(6); cell.setCellValue(sdList.get(i).getMay()); cell.setCellStyle(cellStyle);
				cell = row.createCell(7); cell.setCellValue(sdList.get(i).getJun()); cell.setCellStyle(cellStyle);
				cell = row.createCell(8); cell.setCellValue(sdList.get(i).getJul()); cell.setCellStyle(cellStyle);
				cell = row.createCell(9); cell.setCellValue(sdList.get(i).getAug()); cell.setCellStyle(cellStyle);
				cell = row.createCell(10); cell.setCellValue(sdList.get(i).getSep()); cell.setCellStyle(cellStyle);
				cell = row.createCell(11); cell.setCellValue(sdList.get(i).getOct()); cell.setCellStyle(cellStyle);
				cell = row.createCell(12); cell.setCellValue(sdList.get(i).getNov()); cell.setCellStyle(cellStyle);
				cell = row.createCell(13); cell.setCellValue(sdList.get(i).getDec()); cell.setCellStyle(cellStyle);
				cell = row.createCell(14); cell.setCellValue(sales_total); cell.setCellStyle(cellStyle);
				}
			}
			
				row = sheet1.createRow(11);
				cell = row.createCell(1); cell.setCellValue("매출 합계"); cell.setCellStyle(cellStyle);
				
				cell = row.createCell(2);  int jan_total = 0; jan_total= sdList.get(0).getJan() + sdList.get(1).getJan() + sdList.get(2).getJan() + sdList.get(3).getJan(); cell.setCellValue(jan_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(3);  int feb_total = 0; feb_total= sdList.get(0).getFeb() + sdList.get(1).getFeb() + sdList.get(2).getFeb() + sdList.get(3).getFeb(); cell.setCellValue(feb_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(4);  int mar_total = 0; mar_total= sdList.get(0).getMar() + sdList.get(1).getMar() + sdList.get(2).getMar() + sdList.get(3).getFeb(); cell.setCellValue(mar_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(5);  int apr_total = 0; apr_total= sdList.get(0).getApr() + sdList.get(1).getApr() + sdList.get(2).getApr() + sdList.get(3).getFeb(); cell.setCellValue(apr_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(6);  int may_total = 0; may_total= sdList.get(0).getMay() + sdList.get(1).getMay() + sdList.get(2).getMay() + sdList.get(3).getFeb(); cell.setCellValue(may_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(7);  int jun_total = 0; jun_total= sdList.get(0).getJun() + sdList.get(1).getJun() + sdList.get(2).getJun() + sdList.get(3).getFeb(); cell.setCellValue(jun_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(8);  int jul_total = 0; jul_total= sdList.get(0).getJul() + sdList.get(1).getJul() + sdList.get(2).getJul() + sdList.get(3).getFeb(); cell.setCellValue(jul_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(9);  int aug_total = 0; aug_total= sdList.get(0).getAug() + sdList.get(1).getAug() + sdList.get(2).getAug() + sdList.get(3).getFeb(); cell.setCellValue(aug_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(10);  int sep_total = 0; sep_total= sdList.get(0).getSep() + sdList.get(1).getSep() + sdList.get(2).getSep() + sdList.get(3).getFeb(); cell.setCellValue(sep_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(11);  int oct_total = 0; oct_total= sdList.get(0).getOct() + sdList.get(1).getOct() + sdList.get(2).getOct() + sdList.get(3).getFeb(); cell.setCellValue(oct_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(12);  int nov_total = 0; nov_total= sdList.get(0).getNov() + sdList.get(1).getNov() + sdList.get(2).getNov() + sdList.get(3).getFeb(); cell.setCellValue(nov_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(13);  int dec_total = 0; dec_total= sdList.get(0).getDec() + sdList.get(1).getDec() + sdList.get(2).getDec() + sdList.get(3).getFeb(); cell.setCellValue(dec_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(14);  int sales_annu_tot = jan_total + feb_total + mar_total + apr_total + may_total + jun_total + jul_total+ aug_total + sep_total + oct_total + nov_total + dec_total;
				cell.setCellValue(sales_annu_tot); cell.setCellStyle(cellStyle6);
				
			sheet1.addMergedRegion(new CellRangeAddress(12, 16, 0, 0));
			for(int j = 4; j <= 7; j++) {

				row = sheet1.createRow(j+8);
				mgt_total = sdList.get(j).getJan() + sdList.get(j).getFeb() + sdList.get(j).getMar() + sdList.get(j).getApr() + sdList.get(j).getMay() + sdList.get(j).getJun()
						+ sdList.get(j).getJul() + sdList.get(j).getAug() + sdList.get(j).getSep() + sdList.get(j).getOct() + sdList.get(j).getNov() + sdList.get(j).getDec();
				
				if(sdList.get(j).getCate_sort().equals("1")) { cell = row.createCell(0); cell.setCellValue("관리비"); cell.setCellStyle(cellStyle); 
				if(sdList.get(j).getCate_sort().equals("1") && sdList.get(j).getSub_cate_cd().equals("101")) { cell = row.createCell(1); cell.setCellValue("전기"); cell.setCellStyle(cellStyle); }
				if(sdList.get(j).getCate_sort().equals("1") && sdList.get(j).getSub_cate_cd().equals("102")) { cell = row.createCell(1); cell.setCellValue("가스"); cell.setCellStyle(cellStyle); }
				if(sdList.get(j).getCate_sort().equals("1") && sdList.get(j).getSub_cate_cd().equals("103")) { cell = row.createCell(1); cell.setCellValue("수도"); cell.setCellStyle(cellStyle); }
				if(sdList.get(j).getCate_sort().equals("1") && sdList.get(j).getSub_cate_cd().equals("109")) { cell = row.createCell(1); cell.setCellValue("기타"); cell.setCellStyle(cellStyle); }
				
				cell = row.createCell(2); cell.setCellValue(sdList.get(j).getJan()); cell.setCellStyle(cellStyle);
				cell = row.createCell(3); cell.setCellValue(sdList.get(j).getFeb()); cell.setCellStyle(cellStyle);
				cell = row.createCell(4); cell.setCellValue(sdList.get(j).getMar()); cell.setCellStyle(cellStyle);
				cell = row.createCell(5); cell.setCellValue(sdList.get(j).getApr()); cell.setCellStyle(cellStyle);
				cell = row.createCell(6); cell.setCellValue(sdList.get(j).getMay()); cell.setCellStyle(cellStyle);
				cell = row.createCell(7); cell.setCellValue(sdList.get(j).getJun()); cell.setCellStyle(cellStyle);
				cell = row.createCell(8); cell.setCellValue(sdList.get(j).getJul()); cell.setCellStyle(cellStyle);
				cell = row.createCell(9); cell.setCellValue(sdList.get(j).getAug()); cell.setCellStyle(cellStyle);
				cell = row.createCell(10); cell.setCellValue(sdList.get(j).getSep()); cell.setCellStyle(cellStyle);
				cell = row.createCell(11); cell.setCellValue(sdList.get(j).getOct()); cell.setCellStyle(cellStyle);
				cell = row.createCell(12); cell.setCellValue(sdList.get(j).getNov()); cell.setCellStyle(cellStyle);
				cell = row.createCell(13); cell.setCellValue(sdList.get(j).getDec()); cell.setCellStyle(cellStyle);
				cell = row.createCell(14); cell.setCellValue(mgt_total); cell.setCellStyle(cellStyle);
				}
				
				
				row = sheet1.createRow(16);
				cell = row.createCell(1); cell.setCellValue("관리비 합계"); cell.setCellStyle(cellStyle);
				
				cell = row.createCell(2);  jan_total= sdList.get(4).getJan() + sdList.get(5).getJan() + sdList.get(6).getJan() + sdList.get(7).getJan(); cell.setCellValue(jan_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(3);  feb_total= sdList.get(4).getFeb() + sdList.get(5).getFeb() + sdList.get(6).getFeb() + sdList.get(7).getFeb(); cell.setCellValue(feb_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(4);  mar_total= sdList.get(4).getMar() + sdList.get(5).getMar() + sdList.get(6).getMar() + sdList.get(7).getMar(); cell.setCellValue(mar_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(5);  apr_total= sdList.get(4).getApr() + sdList.get(5).getApr() + sdList.get(6).getApr() + sdList.get(7).getApr(); cell.setCellValue(apr_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(6);  may_total= sdList.get(4).getMay() + sdList.get(5).getMay() + sdList.get(6).getMay() + sdList.get(7).getMay(); cell.setCellValue(may_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(7);  jun_total= sdList.get(4).getJun() + sdList.get(5).getJun() + sdList.get(6).getJun() + sdList.get(7).getJun(); cell.setCellValue(jun_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(8);  jul_total= sdList.get(4).getJul() + sdList.get(5).getJul() + sdList.get(6).getJul() + sdList.get(7).getJul(); cell.setCellValue(jul_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(9);  aug_total= sdList.get(4).getAug() + sdList.get(5).getAug() + sdList.get(6).getAug() + sdList.get(7).getAug(); cell.setCellValue(aug_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(10); sep_total= sdList.get(4).getSep() + sdList.get(5).getSep() + sdList.get(6).getSep() + sdList.get(7).getSep(); cell.setCellValue(sep_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(11); oct_total= sdList.get(4).getOct() + sdList.get(5).getOct() + sdList.get(6).getOct() + sdList.get(7).getOct(); cell.setCellValue(oct_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(12); nov_total= sdList.get(4).getNov() + sdList.get(5).getNov() + sdList.get(6).getNov() + sdList.get(7).getNov(); cell.setCellValue(nov_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(13); dec_total= sdList.get(4).getDec() + sdList.get(5).getDec() + sdList.get(6).getDec() + sdList.get(7).getDec(); cell.setCellValue(dec_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(14);  int mgt_annu_tot = jan_total + feb_total + mar_total + apr_total + may_total + jun_total + jul_total+ aug_total + sep_total + oct_total + nov_total + dec_total;
				cell.setCellValue(mgt_annu_tot); cell.setCellStyle(cellStyle6);
			}
			
			sheet1.addMergedRegion(new CellRangeAddress(17, 21, 0, 0));
			for(int k = 8; k <= 11; k++) {

				row = sheet1.createRow(k+9);
				op_total = sdList.get(k).getJan() + sdList.get(k).getFeb() + sdList.get(k).getMar() + sdList.get(k).getApr() + sdList.get(k).getMay() + sdList.get(k).getJun()
						+ sdList.get(k).getJul() + sdList.get(k).getAug() + sdList.get(k).getSep() + sdList.get(k).getOct() + sdList.get(k).getNov() + sdList.get(k).getDec();
				
				if(sdList.get(k).getCate_sort().equals("2")) { cell = row.createCell(0); cell.setCellValue("운영비"); cell.setCellStyle(cellStyle); 
				if(sdList.get(k).getCate_sort().equals("2") && sdList.get(k).getSub_cate_cd().equals("201")) { cell = row.createCell(1); cell.setCellValue("수리비"); cell.setCellStyle(cellStyle); }
				if(sdList.get(k).getCate_sort().equals("2") && sdList.get(k).getSub_cate_cd().equals("202")) { cell = row.createCell(1); cell.setCellValue("소모품"); cell.setCellStyle(cellStyle); }
				if(sdList.get(k).getCate_sort().equals("2") && sdList.get(k).getSub_cate_cd().equals("203")) { cell = row.createCell(1); cell.setCellValue("청소비"); cell.setCellStyle(cellStyle); }
				if(sdList.get(k).getCate_sort().equals("2") && sdList.get(k).getSub_cate_cd().equals("209")) { cell = row.createCell(1); cell.setCellValue("기타"); cell.setCellStyle(cellStyle); }
				
				cell = row.createCell(2); cell.setCellValue(sdList.get(k).getJan()); cell.setCellStyle(cellStyle);
				cell = row.createCell(3); cell.setCellValue(sdList.get(k).getFeb()); cell.setCellStyle(cellStyle);
				cell = row.createCell(4); cell.setCellValue(sdList.get(k).getMar()); cell.setCellStyle(cellStyle);
				cell = row.createCell(5); cell.setCellValue(sdList.get(k).getApr()); cell.setCellStyle(cellStyle);
				cell = row.createCell(6); cell.setCellValue(sdList.get(k).getMay()); cell.setCellStyle(cellStyle);
				cell = row.createCell(7); cell.setCellValue(sdList.get(k).getJun()); cell.setCellStyle(cellStyle);
				cell = row.createCell(8); cell.setCellValue(sdList.get(k).getJul()); cell.setCellStyle(cellStyle);
				cell = row.createCell(9); cell.setCellValue(sdList.get(k).getAug()); cell.setCellStyle(cellStyle);
				cell = row.createCell(10); cell.setCellValue(sdList.get(k).getSep()); cell.setCellStyle(cellStyle);
				cell = row.createCell(11); cell.setCellValue(sdList.get(k).getOct()); cell.setCellStyle(cellStyle);
				cell = row.createCell(12); cell.setCellValue(sdList.get(k).getNov()); cell.setCellStyle(cellStyle);
				cell = row.createCell(13); cell.setCellValue(sdList.get(k).getDec()); cell.setCellStyle(cellStyle);
				cell = row.createCell(14); cell.setCellValue(op_total); cell.setCellStyle(cellStyle);
			}

				row = sheet1.createRow(21);
				
				cell = row.createCell(1); cell.setCellValue("운영비 합계"); cell.setCellStyle(cellStyle);
				cell = row.createCell(2);  jan_total= sdList.get(8).getJan() + sdList.get(9).getJan() + sdList.get(10).getJan() + sdList.get(11).getJan(); cell.setCellValue(jan_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(3);  feb_total= sdList.get(8).getFeb() + sdList.get(9).getFeb() + sdList.get(10).getFeb() + sdList.get(11).getFeb(); cell.setCellValue(feb_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(4);  mar_total= sdList.get(8).getMar() + sdList.get(9).getMar() + sdList.get(10).getMar() + sdList.get(11).getMar(); cell.setCellValue(mar_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(5);  apr_total= sdList.get(8).getApr() + sdList.get(9).getApr() + sdList.get(10).getApr() + sdList.get(11).getApr(); cell.setCellValue(apr_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(6);  may_total= sdList.get(8).getMay() + sdList.get(9).getMay() + sdList.get(10).getMay() + sdList.get(11).getMay(); cell.setCellValue(may_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(7);  jun_total= sdList.get(8).getJun() + sdList.get(9).getJun() + sdList.get(10).getJun() + sdList.get(11).getJun(); cell.setCellValue(jun_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(8);  jul_total= sdList.get(8).getJul() + sdList.get(9).getJul() + sdList.get(10).getJul() + sdList.get(11).getJul(); cell.setCellValue(jul_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(9);  aug_total= sdList.get(8).getAug() + sdList.get(9).getAug() + sdList.get(10).getAug() + sdList.get(11).getAug(); cell.setCellValue(aug_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(10); sep_total= sdList.get(8).getSep() + sdList.get(9).getSep() + sdList.get(10).getSep() + sdList.get(11).getSep(); cell.setCellValue(sep_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(11); oct_total= sdList.get(8).getOct() + sdList.get(9).getOct() + sdList.get(10).getOct() + sdList.get(11).getOct(); cell.setCellValue(oct_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(12); nov_total= sdList.get(8).getNov() + sdList.get(9).getNov() + sdList.get(10).getNov() + sdList.get(11).getNov(); cell.setCellValue(nov_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(13); dec_total= sdList.get(8).getDec() + sdList.get(9).getDec() + sdList.get(10).getDec() + sdList.get(11).getDec(); cell.setCellValue(dec_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(14);  int op_annu_tot = jan_total + feb_total + mar_total + apr_total + may_total + jun_total + jul_total+ aug_total + sep_total + oct_total + nov_total + dec_total;
				cell.setCellValue(op_annu_tot); cell.setCellStyle(cellStyle6);
			}
			
			sheet1.addMergedRegion(new CellRangeAddress(22, 27, 0, 0));			
			for(int l = 12; l <= 17; l++) {
				
				row = sheet1.createRow(l+10);
				lab_total = sdList.get(l).getJan() + sdList.get(l).getFeb() + sdList.get(l).getMar() + sdList.get(l).getApr() + sdList.get(l).getMay() + sdList.get(l).getJun()
						+ sdList.get(l).getJul() + sdList.get(l).getAug() + sdList.get(l).getSep() + sdList.get(l).getOct() + sdList.get(l).getNov() + sdList.get(l).getDec();
				
				if(sdList.get(l).getCate_sort().equals("3")) { cell = row.createCell(0); cell.setCellValue("인건비"); cell.setCellStyle(cellStyle); 
				if(sdList.get(l).getCate_sort().equals("3") && sdList.get(l).getSub_cate_cd().equals("301")) { cell = row.createCell(1); cell.setCellValue("책임총무"); cell.setCellStyle(cellStyle); }
				if(sdList.get(l).getCate_sort().equals("3") && sdList.get(l).getSub_cate_cd().equals("302")) { cell = row.createCell(1); cell.setCellValue("총무1"); cell.setCellStyle(cellStyle); }
				if(sdList.get(l).getCate_sort().equals("3") && sdList.get(l).getSub_cate_cd().equals("303")) { cell = row.createCell(1); cell.setCellValue("총무2"); cell.setCellStyle(cellStyle); }
				if(sdList.get(l).getCate_sort().equals("3") && sdList.get(l).getSub_cate_cd().equals("304")) { cell = row.createCell(1); cell.setCellValue("총무3"); cell.setCellStyle(cellStyle); }
				if(sdList.get(l).getCate_sort().equals("3") && sdList.get(l).getSub_cate_cd().equals("305")) { cell = row.createCell(1); cell.setCellValue("총무4"); cell.setCellStyle(cellStyle); }
				if(sdList.get(l).getCate_sort().equals("3") && sdList.get(l).getSub_cate_cd().equals("309")) { cell = row.createCell(1); cell.setCellValue("기타"); cell.setCellStyle(cellStyle); }
				
				cell = row.createCell(2); cell.setCellValue(sdList.get(l).getJan()); cell.setCellStyle(cellStyle);
				cell = row.createCell(3); cell.setCellValue(sdList.get(l).getFeb()); cell.setCellStyle(cellStyle);
				cell = row.createCell(4); cell.setCellValue(sdList.get(l).getMar()); cell.setCellStyle(cellStyle);
				cell = row.createCell(5); cell.setCellValue(sdList.get(l).getApr()); cell.setCellStyle(cellStyle);
				cell = row.createCell(6); cell.setCellValue(sdList.get(l).getMay()); cell.setCellStyle(cellStyle);
				cell = row.createCell(7); cell.setCellValue(sdList.get(l).getJun()); cell.setCellStyle(cellStyle);
				cell = row.createCell(8); cell.setCellValue(sdList.get(l).getJul()); cell.setCellStyle(cellStyle);
				cell = row.createCell(9); cell.setCellValue(sdList.get(l).getAug()); cell.setCellStyle(cellStyle);
				cell = row.createCell(10); cell.setCellValue(sdList.get(l).getSep()); cell.setCellStyle(cellStyle);
				cell = row.createCell(11); cell.setCellValue(sdList.get(l).getOct()); cell.setCellStyle(cellStyle);
				cell = row.createCell(12); cell.setCellValue(sdList.get(l).getNov()); cell.setCellStyle(cellStyle);
				cell = row.createCell(13); cell.setCellValue(sdList.get(l).getDec()); cell.setCellStyle(cellStyle);
				cell = row.createCell(14); cell.setCellValue(lab_total); cell.setCellStyle(cellStyle);
			}

				row = sheet1.createRow(27);
				cell = row.createCell(1); cell.setCellValue("인건비 합계"); cell.setCellStyle(cellStyle);	
				cell = row.createCell(2);  jan_total= sdList.get(12).getJan() + sdList.get(13).getJan() + sdList.get(14).getJan() + sdList.get(15).getJan() + sdList.get(16).getJan() + sdList.get(17).getJan(); cell.setCellValue(jan_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(3);  feb_total= sdList.get(12).getFeb() + sdList.get(13).getFeb() + sdList.get(14).getFeb() + sdList.get(15).getFeb() + sdList.get(16).getFeb() + sdList.get(17).getFeb(); cell.setCellValue(feb_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(4);  mar_total= sdList.get(12).getMar() + sdList.get(13).getMar() + sdList.get(14).getMar() + sdList.get(15).getMar() + sdList.get(16).getMar() + sdList.get(17).getMar(); cell.setCellValue(mar_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(5);  apr_total= sdList.get(12).getApr() + sdList.get(13).getApr() + sdList.get(14).getApr() + sdList.get(15).getApr() + sdList.get(16).getApr() + sdList.get(17).getApr(); cell.setCellValue(apr_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(6);  may_total= sdList.get(12).getMay() + sdList.get(13).getMay() + sdList.get(14).getMay() + sdList.get(15).getMay() + sdList.get(16).getMay() + sdList.get(17).getMay(); cell.setCellValue(may_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(7);  jun_total= sdList.get(12).getJun() + sdList.get(13).getJun() + sdList.get(14).getJun() + sdList.get(15).getJun() + sdList.get(16).getJun() + sdList.get(17).getJun(); cell.setCellValue(jun_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(8);  jul_total= sdList.get(12).getJul() + sdList.get(13).getJul() + sdList.get(14).getJul() + sdList.get(15).getJul() + sdList.get(16).getJul() + sdList.get(17).getJul(); cell.setCellValue(jul_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(9);  aug_total= sdList.get(12).getAug() + sdList.get(13).getAug() + sdList.get(14).getAug() + sdList.get(15).getAug() + sdList.get(16).getAug() + sdList.get(17).getAug(); cell.setCellValue(aug_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(10); sep_total= sdList.get(12).getSep() + sdList.get(13).getSep() + sdList.get(14).getSep() + sdList.get(15).getSep() + sdList.get(16).getSep() + sdList.get(17).getSep(); cell.setCellValue(sep_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(11); oct_total= sdList.get(12).getOct() + sdList.get(13).getOct() + sdList.get(14).getOct() + sdList.get(15).getOct() + sdList.get(16).getOct() + sdList.get(17).getOct(); cell.setCellValue(oct_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(12); nov_total= sdList.get(12).getNov() + sdList.get(13).getNov() + sdList.get(14).getNov() + sdList.get(15).getNov() + sdList.get(16).getNov() + sdList.get(17).getNov(); cell.setCellValue(nov_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(13); dec_total= sdList.get(12).getDec() + sdList.get(13).getDec() + sdList.get(14).getDec() + sdList.get(15).getDec() + sdList.get(16).getDec() + sdList.get(17).getDec(); cell.setCellValue(dec_total); cell.setCellStyle(cellStyle);
				cell = row.createCell(14);  int lab_annu_tot = jan_total + feb_total + mar_total + apr_total + may_total + jun_total + jul_total+ aug_total + sep_total + oct_total + nov_total + dec_total;
				cell.setCellValue(lab_annu_tot); cell.setCellStyle(cellStyle6);
			}	
			
			sheet1.addMergedRegion(new CellRangeAddress(28, 28, 0, 1));
			for(int n = 18; n <= 18; n++) {

				row = sheet1.createRow(n+10);
				loyalty_total = sdList.get(n).getJan() + sdList.get(n).getFeb() + sdList.get(n).getMar() + sdList.get(n).getApr() + sdList.get(n).getMay() + sdList.get(n).getJun()
						+ sdList.get(n).getJul() + sdList.get(n).getAug() + sdList.get(n).getSep() + sdList.get(n).getOct() + sdList.get(n).getNov() + sdList.get(n).getDec();
				
				if(sdList.get(n).getCate_sort().equals("4")) { cell = row.createCell(0); cell.setCellValue("로열티"); cell.setCellStyle(cellStyle); 
				if(sdList.get(n).getCate_sort().equals("4") && sdList.get(n).getSub_cate_cd().equals("401")) { cell = row.createCell(1); cell.setCellValue("로열티"); cell.setCellStyle(cellStyle); }
				
				cell = row.createCell(2); cell.setCellValue(sdList.get(n).getJan()); cell.setCellStyle(cellStyle);
				cell = row.createCell(3); cell.setCellValue(sdList.get(n).getFeb()); cell.setCellStyle(cellStyle);
				cell = row.createCell(4); cell.setCellValue(sdList.get(n).getMar()); cell.setCellStyle(cellStyle);
				cell = row.createCell(5); cell.setCellValue(sdList.get(n).getApr()); cell.setCellStyle(cellStyle);
				cell = row.createCell(6); cell.setCellValue(sdList.get(n).getMay()); cell.setCellStyle(cellStyle);
				cell = row.createCell(7); cell.setCellValue(sdList.get(n).getJun()); cell.setCellStyle(cellStyle);
				cell = row.createCell(8); cell.setCellValue(sdList.get(n).getJul()); cell.setCellStyle(cellStyle);
				cell = row.createCell(9); cell.setCellValue(sdList.get(n).getAug()); cell.setCellStyle(cellStyle);
				cell = row.createCell(10); cell.setCellValue(sdList.get(n).getSep()); cell.setCellStyle(cellStyle);
				cell = row.createCell(11); cell.setCellValue(sdList.get(n).getOct()); cell.setCellStyle(cellStyle);
				cell = row.createCell(12); cell.setCellValue(sdList.get(n).getNov()); cell.setCellStyle(cellStyle);
				cell = row.createCell(13); cell.setCellValue(sdList.get(n).getDec()); cell.setCellStyle(cellStyle);
				cell = row.createCell(14); cell.setCellValue(loyalty_total); cell.setCellStyle(cellStyle6);
				}

			}
			
			sheet1.addMergedRegion(new CellRangeAddress(29, 29, 0, 1));
			for(int o = 20; o <= 20; o++) {

				row = sheet1.createRow(o+9);
				rental_total = sdList.get(o).getJan() + sdList.get(o).getFeb() + sdList.get(o).getMar() + sdList.get(o).getApr() + sdList.get(o).getMay() + sdList.get(o).getJun()
						+ sdList.get(o).getJul() + sdList.get(o).getAug() + sdList.get(o).getSep() + sdList.get(o).getOct() + sdList.get(o).getNov() + sdList.get(o).getDec();
				if(sdList.get(o).getCate_sort().equals("5")) { cell = row.createCell(0); cell.setCellValue("임대료"); cell.setCellStyle(cellStyle); 
				if(sdList.get(o).getCate_sort().equals("5") && sdList.get(o).getSub_cate_cd().equals("501")) { cell = row.createCell(1); cell.setCellValue("임대료"); cell.setCellStyle(cellStyle); }
				
				cell = row.createCell(2); cell.setCellValue(sdList.get(o).getJan()); cell.setCellStyle(cellStyle);
				cell = row.createCell(3); cell.setCellValue(sdList.get(o).getFeb()); cell.setCellStyle(cellStyle);
				cell = row.createCell(4); cell.setCellValue(sdList.get(o).getMar()); cell.setCellStyle(cellStyle);
				cell = row.createCell(5); cell.setCellValue(sdList.get(o).getApr()); cell.setCellStyle(cellStyle);
				cell = row.createCell(6); cell.setCellValue(sdList.get(o).getMay()); cell.setCellStyle(cellStyle);
				cell = row.createCell(7); cell.setCellValue(sdList.get(o).getJun()); cell.setCellStyle(cellStyle);
				cell = row.createCell(8); cell.setCellValue(sdList.get(o).getJul()); cell.setCellStyle(cellStyle);
				cell = row.createCell(9); cell.setCellValue(sdList.get(o).getAug()); cell.setCellStyle(cellStyle);
				cell = row.createCell(10); cell.setCellValue(sdList.get(o).getSep()); cell.setCellStyle(cellStyle);
				cell = row.createCell(11); cell.setCellValue(sdList.get(o).getOct()); cell.setCellStyle(cellStyle);
				cell = row.createCell(12); cell.setCellValue(sdList.get(o).getNov()); cell.setCellStyle(cellStyle);
				cell = row.createCell(13); cell.setCellValue(sdList.get(o).getDec()); cell.setCellStyle(cellStyle);
				cell = row.createCell(14); cell.setCellValue(rental_total); cell.setCellStyle(cellStyle6);
				
			}

			}
			sheet1.addMergedRegion(new CellRangeAddress(30, 30, 0, 1)); 
			row = sheet1.createRow(30);  cell = row.createCell(0); cell.setCellValue("영업이익");  cell.setCellStyle(cellStyle3); cell = row.createCell(1); cell.setCellStyle(cellStyle3); 
			for(int q=2; q<=14;q++) {
				cell = row.createCell(q); cell.setCellStyle(cellStyle3);
			}
			
			sheet1.addMergedRegion(new CellRangeAddress(31, 31, 0, 1)); 
			sheet1.addMergedRegion(new CellRangeAddress(31, 31, 2, 14));
			row = sheet1.createRow(31); cell = row.createCell(0); cell.setCellStyle(cellStyle); cell.setCellValue(""); cell = row.createCell(1); cell.setCellStyle(cellStyle);
			for(int u=2; u<=14;u++) {
				cell = row.createCell(u); cell.setCellStyle(cellStyle);
			}
			
			sheet1.addMergedRegion(new CellRangeAddress(32, 32, 0, 1)); 
			row = sheet1.createRow(32); cell = row.createCell(0); cell.setCellStyle(cellStyle); cell.setCellValue("감가상각"); cell = row.createCell(1); cell.setCellStyle(cellStyle);
			for(int q=2; q<=14;q++) {
				cell = row.createCell(q); cell.setCellStyle(cellStyle);
			}
			
			sheet1.addMergedRegion(new CellRangeAddress(33, 33, 0, 1)); 
			row = sheet1.createRow(33); cell = row.createCell(0); cell.setCellStyle(cellStyle); cell.setCellValue("이자비용"); cell = row.createCell(1); cell.setCellStyle(cellStyle);
			for(int r=2; r<=14;r++) {
				cell = row.createCell(r); cell.setCellStyle(cellStyle);
			}
			
			sheet1.addMergedRegion(new CellRangeAddress(34, 34, 0, 1)); 
			sheet1.addMergedRegion(new CellRangeAddress(34, 34, 2, 14));
			row = sheet1.createRow(34); cell = row.createCell(0); cell.setCellStyle(cellStyle); cell.setCellValue(""); cell = row.createCell(1); cell.setCellStyle(cellStyle);
			for(int w=2; w<=14;w++) {
				cell = row.createCell(w); cell.setCellStyle(cellStyle);
			}
			
			sheet1.addMergedRegion(new CellRangeAddress(35, 35, 0, 1)); 
			row = sheet1.createRow(35); cell = row.createCell(0); cell.setCellStyle(cellStyle4); cell.setCellValue("세전이익"); cell = row.createCell(1); cell.setCellStyle(cellStyle4);
			for(int s=2; s<=14;s++) {
				cell = row.createCell(s); cell.setCellStyle(cellStyle4);
			}
			
			
			
			branch_name = URLEncoder.encode(branch_name, "UTF-8");
			String data = null;
			data = URLEncoder.encode("Monthly매출통계","UTF-8");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
			String cur_datetime = sdf.format(date);
			String path = System.getProperty("user.home");
			File xlsFile = new File(path+File.separator+"Downloads"+File.separator+cur_datetime+"_"+branch_name+"_"+data+".xlsx");
			
			FileOutputStream fileOut = new FileOutputStream(xlsFile);
			xlsWb.write(fileOut); 
			xlsWb.close();
	
			response.setContentType("Application/octet-stream");
			response.setHeader("Content-Transfer-Encoding", "binary");
			response.setHeader("Content-Disposition", String.format("Attachment; Filename=\"%s\"", branch_name + "_" + data + ".xlsx"));
			response.setHeader("Content-Length", Long.toString(xlsFile.length()));
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1");
			
			byte b[] = new byte[1024];
			
		BufferedInputStream bi = new BufferedInputStream(new FileInputStream(xlsFile));
		BufferedOutputStream bo = new BufferedOutputStream(response.getOutputStream());
			
			try {
				int read = 0;
				while((read = bi.read(b)) != -1) {
					bo.write(b,0,read);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (bo != null) bo.close();
				if (bi != null) bi.close();
			}
			
			try {
				if (xlsFile.exists()) xlsFile.delete();
			} catch (Exception e) {}
			
	}
}