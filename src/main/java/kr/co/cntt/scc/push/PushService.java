package kr.co.cntt.scc.push;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javapns.communication.ConnectionToAppleServer;
import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import kr.co.cntt.scc.app.student.model.AppClientBranch;
import kr.co.cntt.scc.service.HistoryService;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;





@Service
@Transactional
@Slf4j
public class PushService {
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	private static final Logger logger = LoggerFactory.getLogger(PushService.class);
	static DefaultConfig defaultConfig = new DefaultConfig();
	
	
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;
		
	/* [아이폰] DB에 deviceID 존재 여부 */
	public Integer getIPhoneKeyCount(Push param_push) {
		String s = " SELECT COUNT(*) "
				+ " FROM iphone_push_info "
				+ " WHERE deviceId = :deviceId ";
		
		CombinedSqlParameterSource source = new CombinedSqlParameterSource(param_push);
		
		return jdbcTemplate.queryForObject(s, source, Integer.class);
	}
	
	/* [아이폰] deviceID, RegistraitionId, Push 수신 동의 여부 Insert  */
	public Integer insertIPhoneRegistrationId(Push param_push) {
		String s = " INSERT INTO iphone_push_info ( "
				 + " deviceId, registrationId, updateDt "
				 + " )  VALUES ( "
				 + " :deviceId, :registrationId "
				 + " ) ";
		
		CombinedSqlParameterSource source = new CombinedSqlParameterSource(param_push);
		
		
		 int result = jdbcTemplate.update(s, source);

	        if (result == 0) {
	            throw new InternalServerError("Failed to create AppStudent");

	        } else {
//	            History history = new History(branchId, Constants.HistoryType.PAY_CREATE, pay.toString());
//	            history.setOrderId(pay.getOrderId());
//	            history.setPayId(payId);
//	            history.setMemberId(pay.getMemberId());
//	            historyService.insertHistory(history);

	        }

	        return result;
	}
	
	/* [아이폰] RegistraitionId Update */
	public Integer updateIPhoneRegistrationId(Push param_push) {
		String s = " UPDATE	iphone_push_info "
				 + " SET registrationId = :registrationId, "
				 + " updateDt	= NOW() "
				 + " WHERE deviceId = :deviceId ";
		
		CombinedSqlParameterSource source = new CombinedSqlParameterSource(param_push);
		
		
		 int result = jdbcTemplate.update(s, source);

	        if (result == 0) {
	            throw new InternalServerError("Failed to create AppStudent");

	        } else {
//	            History history = new History(branchId, Constants.HistoryType.PAY_CREATE, pay.toString());
//	            history.setOrderId(pay.getOrderId());
//	            history.setPayId(payId);
//	            history.setMemberId(pay.getMemberId());
//	            historyService.insertHistory(history);

	        }

	        return result;
		
		
	}
	
	/* [아이폰] DB에 alarm_yn, agree_yn 정보 */
	public Push getIPhoneRegistrationInfo(Push param_push) {
		String s = " SELECT deviceId, registrationId, alarmYN, agreeYN, locationYN "
				+ " FROM iphone_push_info "
				+ " WHERE deviceId = :deviceId "
				+ " LIMIT 1 ";
		
		CombinedSqlParameterSource source = new CombinedSqlParameterSource(param_push);
		
		return jdbcTemplate.queryForObject(s, source, Push.class);
	}
	
	
	/* [안드로이드] DB에 deviceID 존재 여부 */
	public Integer getAndroidKeyCount(Push param_push) {
		String s = " SELECT COUNT(*) "
				 + " FROM android_push_info "
				 + " WHERE deviceId = :deviceId ";
		
		CombinedSqlParameterSource source = new CombinedSqlParameterSource(param_push);
		
		return jdbcTemplate.queryForObject(s, source, Integer.class);
	}
		
	/* [안드로이드] deviceID, RegistraitionId, Push 수신 동의 여부 Insert  */
	public Integer insertAndroidRegistrationId(Push param_push) {
		String s = " INSERT INTO android_push_info ( "
				+ " deviceId, registrationId "
				 + " ) VALUES ( "
				 + " :deviceId, :registrationId "
				 + " ) ";
		
		CombinedSqlParameterSource source = new CombinedSqlParameterSource(param_push);
		
		
		 int result = jdbcTemplate.update(s, source);

	        if (result == 0) {
	            throw new InternalServerError("Failed to create AppStudent");

	        } else {
//	            History history = new History(branchId, Constants.HistoryType.PAY_CREATE, pay.toString());
//	            history.setOrderId(pay.getOrderId());
//	            history.setPayId(payId);
//	            history.setMemberId(pay.getMemberId());
//	            historyService.insertHistory(history);

	        }

	        return result;
		
		//return this.pushDao.insertAndroidRegistrationId(param_push);
	}
	
	/* [안드로이드] RegistraitionId Update */
	public Integer updateAndroidRegistrationId(Push param_push) {
		String s = " UPDATE	android_push_info "
				 + " SET registrationId = :registrationId, "
				 + " updateDt = NOW() "
				 + " WHERE device_id = :deviceId ";
		
		CombinedSqlParameterSource source = new CombinedSqlParameterSource(param_push);
		
		
		 int result = jdbcTemplate.update(s, source);

	        if (result == 0) {
	            throw new InternalServerError("Failed to create AppStudent");

	        } else {
//	            History history = new History(branchId, Constants.HistoryType.PAY_CREATE, pay.toString());
//	            history.setOrderId(pay.getOrderId());
//	            history.setPayId(payId);
//	            history.setMemberId(pay.getMemberId());
//	            historyService.insertHistory(history);

	        }

	        return result;
		
		//return this.pushDao.updateAndroidRegistrationId(param_push);
	}
	
	/* [안드로이드] DB에 alarm_yn, agree_yn 정보 */
	public Push getAndroidRegistrationInfo(Push param_push) {
		String s = " SELECT deviceId, registrationId, alarmYN, agreeYN, locationYN "
         + " FROM android_push_info "
         + " WHERE deviceId = :deviceId "
         + " LIMIT 1 ";
		
		CombinedSqlParameterSource source = new CombinedSqlParameterSource(param_push);
		
		return jdbcTemplate.queryForObject(s, source, Push.class);
		//return this.pushDao.getAndroidRegistrationInfo(param_push);
	}
	

	
	
	/**
	 * 아이폰 APNS 메시지 발송
	 * @param registrationId
	 * @return ApiResult
	 */
	public ApiResultPush IPhonePushSend(String registrationId) {
		logger.info("=== METHOD NAME : IPhonePushSend ===");
		
		
		//String reg_id = this.pushDao.getPickupIPhoneKeyInfo(param_push);
		
		System.out.println("registrationId : " + registrationId);
		
		ApiResultPush apiResult = new ApiResultPush();
		
		try{
			// 푸시 데이터 생성
			PushNotificationPayload payLoad = new PushNotificationPayload();
			
			payLoad.addAlert("스터디센터입니다.");
			payLoad.addBadge(1);
			payLoad.addSound(defaultConfig.SOUND);
			//payLoad.addCustomDictionary("linkUrl", linkUrl);
			
			PushNotificationManager pushManager = new PushNotificationManager();
		    pushManager.initializeConnection(
		    // test : APNS_DEV_HOST, real : APNS_HOST
		    // adHoc로 만든 ipa는 APNS_HOST로 수정해야함
					new AppleNotificationServerBasicImpl(
							resourceLoader.getResource("classpath:/iPhoneKey/" + defaultConfig.CERTIFICATE_PATH).getURL().getPath(),
							defaultConfig.CERTIFICATE_PWD, ConnectionToAppleServer.KEYSTORE_TYPE_PKCS12,
							defaultConfig.APNS_HOST, defaultConfig.APNS_PORT));
//		    new AppleNotificationServerBasicImpl(defaultConfig.CERTIFICATE_PATH, defaultConfig.CERTIFICATE_PWD,ConnectionToAppleServer.KEYSTORE_TYPE_PKCS12, defaultConfig.APNS_HOST, defaultConfig.APNS_PORT));
		    
		    List<PushedNotification> notifications = new ArrayList<PushedNotification>();
		    
	    	// 싱글캐스트 푸시 전송
	    	Device device = new BasicDevice();
	    	device.setToken(registrationId);
	    	PushedNotification notification = pushManager.sendNotification(device, payLoad);
	    	notifications.add(notification);
		    
			List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
			List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);
			int failed = failedNotifications.size();
			int successful = successfulNotifications.size();
			
			//resultCode = "success="+Integer.toString(successful)+",failed="+Integer.toString(failed);
			
			if(failed > 0){
				apiResult.setResult_code("E0004");
				apiResult.setResult_message("푸시 발송 실패");
			} else {
				apiResult.setResult_code("S0000");
				apiResult.setResult_message("성공");
			}
			
			pushManager.stopConnection();
		
		} catch (Exception e) {
			apiResult.setResult_code("E9999");
			apiResult.setResult_message("예외 발생");
			e.printStackTrace();
		}
		
		return apiResult;
	}
	
	/**
	 * 안드로이드 GCM 메시지 발송
	 * @param registrationId
	 * @return ApiResult
	 */
	public ApiResultPush AndroidPushSend(String registrationId) {
		logger.info("=== METHOD NAME : AndroidPushSend ===");
		
		Result result = null;
		ApiResultPush apiResult = new ApiResultPush();
		
		
		try {
			
			Sender sender = new Sender(defaultConfig.simpleApiKey);
			Message message = new Message.Builder()
			.collapseKey(defaultConfig.MESSAGE_ID)
			.delayWhileIdle(defaultConfig.SHOW_ON_IDLE)
			.timeToLive(defaultConfig.LIVE_TIME)
			.addData("pushMessage", java.net.URLEncoder.encode("주문하신 메뉴가 제조완료되었습니다.", "EUC-KR"))		// pushMessage	: 푸쉬 메세지
			.addData("msg", java.net.URLEncoder.encode("주문하신 메뉴가 제조완료되었습니다.", "EUC-KR"))				// msg			: 푸쉬 메세지
			//.addData("pushType", "ad") // test
			.addData("pushType", "pick")	//real													// pushType		: ad(광고 푸쉬), ge(일반 푸시)
			//.addData("pushImage", "https://mobilehome.lotteria.com/img/app_promotion.jpg")	// pushImage	: 이미지 경로 (권장 사이즈는 720 * 350)
			//.addData("linkUrl", "https://mobilehome.lotteria.com")							// linkUrl		: 클릭 시 연결되는 링크
			.build();
			
			if ( !registrationId.isEmpty() )
				result = sender.send(message, registrationId, defaultConfig.RETRY);
			
			logger.info("MessageId : " + result.getMessageId());
			logger.info("ErrorCodeName : " + result.getErrorCodeName());
			
			if(result.getMessageId() != null && result.getErrorCodeName() == null) {
				apiResult.setResult_code("S0000");
				apiResult.setResult_message("성공");
			} else {
				apiResult.setResult_code("E0004");
				apiResult.setResult_message("푸시 발송 실패");
			}
			
		} catch (IOException e) {
			apiResult.setResult_code("E9999");
			apiResult.setResult_message("예외 발생");
			e.printStackTrace();
		}
		
		return apiResult;
	}
}
