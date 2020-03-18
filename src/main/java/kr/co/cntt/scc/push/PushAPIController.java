package kr.co.cntt.scc.push;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping("/app/client/api/v1")
public class PushAPIController {
	
	private static final Logger logger = LoggerFactory.getLogger(PushAPIController.class);
	
	@Autowired
	private PushService pushService;
	
	/**
	 * GCM, APNS Registration Key Insert
	 * 예약픽업 Smith:
	 * 앱 첫 설치 및 재설치 시 deviceId와 registrationId를 DB에 저장시킨다.
	 * 테이블에 deviceId가 없으면 insert, 있으면 registrationId만 update
	 */
	@RequestMapping(value = "/push")
	@ResponseBody
	public ApiResultPush insertRegistrationId(@ModelAttribute Push param_push, final HttpServletRequest request, final HttpServletResponse response) {
		
		logger.info("=== API NAME : insertRegistraionId ===");
		
		ApiResultPush apiResult = new ApiResultPush();
		Integer resultCount = 0;
		Integer AndroidDeviceIdCount = 0;
		Integer IPhoneDeviceIdCount  = 0;
		
		if ( param_push.getRegistrationId() == null 
				|| param_push.getOsType() == null
				|| param_push.getDeviceId() == null ) {
			
			System.out.println("========== 파라미터 누락 : "+param_push.getRegistrationId()+" : "+param_push.getOsType()+" : "+param_push.getDeviceId());
			
			apiResult.setResult_code("E0002");
			apiResult.setResult_message("파라미터 누락");
		} else {
			/* 아이폰 */
			if( param_push.getOsType().equals("iphone") ) {
				
				IPhoneDeviceIdCount = this.pushService.getIPhoneKeyCount(param_push);
				
				/* 해당 deviceId의 존재 여부에 따른 분기 */
				if(IPhoneDeviceIdCount == 0) {
					
					resultCount = this.pushService.insertIPhoneRegistrationId(param_push);
				} else {
					
					resultCount = this.pushService.updateIPhoneRegistrationId(param_push);
				}
				
			/* 안드로이드 */
			} else {
					//2017-05-25 Jake 안드로이드 v6.0 이상시 권한 미획득으로 디바이스정보 미발췌 현상(devidid->registrationid)
					if(param_push.getDeviceId().equals("")){					
						param_push.setDeviceId(param_push.getRegistrationId());
					}
				
				AndroidDeviceIdCount = this.pushService.getAndroidKeyCount(param_push);
				
				/* 해당 deviceId의 존재 여부에 따른 분기 */
				if(AndroidDeviceIdCount == 0) {
					
					resultCount = this.pushService.insertAndroidRegistrationId(param_push);
				} else {
					
					resultCount = this.pushService.updateAndroidRegistrationId(param_push);
				}
			}
			
			if (resultCount < 1) {
				apiResult.setResult_code("E0001");
				apiResult.setResult_message("데이터 등록 실패");
			} else {
				apiResult.setResult_code("S0000");
				apiResult.setResult_message("성공");
			}
		}		
		
		return apiResult;
	}
	
	/**
	 * GCM, APNS 예약픽업 푸시용 Registration Key Insert
	 * @param Push(osType, orderId, deviceId, registrationId)
	 */
	@RequestMapping(value = "/insertPickupRegistrationId.json")
	@ResponseBody
	public ApiResultPush insertPickupRegistrationId(@ModelAttribute Push param_push) {
		
		logger.info("=== API NAME : insertRegistraionId ===");
		
		ApiResultPush apiResult = new ApiResultPush();
		Integer resultCount = 0;
		Push pushInfo = new Push();
		
		System.out.println("getRegistrationId : " + param_push.getRegistrationId());
		System.out.println("getOsType : " + param_push.getOsType());
		System.out.println("getDeviceId : " + param_push.getDeviceId());
		System.out.println("getOrderId : " + param_push.getOrderId());
		
		if ( param_push.getRegistrationId() == null 
				|| param_push.getOsType() == null
				|| param_push.getDeviceId() == null 
				|| param_push.getOrderId() == null ) {
			
			System.out.println("========== 파라미터 누락 : "+param_push.getRegistrationId()+" : "+param_push.getOsType()+" : "+param_push.getDeviceId()+" : "+param_push.getOrderId());
			
			apiResult.setResult_code("E0002");
			apiResult.setResult_message("파라미터 누락");
		} else {
			
			//pushInfo = this.pushService.getPickupKeyInfo(param_push);
			
			if(pushInfo == null) {	
				//resultCount = this.pushService.insertPickupKeyInfo(param_push);
			}
			
			if (resultCount < 1) {
				apiResult.setResult_code("E0001");
				apiResult.setResult_message("데이터 등록 실패");
			} else {
				apiResult.setResult_code("S0000");
				apiResult.setResult_message("성공");
			}
		}		
		
		return apiResult;
	}
	
	/**
	 * 예약픽업 제조완료 푸쉬 발송
	 * @param orderId, osType
	 */
	@RequestMapping(value="/sendPickupPush")
	@ResponseBody
	public ApiResultPush sendPickupPush(@ModelAttribute Push param_push) {
		
		logger.info("=== API NAME : sendPickupPush ===");
		
		
		ApiResultPush apiResult = new ApiResultPush();
		Push pushInfo = new Push();
		
		if(param_push == null || param_push.getOsType() == null || param_push.getOrderId() == null) {
			apiResult.setResult_code("E0002");
			apiResult.setResult_message("파라미터 누락");
			return apiResult;
		}
		
		try {
			if(param_push.getOsType().equals("iphone")) {
				//orderId로 아이폰 registrationId 검색
				//pushInfo = this.pushService.getPickupKeyInfo(param_push);
				
				if(pushInfo == null || pushInfo.getAlarmYN().equals("N")) {
					apiResult.setResult_code("E0003");
					apiResult.setResult_message(pushInfo == null? "예약픽업 키 미존재" : "푸시 알람 거부");
				} else {
					logger.info("registrationId : " + pushInfo.getRegistrationId());
					apiResult = this.pushService.IPhonePushSend(pushInfo.getRegistrationId());
				}
				
			} else {
				//orderId로 안드로이드 registrationId 검색
				//pushInfo = this.pushService.getPickupKeyInfo(param_push);
				
				if(pushInfo == null || pushInfo.getAlarmYN().equals("N")) {
					apiResult.setResult_code("E0003");
					apiResult.setResult_message(pushInfo == null? "예약픽업 키 미존재" : "푸시 알람 거부");
				} else {
					logger.info("registrationId : " + pushInfo.getRegistrationId());
					apiResult = this.pushService.AndroidPushSend(pushInfo.getRegistrationId());
				}
			}
		} catch (Exception e) {
			apiResult.setResult_code("E9999");
			apiResult.setResult_message("예외 발생");
			e.printStackTrace(); 
		}
		
		return apiResult;
	}
	

	
	
}