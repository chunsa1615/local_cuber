package kr.co.cntt.scc.app.admin.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.Constants.ExamType;
import kr.co.cntt.scc.app.admin.common.ApiResult;
import kr.co.cntt.scc.app.admin.common.ApiResultConsts;
import kr.co.cntt.scc.app.admin.model.AppAdminMember;
import kr.co.cntt.scc.app.admin.model.AppAdminUserInfo;
import kr.co.cntt.scc.app.admin.model.accounting;
import kr.co.cntt.scc.app.admin.model.accounting.AccountingPay;
import kr.co.cntt.scc.app.admin.model.accountingMonth;
import kr.co.cntt.scc.app.admin.model.accountingMonth.AccountingMonthPay;
import kr.co.cntt.scc.app.admin.model.accountingMonth.Year;
import kr.co.cntt.scc.app.admin.model.administer;
import kr.co.cntt.scc.app.admin.model.administer.DeskRatio;
import kr.co.cntt.scc.app.admin.model.main;
import kr.co.cntt.scc.app.admin.model.main.DeskStatus;
import kr.co.cntt.scc.app.admin.model.main.Notice;
import kr.co.cntt.scc.app.admin.model.notice;
import kr.co.cntt.scc.app.admin.model.notice.NoticeDetail;
import kr.co.cntt.scc.app.admin.model.register;
import kr.co.cntt.scc.app.admin.model.register.RegisterPay;
import kr.co.cntt.scc.app.admin.model.register.RegisterReservation;
import kr.co.cntt.scc.app.admin.model.search;
import kr.co.cntt.scc.app.admin.model.search.SearchMember;
import kr.co.cntt.scc.app.admin.model.settingSearch;
import kr.co.cntt.scc.app.admin.model.settingSearch.ResponseVersion;
import kr.co.cntt.scc.app.admin.model.statisticsUser;
import kr.co.cntt.scc.app.admin.model.statisticsUser.Apart;
import kr.co.cntt.scc.app.admin.model.statisticsUser.Area;
import kr.co.cntt.scc.app.admin.model.statisticsUser.Statistics;
import kr.co.cntt.scc.app.admin.model.statisticsUser.Student;
import kr.co.cntt.scc.app.admin.model.statisticsUser.Time;
import kr.co.cntt.scc.app.admin.model.statusList;
import kr.co.cntt.scc.app.admin.model.statusList.StatusListDeskInfo;
import kr.co.cntt.scc.app.admin.model.statusList.StatusListEntry;
import kr.co.cntt.scc.app.admin.model.statusList.TotalLearning;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.model.BranchMember;
import kr.co.cntt.scc.model.Entry;
import kr.co.cntt.scc.model.Reservation;
import kr.co.cntt.scc.model.User;
import kr.co.cntt.scc.service.BranchDesignService;
import kr.co.cntt.scc.service.BranchMemberService;
import kr.co.cntt.scc.service.BranchPayService;
import kr.co.cntt.scc.service.BranchReservationService;
import kr.co.cntt.scc.service.BranchService;
import kr.co.cntt.scc.service.UserService;
import kr.co.cntt.scc.util.DateUtil;
import lombok.extern.slf4j.Slf4j;


//@RequestMapping(value="/push/subscription", method=RequestMethod.POST, headers = {"content-type=application/json"})
@RestController
@Slf4j
@RequestMapping("/app/admin/api/v1")
public class AppAdminApiV1Controller {

	//테스트
	String branchIdTest = "7ebf50c6-3b2e-41b0-9272-55c5bb80a839";
	String userIdTest = "e1e44309-c132-4b28-8614-04a18a38800a";
	String memberIdTest = "916f06ee-9805-4226-afcf-24c6df31bf00";
	String memberNmTest = "홍길동";
	
	//운영
	//String branchIdTest = "033f8817-71a0-4feb-bf7b-f9f184da7317";
	//String memberIdTest = "d42da894-584e-4830-a6cd-f41b43f2745d";
	//String memberNmTest = "권오중";
	
	//DecimalFormat Commas = new DecimalFormat("#,###");
	
    @Autowired
    UserService userService;
    
    @Autowired
    private BranchService branchService;

    @Autowired
    private BranchReservationService branchReservationService;
    
    @Autowired
    private BranchDesignService branchDesignService;
    
    @Autowired
    private BranchPayService branchPayService;
    
    @Autowired
    private BranchMemberService branchMemberService;
    
    @Autowired
    PasswordEncoder passwordEncoder;    
    
    @RequestMapping(value = "/login", method = { RequestMethod.POST, RequestMethod.GET })
    //public @ResponseBody
    //AppAdminUserInfo.Response getLogin(String name, String userPw) {
    //paramPagingNum 이 1이면 1~10 번째 데이터, 2이면 11~20 데이터
    public @ResponseBody ResponseEntity<?> getLogin(@RequestParam(value="userId", required=false) String paramName, @RequestParam(value="userPw", required=false) String paramPw,
    												@RequestParam(value="auto", defaultValue="FALSE", required=false) Boolean paramAuto, @RequestParam(value="uuid", required=false) String paramUuid,
    												@RequestParam(value="os", required=false) String paramOs, @RequestParam(value="version", required=false) String paramVersion, 
    												@RequestParam(value="device", required=false) String paramDevice, @RequestParam(value="pushId", required=false) String paramPushId) {
    	//public @ResponseBody ResponseEntity<?> getLogin(@RequestParam(value="userId", required=false) String paramName, @RequestParam(value="userPw", required=false) String paramPw ){
        //String userId = AuthUtil.getCurrentUserId();

//    	paramName = "testadmin";
//    	paramPw = "testadmin!@";    	
//    	paramUuid = "";
//    	paramOs = "";
//    	paramVersion = "";
//    	paramDevice = "";
//    	paramPushId = "";
    	//userPw = "5ab97e48c0f7c8f41bf6f7b3a70ba6079520d1255c4768b7ede7fcde5e71f222b34c47ea04badd57";

    	List<User> user = userService.findUser(paramName);
  	
        // 결과
        ModelMapper modelMapper = new ModelMapper();
        
    	AppAdminUserInfo.Response result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	
    	//파라미터 null체크
    	if (paramName == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_ID;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramPw == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_PASSWORD;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}  	    	
    	if (paramAuto == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_AUTO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramUuid == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_UUID;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}  
    	if (paramOs == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_OS;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramVersion == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_VERSION;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}  
    	if (paramDevice == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_DEVICE;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramPushId == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_PUSHID;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}     
    	
    	if (user.size() > 0 ) {
    		if (passwordEncoder.matches(paramPw, user.get(0).getPassword()) == true) {
    			String branchId = userService.selectPrimaryBranchIdByUserId(user.get(0).getUserId());
    			
	    		if (branchId != null) {
	    			Branch branch = branchService.selectBranch(branchId);
	    			if (branch != null) {
	    				// app_admin 정보 추가
	    				// paramName, paramAuto, paramUuid, paramOs, paramVersion, paramDevice, paramPushid
	    				
	    				
	    				//List<AppAdminUserInfo> AppAdminUserInfoList = userService.selectPushYn(paramName, branchId);
//	    				if (AppAdminUserInfoList.size() > 0) {
	    				int result = userService.insertAppAdmin(branchId, user.get(0).getUserId(), paramName, paramAuto, paramUuid, paramOs, paramVersion, paramDevice, paramPushId);
	    				//int result = userService.insertAppAdmin(paramName, branchId , true, null, null, null, null, null);
	    				
	    				if (result == 0) {
	    			            		
	    			    } else {
	    			    	List<AppAdminUserInfo> AppAdminUserInfoList = userService.selectPushYn(paramName, branchId, "LOGIN");
	    			    	result_data = modelMapper.map("", AppAdminUserInfo.Response.class);

		    				result_data.setUserId(user.get(0).getUserId());
		    				result_data.setBranchId(branch.getBranchId());
		    				result_data.setPushYn(AppAdminUserInfoList.get(0).isPushYn());
		    				
		    		        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
		    		            		        
		    		        header.setResult_code(result_code.getCode());
		    		        header.setResult_message(result_code.getMessage());
		    		            		        
		    		        apiResult = new ApiResult(result_data, header);    	
	    			    }

	    			} else {	    				
	    				result_code = ApiResultConsts.resultCode.ERROR_BRANCH_NOT_MATCH;
	        	        
	        	        header.setResult_code(result_code.getCode());
	        	        header.setResult_message(result_code.getMessage());
	        	        apiResult = new ApiResult(result_data, header);

	            		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	    			}
	    		}
    		}
    		else {
    			result_code = ApiResultConsts.resultCode.ERROR_LOGIN_NOT_MATCH;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    	}
    	else {
    		result_code = ApiResultConsts.resultCode.ERROR_LOGIN_NOT_MATCH;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
    	apiResult = new ApiResult(result_data, header);
    	
    	userService.insertLogHistory(result_data.getBranchId() ,result_data.getUserId(), "LOGIN");
    	
    	return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }
    
   
    @RequestMapping(value = "/main", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> getMainGET(@RequestHeader(value="userId", required=false) String paramUserId, @RequestHeader(value="branchId", required=false)String paramBranchId) {
    //public @ResponseBody ResponseEntity<?> getMainGET(@RequestParam(value="userId", required=true) String paramUserId, @RequestParam(value="branchId", required=true)String paramBranchId) {
    	//paramUserId = userIdTest;
    	//paramBranchId = branchIdTest;
    	// 결과
        ModelMapper modelMapper = new ModelMapper();        
        
        main.Response result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	
    	if (paramUserId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBranchId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

                
        DeskStatus desk_data = modelMapper.map("", DeskStatus.class);
        List<Reservation> reservationList = branchReservationService.selectReservationCountList(paramBranchId, "today");
        Integer deskList = branchDesignService.selectDeskCountList(paramBranchId);
        
        
        desk_data.setCurDesk(reservationList.size());
		//전체 좌석 수
		if(deskList != null && deskList > 0) {
			desk_data.setTotalDesk(deskList);
		}
		//입실율
		if (desk_data.getCurDesk() > 0) {
			double deskRatio = (double) desk_data.getCurDesk() / (double)desk_data.getTotalDesk() * 100;
			
			
			desk_data.setCurDeskRatio( Math.round(deskRatio * 100) / 100.0 );
			
		}
		else {
			desk_data.setCurDeskRatio(0.0);
		}
		
        
        //main.Notice notice_data = modelMapper.map("", main.Notice.class);
        List<Notice> noticeList = userService.getNoticeList(paramBranchId);   // new ArrayList();
        if (noticeList.size() > 0) {
        	for (Notice n : noticeList) {
        		n.setNoticeDt((DateUtil.getCurrentDateStringAppAdminParse(n.getNoticeDt()))); 
        	}
        }
        
        
        result_data = modelMapper.map("", main.Response.class);
        result_data.setName(branchService.selectBranchName(paramBranchId));
        result_data.setDeskStatus(desk_data);
        result_data.setNoticeList(noticeList);
        
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }    
    
    @RequestMapping(value = "/noticeList", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> getNoticeListGET(@RequestHeader(value="userId", required=false) String paramUserId, @RequestHeader(value="branchId", required=false)String paramBranchId) {        
    	//paramUserId = userIdTest;
    	//paramBranchId = branchIdTest;
    	
    	// 결과
        ModelMapper modelMapper = new ModelMapper();
                       
        notice.NoticeDetailResponse result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	
    	if (paramUserId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBranchId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
        //List<NoticeDetail> noticeList = new ArrayList();
    	List<NoticeDetail> noticeList = userService.getNoticeList2(paramBranchId);
    	if (noticeList.size() > 0) {
        	for (NoticeDetail n : noticeList) {
        		n.setNoticeDt((DateUtil.getCurrentDateStringAppAdminParse(n.getNoticeDt()))); 
        	}
        }
        
		 result_data = modelMapper.map("", notice.NoticeDetailResponse.class);
		result_data.setNoticeList(noticeList);

        
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }   
    
    @RequestMapping(value = "/noticeDetail", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> getNoticeDetailGET(@RequestHeader(value="userId", required=false) String paramUserId, @RequestHeader(value="branchId", required=false)String paramBranchId, @RequestParam(value="noticeId", required=false) String paramNoticeId) {
    	//paramUserId = userIdTest;
    	//paramBranchId = branchIdTest;
    	
    	// 결과
        ModelMapper modelMapper = new ModelMapper();

        notice.Response result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	
    	if (paramUserId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBranchId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramNoticeId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_NOTICEID;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}    	
    	
    	 List<notice.Response> noticeDetailList = userService.getNoticeDetail(paramBranchId, paramNoticeId);
    	
    	 
    	 result_data = modelMapper.map("", notice.Response.class);    	 
    	 result_data.setTitle(noticeDetailList.get(0).getTitle());
    	 result_data.setNoticeDt(DateUtil.getCurrentDateStringAppAdminParse(noticeDetailList.get(0).getNoticeDt()));
    	 result_data.setContent(noticeDetailList.get(0).getContent());
    	 
    	
    	//result_data = modelMapper.map("", notice.Response.class);
		
        //result_data.setNoticeDt("2017.06.19");
        //result_data.setTitle("7월 25일 씨엔티 스터디 카페 창업 설명회를 개최합니다.");
        //result_data.setContent("7월 25일 씨엔티 스터디 카페 창업 설명회를 개최합니다. <br> 많은 참여 바랍니다.");
        
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());

        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }
 
    
    @RequestMapping(value = "/userSearch", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> getUserSerchGET(@RequestHeader(value="userId", required=false) String paramUserId, @RequestHeader(value="branchId", required=false)String paramBranchId, 
    														@RequestParam(value="searchType", required=false) String paramSearchType, @RequestParam(value="name", required=false) String paramName,
    														@RequestParam(value="startDt", required=false) String paramStartDt, @RequestParam(value="endDt", required=false) String paramEndDt,
    														@RequestParam(value="roomType", required=false) String paramRoomType, @RequestParam(value="pagingNum", defaultValue="1", required=false) Integer pagingNum) {
//    public @ResponseBody ResponseEntity<?> getUserSerchGET(@RequestParam(value="branchId", required=true)String paramBranchId, 
//			@RequestParam(value="name", required=false) String paramName,
//			@RequestParam(value="pagingNum", required=true) Integer pagingNum) {
    	//String searchType = 회원검색(M)/열람실조회(R)/입퇴실현황(I)
//        paramUserId = "asdf" ;
//        paramBranchId = branchIdTest ;
//        paramSearchType = "M";
    	
    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        search.Response result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	
    	if (paramUserId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBranchId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}    	
    	if (paramSearchType== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_SEARCHTYPE;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	
    	
    	Integer paramPagingNum;
        if (pagingNum == null) {
        	paramPagingNum = 0;
        } else {
        	paramPagingNum = (pagingNum-1) * 10; //2일 경우 10, 3일 경우 20
        }
        
        Integer roomType = null;
        if(!StringUtils.isEmpty(paramRoomType)) {
        	if(paramRoomType.equals("MULTI")) {
        		roomType = 10;
        	}
        	else if (paramRoomType.equals("SINGLE")) {
        		roomType = 20;
        	}
        	else if (paramRoomType.equals("PRIVATE")) {
        		roomType = 30;
        	}
        }
        
        List<SearchMember> searchMemberList = branchReservationService.selectAppUserSearchList(paramBranchId, paramName, roomType, paramPagingNum);
        if (searchMemberList.size() > 0) {
        	List<SearchMember> searchMemberTotal = branchReservationService.selectAppUserSearchList(paramBranchId, paramName, roomType, null);
        	for (SearchMember sm : searchMemberList) {
        		sm.setReservationDt(DateUtil.getCurrentDateStringAppAdminParse(sm.getReservationDt()));
        	}
        	result_data = modelMapper.map("", search.Response.class);        	
            result_data.setTotalCount(searchMemberTotal.size());     
            result_data.setPagingNum(pagingNum);
            result_data.setMemberList(searchMemberList);
        } else {
    		result_code = ApiResultConsts.resultCode.ERROR_NO_CONTENT;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(null, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
        }        
        
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }
       
    
    //회원정보 상세
    @RequestMapping(value = "/userInfo", method = { RequestMethod.GET, RequestMethod.POST } )
    public @ResponseBody ResponseEntity<?> getUserSerchGET(@RequestHeader(value="userId", required=false) String paramUserId, @RequestHeader(value="branchId", required=false)String paramBranchId, 
    														@RequestParam(value="memberId", required=false) String paramMemberId) {
    	//paramUserId = " daf" ;
        //paramBranchId = branchIdTest;
        //paramMemberId = "f4b081ff-5b8f-4fc2-9959-053fa9256753";
    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        
        AppAdminMember.Response result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	
    	if (paramUserId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBranchId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}    	
    	if (paramMemberId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_MEMBERID;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}    	
    	
        AppAdminMember appAdminMember = branchReservationService.selectAppUserInfo(paramBranchId, paramMemberId);
        
        
        
        Object genderType = Constants.GenderType.getMap().get(Integer.parseInt(appAdminMember.getGender()));
        if (genderType == null) {
        	appAdminMember.setGender("선택안함");
        } else {
        	appAdminMember.setGender(genderType.toString());
        }
        
        Object examType = Constants.ExamType.getMap().get(Integer.parseInt(appAdminMember.getExamType()));
        if (examType == null) {
        	appAdminMember.setExamType("선택안함");
        } else {
        	appAdminMember.setExamType(examType.toString());
        }
        
        if (appAdminMember.getSchoolGrade().equals(String.valueOf(-1))) {
        	appAdminMember.setSchoolGrade("성인");
        } else {        	
        	appAdminMember.setSchoolGrade(String.valueOf(appAdminMember.getSchoolGrade()));
        }
        
        result_data = modelMapper.map(appAdminMember, AppAdminMember.Response.class);
		
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }
    
    
    //회원정보 상세
	@RequestMapping(value = "/userRegisterList", method = { RequestMethod.GET, RequestMethod.POST} )
    public @ResponseBody ResponseEntity<?> getUserRegisterListGET(@RequestHeader(value="userId", required=false) String paramUserId, @RequestHeader(value="branchId", required=false)String paramBranchId,
    															@RequestParam(value="memberId", required=false) String paramMemberId, @RequestParam(value="pagingNum", defaultValue="1", required=false) Integer pagingNum) {

//		paramUserId = " daf" ;
//        paramBranchId = branchIdTest;
//        paramMemberId = "f4b081ff-5b8f-4fc2-9959-053fa9256753";
        // 결과
        ModelMapper modelMapper = new ModelMapper();
        
        register.Response result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
	    	
    	if (paramUserId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBranchId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramMemberId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_MEMBERID;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}    	
    	
	    	
        Integer paramPagingNum;
        if (pagingNum == null) {
        	paramPagingNum = 0;
        } else {
        	paramPagingNum = (pagingNum-1) * 10; //2일 경우 10, 3일 경우 20
        }


        List<RegisterReservation> registerReservationList = branchPayService.selectAppRegisterPayList(paramBranchId, paramMemberId, paramPagingNum);
        List<RegisterReservation> registerReservationTotal = branchPayService.selectAppRegisterPayList(paramBranchId, paramMemberId, null);
        List<RegisterReservation> registerReservationListTemp = new ArrayList<RegisterReservation>();
        List<RegisterPay> registerPayList = new ArrayList<RegisterPay>();
        RegisterPay registerPay = new RegisterPay();
        
        String orderIdTemp = "";
        
        for (RegisterReservation rr : registerReservationList) {        	
        	Object payType = Constants.PayType.getMap().get(Integer.parseInt(rr.getPayType()));
            if (payType == null) {                	
            	rr.setPayType("선택안함");
            	//registerReservationListTemp.get(0).setPayType("선택안함");
            } else {
            	rr.setPayType(payType.toString());
            	//registerReservationListTemp.get(0).setPayType(payType.toString());
            }
            Object payStateType = Constants.PayStateType.getMap().get(Integer.parseInt(rr.getPayStateType()));
            if (payStateType == null) {                	
            	rr.setPayStateType("선택안함");
            	//registerReservationListTemp.get(0).setPayType("선택안함");
            } else {
            	rr.setPayStateType(payStateType.toString());
            	//registerReservationListTemp.get(0).setPayType(payType.toString());
            }
            Object reservationStatus = Constants.ReservationStatusType.getMap().get(Integer.parseInt(rr.getReservationStatus()));
            if (reservationStatus == null) {
            	rr.setReservationStatus("기타");
            } else {
            	rr.setReservationStatus(reservationStatus.toString());
            }
            //System.out.println("==========================================="+rr.getPayDt());
            rr.setPayDt(DateUtil.getCurrentDateStringAppAdminParse(rr.getPayDt()));            
            
        	if (orderIdTemp.equals(rr.getOrderId())) {        		
        		        		
        		registerReservationListTemp.add(rr);
        		registerPay.setOrderId(rr.getOrderId());        		
        		registerPay.setPayDt(rr.getPayDt());
        		registerPay.setName(rr.getName());        		
        		registerPay.setPayType(rr.getPayType());
        		registerPay.setPayAmount(rr.getPayAmount());
        		registerPay.setRoomName(rr.getRoomName());
        		registerPay.setDeskName(rr.getDeskName());
        		registerPay.setPayStateType(rr.getPayStateType());
        		registerPay.setRegisterReservationList(registerReservationListTemp);
        		
        		if (registerReservationList.indexOf(rr) == registerReservationList.size()-1) {
        			registerPayList.add(registerPay);
        			registerReservationListTemp = new ArrayList<RegisterReservation>();
        			registerPay = new RegisterPay();
        		}
        		
        	} else {
        		if (registerReservationList.indexOf(rr) == 0) {
        			
        		} else {
        			registerPayList.add(registerPay);
        			registerReservationListTemp = new ArrayList<RegisterReservation>();
        			registerPay = new RegisterPay();
        		}

        		registerReservationListTemp.add(rr);
        		registerPay.setOrderId(rr.getOrderId());
        		registerPay.setPayDt(rr.getPayDt());
        		registerPay.setName(rr.getName());
        		registerPay.setPayType(rr.getPayType());
        		registerPay.setPayAmount(rr.getPayAmount());
        		registerPay.setRoomName(rr.getRoomName());
        		registerPay.setDeskName(rr.getDeskName());
        		registerPay.setPayStateType(rr.getPayStateType());
        		registerPay.setRegisterReservationList(registerReservationListTemp);
        	}
        	
        	orderIdTemp = rr.getOrderId();
        	if (registerReservationList.indexOf(rr) == registerReservationList.size()-1) {
    			registerPayList.add(registerPay);    			
    		}
        	
        }
                
        
        result_data = modelMapper.map("", register.Response.class);
		       
        result_data.setTotalCount(registerReservationTotal.size());
        result_data.setPagingNum(pagingNum);
        result_data.setRegisterPayList(registerPayList);
        
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }
    
		
		
    //입퇴실현황 목록
	@RequestMapping(value = "/userStatusList", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> getUserStatusListGET(@RequestHeader(value="userId", required=false) String paramUserId, @RequestHeader(value="branchId", required=false)String paramBranchId,
																@RequestParam(value="memberId", required=false) String paramMemberId, @RequestParam(value="pagingNum", defaultValue="1", required=false) Integer pagingNum) {
//	public @ResponseBody ResponseEntity<?> getUserStatusListGET(@RequestParam(value="branchId", required=true)String paramBranchId,
//			@RequestParam(value="memberId", required=true) String paramMemberId, @RequestParam(value="pagingNum", defaultValue="1", required=true) Integer pagingNum) {
        // 결과
        ModelMapper modelMapper = new ModelMapper();
        
        statusList.Response result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
	    	
    	if (paramUserId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBranchId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramMemberId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_MEMBERID;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
        Integer paramPagingNum;
        if (pagingNum == null) {
        	paramPagingNum = 0;
        } else {
        	paramPagingNum = (pagingNum-1) * 10; //2일 경우 10, 3일 경우 20
        }


        StatusListDeskInfo statusListDeskInfo = branchMemberService.selectAppStatusListDeskInfo(paramBranchId, paramMemberId);        
    	Object entryType = Constants.EntryType.getMap().get(statusListDeskInfo.getEntryType());
        if (entryType == null) {                	
        	statusListDeskInfo.setEntryType("기타");
        } else {
        	statusListDeskInfo.setEntryType(entryType.toString());
        }
        
        List<StatusListEntry> totalLearningTm = branchMemberService.selectStatusListEntryList(paramBranchId, paramMemberId, null);
        List<StatusListEntry> statusListEntryList = branchMemberService.selectStatusListEntryList(paramBranchId, paramMemberId, paramPagingNum);        

        
        for (StatusListEntry se : statusListEntryList) {
        	
        	Object entryListType = Constants.EntryType.getMap().get(se.getEntryType());
            if (entryListType == null) {                	
            	se.setEntryType("기타");
            } else {
            	se.setEntryType(entryListType.toString());
            }
              
        }

        TotalLearning totalLearning = new TotalLearning();
        int diff = 0;
        for (StatusListEntry sle : totalLearningTm) {
        	////////total 시간 계산
            if (sle.getEntryType().equals("1")) {
            	//dateIn.
            	totalLearning.setEntryDt(sle.getEntryDtOg());
            	totalLearning.setEntryType("1");

            } else if (sle.getEntryType().equals("2")) {            	
            	if (totalLearning.getEntryType().equals("2")) {
            		
            	} else if (totalLearning.getEntryType().equals("1")) {
	            	Calendar startCal = Calendar.getInstance();
	            	Calendar endCal = Calendar.getInstance();
	            	
	            	startCal.setTime(totalLearning.getEntryDt());
	            	endCal.setTime(sle.getEntryDtOg());
	            	
	            	long diffMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
	            	diff += (int)(diffMillis/(1000 * 60));

	            	totalLearning = new TotalLearning();
	            	totalLearning.setEntryDt(sle.getEntryDtOg());
	            	totalLearning.setEntryType("2");
            	}
            }
        }
                
        result_data = modelMapper.map("", statusList.Response.class);
		       
        //result_data.setTotalCount(1);
        result_data.setTotalLearningTm(diff);
        result_data.setPagingNum(pagingNum);
        result_data.setTotalCount(totalLearningTm.size());
        result_data.setStatusListDeskInfo(statusListDeskInfo);
        result_data.setStatusListEntryList(statusListEntryList);
        
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }
	
	
	//회계관리(매출조회)
	@RequestMapping(value = "/accountingSearch", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> getAccountingSearchGET(@RequestHeader(value="userId", required=false) String paramUserId, @RequestHeader(value="branchId", required=false)String paramBranchId,
													    		@RequestParam(value="startDt", required=false) String paramStartDt, @RequestParam(value="endDt", required=false) String paramEndDt,
													    		@RequestParam(value="searchType", required=false) String paramSearchType, @RequestParam(value="pagingNum", defaultValue="1", required=false) Integer pagingNum) {	
																// searchType = 매출(INCOME)/지출(EXPENSE)/미수금(RECEIVABLE)
		//paramUserId = userIdTest;
		//paramBranchId = branchIdTest;
		//paramSearchType = "EXPENSE";
		//paramStartDt = "17-05-20";
		//paramEndDt = "17-08-20";
		
        // 결과
        ModelMapper modelMapper = new ModelMapper();
        
        accounting.Response result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
	    	
    	if (paramUserId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBranchId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramStartDt== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_STARTDT;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramEndDt== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_ENDDT;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramSearchType== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_SEARCHTYPE;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}    	
    	
    	
		Integer paramPagingNum;
        if (pagingNum == null) {
        	paramPagingNum = 0;
        } else {
        	paramPagingNum = (pagingNum-1) * 10; //2일 경우 10, 3일 경우 20
        }
        
        
        List<AccountingPay> accountingPayList = branchPayService.selectAccountingPayList(paramBranchId, paramStartDt, paramEndDt, paramSearchType, paramPagingNum);        
        List<AccountingPay> accountingPayTotal = branchPayService.selectAccountingPayList(paramBranchId, paramStartDt, paramEndDt, paramSearchType, null);
        //AccountingPay accountingPay = new AccountingPay();
        //data1.add(accountingPay);
        if (accountingPayList.size() > 0) {
	        for (AccountingPay ap : accountingPayList) {
	        	ap.setPayDt(DateUtil.getCurrentDateStringAppAdminParse(ap.getPayDt()));
	            if (paramSearchType.equals("INCOME")) {
	            	ap.setPayInOutType("매출");
	            } else if (paramSearchType.equals("EXPENSE")){
	            	ap.setPayInOutType("지출");
	            	Object expenseGroup = Constants.ExpenseGroup.getMap().get(Integer.parseInt(ap.getExpenseGroup()));
	            	Object expenseOption = Constants.ExpenseOption.getMap().get(Integer.parseInt(ap.getExpenseOption()));
	            	
	            	ap.setExpenseType(expenseGroup.toString()+"("+expenseOption.toString()+")");
	            } else if (paramSearchType.equals("RECEIVABLE")){
	            	ap.setPayInOutType("미수금");	            	
	            	
	            } else {
	            	ap.setPayInOutType("기타");
	            }
	        }
        }

        result_data = modelMapper.map("", accounting.Response.class);
		result_data.setPagingNum(pagingNum);
        result_data.setTotalCount(accountingPayList.size());
        //현재 매출현황(해당 월 1일부터 ~ 현재까지)
        String monthPay = branchPayService.selectMonthPayAmount(paramBranchId);
		if (monthPay != null){
			Integer payAmount = Integer.parseInt(monthPay);
			if (payAmount > 0) {				
				result_data.setMonthAmount(monthPay);
			}
			else {
				
				result_data.setMonthAmount("0");
			}
		}
		else {
			result_data.setMonthAmount("0");
		}
		
		//현재 매출현황(당일)
		String todayPay = branchPayService.selectTodayPayAmount(paramBranchId);
		if (todayPay != null){
			Integer todayPayAmount = Integer.parseInt(todayPay);
			if (todayPayAmount > 0) {
				result_data.setCurAmount(todayPay);
			}
			else {
				result_data.setCurAmount("0");
			}
		}
		else {
			result_data.setCurAmount("0");
		}
		
		
		//현금
		String cashPay = branchPayService.selectPayTypeAmount(paramBranchId, paramStartDt, paramEndDt, 1);
		if (cashPay != null){
			Integer receivablePayAmount = Integer.parseInt(cashPay);
			if (receivablePayAmount > 0) {
				result_data.setCashAmount(cashPay);
			}
			else {
				result_data.setCashAmount("0");
			}
		}
		else {
			result_data.setCashAmount("0");
		}
				
		//카드
		String cardPay = branchPayService.selectPayTypeAmount(paramBranchId, paramStartDt, paramEndDt, 2);
		if (cardPay != null){
			Integer receivablePayAmount = Integer.parseInt(cardPay);
			if (receivablePayAmount > 0) {
				result_data.setCardAmount(cardPay);
			}
			else {
				result_data.setCardAmount("0");
			}
		}
		else {
			result_data.setCardAmount("0");
		}
		
		//미수금
		String receivablePay = branchPayService.selectPayTypeAmount(paramBranchId, paramStartDt, paramEndDt, 3);
		if (receivablePay != null){
			Integer receivablePayAmount = Integer.parseInt(receivablePay);
			if (receivablePayAmount > 0) {
				result_data.setReceivableAmount(receivablePay);
			}
			else {
				result_data.setReceivableAmount("0");
			}
		}
		else {
			result_data.setReceivableAmount("0");
		}
		
        result_data.setTotalCount(accountingPayTotal.size());
        result_data.setAccountingPayList(accountingPayList);
        
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }
	

	
	//회계관리(매출조회)
	@RequestMapping(value = "/accountingMonthSearch", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> getAccountingMonthSearchGET( @RequestHeader(value="userId", required=false) String paramUserId, @RequestHeader(value="branchId", required=false)String paramBranchId) {
		//paramUserId = userIdTest;
		//paramBranchId =  branchIdTest;
        // 결과
        ModelMapper modelMapper = new ModelMapper();
        
        accountingMonth.Response result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
	    	
    	if (paramUserId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBranchId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
        List<AccountingMonthPay> accountingMonthPayList = branchPayService.selectaccountingMonthPayAmount(paramBranchId);              
        
        //AccountingMonthPay accountingMonthPay = new AccountingMonthPay();
        //data1.add(accountingMonthPay);
        List<Year> yearList =  new ArrayList(); //new ArrayList<Year>();
        List<AccountingMonthPay> AccountingMonthPayTemp = new ArrayList<AccountingMonthPay>();
        String yearTemp = "";
        Year year = new Year();
        
        Integer totalAmount = 0;
        for (AccountingMonthPay amp : accountingMonthPayList ) {        	
	
        	if (yearTemp.equals(amp.getYear())) {
        		year.setYear(amp.getYear());
        		AccountingMonthPayTemp.add(amp);
            	year.setAccountingMonthPayList( AccountingMonthPayTemp );
            	if (accountingMonthPayList.indexOf(amp) == accountingMonthPayList.size()-1) { 
            		yearList.add(year); 
            		AccountingMonthPayTemp = new ArrayList<AccountingMonthPay>();
            		year = new Year();
            	}

        	} else {        		
	    		if (accountingMonthPayList.indexOf(amp) == 0) {
	        		
	    		} else {
	    			yearList.add(year);
	    			AccountingMonthPayTemp = new ArrayList<AccountingMonthPay>();
	    			year = new Year();
	    		}
        		        		
        		year.setYear(amp.getYear());
            	AccountingMonthPayTemp.add(amp);
            	year.setAccountingMonthPayList(AccountingMonthPayTemp);
            	if (accountingMonthPayList.indexOf(amp) == accountingMonthPayList.size()-1) { 
            		yearList.add(year);             		
            	}

        	}
        	totalAmount += Integer.parseInt(amp.getAmount());
        	yearTemp = amp.getYear();
        	
        }
        
        //data2.add(year);

        result_data = modelMapper.map("", accountingMonth.Response.class);		       

        result_data.setYearList(yearList);
        result_data.setTotalAmount(totalAmount.toString());
        
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }
	
	
		
	//통계관리
	@RequestMapping(value = "/statisticsUser", method = { RequestMethod.GET, RequestMethod.POST} )
    public @ResponseBody ResponseEntity<?> getStatisticsUserGET(@RequestHeader(value="userId", required=false) String paramUserId, @RequestHeader(value="branchId", required=false)String paramBranchId,
													    		@RequestParam(value="startDt", required=false) String paramStartDt, @RequestParam(value="endDt", required=false) String paramEndDt,
													    		@RequestParam(value="searchType", required=false) String paramSearchType) {
	//public @ResponseBody ResponseEntity<?> getStatisticsUserGET(@RequestParam(value="searchType", required=true) String paramSearchType) {
		//paramUserId = userIdTest;
		//paramBranchId = branchIdTest;
		// SearchType 직업(S)/지역(A)/시험(E)/누적시간(T)				
        // 결과
        ModelMapper modelMapper = new ModelMapper();

        statisticsUser.Response resultJobData = null;
        statisticsUser.AreaResponse resultAreaData = null;
        statisticsUser.JobResponse resultJob2Data = null;
        statisticsUser.TimeResponse resultTimeData = null;
        
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
	    	
    	if (paramUserId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());	        
	        apiResult = new ApiResult(null, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBranchId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(null, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramSearchType== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_SEARCHTYPE;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(null, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	//totalMemberCount
    	List<BranchMember> memberList = branchMemberService.selectAllMemberList(paramBranchId);
    	
        List<Statistics> data1 = new ArrayList();
        Statistics statistics = new Statistics();
        statistics.setType("세무직");
        statistics.setCount("24");
        data1.add(statistics);
        statistics = new Statistics();
        statistics.setType("소방직");
        statistics.setCount("20");
        data1.add(statistics);
        statistics = new Statistics();
        statistics.setType("경찰직");
        statistics.setCount("18");
        data1.add(statistics);
        statistics = new Statistics();
        statistics.setType("임용고시");
        statistics.setCount("15");
        data1.add(statistics);
        statistics = new Statistics();
        statistics.setType("편입");
        statistics.setCount("23");
        data1.add(statistics);
        
        statisticsUser.Student student_data = modelMapper.map("", statisticsUser.Student.class);
        student_data.setJob("고등학생");
        student_data.setRatio("35");
        
        //홍제동, 대현동, 대신동, 신촌동, 봉원동, 창천동, 연희동, 홍은동, 북가좌동, 남가좌동,
        List<Area> data2 = new ArrayList();
        Area area = new Area();
        area.setArea("홍제동");
        
        List<Student> studentList = new ArrayList();
        Student student = new Student();
        
        student.setJob("중학생");
        student.setRatio("25");
        studentList.add(student);
        
        student = new Student();
        student.setJob("고등학생");
        student.setRatio("32");
        studentList.add(student);
        
        student = new Student();
        student.setJob("대학생");
        student.setRatio("28");
        studentList.add(student);
        
        student = new Student();
        student.setJob("직장인");
        student.setRatio("15");
        studentList.add(student);
        
        
        area.setStudentList(studentList);        
        data2.add(area);
        
        area = new Area();
        area.setArea("북가좌동");
        
        studentList = new ArrayList();
        student = new Student();
        student.setJob("고등학생");
        student.setRatio("42");
        studentList.add(student);
        
        student = new Student();
        student.setJob("성인");
        student.setRatio("19");
        studentList.add(student);
        
        student = new Student();
        student.setJob("대학생");
        student.setRatio("28");
        studentList.add(student);
        
        student = new Student();
        student.setJob("직장인");
        student.setRatio("11");
        studentList.add(student);

        area.setStudentList(studentList); //지역별        
        data2.add(area);
        
        area = new Area();
        area.setArea("신촌동");
        
        studentList = new ArrayList();
        student = new Student();
        student.setJob("고등학생");
        student.setRatio("27");
        studentList.add(student);
        
        student = new Student();
        student.setJob("성인");
        student.setRatio("5");
        studentList.add(student);
        
        student = new Student();
        student.setJob("대학생");
        student.setRatio("36");
        studentList.add(student);
        
        student = new Student();
        student.setJob("직장인");
        student.setRatio("32");
        studentList.add(student);

        area.setStudentList(studentList); //지역별        
        data2.add(area);
        
        area = new Area();
        area.setArea("연희동");
        
        studentList = new ArrayList();
        student = new Student();
        student.setJob("고등학생");
        student.setRatio("50");
        studentList.add(student);
        
        student = new Student();
        student.setJob("성인");
        student.setRatio("23");
        studentList.add(student);
        
        student = new Student();
        student.setJob("대학생");
        student.setRatio("20");
        studentList.add(student);
        
        student = new Student();
        student.setJob("직장인");
        student.setRatio("7");
        studentList.add(student);

        area.setStudentList(studentList); //지역별        
        data2.add(area);

        
        //아파트 데이터
        List<Apart> apartList = new ArrayList();
        Apart apart = new Apart();
        apart.setName("아이파크 아파트");
        apart.setRatio("14");
        apartList.add(apart);
        
        apart = new Apart();
        apart.setName("자이 아파트");
        apart.setRatio("32");
        apartList.add(apart);
        
        apart = new Apart();
        apart.setName("센트레빌 아파트");
        apart.setRatio("9");
        apartList.add(apart);
        
        apart = new Apart();
        apart.setName("래미안 아파트");
        apart.setRatio("26");
        apartList.add(apart);
        
        apart = new Apart();
        apart.setName("월드컵파크 아파트");
        apart.setRatio("19");
        apartList.add(apart);
        
        ////////////////////////////////////////////////////////직업별
        List<Student> studentList2List = new ArrayList();
        Student student2 = new Student();
        student2.setJob("중학생");
        student2.setRatio("25");
        studentList2List.add(student2);
        
        student2 = new Student();
        student2.setJob("고등학생");
        student2.setRatio("42");
        studentList2List.add(student2);
        
        student2 = new Student();
        student2.setJob("대학생");
        student2.setRatio("18");
        studentList2List.add(student2);
        
        student2 = new Student();
        student2.setJob("편입생");
        student2.setRatio("9");
        studentList2List.add(student2);
        
        student2 = new Student();
        student2.setJob("재수생");
        student2.setRatio("6");
        studentList2List.add(student2);
        
        
        ////////////////////////////////////////누적시간별
        List<Time> timeList = new ArrayList();
        Time time = new Time();
        time.setTime("10시간 이하");
        time.setRatio("10");
        timeList.add(time);
        
        time = new Time();
        time.setTime("20시간 이하");
        time.setRatio("29");
        timeList.add(time);
        
        time = new Time();
        time.setTime("50시간 이하");
        time.setRatio("45");
        timeList.add(time);
        
        time = new Time();
        time.setTime("100시간 이하");
        time.setRatio("21");
        timeList.add(time);
        
        resultJobData = modelMapper.map("", statisticsUser.Response.class);
        resultAreaData = modelMapper.map("", statisticsUser.AreaResponse.class);
        resultJob2Data = modelMapper.map("", statisticsUser.JobResponse.class);
        resultTimeData = modelMapper.map("", statisticsUser.TimeResponse.class);
        result_code = null;
        apiResult = null;
        
                
        if (paramSearchType != null) {
        	
	        if (paramSearchType.equals("S")) { //직업별
	        	resultJob2Data.setTotalMemberCount(memberList.size());
	        	resultJob2Data.setJobList(studentList2List);

	        	result_code = (resultJob2Data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
	        	header = modelMapper.map("", ApiResult.header.class);
	            header.setResult_code(result_code.getCode());
	            header.setResult_message(result_code.getMessage());
	        	apiResult = new ApiResult(resultJob2Data, header);
	        }
	        else if (paramSearchType.equals("A")) { //지역별
	        	resultAreaData.setTotalMemberCount(memberList.size());
	        	resultAreaData.setAreaList(data2);
	        	resultAreaData.setApartList(apartList);	        		        	
	        	result_code = (resultAreaData != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
	        	header = modelMapper.map("", ApiResult.header.class);
	            header.setResult_code(result_code.getCode());
	            header.setResult_message(result_code.getMessage());
	        	apiResult = new ApiResult(resultAreaData, header);
	        	
	        }
	        else if (paramSearchType.equals("E")) { //시험별
	        	resultJobData.setStatisticsList(data1);
	        	resultJobData.setTotalMemberCount(memberList.size());
	        	result_code = (resultJobData != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
	        	header = modelMapper.map("", ApiResult.header.class);
	            header.setResult_code(result_code.getCode());
	            header.setResult_message(result_code.getMessage());
	        	apiResult = new ApiResult(resultJobData, header);
	        	
	        }
	        else if (paramSearchType.equals("T")) { //누적시간별
	        	resultTimeData.setTimeList(timeList);
	        	resultTimeData.setTotalMemberCount(memberList.size());
	        	result_code = (resultTimeData != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
	        	header = modelMapper.map("", ApiResult.header.class);
	            header.setResult_code(result_code.getCode());
	            header.setResult_message(result_code.getMessage());
	        	apiResult = new ApiResult(resultTimeData, header);
	        }
	        else {
	        	resultJobData.setStatisticsList(data1);
	        	result_code = (resultJobData != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
	        	header = modelMapper.map("", ApiResult.header.class);
	            header.setResult_code(result_code.getCode());
	            header.setResult_message(result_code.getMessage());
	        	apiResult = new ApiResult(resultJobData, header);
	        }
        } else {
        	resultJobData.setStatisticsList(data1);
        	result_code = (resultJobData != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        	header = modelMapper.map("", ApiResult.header.class);
            header.setResult_code(result_code.getCode());
            header.setResult_message(result_code.getMessage());
        	apiResult = new ApiResult(resultJobData, header);
        }

        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }
	
		
	//설정(조회)
	@RequestMapping(value = "/settingSearch", method = { RequestMethod.GET, RequestMethod.POST } )
    public @ResponseBody ResponseEntity<?> getSettingSearchGET(@RequestHeader(value="userId", required=false) String paramUserId, @RequestHeader(value="branchId", required=false)String paramBranchId) {        
		// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
	    	
    	if (paramUserId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());	        
	        apiResult = new ApiResult(null, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBranchId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(null, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
        
    	settingSearch.Response result_data = modelMapper.map("", settingSearch.Response.class);
       
        
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }
	
	
	//설정(조회)
	@RequestMapping(value = "/administer", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> getAdministerGET(@RequestHeader(value="userId", required=false) String paramUserId, @RequestHeader(value="branchId", required=false)String paramBranchId) {        
		//paramUserId = userIdTest;
		//paramBranchId = branchIdTest;
		// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
	    	
    	if (paramUserId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());	        
	        apiResult = new ApiResult(null, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBranchId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(null, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
        administer.CurDeskStatus curDesk_data = modelMapper.map("", administer.CurDeskStatus.class);
        List<Reservation> reservationList = branchReservationService.selectReservationCountList(paramBranchId, "today");
        Integer deskList = branchDesignService.selectDeskCountList(paramBranchId);
        
        double deskRatio = 0;
        if (deskList != null && deskList >0) {
        	curDesk_data.setTotalDesk(deskList);
        }
        curDesk_data.setCurDesk(reservationList.size());
        if (curDesk_data.getCurDesk() > 0) {
        	deskRatio = (double) curDesk_data.getCurDesk() / (double)curDesk_data.getTotalDesk() * 100;
        	curDesk_data.setCurDeskRatio(Math.round(deskRatio *100) / 100.0);
        } else {
        	curDesk_data.setCurDeskRatio(0.0);
        }
        
        administer.BeforeDeskStatus beforeDesk_data = modelMapper.map("", administer.BeforeDeskStatus.class);
        //전일변동 
		List<Reservation> reservationTwodaysagoList = branchReservationService.selectReservationCountList(paramBranchId, "twodaysago");
		List<Reservation> reservationYesterdayList = branchReservationService.selectReservationCountList(paramBranchId, "yesterday");
        
        double beforeDeskRatio = 0;
        if (reservationYesterdayList.size() > 0) {        	 
        	beforeDeskRatio = ((double)reservationYesterdayList.size() - (double)reservationTwodaysagoList.size() ) / (double)reservationTwodaysagoList.size() * 100;
        	beforeDesk_data.setBeforeDeskRatio(Math.round(beforeDeskRatio * 100) / 100.0);
        } else {
        	beforeDesk_data.setBeforeDeskRatio(0.0);
        }
                
        
        //administer.DeskRatio deskRatio_data = modelMapper.map("", administer.DeskRatio.class);
        //List<DeskRatio> deskRatioList = new ArrayList();
        
        //List<DeskRatio> deskRatioList = branchReservationService.selectAppRegisterRoomTypeCountList(paramBranchId); //룸 타입에 따른 입실률
        
        List<DeskRatio> deskRatioList = new ArrayList();
        DeskRatio deskRatioObject = new DeskRatio();
        
        if (reservationList.size() > 0) {
        	deskRatioObject.setDeskRatio(Math.round(deskRatio *100) / 100.0);
        	deskRatioObject.setDeskType("입실률");
        	deskRatioList.add(deskRatioObject);
        	
        	deskRatioObject = new DeskRatio();
        	deskRatioObject.setDeskRatio(100 - (Math.round(deskRatio *100) / 100.0));
        	deskRatioObject.setDeskType("공석률");
        	deskRatioList.add(deskRatioObject);
        	
        } else {        	 
        	deskRatioObject.setDeskRatio(0.0);
        	deskRatioObject.setDeskType("입실률");
        	deskRatioList.add(deskRatioObject);
        	
        	deskRatioObject = new DeskRatio();
        	deskRatioObject.setDeskRatio(100.0);
        	deskRatioObject.setDeskType("공석률");
        	deskRatioList.add(deskRatioObject);
        }
        
        
        administer.Response result_data = modelMapper.map("", administer.Response.class);
        result_data.setCurDeskStatus(curDesk_data);
        result_data.setBeforeDeskStatus(beforeDesk_data);
        result_data.setDeskRatio(deskRatioList);
        
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }
	
	
	@RequestMapping(value = "/version", method = { RequestMethod.GET, RequestMethod.POST } )
    public @ResponseBody ResponseEntity<?> getVersionGET(@RequestParam(value="os") String paramOs) {
        //안드로이드(A)/아이폰(I)

		// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
	    	
    	if (paramOs == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_OS;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());	        
	        apiResult = new ApiResult(null, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
        
        Integer type = 10;
        List<ResponseVersion> responseVersion = userService.selectVersion(paramOs, type);
        settingSearch.ResponseVersion result_data = modelMapper.map("", settingSearch.ResponseVersion.class);
        result_data.setVersion(responseVersion.get(0).getVersion());
        result_data.setUpdateYn(responseVersion.get(0).isUpdateYn());
        
        
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }
	
	
	//설정(조회)
	@RequestMapping(value = "/setting", method = { RequestMethod.GET, RequestMethod.POST } )
    public @ResponseBody ResponseEntity<?> getSettingGET(@RequestHeader(value="userId", required=false) String paramUserId, @RequestHeader(value="branchId", required=false)String paramBranchId, @RequestParam(value="pushYn", required=false) Boolean paramPushYn) {
		//paramUserId = userIdTest;
		//paramBranchId = branchIdTest;

		//paramPushYn = false;
		// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
	    	
    	if (paramUserId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());	        
	        apiResult = new ApiResult(null, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBranchId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(null, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
		
		List<AppAdminUserInfo> AppAdminUserInfoList = userService.selectPushYn(paramUserId, paramBranchId, "SETTING");
		
        settingSearch.ResponseSetting result_data = modelMapper.map("", settingSearch.ResponseSetting.class);
        if (AppAdminUserInfoList.size() > 0 ) {
        	result_data.setPushYn(paramPushYn);
        	userService.updatePushYn(paramUserId, paramBranchId, paramPushYn);
        } else {
			result_code = ApiResultConsts.resultCode.ERROR_NO_CONTENT;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());	        
	        apiResult = new ApiResult(null, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
        }
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }
	
	
	//로그아웃
	@RequestMapping(value = "/logout", method = { RequestMethod.GET, RequestMethod.POST } )
	public @ResponseBody ResponseEntity<?> getLogout(@RequestHeader(value="userId", required=false) String paramUserId, @RequestHeader(value="branchId", required=false)String paramBranchId) {

		// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
	    	
    	if (paramUserId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());	        
	        apiResult = new ApiResult(null, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBranchId== null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(null, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	settingSearch.ResponseLogout result_data = modelMapper.map("", settingSearch.ResponseLogout.class);
    	int result = userService.insertLogHistory(paramBranchId, paramUserId, "LOGOUT");
    	if (result == 0) {
    		result_data.setSuccessYn(false);
    	} else { 
    		result_data.setSuccessYn(true);
    	}
    	
    	result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
    	return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	
	
	
}
