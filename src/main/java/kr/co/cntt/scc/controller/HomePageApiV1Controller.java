package kr.co.cntt.scc.controller;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.alimTalk.AlimTalk;
import kr.co.cntt.scc.alimTalk.AlimTalkService;
import kr.co.cntt.scc.app.admin.common.ApiResult;
import kr.co.cntt.scc.app.admin.common.ApiResultConsts;
import kr.co.cntt.scc.app.admin.controller.APIController;
import kr.co.cntt.scc.app.student.model.AppClientMember;
import kr.co.cntt.scc.app.student.model.Result;
import kr.co.cntt.scc.model.BranchMember;
import kr.co.cntt.scc.model.Desk;
import kr.co.cntt.scc.model.Entry;
import kr.co.cntt.scc.model.Expense;
import kr.co.cntt.scc.model.Goods;
import kr.co.cntt.scc.model.MembershipCard;
import kr.co.cntt.scc.model.Notification;
import kr.co.cntt.scc.model.Order;
import kr.co.cntt.scc.model.Page;
import kr.co.cntt.scc.model.Pay;
import kr.co.cntt.scc.model.Rental;
import kr.co.cntt.scc.model.Reservation;
import kr.co.cntt.scc.model.Room;
import kr.co.cntt.scc.model.SafeService;
import kr.co.cntt.scc.model.SeatReservation;
import kr.co.cntt.scc.service.HomePageService;
import kr.co.cntt.scc.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * API 컨트롤러
 * 버전 1
 * Created by jslivane on 2016. 5. 1..
 */
@RestController
@Slf4j
@RequestMapping("/e/home/api/v1")
public class HomePageApiV1Controller {

    @Autowired
    private HomePageService homePageService;

	
	
    
    public HomePageApiV1Controller() {
    }

    /**************************************************************************************/


    /**
     * (지점의) CNT 스터디센터 홈페이지로부터 공지사항 추가
     * @param branchId
     * @return
     */    
	@RequestMapping(value = "/notice", method = { RequestMethod.POST,  RequestMethod.GET})
    public @ResponseBody ResponseEntity<?> insertNotice(@RequestParam(value="noticeId", required=false) String noticeId, @RequestParam(value="branchId", required=false)String branchId, 
    													@RequestParam(value="title", required=false) String title, @RequestParam(value="content", required=false)String content,
    													@RequestParam(value="noticeDt", required=false) String noticeDt) {

	ModelMapper modelMapper = new ModelMapper();
    
    Result.Response result_data = modelMapper.map("", Result.Response.class);
	ApiResultConsts.resultCode result_code = null;
	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
	ApiResult apiResult = null;
    	
	homePageService.insertNotice(noticeId, branchId, title, content, noticeDt);
	
    
	result_data.setResultYn(true);
	
	result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
	    
    header = modelMapper.map("", ApiResult.header.class);
    header.setResult_code(result_code.getCode());
    header.setResult_message(result_code.getMessage());
    
    apiResult = new ApiResult(result_data, header);
    
    return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }

	
	/**
     * (지점의) CNT 스터디센터 홈페이지로부터 공지사항 수정
     * @param branchId
     * @return
     */    
	@RequestMapping(value = "/updateNotice", method = { RequestMethod.POST })
    public @ResponseBody ResponseEntity<?> updateNotice(@RequestParam(value="noticeId", required=false) String noticeId, @RequestParam(value="branchId", required=false)String branchId, 
    													@RequestParam(value="title", required=false) String title, @RequestParam(value="content", required=false)String content,
    													@RequestParam(value="noticeDt", required=false) String noticeDt) {

	ModelMapper modelMapper = new ModelMapper();
    
    Result.Response result_data = modelMapper.map("", Result.Response.class);
	ApiResultConsts.resultCode result_code = null;
	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
	ApiResult apiResult = null;
    	
	homePageService.insertNotice(noticeId, branchId, title, content, noticeDt);
	
    
	result_data.setResultYn(true);
	
	result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
	    
    header = modelMapper.map("", ApiResult.header.class);
    header.setResult_code(result_code.getCode());
    header.setResult_message(result_code.getMessage());
    
    apiResult = new ApiResult(result_data, header);
    
    return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

    }
	
	
	
}
