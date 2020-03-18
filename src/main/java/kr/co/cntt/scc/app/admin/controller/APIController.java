package kr.co.cntt.scc.app.admin.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import kr.co.cntt.scc.alimTalk.AlimTalkService;
import kr.co.cntt.scc.util.JSONUtils;
import kr.co.cntt.scc.app.admin.common.ApiResult;
import kr.co.cntt.scc.app.admin.common.ApiResultConsts;
import kr.co.cntt.scc.app.admin.common.ApiResultConsts.resultCode;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class APIController {

	public static ResponseEntity<?> apiResult(ApiResult apiResult, String result_code, String result_message) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json;charset=UTF-8");
		
		HttpStatus httpStatus = null;

		int code_length = ApiResultConsts.resultCode.values().length;
		resultCode[] apiResultArr = ApiResultConsts.resultCode.values();
		
		for (int i = 0; i < code_length; i++) {
			
			if ( apiResultArr[i].getCode().equals(result_code) ) {
				httpStatus = apiResultArr[i].getHttpsCode();
			}
		}
		
		log.info("API HttpStatus CODE : " + httpStatus);
		
		// 성공 및 실패 로그
		if (result_code.equals(ApiResultConsts.resultCode.SUCCESS.getCode())) {
			
			// 성공시 : 기본 성공메세지, 코드 적용되어 있음
			log.info("API SUCCESS CODE : {}, MESSAGE : {}, DATA : {}", result_code, result_message, JSONUtils.toJson(apiResult.getBody()));
			
		} else {
			
			log.info("API FAIL ERROR CODE : {}, MESSAGE : {}", result_code, result_message);
		}
		
		//apiResult.setHeader(header);
		return new ResponseEntity<>(apiResult, responseHeaders, httpStatus);
	}
	
}
