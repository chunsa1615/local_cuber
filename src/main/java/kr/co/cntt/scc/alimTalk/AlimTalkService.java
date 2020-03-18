package kr.co.cntt.scc.alimTalk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.apache.commons.lang.StringUtils;
import org.springframework.util.StringUtils;

import com.google.i18n.phonenumbers.NumberParseException;

import kr.co.cntt.scc.app.admin.common.ApiResultConsts;
import kr.co.cntt.scc.service.AlimTalkSqlService;
import kr.co.cntt.scc.service.HistoryService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlimTalkService {

	public static final String ALIMTALK_SUCCESS_CODE = "0000";
	public static final String AUTH_CHANNEL_CD = "ALIMTALK";

	@Resource(name="alimTalkSender")
	AlimTalkSender sender;
	
	@Autowired
	private AlimTalkSqlService alimTalkSqlService;   
	
	
	
	
	public AlimTalkService send(AlimTalk alimtalk, AlimTalkType type) {

		AlimTalk.Response response = null;
		AlimTalkResult result = new AlimTalkResult();
		
		String phone_number = alimtalk.getPhone_number();
		phone_number = phone_number.replaceAll("-", "");
		phone_number = "82" + phone_number.substring(1, phone_number.length());
		
		alimtalk.setPhone_number(phone_number);
		AlimTalk o_alimtalk = organizeAlimtalk(alimtalk);
		
		try {
			
			response = sendProcess(o_alimtalk, type);
			result.setResult_data(response);
			
			if (!response.getCode().equals(ALIMTALK_SUCCESS_CODE)) {
				result.setResult_code(ApiResultConsts.resultCode.ERROR_REVERSE_API_CALL.getCode());
				result.setResult_message(ApiResultConsts.resultCode.ERROR_REVERSE_API_CALL.getMessage());
			}
			
		} catch ( Exception e) {
			//ServiceExceptionHandler.printErrorStack(log, e);
			result.setResult_code(ApiResultConsts.resultCode.ERROR_SYSTEM.getCode());
			result.setResult_message(ApiResultConsts.resultCode.ERROR_SYSTEM.getMessage());
		}
		
		log.info("Alimtalk result Data == > {}", result);
		//return result;
		return null;
	}
	
	private AlimTalk organizeAlimtalk(AlimTalk alimtalk) {
		AlimTalk o_alimtalk; 
		// 주문 번호가 존재할 때에는 주문 관련 정보를 조회, 존재하지 않을 경우에는 넘어온 데이터 기반으로 API호출
		if ( StringUtils.isEmpty(alimtalk.getBranchId())) {
			o_alimtalk = alimtalk;
		} else {
			//o_alimtalk = alimTalkDao.getAlimTalkOrderInfo(alimtalk.getOrder_id());
			o_alimtalk = alimtalk;
		}
		
		log.info("Alimtalk Order Data == > {}", o_alimtalk);
		
		return o_alimtalk;
	}

	private AlimTalk.Response sendProcess(AlimTalk alimtalk, AlimTalkType type) throws NumberParseException {
		AlimTalk.Response response = new AlimTalk.Response();

		if (alimtalk != null) {
			// 템플릿 코드, 템플릿 메세지 조회
			TbAlimtalkTemplate template = alimTalkSqlService.getAlimtalkTemplate(type.toString());
			alimtalk.setTemplate_cd(template.getTemplate_cd());			
			alimtalk.setTemplate_message(template.getTemplate_message());						
			//alimtalk.setSender_key("7c340b8e570e312bb290d8f91d8691ae7450d757"); //스터디카페어은센터						
			alimtalk.setSender_key(alimTalkSqlService.getArvApplyId(alimtalk.getBranchId(), "ALIMTALK")); //발신프로필 키			
			alimtalk.setMessage(substitueTemplateMessage(alimtalk));

			response = sender.send(alimtalk);
		}

		return response;
	}

	/**
	 * Substitutes variables within a template string by alimTalk property
	 * values.
	 * 
	 * @param alimtalk
	 * @return message
	 */
	private String substitueTemplateMessage(AlimTalk alimtalk) {
		String template = alimtalk.getTemplate_message();

		Map<String, String> values = new ConcurrentHashMap<String, String>();
		values.put("열람번호", alimtalk.getRoomName());
		values.put("좌석번호", alimtalk.getDeskName());
		values.put("회원명", alimtalk.getMemberName());		
		values.put("학교", alimtalk.getSchool());
		values.put("등록", alimtalk.getRegistration());
		values.put("가격", alimtalk.getAmount());

		StrSubstitutor sub = new StrSubstitutor(values, "#{", "}");

		return sub.replace(template);
	}

    public static AlimTalkType getType(String type) {
        AlimTalkType alimTalkType = null;

        try {
            alimTalkType = AlimTalkType.valueOf(type);
        } catch (NullPointerException | IllegalArgumentException e) {
            //ServiceExceptionHandler.printErrorStack(log, e);
        }

        return alimTalkType;
    }
	
}
