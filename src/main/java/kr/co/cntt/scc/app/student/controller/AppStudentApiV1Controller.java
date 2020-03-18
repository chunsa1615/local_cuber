package kr.co.cntt.scc.app.student.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Database.FileFormat;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.TableBuilder;
import com.healthmarketscience.jackcess.util.ColumnValidator;
import com.healthmarketscience.jackcess.util.SimpleColumnValidator;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.Constants.AreaTypes;
import kr.co.cntt.scc.Constants.InterestTypes;
import kr.co.cntt.scc.Constants.JobTypes;
import kr.co.cntt.scc.app.admin.common.ApiResult;
import kr.co.cntt.scc.app.admin.common.ApiResultConsts;
import kr.co.cntt.scc.app.admin.controller.APIController;
import kr.co.cntt.scc.app.admin.model.register.RegisterReservation;
import kr.co.cntt.scc.app.admin.model.settingSearch;
import kr.co.cntt.scc.app.admin.model.settingSearch.ResponseVersion;
import kr.co.cntt.scc.app.admin.model.statusList.StatusListEntry;
import kr.co.cntt.scc.app.admin.model.statusList.TotalLearning;
import kr.co.cntt.scc.app.admin.service.AppSmsCertify;
import kr.co.cntt.scc.app.student.model.AppClientBranch;
import kr.co.cntt.scc.app.student.model.AppClientMember;
import kr.co.cntt.scc.app.student.model.CommonCode;
import kr.co.cntt.scc.app.student.model.Mypage;
import kr.co.cntt.scc.app.student.model.Rank;
import kr.co.cntt.scc.app.student.model.ReportingTime;
import kr.co.cntt.scc.app.student.model.ReservationBranch;
import kr.co.cntt.scc.app.student.model.Result;
import kr.co.cntt.scc.app.student.model.StatisticsEntry;
import kr.co.cntt.scc.app.student.model.Student;
import kr.co.cntt.scc.app.student.model.UpdateCenter;
import kr.co.cntt.scc.app.student.model.join;
import kr.co.cntt.scc.app.student.service.AppClientBranchService;
import kr.co.cntt.scc.app.student.service.AppClientMemberService;
import kr.co.cntt.scc.app.student.service.SeatApplicationService;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.model.BranchMember;
import kr.co.cntt.scc.model.Entry;
import kr.co.cntt.scc.model.SafeService;
import kr.co.cntt.scc.model.SeatReservation;
import kr.co.cntt.scc.service.BranchMemberService;
import kr.co.cntt.scc.service.BranchPayService;
import kr.co.cntt.scc.service.BranchSafeServiceService;
import kr.co.cntt.scc.service.BranchSeatReservationService;
import kr.co.cntt.scc.service.BranchService;
import kr.co.cntt.scc.service.HistoryService;
import kr.co.cntt.scc.service.SmsService;
import kr.co.cntt.scc.service.UserService;
import kr.co.cntt.scc.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/app/client/api/v1")
public class AppStudentApiV1Controller {
	
	
	
	@Autowired
	private AppClientMemberService appMemberService;
	
	@Autowired
	private BranchService branchService;
	
	@Autowired
	private AppClientBranchService appClientBranchService;
	
	@Autowired
	private BranchSafeServiceService branchSafeServiceService;
	
	@Autowired
	private SeatApplicationService seatApplicationService;
	
	@Autowired
	private BranchMemberService branchMemberService;
	
	@Autowired
	private BranchPayService branchPayService;
	
	@Autowired
	private SmsService smsService;
	
	@Autowired
	private AppSmsCertify appSmsCertify;
	
	@Autowired
	private BranchSeatReservationService branchSeatReservationService;
	
	//@Autowired
	//private PushService pushService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private HistoryService historyService;
	
	
//	@RequestMapping(value = "/pushTest", method = { RequestMethod.GET, RequestMethod.POST })
//    public @ResponseBody ResponseEntity<?> pushTest(@RequestParam(value="id", required=false) String paramId) {
//        
//		
//		ModelMapper modelMapper = new ModelMapper();
//        
//        join.RegistYnResponse result_data = modelMapper.map("", join.RegistYnResponse.class);
//    	ApiResultConsts.resultCode result_code = null;
//    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
//    	ApiResult apiResult = null;
//    	    	
//    	ApiResultPush apiResultPush =  pushService.IPhonePushSend(paramId);
//    	
//    	System.out.println("==============================resultpush============"+apiResultPush.getResult_code() + "/////////////" + apiResultPush.getResult_message());
//    	
//    	result_data.setRegistYn(true);
//    	
//		result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
//	    
//	    header = modelMapper.map("", ApiResult.header.class);
//	    header.setResult_code(result_code.getCode());
//	    header.setResult_message(result_code.getMessage());
//	    
//	    apiResult = new ApiResult(result_data, header);
//	    
//	    return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
//	}
//	
	
	
	
	@RequestMapping(value = "/checkPw", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> checkPw(@RequestParam(value="id", required=false) String paramId, @RequestParam(value="pw", required=false)String paramPw) {
		
		ModelMapper modelMapper = new ModelMapper();
        
        Result.Response result_data = modelMapper.map("", Result.Response.class);
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	
    	if (paramId == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	} else {
    		Pattern p = Pattern.compile("^[a-zA-Z]{1}[a-zA-Z0-9_].{4,18}$");
    		Matcher m = p.matcher(paramId);
    		if (m.find()) {
    			
    		} else {
    			result_data.setResultYn(false);
    			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_PATTERN_ID;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

    			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    		
    	}	

    	if (paramPw == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	} else {
    		Pattern p = Pattern.compile("([a-zA-Z0-9].*[!,@,#,$,%,^,&,*,?,_,~])$|([!,@,#,$,%,^,&,*,?,_,~].*[a-zA-Z0-9])$");
    		Matcher m = p.matcher(paramPw);
    		if (m.find() && (paramPw.length() > 7 && paramPw.length() < 21)) {
    			
    		} else {
    			result_data.setResultYn(false);
    			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_PATTERN_PW;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

    			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    	}	
    	
    	
    	result_data.setResultYn(true);
    	
		result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
		    
	    header = modelMapper.map("", ApiResult.header.class);
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    
	    apiResult = new ApiResult(result_data, header);
	    
	    return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		    
	}
	
	
    @RequestMapping(value = "/registYn", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> registYn(@RequestParam(value="name", required=false) String paramName, @RequestParam(value="gender", required=false)String paramGender, 
    											@RequestParam(value="birthDt", required=false) String paramBirthDt, @RequestParam(value="tel", required=false)String paramTel, 
    											@RequestParam(value="address1", required=false)String paramAddress1, @RequestParam(value="role", required=false) Integer paramRole) {
    //public @ResponseBody ResponseEntity<?> join01(@RequestParam(value="name", required=false) String paramName, @RequestParam(value="tel", required=false)String paramTel ) {
//    	paramName = "김기덕3";
//    	paramTel = "8737";
//    	paramGender = "10";
//    	paramBirthDt = "";
//    	paramAddress1 = "";
//    	paramRole = 10;
    	//paramBranchId = branchIdTest;
    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        join.RegistYnResponse result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	    	
    	if (paramName == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramGender == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramBirthDt == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramTel == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramAddress1 == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramRole == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	
    	
    	result_data = modelMapper.map("", join.RegistYnResponse.class);
    	
    	if (paramRole == 10) { //일반일 경우
    		List<join.RegistYnResponse> join01List =  appMemberService.selectAppStudentMember(paramName, paramTel); //학생 회원
    		
    		if (join01List.size() > 0) {
        		result_data.setRegistYn(true);
                
        		int idSize = 3;
        		if (join01List.get(0).getId().length() > 0 ) {
        			idSize = join01List.get(0).getId().length()/2;
        		} else {
        			
        		}
        		
        		result_data.setId(join01List.get(0).getId().replaceAll("(?<=.{"+ idSize +"}).", "*"));
        		String strJoinDt = DateUtil.getCurrentDateStringAppAdminParse(join01List.get(0).getJoinDt().substring(0, 10));
        		result_data.setJoinDt(strJoinDt);
        		//result_data.setNo(join01List.get(0).getNo());
        	} else { //회원 데이터 없음        		
    			result_data.setRegistYn(false);

        	}
    	} 
    	else if(paramRole == 20) {
    		List<join.RegistYnResponse> join01List =  appMemberService.selectAppParentsMember(paramName, paramTel); //학부모 회원
    		
    		if (join01List.size() > 0) {
	    		result_data.setRegistYn(true);
	    		result_data.setId(join01List.get(0).getId());
	    		result_data.setJoinDt(join01List.get(0).getJoinDt());
	    		//result_data.setNo(join01List.get(0).getNo());
    		} else { //회원 데이터 없음    			
    			result_data.setRegistYn(false);

    			
    		}
    	}
    	else { //role의 값이 10이나 20이 아닐경우
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }
    
    
    @RequestMapping(value = "/join", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> join(@RequestBody(required = false) AppClientMember appMember) { 
    
    ModelMapper modelMapper = new ModelMapper();    
    
    join.JoinResponse result_data = null;
	ApiResultConsts.resultCode result_code = null;
	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
	ApiResult apiResult = null;


	
	//파라미터 null체크
	if (appMember.getName() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	
	if (appMember.getGender() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	if (appMember.getBirthDt() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	if (appMember.getEmail() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	if (appMember.getTel() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}	
	if (appMember.getAddress1() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	if (appMember.getAddress2() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	if (appMember.getAddressDetail() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());		
	}
	if (appMember.getAddressType() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());		
	}	
	
	if (appMember.getId() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	if (appMember.getPostcode() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	if (appMember.getPw() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	} 
	
	if (appMember.getPersonalYn() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}	
	if (appMember.getUtilYn() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}	
	if (appMember.getRole() == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	} else {
		if (appMember.getJob() == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (appMember.getInterest() == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (appMember.getSmsYes() == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (appMember.getEnterexitYes() == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		
	}
	

	
	result_data = modelMapper.map("", join.JoinResponse.class);
	
	if (appMember.getRole() == 10) { //학생 회원
		//가입 중복체크
		List<join.RegistYnResponse> join01List =  appMemberService.selectAppStudentMember(appMember.getName(), appMember.getTel()); //학생 회원
		
		if (join01List.size() > 0) { //중복 학생회원 존재
			result_data.setRegistYn(false);
			
			result_code = ApiResultConsts.resultCode.ERROR_ID_DUPL;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());   	
    		
    	} else { //회원 데이터 없음
    		//아이디 중복체크
    		List<String> Id = appMemberService.selectAppStudentCheckIdDupl(appMember.getId());
    		if (Id.size() > 0) {    			    			    		
	    			result_data.setRegistYn(false);
	    			
	    			result_code = ApiResultConsts.resultCode.ERROR_ID_DUPL2;
	    	        
	    	        header.setResult_code(result_code.getCode());
	    	        header.setResult_message(result_code.getMessage());
	    	        apiResult = new ApiResult(result_data, header);
	
	        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    			
    		} else {
    			Id = appMemberService.selectAppParentsCheckIdDupl(appMember.getId());
    			if (Id.size() > 0) {
    				result_data.setRegistYn(false);
	    			
	    			result_code = ApiResultConsts.resultCode.ERROR_ID_DUPL2;
	    	        
	    	        header.setResult_code(result_code.getCode());
	    	        header.setResult_message(result_code.getMessage());
	    	        apiResult = new ApiResult(result_data, header);
	
	        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    			} 
    		}
    		
    		String appId = UUID.randomUUID().toString();
    		int result = appMemberService.insertAppStudentMember(appId);
    		if (result == 0) {
    			result_code = ApiResultConsts.resultCode.ERROR_SERVER;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

    			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    		
    		List<join.RegistYnResponse> appIdList = appMemberService.selectAppStudentMemberByAppId(appId);
    		String no = "C"+appIdList.get(0).getAutoIncrement();
    		
    		//appMember.setTransferYn(true);
    		appMember.setAppId(appId);
    		appMember.setNo(no);    		    		
    		
    		
    		
    		//if (appIdList.size() >0) {
    			//result = appMemberService.updateAppStudentMemberForNo(appId, appMember);
    			//if (result == 0) {
        			//result_code = ApiResultConsts.resultCode.ERROR_SERVER;
        	        
        	        //header.setResult_code(result_code.getCode());
        	        //header.setResult_message(result_code.getMessage());
        	        //apiResult = new ApiResult(result_data, header);

        			//return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
        		//}
    		//}		
        	//코디정보 유무 check
        	//List<AppClientMember> appStudentMemberList = appMemberService.selectAppMemberList(no, appMember.getRole()); //코디에서 검색할 name과 tel을 가져옴 from app_student_member
        	//if (appStudentMemberList.size() > 0) { 

        		//result_data.setName(appStudentMemberList.get(0).getName());
        		
        		//List<AppClientBranch.CenterList> centerList = appMemberService.selectCodiMember(appStudentMemberList.get(0).getName(), appStudentMemberList.get(0).getTel());
    			
    			List<AppClientBranch.CenterList> centerList = appMemberService.selectCodiMember(appMember.getName(), appMember.getTel());
    		
        		if (centerList.size() > 0) { //코디에 정보가 있을경우
        			appMember.setTransferYn(true);
        			appMemberService.updateAppStudentMemberForNo(appId, appMember);
        			result_data.setCenterList(centerList);
     		
        			for (AppClientBranch.CenterList jc : centerList) {        				
    	        		//result = appMemberService.insertAppBranchManager(appStudentMemberList.get(0).getAppId(), appStudentMemberList.get(0).getNo(), jc.getBranchId(), jc.getMemberId());
        				result = appMemberService.insertAppBranchManager(appMember.getAppId(), appMember.getNo(), jc.getBranchId(), jc.getMemberId());
    	        		if (result == 0) {
    	        			result_code = ApiResultConsts.resultCode.ERROR_SERVER;
    	        	        
    	        	        header.setResult_code(result_code.getCode());
    	        	        header.setResult_message(result_code.getMessage());
    	        	        apiResult = new ApiResult(result_data, header);

    	        			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	        		}
    	        		result = appMemberService.updateSmsApproveApp(jc.getBranchId(), jc.getMemberId(), appMember.getNo(), appMember.getSmsYes(), appMember.getEnterexitYes(), appMember.getPersonalYn(), appMember.getUtilYn());
    	        		if (result == 0) {
    	        			result_code = ApiResultConsts.resultCode.ERROR_SERVER;
    	        	        
    	        	        header.setResult_code(result_code.getCode());
    	        	        header.setResult_message(result_code.getMessage());
    	        	        apiResult = new ApiResult(result_data, header);

    	        			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	        		}
    	        		//코디 회원에게 appNo 데이터 삽입
    	        		appMemberService.updateBranchMemberAppNo(jc.getBranchId(), jc.getMemberId(), appMember.getNo());
        			}
        			
        			
        			
        		} else { //코디에 정보가 없을경우
        			appMember.setTransferYn(false);
        			appMemberService.updateAppStudentMemberForNo(appId, appMember);
        			appMemberService.insertSmsApproveApp(appMember.getNo(), appMember.getSmsYes(), appMember.getEnterexitYes(), appMember.getPersonalYn(), appMember.getUtilYn());
        		}
        		result_data.setRegistYn(true);
        		result_data.setName(appMember.getName());
        		result_data.setNo(appMember.getNo());
        	//} else { //코디에 정보가 없음
        	//	result_data.setRegistYn(false);
        	//}

    	}
	
		//appMemberService.updateAppStudentMemberForId(appMember.getNo(), appMember.getId(), appMember.getPw());
	}
	else if (appMember.getRole() == 20) { //학부모 회원
		//appMemberService.updateAppParentsMemberForId(appMember.getNo(), appMember.getId(), appMember.getPw());
		List<join.RegistYnResponse> join01List =  appMemberService.selectAppParentsMember(appMember.getName(), appMember.getTel()); //학부모 회원
		
		if (join01List.size() > 0) { //중복 학부모 회원 존재
			result_data.setRegistYn(false);
			
			result_code = ApiResultConsts.resultCode.ERROR_ID_DUPL;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());   	
    		
    		//result_data.setNo(join01List.get(0).getNo());
		} else { //회원 데이터 없음    			
			String appId = UUID.randomUUID().toString();
    		appMemberService.insertAppParentsMember(appId);
    		List<join.RegistYnResponse> appIdList = appMemberService.selectAppParentsMemberByAppId(appId);
    		String no = "P"+appIdList.get(0).getAutoIncrement();
    		appMember.setNo(no);
    		
    		appMemberService.updateAppParentsMemberForNo(appId, appMember);
    		
    		result_data.setNo(appMember.getNo());
    		result_data.setName(appMember.getName());
    		
    		result_data.setRegistYn(true);
			
		}
	}
	else {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
        
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}

	
	
    result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
    
    header = modelMapper.map("", ApiResult.header.class);
    header.setResult_code(result_code.getCode());
    header.setResult_message(result_code.getMessage());
    
    apiResult = new ApiResult(result_data, header);
    
    return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    
    }
    
    
    @RequestMapping(value = "/mypageCenterList", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> mypageCenterList(@RequestHeader(value="no", required=false) String paramNo, @RequestHeader(value="role", required=false)Integer paramRole ) {
    //public @ResponseBody ResponseEntity<?> mypageCenterList(@RequestParam(value="no", required=false) String paramNo, @RequestParam(value="role", required=false)Integer paramRole ) {

    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        AppClientBranch.CenterListResponse result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	    	
    	if (paramNo == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	if (paramRole == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	
    	
    	result_data = modelMapper.map("", AppClientBranch.CenterListResponse.class);
    	
    	if (paramRole == 10) { //일반일 경우
    		List<AppClientMember> appMemberList = appMemberService.selectAppMemberList(paramNo, paramRole);
    		if (appMemberList.size() > 0) {
	    		List<AppClientBranch.CenterList> centerList = appClientBranchService.selectAppClientBranch(paramNo);
	    		List<AppClientBranch.CenterList> allCenterList = appClientBranchService.selectBranchList();
	
				for (AppClientBranch.CenterList ac : allCenterList) {
					if (centerList.contains(ac)) {
						ac.setRegistYn(true);
					} else {
						ac.setRegistYn(false);
					}
					
					if (appMemberList.get(0).getMainBranchId() != null) {
						if (appMemberList.get(0).getMainBranchId().equals(ac.getBranchId())) {
							ac.setMainBranchYn(true);
						} else {
							ac.setMainBranchYn(false);
						}
					} else {
						ac.setMainBranchYn(false);
					}
				}   		    		
	    		
	    		result_data.setCenterList(allCenterList);
    		} else {
    			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    		
    		
    	} else { //role의 값이 10이나 20이 아닐경우
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }
    
    
    @RequestMapping(value = "/updateCenter", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> updateCenter(@RequestHeader(value="no", required=false) String paramNo, @RequestHeader(value="role", required=false)Integer paramRole,
    //public @ResponseBody ResponseEntity<?> updateCenter(@RequestParam(value="no", required=false) String paramNo, @RequestParam(value="role", required=false)Integer paramRole,
    											@RequestParam(value="branchId", required=false) String paramBranchId) {
 
    	
	// 결과
	ModelMapper modelMapper = new ModelMapper();
	
	UpdateCenter.Response result_data = null;
	ApiResultConsts.resultCode result_code = null;
	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
	ApiResult apiResult = null;
	    	
	if (paramNo == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	    
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    apiResult = new ApiResult(result_data, header);
	
		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	if (paramRole == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	    
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    apiResult = new ApiResult(result_data, header);
	
		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	if (paramBranchId == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	    
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    apiResult = new ApiResult(result_data, header);
	
		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	
	
	result_data = modelMapper.map("", UpdateCenter.Response.class);
	
	if (paramRole == 10) { //일반일 경우
		String branchNm = branchService.selectBranchName(paramBranchId);
		if (branchNm == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
		    
		    header.setResult_code(result_code.getCode());
		    header.setResult_message(result_code.getMessage());
		    apiResult = new ApiResult(result_data, header);
		
			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
				
		List<AppClientBranch.CenterList> myCenter = appClientBranchService.selectAppClientBranchBybranchId(paramNo, paramBranchId);
		if(myCenter.size() > 0) {
			appMemberService.updateMainBranch(paramNo, paramRole, myCenter.get(0).getBranchId(), myCenter.get(0).getMemberId());
			
			result_data.setSuccessYn(true);
			result_data.setBranchNm(myCenter.get(0).getBranchNm());
			result_data.setNo(paramNo);
			
		} else { //데이터가 없으면 insert
			List<AppClientMember> member = appMemberService.selectAppMemberList(paramNo, paramRole);
			if(member.size() > 0) {
				String memberId = UUID.randomUUID().toString();
				appClientBranchService.insertAppBranchManager(member.get(0).getAppId(), paramNo, memberId, paramBranchId);
				//mainBranchId 업데이트
				appMemberService.updateMainBranch(paramNo, paramRole, paramBranchId, memberId);
				
				result_data.setSuccessYn(true);
				result_data.setBranchNm(branchNm);
				result_data.setNo(paramNo);
				
			} else {
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			    
			    header.setResult_code(result_code.getCode());
			    header.setResult_message(result_code.getMessage());
			    apiResult = new ApiResult(result_data, header);
			
				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			}
		}
	
	
	}     	
	else { //role의 값이 10이나 20이 아닐경우
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	    
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    apiResult = new ApiResult(result_data, header);
	
		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	
	
	result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
	
	header = modelMapper.map("", ApiResult.header.class);
	header.setResult_code(result_code.getCode());
	header.setResult_message(result_code.getMessage());
	
	apiResult = new ApiResult(result_data, header);
	
	return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    
    }
    
    
    // http://localhost:8888/app/client/api/v1/mypageUpdate?no=C1020&role=10&smsYes=true&enterexitYes=false&address1=testaddress&job=백수
    @RequestMapping(value = "/mypageUpdate", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> mypageUpdate(@RequestHeader(value="no", required=false) String paramNo, @RequestHeader(value="role", required=false)Integer paramRole, 
    //public @ResponseBody ResponseEntity<?> mypageUpdate(@RequestParam(value="no", required=false) String paramNo, @RequestParam(value="role", required=false)Integer paramRole,
    											@RequestParam(value="tel", required=false) String paramTel, @RequestParam(value="smsYes", required=false) Boolean paramSmsYes, 
    											@RequestParam(value="enterexitYes", required=false) Boolean paramEnterexitYes, @RequestParam(value="address1", required=false) String paramAddress1,
    											@RequestParam(value="address2", required=false) String paramAddress2, @RequestParam(value="addressDetail", required=false) String paramAddressDetail,
    											@RequestParam(value="addressType", required=false) Integer paramAddressType, @RequestParam(value="addressType", required=false) String paramPostcode,
    											@RequestParam(value="job", required=false) String paramJob, @RequestParam(value="interest", required=false) String paramInterest, 
    											@RequestParam(value="birthDt", required=false) String paramBirthDt, @RequestParam(value="email", required=false) String paramEmail ) {
    // 센터선택/변경에서 센턱 선택(+센터목록)의 로직은? 
    	
	// 결과
	ModelMapper modelMapper = new ModelMapper();
	
	Mypage.Response result_data = null;
	ApiResultConsts.resultCode result_code = null;
	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
	ApiResult apiResult = null;
	    	
	if (paramNo == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	    
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    apiResult = new ApiResult(result_data, header);
	
		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	if (paramRole == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	    
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    apiResult = new ApiResult(result_data, header);
	
		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	
	if (paramAddress1 == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	    
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    apiResult = new ApiResult(result_data, header);
	
		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}	
	
	if (paramAddress2 == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	    
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    apiResult = new ApiResult(result_data, header);
	
		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	if (paramAddressDetail == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	    
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    apiResult = new ApiResult(result_data, header);
	
		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}	
	if (paramPostcode == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	    
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    apiResult = new ApiResult(result_data, header);
	
		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	if (paramAddressType == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	    
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    apiResult = new ApiResult(result_data, header);
	
		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	if (paramBirthDt == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	    
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    apiResult = new ApiResult(result_data, header);
	
		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	if (paramEmail == null) {
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	    
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    apiResult = new ApiResult(result_data, header);
	
		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}

		
	
	
	result_data = modelMapper.map("", Mypage.Response.class);
	
	AppClientMember appMember = new AppClientMember();
	String OgTel = null; //전화번호가 변경될 경우 기존 전화번호(tel)를 저장시킬 임시 변수(추후 bf_tel에 저장됨)
	
	if (paramTel == null) {
	} else {
		appMember.setTel(paramTel);
		List<AppClientMember> appMemberList = appMemberService.selectAppMemberList(paramNo, paramRole);
		if (appMemberList.size() > 0) {
			OgTel = appMemberList.get(0).getTel();
			appMember.setBfTel(OgTel);
		}
	}		
	appMember.setAddress1(paramAddress1);
	appMember.setAddress2(paramAddress2);
	appMember.setAddressDetail(paramAddressDetail);
	appMember.setPostcode(paramPostcode);
	appMember.setAddressType(paramAddressType);
	appMember.setBirthDt(paramBirthDt);
	appMember.setEmail(paramEmail);
	
	
	
	if (paramRole == 10) { //일반일 경우
		if (paramSmsYes == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
		    
		    header.setResult_code(result_code.getCode());
		    header.setResult_message(result_code.getMessage());
		    apiResult = new ApiResult(result_data, header);
		
			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		} else {
			appMember.setSmsYes(paramSmsYes);
		}
		
		if (paramEnterexitYes == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
		    
		    header.setResult_code(result_code.getCode());
		    header.setResult_message(result_code.getMessage());
		    apiResult = new ApiResult(result_data, header);
		
			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		} else {
			appMember.setEnterexitYes(paramEnterexitYes);
		}
		
		if (paramJob == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
		    
		    header.setResult_code(result_code.getCode());
		    header.setResult_message(result_code.getMessage());
		    apiResult = new ApiResult(result_data, header);
		
			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		} else {
			appMember.setJob(paramJob);
		}
		
		if (paramInterest == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
		    
		    header.setResult_code(result_code.getCode());
		    header.setResult_message(result_code.getMessage());
		    apiResult = new ApiResult(result_data, header);
		
			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		} else {
			appMember.setInterest(paramInterest);
		}
					
		//학생이 정보변경 할 경우, 코디와 연동 되어있는 회원들은 코디 정보도 같이 update (부모는 연동 없음) 
		
		//넘어온 전화번호가 현재와 동일한지 check
		//List<AppMember> appMemberList = appMemberService.selectStudentMemberList(paramNo);
		//if (appMemberList.size() > 0) {
			
		//}
		appMemberService.updateAppMember(paramNo, paramRole, appMember); //app정보 변경
		
		//학부모 안심서비스 데이터(studentTel) 변경
		if(appMember.getTel() != null) {
			List<String> studentNo = branchSafeServiceService.selectSafeServiceList(paramNo, paramRole, 20);
			if(studentNo.size() > 0) {
				branchSafeServiceService.updateSafeService(paramNo, paramRole, appMember.getTel());
			}
		}
		
		List<BranchMember> branchMember = appMemberService.selectCodiMemberByAppNo(paramNo);
		if (branchMember.size() > 0) { //정보가 있으면 코디 정보도 존재함
			appMemberService.updateCodiInfo(paramNo, appMember.getTel(), appMember.getBfTel());
		}
		//sms_approve 정보변경
		appMemberService.updateSmsApproveAppByNo(paramNo, appMember.getSmsYes(), appMember.getEnterexitYes());
		result_data.setUpdResult(true);
	
	}
	else if (paramRole == 20) { //학부모일 경우		
		appMemberService.updateAppMember(paramNo, paramRole, appMember); //app정보 변경
		//학부모의 전화번호가 변경될 경우, 학부모 안심서비스 및 학생의 telParents도 변경
		if(appMember.getTel() != null) {
			List<String> studentNo = branchSafeServiceService.selectSafeServiceList(paramNo, paramRole, 20);
			if(studentNo.size() > 0) {
				branchSafeServiceService.updateSafeService(paramNo, paramRole, appMember.getTel());
				appMemberService.updateAppStudentMembertTelParent(studentNo.get(0), appMember.getTel());
			}
		}
		
		
		result_data.setUpdResult(true);
		
	}
	else { //role의 값이 10이나 20이 아닐경우
		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	    
	    header.setResult_code(result_code.getCode());
	    header.setResult_message(result_code.getMessage());
	    apiResult = new ApiResult(result_data, header);
	
		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	
	
			
	result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
	
	header = modelMapper.map("", ApiResult.header.class);
	header.setResult_code(result_code.getCode());
	header.setResult_message(result_code.getMessage());
	
	apiResult = new ApiResult(result_data, header);
	
	return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    
    }
    
    
    
    
    
    
    @RequestMapping(value = "/parentsActivate", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> parentsActivate(@RequestHeader(value="no", required=false) String paramNo, @RequestHeader(value="role", required=false) Integer paramRole,
    //public @ResponseBody ResponseEntity<?> parentsActivate(@RequestParam(value="no", required=false) String paramNo, @RequestParam(value="role", required=false) Integer paramRole,
    											@RequestParam(value="parentsId", required=false) String paramParentsId, @RequestParam(value="parentsTel", required=false) String paramParentsTel, 
    											@RequestParam(value="studentId", required=false) String paramStudentId, @RequestParam(value="studentTel", required=false) String paramStudentTel) {
    //public @ResponseBody ResponseEntity<?> join01(@RequestParam(value="name", required=false) String paramName, @RequestParam(value="tel", required=false)String paramTel ) {
//    	paramName = "김기덕3";
//    	paramTel = "8737";
//    	paramGender = "10";
//    	paramBirthDt = "";
//    	paramAddress1 = "";
//    	paramRole = 10;
    	//paramBranchId = branchIdTest;
    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        Result.Response result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	    	
    	if (paramNo == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramRole == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramParentsId == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramParentsTel == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramStudentId == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	if (paramStudentTel == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	    	
    	result_data = modelMapper.map("", Result.Response.class);
    	

    	if(paramRole == 20) {
    		List<AppClientMember> parentsList = appMemberService.selectAppMemberList(paramNo, paramRole); //입력된 정보와 부모정보 확인
    		List<AppClientMember> studentList = appMemberService.selectAppStudentListById(paramStudentId, paramStudentTel);    		
    		
    		if (parentsList.size() > 0) {
    			if (parentsList.get(0).getId().equals(paramParentsId)) {
    				if (parentsList.get(0).getTel().equals(paramParentsTel)) {
    					if (studentList.size() > 0) {
    						if (studentList.get(0).getMainBranchId() == null || studentList.get(0).equals("")) {    							
    				    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_MAINBRANCH;
    					        
    					        header.setResult_code(result_code.getCode());
    					        header.setResult_message(result_code.getMessage());
    					        apiResult = new ApiResult(result_data, header);

    				    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    						} else {
    							//학생의 가입된 센터 정보
    				    		List<AppClientMember> branchList = appMemberService.selectAppBranchManager(studentList.get(0).getNo(), studentList.get(0).getMainBranchId());
    				    		
    				    		if (branchList.size() > 0) {
    				    			List<SafeService> safeServiceList = branchSafeServiceService.selectSafeServiceList(studentList.get(0).getMainBranchId(), parentsList.get(0).getAppId(), studentList.get(0).getAppId());
    				    			if (safeServiceList.size() > 0) { //데이터 있으면 중복 오류    				    				
    				    				
    				    				result_code = ApiResultConsts.resultCode.ERROR_PARENTS_SERVICE_DUPL;
        				    	        
        				    	        header.setResult_code(result_code.getCode());
        				    	        header.setResult_message(result_code.getMessage());
        				    	        apiResult = new ApiResult(result_data, header);

        				        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    				    				
    				    			} else {
    				    				//오늘 날짜 기준 중복데이터 select
    				    				List<SafeService> safeServiceListToday = branchSafeServiceService.selectSafeServiceListToday(studentList.get(0).getMainBranchId(), parentsList.get(0).getAppId(), studentList.get(0).getAppId());
    				    				if (safeServiceListToday.size() > 0) {
    				    					
    				    					result_code = ApiResultConsts.resultCode.ERROR_ID_DUPL;
            				    	        
            				    	        header.setResult_code(result_code.getCode());
            				    	        header.setResult_message(result_code.getMessage());
            				    	        apiResult = new ApiResult(result_data, header);

            				        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    				    				
    				    				} else {
    				    				
    				    				
	    		    						SafeService safeService = new SafeService(); 
	    				    				
	    				    				safeService.setParentsAppId(parentsList.get(0).getAppId());
	    				    				safeService.setParentsName(parentsList.get(0).getName());
	    				    				safeService.setParentsNo(parentsList.get(0).getNo());
	    				    				safeService.setParentsId(parentsList.get(0).getId());
	    				    				safeService.setParentsTel(parentsList.get(0).getTel());
	    				    				    				    				
	    				    				safeService.setStudentAppId(studentList.get(0).getAppId());
	    				    				safeService.setStudentName(studentList.get(0).getName());
	    				    				safeService.setStudentNo(studentList.get(0).getNo());
	    				    				safeService.setStudentId(studentList.get(0).getId());
	    				    				safeService.setStudentTel(studentList.get(0).getTel());
	    				    				    				    				
	    				    				safeService.setBranchId(studentList.get(0).getMainBranchId());
	    				    				safeService.setStartDt(DateUtil.getCurrentDate());
	    				    				safeService.setStatus(10);
	    				    				
	    				    				branchSafeServiceService.insertParentsSafeService(safeService);
	
	//        				    			branchSafeServiceService.insertParentsSafeService(parentsList.get(0).getAppId(), parentsList.get(0).getName(), 
	//										parentsList.get(0).getNo(), parentsList.get(0).getId(), parentsList.get(0).getTel(),
	//										studentList.get(0).getAppId(), studentList.get(0).getName(), 
	//										studentList.get(0).getNo(), studentList.get(0).getId(),
	//										studentList.get(0).getTel(), studentList.get(0).getMainBranchId(), nowDate);
	//appMemberService.updateBranchStudentMemberFortelParent(studentList.get(0).getNo(), 10, paramParentsTel);
	    				    				}

    				    			}
    				    			
    				    			

    				    		} else {
    				    			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    				    	        
    				    	        header.setResult_code(result_code.getCode());
    				    	        header.setResult_message(result_code.getMessage());
    				    	        apiResult = new ApiResult(result_data, header);

    				        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    				    		}
	    						
//	    						if(parentsList.get(0).getMainChildNo() == null) { //주자식 정보 없으면 넣어줌-> 추후에는 코디에서 처리
//	    							appMemberService.updateAppParentsMemberMainChild(paramNo, studentList.get(0).getNo());
//	    						}
	    						
	    						result_data.setResultYn(true);
    						}
    					} else {
    						//result_data.setResultYn(false); //정보 잘못입력    						
    						
    						result_code = ApiResultConsts.resultCode.ERROR_PARENTS_SERVICE_NOT_MATCH;
			    	        
			    	        header.setResult_code(result_code.getCode());
			    	        header.setResult_message(result_code.getMessage());
			    	        apiResult = new ApiResult(result_data, header);

			        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    					}
    					
    				} else {
    					//result_data.setResultYn(false); //정보 잘못입력
    					
    					result_code = ApiResultConsts.resultCode.ERROR_PARENTS_SERVICE_NOT_MATCH;
		    	        
		    	        header.setResult_code(result_code.getCode());
		    	        header.setResult_message(result_code.getMessage());
		    	        apiResult = new ApiResult(result_data, header);

		        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    				}
    				
    			} else {
    				//result_data.setResultYn(false); //정보 잘못입력
    				
    				result_code = ApiResultConsts.resultCode.ERROR_PARENTS_SERVICE_NOT_MATCH;
	    	        
	    	        header.setResult_code(result_code.getCode());
	    	        header.setResult_message(result_code.getMessage());
	    	        apiResult = new ApiResult(result_data, header);

	        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    			}
    			
    		} else {
    			//result_data.setResultYn(false); //정보 잘못입력
    			
    			result_code = ApiResultConsts.resultCode.ERROR_PARENTS_SERVICE_NOT_MATCH;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		} 

    	}
    	else { //role의 값이 10이나 20이 아닐경우
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }
    
    
    @RequestMapping(value = "/parents", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> parents(@RequestHeader(value="no", required=false) String paramNo, @RequestHeader(value="role", required=false) Integer paramRole ) { 
    //public @ResponseBody ResponseEntity<?> parents(@RequestParam(value="no", required=false) String paramNo, @RequestParam(value="role", required=false) Integer paramRole ) {


    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        Student.Response result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	    	
    	if (paramNo == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	if (paramRole == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	
    	
    	result_data = modelMapper.map("", Student.Response.class);
    	Student.StudentObject studentObject = new Student.StudentObject();
    	List<Student.StudentObject> studentList = new ArrayList<Student.StudentObject>();
    	
    	if(paramRole == 20) {
    		List<AppClientMember> appMemberList = appMemberService.selectAppMemberList(paramNo, paramRole);
    		if (appMemberList.size() > 0) {
    		
	    		List<String> studentNo = branchSafeServiceService.selectSafeServiceList(paramNo, paramRole, 20);
	    		List<AppClientMember> appParentsList = appMemberService.selectAppMemberList(paramNo, paramRole);
	    		if (studentNo.size() > 0 && appParentsList.size() > 0) {
	
	    			for (String no : studentNo) {
	    				List<AppClientMember> appClientMemberList = appMemberService.selectAppMemberList(no, 10);
	    				
	    				if(appClientMemberList.size() > 0) {
	    					//자식의 mainBranch로 등록 데이터 찾기
	    					List<AppClientMember> childInfo = appMemberService.selectAppBranchManager(no, appClientMemberList.get(0).getMainBranchId());
	    					
	    					if(childInfo.size() > 0) {
	    						List<RegisterReservation> registerReservationTotal = branchPayService.selectAppRegisterPayList2(appClientMemberList.get(0).getMainBranchId(), childInfo.get(0).getMemberId());
	    						if (registerReservationTotal.size() > 0) {
	    							studentObject.setDeskName(registerReservationTotal.get(0).getDeskName());
	    							studentObject.setRoomName(registerReservationTotal.get(0).getRoomName());
	    						}
	    						studentObject.setName(appClientMemberList.get(0).getName());
	    						studentObject.setBranchNm(childInfo.get(0).getBranchNm());
	    						studentObject.setNo(appClientMemberList.get(0).getNo());
	    						studentObject.setStudentId(appClientMemberList.get(0).getId());
	 
	    						if (appParentsList.get(0).getMainChildNo() != null && appParentsList.get(0).getMainChildNo().equals(studentObject.getNo())) { //부모의 mainChild 인지 아닌지 체크
	    							studentObject.setMainChildYn(true);
	    						} else {
	    							studentObject.setMainChildYn(false);
	    						}
	    						studentList.add(studentObject);
	    						studentObject = new Student.StudentObject(); 
	    					}
	    					
	    					
	    					
	    				} else { //데이터 없음
	    					
	    				}
	    			}
	    			result_data.setStudentList(studentList);
	    			
	    		} else { 
	    			result_data.setStudentList(null);
	    		}
    		
    		} else {
    			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    		
    		
    	}
    	else { //role의 값이 10이나 20이 아닐경우
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }
    
    
    @RequestMapping(value = "/reservation", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> reservation(@RequestHeader(value="no", required=false) String paramNo, @RequestHeader(value="role", required=false) Integer paramRole,
    //public @ResponseBody ResponseEntity<?> reservation(@RequestParam(value="no", required=false) String paramNo, @RequestParam(value="role", required=false) Integer paramRole,    
    														@RequestParam(value="type", required=false) Integer paramType, @RequestParam(value="branchId", required=false) String paramBranchId) {
    	
    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        
        AppClientBranch.Response result_data = null;
    	
        ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	    	
    	if (paramNo == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	if (paramRole == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	if (paramType == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	    	
//    	if (paramBranchId == null) {
//    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
//	        
//	        header.setResult_code(result_code.getCode());
//	        header.setResult_message(result_code.getMessage());
//	        apiResult = new ApiResult(result_data, header);
//
//    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
//    	}
    	
    	result_data = modelMapper.map("", AppClientBranch.Response.class);    	    	
    	
    	if (paramRole == 20) {  //학부모가 좌석예약이 되면 오류
    		if (paramType == 10) {
    			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    	}
    	
    	AppClientBranch.Desk desk = new AppClientBranch.Desk();
    	List<AppClientBranch.Desk> deskList = new ArrayList<AppClientBranch.Desk>(); 
    	
    	if (paramBranchId == null || paramBranchId.equals("")) {
    		List<Branch> branchOne = branchService.selectBranchListOne();
    		paramBranchId = branchOne.get(0).getBranchId();
    	} else {
    		
    	}
    	
    	//센터 정보
    	List<AppClientBranch.Branch> appClientBranch = appClientBranchService.selectBranchListForReservation(paramBranchId);
		if(appClientBranch.size() > 0) {
			
			result_data.setBranch(appClientBranch.get(0));
	    	//좌석 리스트
			if(appClientBranch.get(0).getReservationYn()) {
				desk.setDeskType(9999);
				desk.setDeskName("전체");
				desk.setRegistYn(true);
				deskList.add(desk);
				desk = new AppClientBranch.Desk();
				//멀티스페이스
				if(appClientBranch.get(0).getMultiYn()) {
					desk.setDeskType(10);
					desk.setDeskName("멀티스페이스");
					desk.setRegistYn(true);
					deskList.add(desk);
					desk = new AppClientBranch.Desk();
					//멀티스페이스(다인석)"), SINGLE(20, "싱글스페이스(1인석)"), PRIVATE(30, "프라이빗큐브(개인실
				} else {
					desk.setDeskType(10);
					desk.setDeskName("멀티스페이스");
					desk.setRegistYn(false);
					deskList.add(desk);
					desk = new AppClientBranch.Desk();					
				}
				//싱글스페이스
				if(appClientBranch.get(0).getSingleYn()) {
					desk.setDeskType(20);
					desk.setDeskName("싱글스페이스");
					deskList.add(desk);
					desk.setRegistYn(true);
					desk = new AppClientBranch.Desk();
					//멀티스페이스(다인석)"), SINGLE(20, "싱글스페이스(1인석)"), PRIVATE(30, "프라이빗큐브(개인실
				} else {
					desk.setDeskType(20);
					desk.setDeskName("싱글스페이스");
					desk.setRegistYn(false);
					deskList.add(desk);
					desk = new AppClientBranch.Desk();				
				}
				//프라이빗큐브
				if(appClientBranch.get(0).getPrivateYn()) {
					desk.setDeskType(30);
					desk.setDeskName("프라이빗큐브");
					deskList.add(desk);
					desk.setRegistYn(true);
					desk = new AppClientBranch.Desk();
					//멀티스페이스(다인석)"), SINGLE(20, "싱글스페이스(1인석)"), PRIVATE(30, "프라이빗큐브(개인실
				} else {
					desk.setDeskType(30);
					desk.setDeskName("프라이빗큐브");
					desk.setRegistYn(false);
					deskList.add(desk);
					desk = new AppClientBranch.Desk();		
				}
				result_data.setDeskList(deskList);
			} else {
				result_data.setDeskList(null);
			}
			
		} else {
    		
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		
		}
    	
		//센터 리스트
		List<AppClientBranch.CenterList> centerList = appClientBranchService.selectCenterListForReservation();
		if(centerList.size() > 0) {
			result_data.setCenterList(centerList);
		} else {
			result_data.setCenterList(null);
		}
		
		//result_data.setDeskThumList(deskThumList);
		List<AppClientBranch.DeskThum> deskThumList = new ArrayList<AppClientBranch.DeskThum>();
		AppClientBranch.DeskThum deskThum = new AppClientBranch.DeskThum(); 
		
		deskThum.setThumUrl("https://www.studycodi.com/files/multi1.jpg");
		deskThumList.add(deskThum);
		deskThum = new AppClientBranch.DeskThum();
		
		deskThum.setThumUrl("https://www.studycodi.com/files/multi2.jpg");
		deskThumList.add(deskThum);
		deskThum = new AppClientBranch.DeskThum();
		
		deskThum.setThumUrl("https://www.studycodi.com/files/multi3.jpg");
		deskThumList.add(deskThum);
		deskThum = new AppClientBranch.DeskThum();
		
		deskThum.setThumUrl("https://www.studycodi.com/files/multi4.jpg");
		deskThumList.add(deskThum);
		deskThum = new AppClientBranch.DeskThum();
		
		deskThum.setThumUrl("https://www.studycodi.com/files/single1.jpg");
		deskThumList.add(deskThum);
		deskThum = new AppClientBranch.DeskThum();
		
		deskThum.setThumUrl("https://www.studycodi.com/files/single2.jpg");
		deskThumList.add(deskThum);
		deskThum = new AppClientBranch.DeskThum();
		
		deskThum.setThumUrl("https://www.studycodi.com/files/single3.jpg");
		deskThumList.add(deskThum);
		deskThum = new AppClientBranch.DeskThum();
		
		deskThum.setThumUrl("https://www.studycodi.com/files/single4.jpg");
		deskThumList.add(deskThum);
		deskThum = new AppClientBranch.DeskThum();
		
		deskThum.setThumUrl("https://www.studycodi.com/files/private1.jpg");
		deskThumList.add(deskThum);
		deskThum = new AppClientBranch.DeskThum();
		
		deskThum.setThumUrl("https://www.studycodi.com/files/private2.jpg");
		deskThumList.add(deskThum);
		deskThum = new AppClientBranch.DeskThum();
		
		deskThum.setThumUrl("https://www.studycodi.com/files/private3.jpg");
		deskThumList.add(deskThum);
		deskThum = new AppClientBranch.DeskThum();
		
		deskThum.setThumUrl("https://www.studycodi.com/files/private4.jpg");
		deskThumList.add(deskThum);
		deskThum = new AppClientBranch.DeskThum();
		
		deskThum.setThumUrl("https://www.studycodi.com/files/private5.jpg");
		deskThumList.add(deskThum);
		deskThum = new AppClientBranch.DeskThum();				
		result_data.setDeskThumList(deskThumList);
		
		
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }
    
    
    //http://localhost:8888/app/client/api/v1/reservationRequest?no=C1020&role=10&type=10&branchId=7ebf50c6-3b2e-41b0-9272-55c5bb80a839&startDt=2017-08-16&roomType=10
    @RequestMapping(value = "/reservationRequest", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> reservationRequest(@RequestHeader(value="no", required=false) String paramNo, @RequestHeader(value="role", required=false) Integer paramRole,
    //public @ResponseBody ResponseEntity<?> reservationRequest(@RequestParam(value="no", required=false) String paramNo, @RequestParam(value="role", required=false) Integer paramRole,    
    														@RequestParam(value="type", required=false) Integer paramType, @RequestParam(value="branchId", required=false) String paramBranchId,
    														@RequestParam(value="startDt", required=false) String paramStartDt, @RequestParam(value="roomType", required=false) Integer paramRoomType) { 


    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        Result.Response result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	    	
    	if (paramNo == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	if (paramRole == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	if (paramType == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	if (paramBranchId == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}    
    	
    	if (paramStartDt == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	if (paramRoomType == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}    	
    	
    	if (paramRole == 20) {  //학부모가 좌석예약이 되면 오류
    		if (paramType == 10) {
    			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    	}
    	
    	
    	List<SeatReservation> seatReservationToday =  branchSeatReservationService.selectSeatReservationListToday(paramBranchId, paramNo, paramRole, paramType);
    	if(seatReservationToday.size() > 0) {
    		result_code = ApiResultConsts.resultCode.ERROR_ID_DUPL;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	} else {
    		
    	}
    	
    	
    	result_data = modelMapper.map("", Result.Response.class);
    	
    	String applicationId = null;
    	AppClientMember appClientMember = new AppClientMember();
		List<AppClientMember> appClientMemberList = appMemberService.selectAppMemberList(paramNo, paramRole);
		if(appClientMemberList.size() > 0) {			
			appClientMember.setAppId(appClientMemberList.get(0).getAppId());
			appClientMember.setMainBranchId(appClientMemberList.get(0).getMainBranchId());
			appClientMember.setNo(appClientMemberList.get(0).getNo());
			appClientMemberList.get(0).setRole(paramRole);
			appClientMember.setRole(appClientMemberList.get(0).getRole());
			appClientMember.setName(appClientMemberList.get(0).getName());
			appClientMember.setGender(appClientMemberList.get(0).getGender());
			appClientMember.setBirthDt(appClientMemberList.get(0).getBirthDt());
			appClientMember.setTel(appClientMemberList.get(0).getTel());
			appClientMember.setEmail(appClientMemberList.get(0).getEmail());
			applicationId = UUID.randomUUID().toString();
		} else {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);
	
			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		
	
		
		/*************** 
		 * 학부모에도 mainBranchId가 있어야 하는지, 있으면 하기 로직 위치 달라짐 -> 등록 데이터 어떻게 넣을지 정하기
		 * *************************/
		 
		List<AppClientBranch.CenterList> myCenter =  appClientBranchService.selectAppClientBranchBybranchId(paramNo, paramBranchId);
		String memberId = null;
		if(myCenter.size() > 0) { //존재하지 않으면 데이터 insert
			memberId = myCenter.get(0).getMemberId();
		} else {
			memberId = UUID.randomUUID().toString();
			appClientBranchService.insertAppBranchManager(appClientMember.getAppId(), paramNo, memberId, paramBranchId);
			//result_data.setResultYn(true);
		}		
		
		
    	if(paramRole == 10) {
    		//일반일 경우 main branch가 있는지 check
    		if(appClientMember.getMainBranchId() == null || appClientMember.getMainBranchId() == "") { //없으면 mainBranchId 지정
    			appMemberService.updateMainBranch(paramNo, paramRole, paramBranchId, memberId);
    		} else {
    			
    		}
    		
    		
			if(paramType == 10) { //사전예약
				seatApplicationService.insertSeatApplication(applicationId, paramBranchId, paramType, paramRoomType, paramStartDt, appClientMember, 10);
    			
    			result_data.setResultYn(true);
    			
    		} else if(paramType == 20) { //무료체험    			    			
    			//무료체험은 한번
				List<SeatReservation> seatReservationDupl =  branchSeatReservationService.selectSeatReservationListDupl(paramBranchId, paramNo, paramRole, 20);
				if (seatReservationDupl.size() > 0) {					
					result_code = ApiResultConsts.resultCode.ERROR_SEAT_DUPL;
			        
			        header.setResult_code(result_code.getCode());
			        header.setResult_message(result_code.getMessage());
			        apiResult = new ApiResult(result_data, header);
			
					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
				}
				
				seatApplicationService.insertSeatApplication(applicationId, paramBranchId, paramType, paramRoomType, paramStartDt, appClientMember, 10);	    			
    			result_data.setResultYn(true);
    			
    		} else {
        		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}

    		
    	} else if(paramRole == 20) {
    		if(paramType == 20) {
    			//무료체험은 한번
				List<SeatReservation> seatReservationDupl =  branchSeatReservationService.selectSeatReservationListDupl(paramBranchId, paramNo, paramRole, 20);
				if (seatReservationDupl.size() > 0) {					
					result_code = ApiResultConsts.resultCode.ERROR_SEAT_DUPL;
			        
			        header.setResult_code(result_code.getCode());
			        header.setResult_message(result_code.getMessage());
			        apiResult = new ApiResult(result_data, header);
			
					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
				}
				
    			seatApplicationService.insertSeatApplication(applicationId, paramBranchId, paramType, paramRoomType, paramStartDt, appClientMember, 10);
    			result_data.setResultYn(true);
    		} else if(paramType == 10) { //학부모는 좌석예약 불가능
        		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		} else {
        		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    	} else { //role의 값이 10이나 20이 아닐경우
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }


    
    //학부모 주자녀 변경
    @RequestMapping(value = "/parentsUpdateChild", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> parentsUpdateChild( @RequestHeader(value="no", required=false) String paramNo, @RequestHeader(value="role", required=false) Integer paramRole,
    														//@RequestParam(value="no", required=false) String paramNo, @RequestParam(value="role", required=false) Integer paramRole,
    														@RequestParam(value="studentId", required=false) String paramStudentId ) {

    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        
        Result.Response result_data = null;
    	
        ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	
    	if (paramNo == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	if (paramRole == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	
    	if (paramStudentId == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	result_data = modelMapper.map("", Result.Response.class);    	
    	
    	if(paramRole == 20) {
    		/*
    		List<AppClientMember> student = appMemberService.selectAppStudentListById(paramStudentId, null);
    		if(student.size() > 0) {
    		} else {
    			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    		*/
    			//자식인지 체크
    		List<AppClientMember> student = null;
    		Boolean flag = false;
    		List<String> studentNoList = branchSafeServiceService.selectSafeServiceList(paramNo, paramRole, 20);
			if(studentNoList.size() > 0) {
				for(String s : studentNoList) {
					student = appMemberService.selectAppMemberList(s, 10);
					if (student.size() > 0) { //해당 정보가 존재하면
						List<AppClientMember> studentNo = appMemberService.selectAppStudentListById(paramStudentId, null);
			    		if(studentNo.size() > 0) {
			    			if(s.equals(studentNo.get(0).getNo())) {
			    				flag = true;
								break;
			    			}
			    		} else {
			    			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			    	        
			    	        header.setResult_code(result_code.getCode());
			    	        header.setResult_message(result_code.getMessage());
			    	        apiResult = new ApiResult(result_data, header);

			        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			    		}
						
					} else {
						
					}
					
				}
			} else {
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			}
			
			if (flag) {
				appMemberService.updateAppParentsMemberMainChild(paramNo, student.get(0).getNo());
				result_data.setResultYn(true);
			} else {
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			}
    			
    		
    		
    	} else {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }
    
    
    
    
    
    @RequestMapping(value = "/reservationSearch", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> reservationSearch(@RequestHeader(value="no", required=false) String paramNo, @RequestHeader(value="role", required=false) Integer paramRole,
    //public @ResponseBody ResponseEntity<?> reservationSearch(@RequestParam(value="no", required=false) String paramNo, @RequestParam(value="role", required=false) Integer paramRole,    
    														@RequestParam(value="type", required=false) Integer paramType, @RequestParam(value="branchId", required=false) String paramBranchId,
    														@RequestParam(value="startDt", required=false) String paramStartDt, @RequestParam(value="roomType", required=false) Integer paramRoomType) { 


    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        ReservationBranch.Response result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	    	
    	if (paramNo == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	if (paramRole == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	if (paramType == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	if (paramBranchId == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}    
    	
    	if (paramStartDt == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	if (paramRoomType == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	result_data = modelMapper.map("", ReservationBranch.Response.class);
    	    	
			
    	if(paramRole == 10) {    		
			if(paramType == 10 || paramType == 20) { //무료체험
				List<ReservationBranch> reservationBranchList = appClientBranchService.selectBranchReservationYn(paramBranchId);
				if(reservationBranchList.size() > 0) {
					List<ReservationBranch.Branch> branchList = new ArrayList<ReservationBranch.Branch>();
					ReservationBranch.Branch branch = new ReservationBranch.Branch(); 
						if(reservationBranchList.get(0).getReservationYn()) {
							if(reservationBranchList.get(0).getMultiYn() && (paramRoomType == 10 || paramRoomType == 9999)) {
								branch.setBranchId(reservationBranchList.get(0).getBranchId());
								branch.setBranchNm(reservationBranchList.get(0).getBranchNm());
								branch.setStartDt(paramStartDt);
								branch.setDeskName("멀티스페이스");
								branch.setRoomType(10);
								branchList.add(branch);
								branch = new ReservationBranch.Branch();
							} else {
								
							}
							
							if(reservationBranchList.get(0).getSingleYn() && (paramRoomType == 20 || paramRoomType == 9999)) {
								branch.setBranchId(reservationBranchList.get(0).getBranchId());
								branch.setBranchNm(reservationBranchList.get(0).getBranchNm());
								branch.setStartDt(paramStartDt);
								branch.setDeskName("싱글스페이스");
								branch.setRoomType(20);
								branchList.add(branch);
								branch = new ReservationBranch.Branch();
							} else {
								
							} 
							if(reservationBranchList.get(0).getPrivateYn() && (paramRoomType == 30 || paramRoomType == 9999)) {
								branch.setBranchId(reservationBranchList.get(0).getBranchId());
								branch.setBranchNm(reservationBranchList.get(0).getBranchNm());
								branch.setStartDt(paramStartDt);
								branch.setDeskName("프라이빗큐브");
								branch.setRoomType(30);
								branchList.add(branch);
								branch = new ReservationBranch.Branch();
							} else {
								
							}
							result_data.setBranchRsvYnList(branchList);
							if(reservationBranchList.get(0).getMultiYn() == false && reservationBranchList.get(0).getSingleYn() == false && reservationBranchList.get(0).getPrivateYn() == false){
								result_data.setBranchRsvYnList(null);
							}
							
						} else {
							result_data.setBranchRsvYnList(null);
						}
					
				} else {
					result_data.setBranchRsvYnList(null);
				}

    		} else {
        		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    		
    	} else if(paramRole == 20) {
    		if(paramType == 10) { //학부모는 좌석예약 불가능
        		
    			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		} else if(paramType == 20) { //무료체험

        		List<ReservationBranch> reservationBranchList = appClientBranchService.selectBranchReservationYn(paramBranchId);
				if(reservationBranchList.size() > 0) {
					List<ReservationBranch.Branch> branchList = new ArrayList<ReservationBranch.Branch>();
					ReservationBranch.Branch branch = new ReservationBranch.Branch(); 
						if(reservationBranchList.get(0).getReservationYn()) {
							if(reservationBranchList.get(0).getMultiYn() && (paramRoomType == 10 || paramRoomType == 9999)) {
								branch.setBranchId(reservationBranchList.get(0).getBranchId());
								branch.setBranchNm(reservationBranchList.get(0).getBranchNm());
								branch.setStartDt(paramStartDt);
								branch.setDeskName("멀티스페이스");
								branch.setRoomType(10);
								branchList.add(branch);
								branch = new ReservationBranch.Branch();
							} 
							if(reservationBranchList.get(0).getSingleYn() && (paramRoomType == 20 || paramRoomType == 9999)) {
								branch.setBranchId(reservationBranchList.get(0).getBranchId());
								branch.setBranchNm(reservationBranchList.get(0).getBranchNm());
								branch.setStartDt(paramStartDt);
								branch.setDeskName("싱글스페이스");
								branch.setRoomType(20);
								branchList.add(branch);
								branch = new ReservationBranch.Branch();
							} 
							if(reservationBranchList.get(0).getPrivateYn() && (paramRoomType == 30 || paramRoomType == 9999)) {
								branch.setBranchId(reservationBranchList.get(0).getBranchId());
								branch.setBranchNm(reservationBranchList.get(0).getBranchNm());
								branch.setStartDt(paramStartDt);
								branch.setDeskName("프라이빗큐브");
								branch.setRoomType(30);
								branchList.add(branch);
								branch = new ReservationBranch.Branch();
							}
							result_data.setBranchRsvYnList(branchList);
							if(reservationBranchList.get(0).getMultiYn() == false && reservationBranchList.get(0).getSingleYn() == false && reservationBranchList.get(0).getPrivateYn() == false){
								result_data.setBranchRsvYnList(null);
							}
							
						} else {
							result_data.setBranchRsvYnList(null);
						}
					
				} else {
					result_data.setBranchRsvYnList(null);
				}
        		
        		
        		
        		
    		} else {
        		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    	} else { //role의 값이 10이나 20이 아닐경우
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }


    @RequestMapping(value = "/mypageLeave", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> mypageLeave(@RequestHeader(value="no", required=false) String paramNo, @RequestHeader(value="role", required=false) Integer paramRole) {
    //public @ResponseBody ResponseEntity<?> mypageLeave(@RequestParam(value="no", required=false) String paramNo, @RequestParam(value="role", required=false) Integer paramRole) {

    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        Result.Response result_data = null;
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	    	
    	if (paramNo == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	if (paramRole == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	
    	
    	result_data = modelMapper.map("", Result.Response.class);
    	
    	if (paramRole == 10) { //일반일 경우    		
    		List<AppClientMember> appClientMember = appMemberService.selectAppMemberList(paramNo, paramRole);
    		if(appClientMember.size() >0) {
    			appMemberService.updateAppMemberLeave(paramNo, paramRole);
    			if(appClientMember.get(0).getTransferYn()) { //true이면 코디 정보와 동기화
    				//3개월 때 삭제
    			} else {
    				
    			}
    			
    			List<String> studentNo = branchSafeServiceService.selectSafeServiceList(paramNo, paramRole, 20);
    			if(studentNo.size() >0) {
    				branchSafeServiceService.deleteSafeService(paramNo, paramRole);
    			}
    			
    			result_data.setResultYn(true);	
    				
    		} else {
        		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    	} 
    	else if(paramRole == 20) { //학부모일 경우
    		List<AppClientMember> appClientMember = appMemberService.selectAppMemberList(paramNo, paramRole);
    		if(appClientMember.size() >0) {
    			appMemberService.updateAppMemberLeave(paramNo, paramRole);

    			
    			List<String> studentNo = branchSafeServiceService.selectSafeServiceList(paramNo, paramRole, 20);
    			if(studentNo.size() >0) {
    				branchSafeServiceService.deleteSafeService(paramNo, paramRole);
    				appMemberService.updateAppStudentMembertTelParent(studentNo.get(0), null);
    			}
    			
    			result_data.setResultYn(true);
    				
    		} else {
        		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    	}
    	else { //role의 값이 10이나 20이 아닐경우
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }



    //리포팅 누적 학습시간
    @RequestMapping(value = "/reportingTime", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> reportingTime(@RequestHeader(value="no", required=false) String paramNo, @RequestHeader(value="role", required=false) Integer paramRole,
    													//@RequestParam(value="no", required=false) String paramNo, @RequestParam(value="role", required=false) Integer paramRole,
    													@RequestParam(value="startDt", required=false) String paramStartDt, @RequestParam(value="endDt", required=false) String paramEndDt) {

    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        
        ReportingTime.Response result_data = null;
    	
        ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	    	
    	if (paramNo == null) {
//    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
//	        
//	        header.setResult_code(result_code.getCode());
//	        header.setResult_message(result_code.getMessage());
//	        apiResult = new ApiResult(result_data, header);
//
//    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	if (paramRole == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	if (paramStartDt == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	if (paramEndDt == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}    	
    	  	
    
    	result_data = modelMapper.map("", ReportingTime.Response.class);    	


        //나의시간 계산
    	String no = null;
    	
    	if (paramRole == 10) { //일반회원
    		no = paramNo;
    	} else if (paramRole == 20) { //학부모 회원
    		List<AppClientMember> childNo = appMemberService.selectAppMemberList(paramNo, paramRole);
    		if (childNo.size() > 0) {
    			no = childNo.get(0).getMainChildNo();
    		}
    		
    	} else {
//    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
//	        
//	        header.setResult_code(result_code.getCode());
//	        header.setResult_message(result_code.getMessage());
//	        apiResult = new ApiResult(result_data, header);
//
//    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		
    	}
	    	
    	List<AppClientMember> appClientMember = appMemberService.selectAppMemberList(no, 10);
    	
    	
    	//List<AppClientMember> appClientMember = appMemberService.selectAppMemberList(paramNo, paramRole);
    	
    	List<StatusListEntry> myTotalLearningTm = null;
    			
        //나의시간 계산
    	TotalLearning totalLearning = new TotalLearning();
    	ReportingTime.Time myTime = new ReportingTime.Time();
    	List<ReportingTime.Time> myTimeList = new ArrayList<ReportingTime.Time>();
    	
    	if(appClientMember.size() > 0) {
    		List<AppClientMember> appMemberId = appMemberService.selectAppBranchManager(paramNo, appClientMember.get(0).getMainBranchId());
    		if(appMemberId.size() > 0) {
    			myTotalLearningTm = branchMemberService.selectStatusListEntryList2(appMemberId.get(0).getBranchId(), appMemberId.get(0).getMemberId(), null, paramStartDt, paramEndDt);
    			if(myTotalLearningTm.size() > 0) {
    				int myDiff = 0;
    		        for (StatusListEntry sle : myTotalLearningTm) {
    		        	////////total 시간 계산    		
    		        	int tempIndex = myTotalLearningTm.indexOf(sle);
    		        	
    		        	if (sle.getEntryType().equals("1") || sle.getEntryType().equals("4")) {
    		            	//dateIn.
    		            	totalLearning.setEntryDt(sle.getEntryDtOg());
    		            	totalLearning.setEntryType(sle.getEntryType());
    		            	totalLearning.setBusinessDt(sle.getBusinessDt());

    		            } else if ((sle.getEntryType().equals("2") || sle.getEntryType().equals("3")) && totalLearning.getEntryType() != null && sle.getBusinessDt().equals(totalLearning.getBusinessDt())) {
    		            	if (totalLearning.getEntryType().equals("2") || totalLearning.getEntryType().equals("3")) {
    		            		
    		            	} else if (totalLearning.getEntryType().equals("1") || totalLearning.getEntryType().equals("4")) {
    			            	Calendar startCal = Calendar.getInstance();
    			            	Calendar endCal = Calendar.getInstance();
    			            	
    			            	startCal.setTime(totalLearning.getEntryDt());
    			            	endCal.setTime(sle.getEntryDtOg());
    			            	
    			            	totalLearning = new TotalLearning();
    			            	totalLearning.setEntryDt(sle.getEntryDtOg());
    			            	totalLearning.setEntryType(sle.getEntryType());
    			            	totalLearning.setBusinessDt(sle.getBusinessDt());
    			            	
    			            	long diffMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
    			
    			            	myDiff += (int)(diffMillis/(1000 * 60));
    		            	}
    		            }
    		        	
    		        	
    		        	if(tempIndex != 0) {
    		        		if(!sle.getBusinessDt().equals(myTotalLearningTm.get(tempIndex-1).getBusinessDt()) || tempIndex == myTotalLearningTm.size()-1) { //같은 데이터            		
    		        			if (tempIndex == myTotalLearningTm.size()-1) {
    		                		totalLearning = new TotalLearning();
    		                	}	            	
    		                		if (myDiff != 0) {
    		        					myTime.setDate(myTotalLearningTm.get(tempIndex-1).getBusinessDt());
    			                		myTime.setTime(myDiff);
    			                		myTimeList.add(myTime);
    			                		
    			                		myDiff = 0;
    			                		myTime = new ReportingTime.Time();
    		                		}

    		        		}

    		        		        		
    		        	}   	
    		     	
    		        }
    			} else {
    				
    			}
    		} else {
//    			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
//    	        
//    	        header.setResult_code(result_code.getCode());
//    	        header.setResult_message(result_code.getMessage());
//    	        apiResult = new ApiResult(result_data, header);
//
//        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    	} else {
//    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
//	        
//	        header.setResult_code(result_code.getCode());
//	        header.setResult_message(result_code.getMessage());
//	        apiResult = new ApiResult(result_data, header);
//
//    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	


        

        //////////////////////////전국 시간계산/////////////////////////////
    	ReportingTime.Time allTime = new ReportingTime.Time();
    	List<ReportingTime.Time> allTimeList = new ArrayList<ReportingTime.Time>();
    	
        List<StatisticsEntry.Entry> statisticsEntryList = branchMemberService.selectStatisticsEntry(paramStartDt, paramEndDt, null);
        if (statisticsEntryList.size() > 0) {
        	for (StatisticsEntry.Entry sel : statisticsEntryList) {
        		allTime.setDate(sel.getBusinessDt());
        		Integer temp = (sel.getTotalTime() / sel.getTotalCount());
        		allTime.setTime(temp);
        		allTimeList.add(allTime);
        		
        		allTime = new ReportingTime.Time();
        	}
        } else {
        	result_data.setAllTimeList(null);
        }

        
        
        if (myTimeList.size() == 0) {
        	ReportingTime.Time myTimeTemp = new ReportingTime.Time(); 
        	for (ReportingTime.Time rt : allTimeList) {
        		myTimeTemp.setDate(rt.getDate());
        		myTimeTemp.setTime(0);
        		myTimeList.add(myTimeTemp);
        		myTimeTemp = new ReportingTime.Time();
        		
        	}
        }
        
        result_data.setMyTimeList(myTimeList);	
    	result_data.setAllTimeList(allTimeList);
    	
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }
    
    //리포팅 랭킹관리
    @RequestMapping(value = "/reportingRank", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> reportingRank(@RequestHeader(value="no", required=false) String paramNo, @RequestHeader(value="role", required=false) Integer paramRole,
														@RequestParam(value="startDt", required=false) String paramStartDt, @RequestParam(value="endDt", required=false) String paramEndDt,
														@RequestParam(value="area", required=false) Integer paramArea ) {

    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        
        Rank.Response result_data = null;
    	
        ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	    	
    	if (paramNo == null) {
//    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
//	        
//	        header.setResult_code(result_code.getCode());
//	        header.setResult_message(result_code.getMessage());
//	        apiResult = new ApiResult(result_data, header);
//
//    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	if (paramRole == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	if (paramStartDt == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	if (paramEndDt == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	if (paramArea == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	
    	result_data = modelMapper.map("", Rank.Response.class);    	

    	
    	Object area = Constants.AreaTypes.getMap().get(paramArea);
    	
    	String memberId = null;
    	//String branchId = null;
    	String no = null;
    	
    	if (paramRole == 10) { //일반회원
    		no = paramNo;
    	} else if (paramRole == 20) { //학부모 회원
    		List<AppClientMember> childNo = appMemberService.selectAppMemberList(paramNo, 10);
    		if (childNo.size() > 0) {
    			no = childNo.get(0).getMainChildNo();
    		}
    		
    	} else {

    		
//    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
//	        
//	        header.setResult_code(result_code.getCode());
//	        header.setResult_message(result_code.getMessage());
//	        apiResult = new ApiResult(result_data, header);
//
//    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		
    	}
	    	
    	List<AppClientMember> appMember = appMemberService.selectAppMemberList(no, 10);
    	if (appMember.size() > 0) {
    		List<AppClientMember> branchManager = appMemberService.selectAppBranchManager(no, appMember.get(0).getMainBranchId()); //memberId 뽑기
    		if (branchManager.size() > 0) {
    			memberId = branchManager.get(0).getMemberId();
    		}
    	} else {
    		AppClientMember tempAppClientMember = new AppClientMember();
    		appMember.add(tempAppClientMember);
    		//appId, no, mainBranchId, studentId AS id, studentPw AS pw,  studentName AS name, tel
    		
//    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
//	        
//	        header.setResult_code(result_code.getCode());
//	        header.setResult_message(result_code.getMessage());
//	        apiResult = new ApiResult(result_data, header);
//
//    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	String strArea = area.toString();
    	if (paramArea == 10) {
    		strArea = null;
    	} 
    	List<String> branchIdList = branchService.selectBranchArea(strArea);
    	    	
    	if (branchIdList.size() > 0) {
    		//for (String branchId : branchIdList) {  			
			List<Rank.RankInfo> rankList = branchMemberService.selectStatisticsEntryRank(paramStartDt, paramEndDt, branchIdList, memberId);
			Rank.RankInfo myRankObject = new Rank.RankInfo(); 
			
			if (rankList.size() > 0) {
				for (Rank.RankInfo rr : rankList) {
					if (rr.getMemberId().equals(memberId)) {
						myRankObject = rr;
					}
				}
				
				result_data.setRankList(rankList);
			}
			result_data.setName(appMember.get(0).getName());
			result_data.setArea(area.toString());
    		result_data.setMyRank(myRankObject.getRank());
    		
    		//}
    	} else {
    		result_data.setName(null);
    		result_data.setArea(area.toString());
    		result_data.setMyRank(null);
    		result_data.setRankList(null);
    		
    	}
    	
    	
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }
    
    

 	//리포팅 누적 학습시간
    @RequestMapping(value = "/reportingTimeTest", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> reportingTimeTest(@RequestHeader(value="no", required=false) String paramNo, @RequestHeader(value="role", required=false) Integer paramRole,
    													//@RequestParam(value="no", required=false) String paramNo, @RequestParam(value="role", required=false) Integer paramRole,
    													@RequestParam(value="startDt", required=false) String paramStartDt, @RequestParam(value="endDt", required=false) String paramEndDt) {

    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        
        ReportingTime.Response result_data = null;
    	
        ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	    	
    	if (paramNo == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	if (paramRole == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}

    	if (paramStartDt == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	if (paramEndDt == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}    	
    	  	
    
    	result_data = modelMapper.map("", ReportingTime.Response.class);    	


        //나의시간 계산
    	List<AppClientMember> appClientMember = appMemberService.selectAppMemberList(paramNo, paramRole);
    	List<StatusListEntry> myTotalLearningTm = null;
    			
    	if(appClientMember.size() > 0) {
    		List<AppClientMember> appMemberId = appMemberService.selectAppBranchManager(paramNo, appClientMember.get(0).getMainBranchId());
    		if(appMemberId.size() > 0) {
    			myTotalLearningTm = branchMemberService.selectStatusListEntryList2(appMemberId.get(0).getBranchId(), appMemberId.get(0).getMemberId(), null, paramStartDt, paramEndDt);
    			if(myTotalLearningTm.size() > 0) {
    				
    			} else {
    				result_data.setMyTimeList(null);
    			}
    		} else {
    			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
    	        
    	        header.setResult_code(result_code.getCode());
    	        header.setResult_message(result_code.getMessage());
    	        apiResult = new ApiResult(result_data, header);

        		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    		}
    	} else {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    			    	
    	List<ReportingTime.Temp> entryMemberIdList = branchMemberService.selectEntryMemberId(null, null);
    	
        //시간 계산
    	TotalLearning totalLearning = new TotalLearning();
    	ReportingTime.TimeTest myTime = new ReportingTime.TimeTest();
    	List<ReportingTime.TimeTest> myTimeList = new ArrayList<ReportingTime.TimeTest>();
    	
    	ReportingTime.Time tempTime = new ReportingTime.Time();
    	List<ReportingTime.Time> tempTimeList = new ArrayList<ReportingTime.Time>();
    	
    	
    	if(entryMemberIdList.size() > 0) {
        	totalLearning = new TotalLearning();
        	myTime = new ReportingTime.TimeTest();
        	myTimeList = new ArrayList<ReportingTime.TimeTest>();
        	
    		for(ReportingTime.Temp em : entryMemberIdList) {
				myTotalLearningTm = branchMemberService.selectStatusListEntryList2(em.getBranchId(), em.getMemberId(), null, null, null);
				
				if(myTotalLearningTm.size() > 0) {
					
				} else {
					//에러
				}

	
		        int myDiff = 0;
		        for (StatusListEntry sle : myTotalLearningTm) {
		        	////////total 시간 계산    		
		        	int tempIndex = myTotalLearningTm.indexOf(sle);
		        			        	
		        	if (sle.getEntryType().equals("1") || sle.getEntryType().equals("4")) {
		            	//dateIn.
		            	totalLearning.setEntryDt(sle.getEntryDtOg());
		            	totalLearning.setEntryType(sle.getEntryType());
		            	totalLearning.setBusinessDt(sle.getBusinessDt());
		
		            } else if ( (sle.getEntryType().equals("2") || sle.getEntryType().equals("3")) && totalLearning.getEntryType() != null && sle.getBusinessDt().equals(totalLearning.getBusinessDt()) ) {
		            	if (totalLearning.getEntryType().equals("2") || totalLearning.getEntryType().equals("3") ) {
		            		
		            	} else if (totalLearning.getEntryType().equals("1") || totalLearning.getEntryType().equals("4")) {
		            		
		            		Calendar startCal = Calendar.getInstance();
			            	Calendar endCal = Calendar.getInstance();
			            	
			            	startCal.setTime(totalLearning.getEntryDt());
			            	endCal.setTime(sle.getEntryDtOg());
			            	
			            	totalLearning = new TotalLearning();
			            	totalLearning.setEntryDt(sle.getEntryDtOg());
			            	//totalLearning.setEntryType("2");
			            	totalLearning.setEntryType(sle.getEntryType());
			            	totalLearning.setBusinessDt(sle.getBusinessDt());
		            		long diffMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
		        			
			            	myDiff += (int)(diffMillis/(1000 * 60));

		            	
		            	}
	
		            }
		        			        	
		        	//날짜가 다른 데이터
		        	if(tempIndex != 0 ) {
			        	if ( !sle.getBusinessDt().equals(myTotalLearningTm.get(tempIndex-1).getBusinessDt()) || tempIndex == myTotalLearningTm.size()-1 ) {		        	
				        	
			        		if (myDiff != 0) {
			            		myTime.setBusinessDt((myTotalLearningTm.get(tempIndex-1).getBusinessDt()));
			            		myTime.setTime(myDiff);
			            		myTime.setBranchId(em.getBranchId());
			            		myTime.setMemberId(em.getMemberId());
			            		
			    				//받아온 정보로 매장명 및 회원명 찾기
			    				List<BranchMember> codiMemberInfo = branchMemberService.selectMemberList(em.getBranchId(), em.getMemberId(), null); //코디에서 멤버찾기
			    								
			    				if (codiMemberInfo.size() > 0) { 
			    					myTime.setBranchNm(codiMemberInfo.get(0).getBranchNm());
									myTime.setMemberNm(codiMemberInfo.get(0).getName());
									myTime.setNo(codiMemberInfo.get(0).getNo());
			    				} else { //코디 정보가 없으면 App 에서 찾기
			    					List<AppClientMember> appClientMemberInfo = appMemberService.selectAppBranchManagerForMemberId(em.getMemberId());
			    					if (appClientMemberInfo.size() > 0) {
			    						List<AppClientMember> appClientName = appMemberService.selectAppMemberList(appClientMemberInfo.get(0).getNo(), 10);
			    						
			    						if (appClientName.size() > 0 ) {
			    							myTime.setBranchNm(appClientMemberInfo.get(0).getBranchNm());
			    							myTime.setMemberNm(appClientName.get(0).getName());
			    							myTime.setNo(appClientMemberInfo.get(0).getNo());
			    						}
			    					}
			    				}
			            		
			    				branchMemberService.insertStatisticsEntry(myTime);
			            		myTimeList.add(myTime);
			            		
			            		tempTime.setDate(myTime.getBusinessDt());
			            		tempTime.setTime(myTime.getTime());
			            		tempTimeList.add(tempTime);
			            		tempTime = new ReportingTime.Time();
			            		
			            		myDiff = 0;
			            		myTime = new ReportingTime.TimeTest();	            		
			            		totalLearning = new TotalLearning();            			        			

				        	}
			        	}
		        	}
		        	
        	
		        	
		        }
        
        
    		}
        
    	}
    	 result_data.setMyTimeList(tempTimeList);
        
        
       //////////////////////////전국 시간계산/////////////////////////////
    	ReportingTime.Time allTime = new ReportingTime.Time();
    	List<ReportingTime.Time> allTimeList = new ArrayList<ReportingTime.Time>();
    	
        List<StatisticsEntry.Entry> statisticsEntryList = branchMemberService.selectStatisticsEntry(paramStartDt, paramEndDt, null);
        if (statisticsEntryList.size() > 0) {
        	for (StatisticsEntry.Entry sel : statisticsEntryList) {
        		allTime.setDate(sel.getBusinessDt());
        		Integer temp = (sel.getTotalTime() / sel.getTotalCount());
        		allTime.setTime(temp);
        		allTimeList.add(allTime);
        		
        		allTime = new ReportingTime.Time();
        	}
        } else {
        	result_data.setAllTimeList(null);
        }

       
    	result_data.setAllTimeList(allTimeList);
    	
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }
    
    
    
    
    //공통코드
    @RequestMapping(value = "/commonCode", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> commonCode(@RequestParam(value="codeType", required=false) Integer paramCodeType ) {

    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        
        CommonCode result_data = null;
    	
        ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;
    	    	
    	if (paramCodeType == null) {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	result_data = modelMapper.map("", CommonCode.class);    	
    	
    	List<CommonCode.Code> codeList = new ArrayList<>();
    	CommonCode.Code tempCode = new CommonCode.Code();
    	
    	//직무분야
    	if(paramCodeType == 10) { 
	    	for(JobTypes jobType: JobTypes.values()) {
	            tempCode.setText(jobType.getText());
	            tempCode.setValue(jobType.getValue());
	            
	            codeList.add(tempCode);
	            tempCode = new CommonCode.Code();
	        }
    	} 
    	//관심분야
    	else if (paramCodeType == 20) {    		
	    	for(InterestTypes interestType: InterestTypes.values()) {
	            tempCode.setText(interestType.getText());
	            tempCode.setValue(interestType.getValue());
	            
	            codeList.add(tempCode);
	            tempCode = new CommonCode.Code();
	        }
    	} 
    	//관심분야
    	else if (paramCodeType == 30) {    		
    		//전국 코드
    		tempCode.setText(AreaTypes.ALL.getText());
    		tempCode.setValue(AreaTypes.ALL.getValue());
    		codeList.add(tempCode);
            tempCode = new CommonCode.Code();
    		    		
    		List<Branch> branchAreaList = branchService.selectBranchArea();
    		    		
    		for(Branch bal : branchAreaList) {
	    		for(AreaTypes AreaType: AreaTypes.values()) {
		            if (AreaType.getValue() == bal.getAreaType()) {
		    			tempCode.setText(AreaType.getText());
			            tempCode.setValue(AreaType.getValue());
			            
			            codeList.add(tempCode);
			            tempCode = new CommonCode.Code();
		            }
	    		}
    		}
    	} 
    	else {
    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        apiResult = new ApiResult(result_data, header);

    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    	}
    	
    	result_data.setCodeList(codeList);
    	
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }

    
    
    //전국 시간 test
    @RequestMapping(value = "/reportingTest", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> reportingTest(//@RequestHeader(value="no", required=false) String paramNo, @RequestHeader(value="role", required=false) Integer paramRole,
    													//@RequestParam(value="no", required=false) String paramNo, @RequestParam(value="role", required=false) Integer paramRole
    													@RequestParam(value="startDt", required=false) String paramStartDt, @RequestParam(value="endDt", required=false) String paramEndDt
    													) {

    	// 결과
        ModelMapper modelMapper = new ModelMapper();
        
        
        ReportingTime.ResponseTest result_data = null;
    	
        ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;


//    	if (paramStartDt == null) {
//    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
//	        
//	        header.setResult_code(result_code.getCode());
//	        header.setResult_message(result_code.getMessage());
//	        apiResult = new ApiResult(result_data, header);
//
//    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
//    	}
//    	
//    	if (paramEndDt == null) {
//    		result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
//	        
//	        header.setResult_code(result_code.getCode());
//	        header.setResult_message(result_code.getMessage());
//	        apiResult = new ApiResult(result_data, header);
//
//    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
//    	}    	
    	  	
    
    	result_data = modelMapper.map("",  ReportingTime.ResponseTest.class);    	


    	//시간 계산
    	List<StatusListEntry> myTotalLearningTm = null;
    			    	
    	//List<ReportingTime.Temp> entryMemberIdList = branchMemberService.selectEntryMemberId(null, null);
    	List<ReportingTime.Temp> entryMemberIdList = branchMemberService.selectEntryMemberId(paramStartDt, paramEndDt);
    	
        //시간 계산
    	TotalLearning totalLearning = new TotalLearning();
    	ReportingTime.TimeTest myTime = new ReportingTime.TimeTest();
    	List<ReportingTime.TimeTest> myTimeList = new ArrayList<ReportingTime.TimeTest>();
    	
    	
    	if(entryMemberIdList.size() > 0) {
        	totalLearning = new TotalLearning();
        	myTime = new ReportingTime.TimeTest();
        	myTimeList = new ArrayList<ReportingTime.TimeTest>();
        	
    		for(ReportingTime.Temp em : entryMemberIdList) {
				//myTotalLearningTm = branchMemberService.selectStatusListEntryList2(em.getBranchId(), em.getMemberId(), null, "2017-09-21", "2017-11-07");
    			myTotalLearningTm = branchMemberService.selectStatusListEntryList2(em.getBranchId(), em.getMemberId(), null, paramStartDt, paramEndDt);
    			
				if(myTotalLearningTm.size() > 0) {
					
				} else {
					//에러
				}

	
		        int myDiff = 0;
		        for (StatusListEntry sle : myTotalLearningTm) {
		        	////////total 시간 계산    		
		        	int tempIndex = myTotalLearningTm.indexOf(sle);
		        			        	
		        	if (sle.getEntryType().equals("1") || sle.getEntryType().equals("4")) {
		            	//dateIn.
		            	totalLearning.setEntryDt(sle.getEntryDtOg());
		            	totalLearning.setEntryType(sle.getEntryType());
		            	totalLearning.setBusinessDt(sle.getBusinessDt());
		
		            } else if ( (sle.getEntryType().equals("2") || sle.getEntryType().equals("3")) && totalLearning.getEntryType() != null && sle.getBusinessDt().equals(totalLearning.getBusinessDt()) ) {
		            	if (totalLearning.getEntryType().equals("2") || totalLearning.getEntryType().equals("3") ) {
		            		
		            	} else if (totalLearning.getEntryType().equals("1") || totalLearning.getEntryType().equals("4")) {
		            		
		            		Calendar startCal = Calendar.getInstance();
			            	Calendar endCal = Calendar.getInstance();
			            	
			            	startCal.setTime(totalLearning.getEntryDt());
			            	endCal.setTime(sle.getEntryDtOg());
			            	
			            	totalLearning = new TotalLearning();
			            	totalLearning.setEntryDt(sle.getEntryDtOg());
			            	//totalLearning.setEntryType("2");
			            	totalLearning.setEntryType(sle.getEntryType());
			            	totalLearning.setBusinessDt(sle.getBusinessDt());
		            		long diffMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
		        			
			            	myDiff += (int)(diffMillis/(1000 * 60));

		            	
		            	}
	
		            }
		        			        	
		        	//날짜가 다른 데이터
		        	if(tempIndex != 0 ) {
			        	if ( !sle.getBusinessDt().equals(myTotalLearningTm.get(tempIndex-1).getBusinessDt()) || tempIndex == myTotalLearningTm.size()-1 ) {		        	
				        	
			        		if (myDiff != 0) {
			            		myTime.setBusinessDt((myTotalLearningTm.get(tempIndex-1).getBusinessDt()));
			            		myTime.setTime(myDiff);
			            		myTime.setBranchId(em.getBranchId());
			            		myTime.setMemberId(em.getMemberId());
			            		
			    				//받아온 정보로 매장명 및 회원명 찾기
			    				List<BranchMember> codiMemberInfo = branchMemberService.selectMemberList(em.getBranchId(), em.getMemberId(), null); //코디에서 멤버찾기
			    								
			    				if (codiMemberInfo.size() > 0) { 
			    					myTime.setBranchNm(codiMemberInfo.get(0).getBranchNm());
									myTime.setMemberNm(codiMemberInfo.get(0).getName());
									myTime.setNo(codiMemberInfo.get(0).getNo());
			    				} else { //코디 정보가 없으면 App 에서 찾기
			    					List<AppClientMember> appClientMemberInfo = appMemberService.selectAppBranchManagerForMemberId(em.getMemberId());
			    					if (appClientMemberInfo.size() > 0) {
			    						List<AppClientMember> appClientName = appMemberService.selectAppMemberList(appClientMemberInfo.get(0).getNo(), 10);
			    						
			    						if (appClientName.size() > 0 ) {
			    							myTime.setBranchNm(appClientMemberInfo.get(0).getBranchNm());
			    							myTime.setMemberNm(appClientName.get(0).getName());
			    							myTime.setNo(appClientMemberInfo.get(0).getNo());
			    						}
			    					}
			    				}
			            		
			    				String timeTempStr = myTotalLearningTm.get(tempIndex-1).getEntryTm().replaceAll(":", "");
			    				//System.out.println("timeTempStr========================================================"+timeTempStr);
			    				Integer timeTempInt = Integer.parseInt(timeTempStr); 
			    				
			    				String timeTemp1Str = "023000";
			    				Integer timeTemp1 = Integer.parseInt(timeTemp1Str);
			    				
			    				String timeTemp2Str = "023300";
			    				Integer timeTemp2 = Integer.parseInt(timeTemp2Str);
			    				
			    				if (timeTemp1 < timeTempInt && timeTempInt < timeTemp2) {

			    					myTime.setAutoYn(true);
			    					//System.out.println("true========================================================");
			    				} else {
			    					myTime.setAutoYn(false);
			    					//System.out.println("false========================================================");
			    				}
			    				
			    				branchMemberService.insertStatisticsEntry(myTime);
			            		myTimeList.add(myTime);
			            		
			            		myDiff = 0;
			            		myTime = new ReportingTime.TimeTest();	            		
			            		//totalLearning = new TotalLearning();            			        			

				        	}
			        	}
		        	}
		        	
        	
		        	
		        }
        
        
    		}
        
    	}

  
    	result_data.setTimeTestList(myTimeList);
        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
        
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
    }
    

    
    @RequestMapping(value = "/settingVersion", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> getVersionGET(
									@RequestParam(value = "os") String paramOs) {
		// 안드로이드(A)/아이폰(I)

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

		Integer type = 20;
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
    
    
    @RequestMapping(value = "/seatTest", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> seatTest() {

		// 결과
		ModelMapper modelMapper = new ModelMapper();

		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		Integer type = 20;
		List<ResponseVersion> responseVersion = userService.selectVersion(null, type);
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
    
    @RequestMapping(value = "/entryTest", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> entryTest() {

    	List<Entry> entryList = branchMemberService.selectCurrentMemberEntryList();
    	if (entryList.size() > 0) {
    		for(Entry entry: entryList) {				
    			if (entry.getEntryType() != 2) {
    				branchMemberService.insertMemberEntryOUT(entry.getBranchId(), entry.getMemberId(), entry.getReservationId(), entry.getDeskId());
    			}
    		}
    	}
		// 결과
		ModelMapper modelMapper = new ModelMapper();

		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		Integer type = 20;
		List<ResponseVersion> responseVersion = userService.selectVersion(null, type);
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


    
    @RequestMapping(value = "/test", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> testest() {

    	
    	//try {
		//	Database db = DatabaseBuilder.open(new File("C:\\Program Files\\FreedomPro\\FreedomProMain.MDB")).get getTable(arg0);
		//	 System.out.println("MDB db ============================ : " + db);
			 
		//} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}

    	String dbFileSpec = "C:\\Program Files\\FreedomPro\\FreedomProMain.MDB";
    	try  {
    		Database db = DatabaseBuilder.open(new File(dbFileSpec));
    		//System.out.println("==================db=============="+db);
    		//for (String tableName : db.getTableNames()) {
    	        //System.out.println("================================="+tableName);
    	        Table table  =  db.getTable("회원관리");
    	        //System.out.println("===================table=============="+table);

    	        for (Row row : table) {
    	        	//System.out.println("==================row=============="+row);
    	        	    	        	
    	        }

    	        //table.getNextRow().
                //        .addRow(4, "권오중3", 2222, "", 4, "", "", "", 0, 4444, "", "", "", "", "", "", "", "", "", 0, "", "", "", "", "", -1, "", "", "", "", 0, "", 0, "", 0);
    	        
    	        Table table2  =  db.getTable("학생분류");
    	        for (Column col : table2.getColumns()) {
    	        	System.out.println("==================col=============="+col);
    	        	    	        	
    	        }
    	       
    	        
    	        //table2.
    	        //System.out.println("==================학생분류=============="+db.getColumnOrder());
    	        
    	        //table2.setAllowAutoNumberInsert(allowAutoNumInsert);
    	        //table2.setAllowAutoNumberInsert(true);
    	        
    	        
    	        //com.healthmarketscience.jackcess.Database. setColumnOrder(ColumnOrder newColumnOrder);

    	        
    	        //table2.addRow("10", "test");


    	        Column codeNum = table2.getColumn("코드번호");

    	        
    	        
    	        table = db.getTable("Test");
    	        
    	        final ColumnValidator cv = new ColumnValidator() {
    	            public Object validate(Column col, Object v1) {
    	              Number num = (Number)v1;
    	              if((num == null) || (num.intValue() < 0)) {
    	                throw new IllegalArgumentException("not gonna happen");
    	              }
    	              return v1;
    	            }
    	          };
    	          
    	          
    	        for(Column col : table2.getColumns()) {
    	          ColumnValidator cur = col.getColumnValidator();
    	          assertNotNull(cur);
    	          if("num".equals(col.getName())) {
    	            assertSame(cv, cur);
    	          } else {
    	            assertSame(SimpleColumnValidator.INSTANCE, cur);
    	          }
    	        }
    	        
    	        //Column idCol = table2.getColumn("id");
    	        Column dataCol = table2.getColumn("코드번호");
    	        Column numCol = table2.getColumn("내용");

    	        try {
    	          //idCol.setColumnValidator(cv);
    	          //fail("IllegalArgumentException should have been thrown");
    	        } catch(IllegalArgumentException e) {
    	          // success
    	        }
    	        //assertSame(SimpleColumnValidator.INSTANCE, idCol.getColumnValidator());
    	        
    	        try {
    	          table2.addRow(10, "test");
    	          //fail("IllegalArgumentException should have been thrown");
    	        } catch(IllegalArgumentException e) {
    	          //assertEquals("not gonna happen", e.getMessage());
    	        }
    	        //optionCursor.findFirstRow(table2.getColumn("코드번호"), "10");
    	        //Map<String, Object> updateRow = optionCursor.getCurrentRow();
    	        //updateRow.put("내용", "test");
    	        //optionCursor.updateCurrentRow(updateRow); // => only to get the same exception.
    				
    	   // }
    	} catch (Exception e) {
    	    e.printStackTrace(System.err);
    	}

    	
    	ModelMapper modelMapper = new ModelMapper();
        
        Result.Response result_data = modelMapper.map("", Result.Response.class);
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;

        
    	result_data.setResultYn(true);
    	
    	result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
    	    
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	}
    
    
    
    @RequestMapping(value = "/testSmartro", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> testSmartro() {

    	
    	//AppStudentApiV1Controller td = new AppStudentApiV1Controller();
    	//td.methodCls();
    	
    	
    	ModelMapper modelMapper = new ModelMapper();
        
        Result.Response result_data = modelMapper.map("", Result.Response.class);
    	ApiResultConsts.resultCode result_code = null;
    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
    	ApiResult apiResult = null;

        
    	result_data.setResultYn(true);
    	
    	result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
    	    
        header = modelMapper.map("", ApiResult.header.class);
        header.setResult_code(result_code.getCode());
        header.setResult_message(result_code.getMessage());
        
        apiResult = new ApiResult(result_data, header);
        
        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	}    
    
    
    

    
}
