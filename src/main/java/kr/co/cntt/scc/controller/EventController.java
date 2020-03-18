package kr.co.cntt.scc.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.app.admin.model.AppAdminUserInfo;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.model.EntryRank;
import kr.co.cntt.scc.model.FreeApplication;
import kr.co.cntt.scc.model.Page;
import kr.co.cntt.scc.model.PageMaker;
import kr.co.cntt.scc.model.PreReservation;
import kr.co.cntt.scc.model.User;
import kr.co.cntt.scc.service.BranchDesignService;
import kr.co.cntt.scc.service.BranchEntryRankService;
import kr.co.cntt.scc.service.BranchFreeApplicationService;
import kr.co.cntt.scc.service.BranchPreReservationService;
import kr.co.cntt.scc.service.BranchReservationService;
import kr.co.cntt.scc.service.BranchService;
import kr.co.cntt.scc.service.SmsService.SmsResultListSendNumber;
import kr.co.cntt.scc.service.SmsService.SmsSendNumber;
import kr.co.cntt.scc.util.DateUtil;

import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
@RequestMapping("/e")
public class EventController {

  @Autowired
  private BranchService branchService;

  @Autowired
  private BranchPreReservationService preReservationService;
  
  @Autowired
  private BranchFreeApplicationService branchFreeApplicationService;
  
  @Autowired
  private BranchEntryRankService branchEntryRankService;

  @Autowired
  private BranchReservationService branchReservationService;
  
  @Autowired
  private BranchDesignService branchDesignService;
  
  private static final String VIEW_NAME_OF_PUBLIC_LAYOUT = "layout_event";

  private ModelAndView modelView(ModelMap model, String title, String view) {
    model.addAttribute("brand", Constants.BRAND_FULL);
    model.addAttribute("title", title);
    model.addAttribute("view", view);
    return new ModelAndView(VIEW_NAME_OF_PUBLIC_LAYOUT);
  }

  /******************************************************************************************************************/

    /**
     * 사전예약 신청서
     * @param model
     * @param bId
     * @return
     */
  @RequestMapping("/pre/{bId}")
  public ModelAndView preReservation(ModelMap model, @PathVariable String bId) {
    // 지점 조회 (숫자아아디 또는 지점명)
    Branch branch = branchService.selectBranchByIdOrName(bId);
    //Branch branch_setup = preReservationService.selectBranchWithOpenDt(branch.getBranchId());
    //branch.setOpenDt(branch_setup.getOpenDt());
    model.addAttribute("branch", branch);
    model.addAttribute("bId", bId);
    return modelView(model, "사전예약 신청", "pre_reservation");
  }

  @RequestMapping(value = "/api/v1/{branchId}/pre", method = RequestMethod.GET)
  @ResponseBody
  public List<PreReservation> getPreReservation(@PathVariable String branchId) {
    return preReservationService.selectPreReservation(branchId, null, null, null, null);
  }
  
  @RequestMapping(value = "/api/v1/{branchId}/pre", method = RequestMethod.POST)
  @ResponseBody
  public PreReservation savePreReservation(@PathVariable String branchId, @RequestBody PreReservation preReservation) {
    // 외부 아이디 생성
    String preReservationId = UUID.randomUUID().toString();
    preReservation.setPreReservationId(preReservationId);
    
    int result = preReservationService.insertPreReservation(preReservation);
    return preReservationService.selectOnePreReservation(branchId, preReservationId);
  }
  
  @RequestMapping(value = "/api/v1/{branchId}/pre/{preReservationId}", method = RequestMethod.PUT)
  @ResponseBody
  public PreReservation updatePreReservation(@PathVariable String branchId, @RequestBody PreReservation preReservation) {
    int result = preReservationService.updatePreReservation(preReservation);
    return preReservation;
  }

  /**
   * (지점의) 사전 예약 삭제
   * @param branchId
   * 
   * @return
   */
  @RequestMapping(value = "/api/v1/{branchId}/pre/{preReservationId}", method = RequestMethod.DELETE)
  @ResponseBody
  public int deleteBranchPreReservation(@PathVariable String branchId, PreReservation preReservation) {
	  	  
      return preReservationService.deletePreReservation(branchId, preReservation);

  }

  /******************************************************************************************************************/

    /**
     * 무료체험 신청서
     * @param model
     * @return
     */
  @RequestMapping(value = {"/try/{bId}", "/try/", "/try" }, method = RequestMethod.GET)
  public ModelAndView branchFreeApplications(ModelMap model, @PathVariable Optional<String> bId) {

      if (bId.isPresent()) {
          // 지점 조회 (숫자아아디 또는 지점명)
          Branch branch = branchService.selectBranchByIdOrName(bId.get());
          model.addAttribute("branch", branch);

          model.addAttribute("sBranch", branch.getBranchId());

      }
      
      try {
          ObjectMapper om = new ObjectMapper();
          // 지점 목록 조회
          List<Branch> branchList = branchService.selectBranchList();
          model.addAttribute("branchListJSON", om.writeValueAsString(branchList));
          model.addAttribute("branchs", branchList);
          
          // 성별 구분
          model.addAttribute("genderTypeMapJSON", om.writeValueAsString(Constants.GenderType.getMap()));
          model.addAttribute("genderTypes", Constants.GenderType.getList());
          
          // 알게 된 경로
          model.addAttribute("cmpRouteTypes", Constants.cmpRouteType.getList());
          
      } catch (JsonProcessingException e) {
          e.printStackTrace();

      }
      	
      // 열람실 유형      
      model.addAttribute("roomTypes", Constants.RoomType.getList());
      
      return modelView(model, "무료체험 신청", "free_applications");

  }
  
  //무료신청 API
  /**
   * (지점의) 무료 신청서 조회
   * 
   */

  @RequestMapping(value = "/api/v1/{branchId}/try", method = RequestMethod.GET)
  @ResponseBody
  public
  List<FreeApplication> getFreeApplication(@PathVariable String branchId) {    	 
      List<FreeApplication> FreeApplicationList = branchFreeApplicationService.selectFreeApplicationList(branchId);
      
      return FreeApplicationList;
      
  }
  
  
  /**
   * (지점의) 무료 신청서 추가
   * 
   */

  //@RequestMapping(value = "/branch/{branchId}/free_applications", method = RequestMethod.GET)
  @RequestMapping(value = "/api/v1/{branchId}/try", method = RequestMethod.POST)
  @ResponseBody
  public
  FreeApplication postFreeApplication(@PathVariable String branchId,
  		@RequestBody FreeApplication freeApplication) {
	    // 외부 아이디 생성
	    String FreeApplicationId = UUID.randomUUID().toString();
	    freeApplication.setFreeApplicationId(FreeApplicationId);
	    
      branchFreeApplicationService.insertedFreeApplication(freeApplication);
      FreeApplication insertedFreeApplication = branchFreeApplicationService.selectFreeApplication(freeApplication.getBranchId());
      return insertedFreeApplication;

  }    
    
  /**
   * (지점의) 무료 신청서 수정
   * 
   */

  //@RequestMapping(value = "/branch/{branchId}/free_applications", method = RequestMethod.GET)
  @RequestMapping(value = "/api/v1/{branchId}/try/{freeApplicationId}", method = RequestMethod.PUT)
  @ResponseBody
  public
  int putFreeApplication(@PathVariable String branchId, @PathVariable String freeApplicationId, @RequestBody FreeApplication freeApplication) {

      return branchFreeApplicationService.updateFreeApplication(freeApplicationId, freeApplication);

  }
  
  /**
   * (지점의) 무료 신청서 삭제
   * 
   */
  @RequestMapping(value = "/api/v1/{branchId}/try/{freeApplicationId}", method = RequestMethod.DELETE)
  @ResponseBody
  public int deleteBranchFreeapplication(@PathVariable String branchId, @PathVariable String freeApplicationId, FreeApplication freeApplication) {
      
	  return branchFreeApplicationService.deleteFreeApplication(branchId, freeApplicationId, freeApplication);

  }
  
  
  
  /******************************************************************/
  @RequestMapping(value = "/app/admin/api/v1/login", method = RequestMethod.POST)
  public @ResponseBody
  AppAdminUserInfo.Response getLogin(String name, String userPw) {

      //String userId = AuthUtil.getCurrentUserId();

  	User user = null;    	

  	//user = userService.selectUser(name, userPw);
  	
  	
      //String branchId = userService.selectPrimaryBranchIdByUserId(userId);

      //Branch branch = branchService.selectBranch(branchId);

      
      // 결과
      ModelMapper modelMapper = new ModelMapper();        
      /*
      if (user == null) {
      	testest = modelMapper.map(null, String.class);
      }
      else {
      	testest = modelMapper.map(user.getUserId(), String.class);
      }
       */
      AppAdminUserInfo.Response response = modelMapper.map("", AppAdminUserInfo.Response.class);
      response.setUserId("5f37382e-8db3-4664-9be8-482105008a10");
      //System.out.println("=======================================testest=========="+response);
      return response;
  }

  
  /**
   * 학습 누적시간 랭킹
   * @param model
   * @return
   */
@RequestMapping(value = {"/entryRank/{branchId}", "/entryRank/", "/entryRank" }, method = RequestMethod.GET)
public ModelAndView GetEntryRank(ModelMap model, @RequestParam(required = false) String branchId,
						        @RequestParam(required = false) String sDate,
						        Page page) {

	PageMaker pageMaker = new PageMaker();
	
	ObjectMapper om = new ObjectMapper();
    if (!StringUtils.isEmpty(branchId)) {
        // 지점 조회 (숫자아아디 또는 지점명)
        //Branch branch = branchService.selectBranchByIdOrName(branchId);
        //model.addAttribute("branch", branch);

        //model.addAttribute("sBranch", branch.getBranchId());
    	sDate += "-01";
        List<EntryRank> EntryRankList = branchEntryRankService.selectEntryRankList(branchId, sDate, page);

        try {
        	
        	model.addAttribute("sBranch", branchId);
        	model.addAttribute("sDate", sDate);
        	
			model.addAttribute("branchEntryRankJSON", om.writeValueAsString(EntryRankList));
			model.addAttribute("branchEntryRank", EntryRankList);
			if (EntryRankList.size() > 0) {
				model.addAttribute("allHour", EntryRankList.get(0).getAllHour());
				model.addAttribute("allMin", EntryRankList.get(0).getAllMin());
			} else {
				model.addAttribute("allHour", 0);
				model.addAttribute("allMin", 0);
			}
			List<EntryRank> EntryRankTotalList = branchEntryRankService.selectEntryRankList(branchId, sDate, null);
			
	        pageMaker.setPage(page);
	        pageMaker.setTotalCount(EntryRankTotalList.size());
	        model.addAttribute("pageMaker", pageMaker);
			
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    } else {
    	
    	pageMaker.setPage(page);
        pageMaker.setTotalCount(1);
        model.addAttribute("pageMaker", pageMaker);
        
        model.addAttribute("allHour", 0);
		model.addAttribute("allMin", 0);
        
        String currentDate = DateUtil.getCurrentDateString();
        model.addAttribute("sDate", currentDate.substring(0, 7));
    }
    
    try {
        
    	//sDate = DateUtil.getCurrentDateString().substring(0, 10);
        
    	List<EntryRank> EntryRankListAll = branchEntryRankService.selectEntryRankListAll(sDate);
    	model.addAttribute("entryRankListAll", EntryRankListAll);
    						
    	//  	지점 목록 조회
        List<Branch> branchList = branchService.selectBranchList();
        model.addAttribute("branchListJSON", om.writeValueAsString(branchList));
        model.addAttribute("branchs", branchList);
        
        // 성별 구분
        model.addAttribute("genderTypeMapJSON", om.writeValueAsString(Constants.GenderType.getMap()));
        model.addAttribute("genderTypes", Constants.GenderType.getList());
        
        // 알게 된 경로
        model.addAttribute("cmpRouteTypes", Constants.cmpRouteType.getList());
        
    } catch (JsonProcessingException e) {
        e.printStackTrace();

    }
    	
    // 열람실 유형      
    model.addAttribute("roomTypes", Constants.RoomType.getList());
    
    return modelView(model, "학습 누적시간 랭킹", "entryRank");

}
  
// 학습 누적시간 랭킹 엑셀 다운로드
@RequestMapping("/{branchId}/entryRank/excelExtract")
public void downloadExcelTest(
						@RequestParam(required = false) String branchId,
						@RequestParam(required = false) String sDate,
						HttpServletRequest request,
						HttpServletResponse response) throws Exception, ParseException ,IOException {
	
	Date date = new Date();
	
	sDate += "-01";
	List<EntryRank> EntryRankTotalList = branchEntryRankService.selectEntryRankList(branchId, sDate, null);
	List<EntryRank> EntryRankListAll = branchEntryRankService.selectEntryRankListAll(sDate);
	
	for (EntryRank e : EntryRankTotalList) {
		for (EntryRank er : EntryRankListAll) {
			if (e.getMemberId().equals(er.getMemberId())) {
				e.setAllRank(er.getRank());
			}

		}
	}
	
	
	//Workbook xlsWb = new HSSFWorkbook(); // Excel 2007 이전 버전
	XSSFWorkbook xlsWb = new XSSFWorkbook(); // Excel 2007 이상
	
	/*Sheet sheet1 = xlsWb.createSheet(cur_datetime+data);*/
	XSSFSheet sheet1 = xlsWb.createSheet("StudyCentreStatistics");	
	//sheet1.autoSizeColumn(1, true);
	
	sheet1.setDefaultColumnWidth((short) 20);
	//sheet1.createFreezePane(2, 0);
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
	
	// 1
	int rownum = 2;	
	row = sheet1.createRow(rownum);
	cell = row.createCell(0); cell.setCellStyle(cellStyle5); cell.setCellValue("전 센터 평균 학습시간");
	if (EntryRankTotalList.size() > 0) {
		cell = row.createCell(1); cell.setCellStyle(cellStyle5); cell.setCellValue(EntryRankTotalList.get(0).getAllHour() + "시간 " + EntryRankTotalList.get(0).getAllMin() + "분");
	}
	
	
	
	rownum = rownum + 2;	
	row = sheet1.createRow(rownum);

	cell = row.createCell(0); cell.setCellStyle(cellStyle5); cell.setCellValue("센터 내 순위");
	cell = row.createCell(1); cell.setCellStyle(cellStyle5); cell.setCellValue("전 센터 내 비교 순위");
	cell = row.createCell(2); cell.setCellStyle(cellStyle5); cell.setCellValue("성명");
	cell = row.createCell(3); cell.setCellStyle(cellStyle5); cell.setCellValue("전화번호 뒷 4자리");
	cell = row.createCell(4); cell.setCellStyle(cellStyle5); cell.setCellValue("소속센터");
	cell = row.createCell(5); cell.setCellStyle(cellStyle5); cell.setCellValue("학습시간(월 누적)");
	cell = row.createCell(6); cell.setCellStyle(cellStyle5); cell.setCellValue("평균 학습시간");
	cell = row.createCell(7); cell.setCellStyle(cellStyle5); cell.setCellValue("출석일 수");


	String branch_name = null;
	if (EntryRankTotalList.size() > 0) {
		for (EntryRank e : EntryRankTotalList){
			rownum++;
			row = sheet1.createRow(rownum);
			cell = row.createCell(0); cell.setCellValue(e.getRank()); cell.setCellStyle(cellStyle);
			cell = row.createCell(1); cell.setCellValue(e.getAllRank()); cell.setCellStyle(cellStyle);
			cell = row.createCell(2); cell.setCellValue(e.getName()); cell.setCellStyle(cellStyle);
			cell = row.createCell(3); cell.setCellValue(e.getNo()); cell.setCellStyle(cellStyle);
			cell = row.createCell(4); cell.setCellValue(e.getBranchNm()); cell.setCellStyle(cellStyle);
			cell = row.createCell(5); cell.setCellValue(e.getHour() + "시간 " + e.getMin() + "분"); cell.setCellStyle(cellStyle);
			cell = row.createCell(6); cell.setCellValue(e.getAvgHour() + "시간 " + e.getAvgMin() + "분"); cell.setCellStyle(cellStyle);
			cell = row.createCell(7); cell.setCellValue(e.getCnt()); cell.setCellStyle(cellStyle);
			branch_name = e.getBranchNm();
		}
	}

	branch_name = URLEncoder.encode(branch_name, "UTF-8");
	String data = null;
	data = URLEncoder.encode("학습누적시간","UTF-8");
	
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

//무료신청 API
/**
 * (지점의) 학습누적 랭킹
 * 
 */

@RequestMapping(value = "/api/v1/{branchId}/entryRank", method = RequestMethod.GET)
@ResponseBody
public
List<EntryRank> getEntryRank(@PathVariable String branchId,
							 @PathVariable String sDate ) {
    List<EntryRank> EntryRankList = branchEntryRankService.selectEntryRankList(branchId, sDate, null);
    
    return EntryRankList;
    
}



  //주소검색
  @RequestMapping("/daum_address")
  public ModelAndView getSafeServiceList(ModelMap model) {
  	return modelView(model, "주소검색 테스트", "daum_address");
  }
  
  //smartro
  @RequestMapping("/smartro")
  public ModelAndView getSmartro(ModelMap model) {
  	return modelView(model, "스마트로 테스트", "smartro");
  }

  
  
//관리자 통계
  @RequestMapping(value = "/api/adminStatistics", method = RequestMethod.GET)    
  @ResponseBody
   public List<Branch> selectAdminStatisticsList(ModelMap model,
 		  										@RequestParam(required = false) String sDate) {    
 	  
 	  try {
 		// 지점 목록 조회
 	        List<Branch> branches = branchService.selectBranchList();
 	        List<Branch> adminStatisticsList = null;
 	        
 	        for (Branch branch : branches ) {
 	        	
 	        	Integer deskList = branchDesignService.selectDeskCountList(branch.getBranchId());
 	        	
 	        	//전체 좌석 수
 				if(deskList != null && deskList > 0) {
 					branch.setTotalDesk(deskList);
 				} else {
 					branch.setTotalDesk(0);
 				}
 				
 				int man_cnt_01 = 0;
 				int woman_cnt_01 = 0;
 				int stu_cnt_01 = 0;
 				int adult_cnt_01 = 0;
 				
 				int man_cnt_02 = 0;
 				int woman_cnt_02 = 0;
 				int stu_cnt_02 = 0;
 				int adult_cnt_02 = 0;
 				
 				int man_cnt_03 = 0;
 				int woman_cnt_03 = 0;
 				int stu_cnt_03 = 0;
 				int adult_cnt_03 = 0;
 				
 				int man_cnt_04 = 0;
 				int woman_cnt_04 = 0;
 				int stu_cnt_04 = 0;
 				int adult_cnt_04 = 0;
 				
 				int man_cnt_05 = 0;
 				int woman_cnt_05 = 0;
 				int stu_cnt_05 = 0;
 				int adult_cnt_05 = 0;
 				
 				int man_cnt_06 = 0;
 				int woman_cnt_06 = 0;
 				int stu_cnt_06 = 0;
 				int adult_cnt_06 = 0;
 				
 				int man_cnt_07 = 0;
 				int woman_cnt_07 = 0;
 				int stu_cnt_07 = 0;
 				int adult_cnt_07 = 0;
 				
 				int man_cnt_08 = 0;
 				int woman_cnt_08 = 0;
 				int stu_cnt_08 = 0;
 				int adult_cnt_08 = 0;
 				
 				int man_cnt_09 = 0;
 				int woman_cnt_09 = 0;
 				int stu_cnt_09 = 0;
 				int adult_cnt_09 = 0;
 				
 				int man_cnt_10 = 0;
 				int woman_cnt_10 = 0;
 				int stu_cnt_10 = 0;
 				int adult_cnt_10 = 0;
 				
 				int man_cnt_11 = 0;
 				int woman_cnt_11 = 0;
 				int stu_cnt_11 = 0;
 				int adult_cnt_11 = 0;
 				
 				int man_cnt_12 = 0;
 				int woman_cnt_12 = 0;
 				int stu_cnt_12 = 0;
 				int adult_cnt_12 = 0;
 				
 				
 				adminStatisticsList = branchReservationService.selectReservationStatisticsList(branch.getBranchId(), sDate);
 				
 				if (adminStatisticsList.size() > 0) {
 					
 					
// 					int mid1_cnt = 0;
// 					int mid2_cnt = 0;
// 					int mid3_cnt = 0;
// 					int high1_cnt = 0;
// 					int high2_cnt = 0;
// 					int high3_cnt = 0;
// 					int orgAdult_cnt = 0;
// 					int etc_cnt = 0;
 					
 					for (Branch b : adminStatisticsList) {
 						if (b.getM().equals("01")) {
 							man_cnt_01 += b.getMan_cnt();	
 							woman_cnt_01 += b.getWoman_cnt();
 							stu_cnt_01 += b.getStu_cnt();
 							adult_cnt_01 += b.getAdult_cnt();
 						} else if (b.getM().equals("02")) {
 							man_cnt_02 += b.getMan_cnt();	
 							woman_cnt_02 += b.getWoman_cnt();
 							stu_cnt_02 += b.getStu_cnt();
 							adult_cnt_02 += b.getAdult_cnt();
 						} else if (b.getM().equals("03")) {
 							man_cnt_03 += b.getMan_cnt();	
 							woman_cnt_03 += b.getWoman_cnt();
 							stu_cnt_03 += b.getStu_cnt();
 							adult_cnt_03 += b.getAdult_cnt();
 						} else if (b.getM().equals("04")) {
 							man_cnt_04 += b.getMan_cnt();	
 							woman_cnt_04 += b.getWoman_cnt();
 							stu_cnt_04 += b.getStu_cnt();
 							adult_cnt_04 += b.getAdult_cnt();
 						} else if (b.getM().equals("05")) {
 							man_cnt_05 += b.getMan_cnt();	
 							woman_cnt_05 += b.getWoman_cnt();
 							stu_cnt_05 += b.getStu_cnt();
 							adult_cnt_05 += b.getAdult_cnt();
 						} else if (b.getM().equals("06")) {
 							man_cnt_06 += b.getMan_cnt();	
 							woman_cnt_06 += b.getWoman_cnt();
 							stu_cnt_06 += b.getStu_cnt();
 							adult_cnt_06 += b.getAdult_cnt();
 						} else if (b.getM().equals("07")) {
 							man_cnt_07 += b.getMan_cnt();	
 							woman_cnt_07 += b.getWoman_cnt();
 							stu_cnt_07 += b.getStu_cnt();
 							adult_cnt_07 += b.getAdult_cnt();
 						} else if (b.getM().equals("08")) {
 							man_cnt_08 += b.getMan_cnt();	
 							woman_cnt_08 += b.getWoman_cnt();
 							stu_cnt_08 += b.getStu_cnt();
 							adult_cnt_08 += b.getAdult_cnt();
 						} else if (b.getM().equals("09")) {
 							man_cnt_09 += b.getMan_cnt();	
 							woman_cnt_09 += b.getWoman_cnt();
 							stu_cnt_09 += b.getStu_cnt();
 							adult_cnt_09 += b.getAdult_cnt();
 						} else if (b.getM().equals("10")) {
 							man_cnt_10 += b.getMan_cnt();	
 							woman_cnt_10 += b.getWoman_cnt();
 							stu_cnt_10 += b.getStu_cnt();
 							adult_cnt_10 += b.getAdult_cnt();
 						} else if (b.getM().equals("11")) {
 							man_cnt_11 += b.getMan_cnt();	
 							woman_cnt_11 += b.getWoman_cnt();
 							stu_cnt_11 += b.getStu_cnt();
 							adult_cnt_11 += b.getAdult_cnt();
 						} else if (b.getM().equals("12")) {
 							man_cnt_12 += b.getMan_cnt();	
 							woman_cnt_12 += b.getWoman_cnt();
 							stu_cnt_12 += b.getStu_cnt();
 							adult_cnt_12 += b.getAdult_cnt();
 						}
 						

 						
 						
// 						mid1_cnt += b.getMid1_cnt();
// 						mid2_cnt += b.getMid2_cnt();
// 						mid3_cnt += b.getMid3_cnt();
// 						high1_cnt += b.getHigh1_cnt();
// 						high2_cnt += b.getHigh2_cnt();
// 						high3_cnt += b.getHigh3_cnt();
// 						orgAdult_cnt += b.getOrgAdult_cnt();
// 						etc_cnt += b.getEtc_cnt();
 					}

 					double manRatio = 0;
 					double womanRatio = 0;
 					double stuRatio = 0;
 					double adultRatio = 0;
 					
// 					if ( ((double)man_cnt + (double)woman_cnt) == (double)0 ){
// 						
// 					} else {
// 						manRatio = (double)man_cnt / ((double)man_cnt + (double)woman_cnt) * 100;
// 						womanRatio = (double)woman_cnt / ((double)man_cnt + (double)woman_cnt) * 100;
// 					} 
// 					
// 					if ( ((double)stu_cnt + (double)adult_cnt) == (double)0 ) {
// 							
// 					} else {
// 						stuRatio = (double)stu_cnt / ((double)stu_cnt + (double)adult_cnt) * 100;
// 						adultRatio = (double)adult_cnt / ((double)stu_cnt + (double)adult_cnt) * 100;
// 					}
 					
 					branch.setMan_cnt_01(man_cnt_01);
 					branch.setWoman_cnt_01(woman_cnt_01);
 					branch.setStu_cnt_01(stu_cnt_01);
 					branch.setAdult_cnt_01(adult_cnt_01);
 					
 					branch.setMan_cnt_02(man_cnt_02);
 					branch.setWoman_cnt_02(woman_cnt_02);
 					branch.setStu_cnt_02(stu_cnt_02);
 					branch.setAdult_cnt_02(adult_cnt_02);
 					
 					branch.setMan_cnt_03(man_cnt_03);
 					branch.setWoman_cnt_03(woman_cnt_03);
 					branch.setStu_cnt_03(stu_cnt_03);
 					branch.setAdult_cnt_03(adult_cnt_03);
 					
 					branch.setMan_cnt_04(man_cnt_04);
 					branch.setWoman_cnt_04(woman_cnt_04);
 					branch.setStu_cnt_04(stu_cnt_04);
 					branch.setAdult_cnt_04(adult_cnt_04);
 					
 					branch.setMan_cnt_05(man_cnt_05);
 					branch.setWoman_cnt_05(woman_cnt_05);
 					branch.setStu_cnt_05(stu_cnt_05);
 					branch.setAdult_cnt_05(adult_cnt_05);
 					
 					branch.setMan_cnt_06(man_cnt_06);
 					branch.setWoman_cnt_06(woman_cnt_06);
 					branch.setStu_cnt_06(stu_cnt_06);
 					branch.setAdult_cnt_06(adult_cnt_06);
 					
 					branch.setMan_cnt_07(man_cnt_07);
 					branch.setWoman_cnt_07(woman_cnt_07);
 					branch.setStu_cnt_07(stu_cnt_07);
 					branch.setAdult_cnt_07(adult_cnt_07);
 					
 					branch.setMan_cnt_08(man_cnt_08);
 					branch.setWoman_cnt_08(woman_cnt_08);
 					branch.setStu_cnt_08(stu_cnt_08);
 					branch.setAdult_cnt_08(adult_cnt_08);
 					
 					branch.setMan_cnt_09(man_cnt_09);
 					branch.setWoman_cnt_09(woman_cnt_09);
 					branch.setStu_cnt_09(stu_cnt_09);
 					branch.setAdult_cnt_09(adult_cnt_09);
 					
 					branch.setMan_cnt_10(man_cnt_10);
 					branch.setWoman_cnt_10(woman_cnt_10);
 					branch.setStu_cnt_10(stu_cnt_10);
 					branch.setAdult_cnt_10(adult_cnt_10);
 					
 					branch.setMan_cnt_11(man_cnt_11);
 					branch.setWoman_cnt_11(woman_cnt_11);
 					branch.setStu_cnt_11(stu_cnt_11);
 					branch.setAdult_cnt_11(adult_cnt_11);
 					
 					branch.setMan_cnt_12(man_cnt_12);
 					branch.setWoman_cnt_12(woman_cnt_12);
 					branch.setStu_cnt_12(stu_cnt_12);
 					branch.setAdult_cnt_12(adult_cnt_12);
 					
// 					branch.setMid1_cnt(mid1_cnt);
// 					branch.setMid2_cnt(mid2_cnt);
// 					branch.setMid3_cnt(mid3_cnt);
// 					branch.setHigh1_cnt(high1_cnt);
// 					branch.setHigh2_cnt(high2_cnt);
// 					branch.setHigh3_cnt(high3_cnt);
// 					branch.setOrgAdult_cnt(orgAdult_cnt);
// 					branch.setEtc_cnt(etc_cnt);
 					
// 					branch.setManRatio(Math.round(manRatio * 100) / 100.0 );
// 					branch.setWomanRatio(Math.round(womanRatio * 100) / 100.0);
// 					branch.setStuRatio(Math.round(stuRatio * 100) / 100.0);
// 					branch.setAdultRatio(Math.round(adultRatio * 100) / 100.0);
 					
 				} else {
 					branch.setMan_cnt_01(man_cnt_01);
 					branch.setWoman_cnt_01(woman_cnt_01);
 					branch.setStu_cnt_01(stu_cnt_01);
 					branch.setAdult_cnt_01(adult_cnt_01);
 					
 					branch.setMan_cnt_02(man_cnt_02);
 					branch.setWoman_cnt_02(woman_cnt_02);
 					branch.setStu_cnt_02(stu_cnt_02);
 					branch.setAdult_cnt_02(adult_cnt_02);
 					
 					branch.setMan_cnt_03(man_cnt_03);
 					branch.setWoman_cnt_03(woman_cnt_03);
 					branch.setStu_cnt_03(stu_cnt_03);
 					branch.setAdult_cnt_03(adult_cnt_03);
 					
 					branch.setMan_cnt_04(man_cnt_04);
 					branch.setWoman_cnt_04(woman_cnt_04);
 					branch.setStu_cnt_04(stu_cnt_04);
 					branch.setAdult_cnt_04(adult_cnt_04);
 					
 					branch.setMan_cnt_05(man_cnt_05);
 					branch.setWoman_cnt_05(woman_cnt_05);
 					branch.setStu_cnt_05(stu_cnt_05);
 					branch.setAdult_cnt_05(adult_cnt_05);
 					
 					branch.setMan_cnt_06(man_cnt_06);
 					branch.setWoman_cnt_06(woman_cnt_06);
 					branch.setStu_cnt_06(stu_cnt_06);
 					branch.setAdult_cnt_06(adult_cnt_06);
 					
 					branch.setMan_cnt_07(man_cnt_07);
 					branch.setWoman_cnt_07(woman_cnt_07);
 					branch.setStu_cnt_07(stu_cnt_07);
 					branch.setAdult_cnt_07(adult_cnt_07);
 					
 					branch.setMan_cnt_08(man_cnt_08);
 					branch.setWoman_cnt_08(woman_cnt_08);
 					branch.setStu_cnt_08(stu_cnt_08);
 					branch.setAdult_cnt_08(adult_cnt_08);
 					
 					branch.setMan_cnt_09(man_cnt_09);
 					branch.setWoman_cnt_09(woman_cnt_09);
 					branch.setStu_cnt_09(stu_cnt_09);
 					branch.setAdult_cnt_09(adult_cnt_09);
 					
 					branch.setMan_cnt_10(man_cnt_10);
 					branch.setWoman_cnt_10(woman_cnt_10);
 					branch.setStu_cnt_10(stu_cnt_10);
 					branch.setAdult_cnt_10(adult_cnt_10);
 					
 					branch.setMan_cnt_11(man_cnt_11);
 					branch.setWoman_cnt_11(woman_cnt_11);
 					branch.setStu_cnt_11(stu_cnt_11);
 					branch.setAdult_cnt_11(adult_cnt_11);
 					
 					branch.setMan_cnt_12(man_cnt_12);
 					branch.setWoman_cnt_12(woman_cnt_12);
 					branch.setStu_cnt_12(stu_cnt_12);
 					branch.setAdult_cnt_12(adult_cnt_12);
 					
 				}
 				
 	        }
 		  
 		  //List<SeatReservation> SeatReservationList = branchSeatReservationService.selectSeatReservationList(branchId);
 		  

 		  
 		  return branches;
 		  
 	  } catch(EmptyResultDataAccessException e) {
 		  
 		  e.printStackTrace();
 		  
 		  return null;
 		  
 	  }
       
 	  
       
   }

///////////////////////////////////////////////
  
  

  /**
   * @RequestMapping(value = "/api/v1/{branchId}/pre", method = RequestMethod.GET)
  @ResponseBody
  public List<PreReservation> getPreReservation(@PathVariable String branchId) {
    return preReservationService.selectPreReservation(branchId, null, null, null, null);
  }
   * SMS 발신번호 리스트 조회
   * @return
   */
  @RequestMapping(value = "/listSmsSendNumber", method = RequestMethod.GET)
  @ResponseBody
  public List<SmsSendNumber> listSmsSendNumber() {
      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      headers.add("x-waple-authorization", Constants.SMS_SERVER_KEY);

      try {
          String url = Constants.SMS_SAVE_SEND_NUMBER_SERVER_URL;

          HttpEntity<String> entity = new HttpEntity<>("", headers);

          ResponseEntity<SmsResultListSendNumber> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, SmsResultListSendNumber.class);
          //ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);


          if (responseEntity.getStatusCode() == HttpStatus.OK) {
              SmsResultListSendNumber result = responseEntity.getBody();

              log.info(result.toString());

              if("200".equals(result.getResult_code())) {
                  for(SmsSendNumber number : result.getNumberList()) {
                      log.info(number.getSendnumber());

                  }

              }

              return result.getNumberList();

          } else {
              log.error("error in listSmsSendNumber", responseEntity.getBody().toString());

          }

      } catch (RestClientException e) {
          log.error("error in listSmsSendNumber", e);

      }

      return null;
  }
  
}
