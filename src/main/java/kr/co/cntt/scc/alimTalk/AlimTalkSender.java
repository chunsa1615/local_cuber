package kr.co.cntt.scc.alimTalk;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
//import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import kr.co.cntt.scc.util.JSONUtils;
import kr.co.cntt.scc.alimTalk.TimeUtils;
import kr.co.cntt.scc.service.HistoryService;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("alimTalkSender")
public class AlimTalkSender {

	private static final Map<String, String> codeMap;
	static {
		codeMap = new ConcurrentHashMap<String, String>();
		codeMap.put("0000", "성공");
		codeMap.put("1001", "Request Body가 Json형식이 아님");
		codeMap.put("1002", "허브 파트너 키가 유효하지 않음");
		codeMap.put("1003", "발신 프로필 키가 유효하지 않음");
		codeMap.put("1004", "Request Body(Json)에서 name을 찾을 수 없음");
		codeMap.put("1005", "발신프로필을 찾을 수 없음");
		codeMap.put("1006", "삭제된 발신프로필. (메시지 사업 담당자에게 문의)");
		codeMap.put("1007", "차단 상태의 발신프로필. (메시지 사업 담당자에게 문의)");
		codeMap.put("1008", "차단 상태의 옐로아이디. (옐로아이디 홈에서 확인)");
		codeMap.put("1009", "닫힘 상태의 옐로아이디. (옐로아이디 홈에서 확인)");
		codeMap.put("1010", "삭제된 옐로아이디. (옐로아이디 홈에서 확인)");
		codeMap.put("1011", "계약정보를 찾을 수 없음. (메시지 사업 담당자에게 문의)");
		codeMap.put("1012", "잘못된 형식의 유저키 요청");
		codeMap.put("1030", "잘못된 파라메터 요청");
		codeMap.put("2003", "메시지 전송 실패(테스트 서버에서 친구관계가 아닌 경우)");
		codeMap.put("2004", "템플릿 일치 확인시 오류 발생(내부 오류 발생)");
		codeMap.put("3000", "예기치 않은 오류 발생");
		codeMap.put("3005", "메시지를 발송했으나 수신확인 안됨 (성공불확실) - 서버에는 암호화 되어 보관되며 3일 이내 수신 가능");
		codeMap.put("3006", "내부 시스템 오류로 메시지 전송 실패");
		codeMap.put("3008", "전화번호 오류");
		codeMap.put("3010", "Json 파싱 오류");
		codeMap.put("3011", "메시지가 존재하지 않음");
		codeMap.put("3012", "메시지 일련번호가 중복됨");
		codeMap.put("3013", "메시지가 비어 있음");
		codeMap.put("3014", "메시지 길이 제한 오류(템플릿별 제한 길이 또는 1000자 초과");
		codeMap.put("3015", "템플릿을 찾을 수 없음");
		codeMap.put("3016", "메시지 내용이 템플릿과 일치하지 않음");
		codeMap.put("3018", "메시지를 전송할 수 없음");
		codeMap.put("4000", "메시지 전송 결과를 찾을 수 없음");
		codeMap.put("4001", "알 수 없는 메시지 상태");
		codeMap.put("9998", "시스템에 문제가 발생하여 담당자가 확인하고 있는 경우");
		codeMap.put("9999", "시스템에 문제가 발생하여 담당자가 확인하고 있는 경우");
	}
	

	public static final String KAKAO_HUB_PARTNER_KEY = "cf11c621ddf49c6f9973a6c4f0b71110eaacf3ab";

	/**
	 * default : push, kinds : push, polling, realtime
	 */
	public static final String KAKAO_ALIMTALK_SEND_TYPE = "push";
	public static final int KAKAO_ALIMTALK_SEND_TIMEOUT = 20;

	//public static final String KAKAO_HOST_URL = "https://dev-bzm-api.kakao.com/";
	public static final String KAKAO_HOST_URL = "https://bzm-api.kakao.com/";

	public static final String KAKAO_ALIMTALK_SEND_URL = "/v2/" + KAKAO_HUB_PARTNER_KEY + "/sendMessage";
	//public static final String KAKAO_ALIMTALK_SEND_URL = "/v1/" + KAKAO_HUB_PARTNER_KEY + "/sendMessageTF"; //TEST

	@Autowired
	private HistoryService historyService;
	
	
	/**
	 * @param AlimTalk
	 * @return AlimTalk.Response ( code, received_at, message)
	 * @throws NumberParseException
	 */
	public AlimTalk.Response send(AlimTalk talk) throws NumberParseException {

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	
		HttpEntity<AlimTalk.Request> httpParam = new HttpEntity<>(getAlimTalkParam(talk), getAlimTalkHeader());
		
		log.info("alimtalk send request ==> {}", httpParam.toString());
		
		ResponseEntity<String> response = restTemplate.postForEntity(
				KAKAO_HOST_URL + KAKAO_ALIMTALK_SEND_URL,
				//KAKAO_HOST_URL + KAKAO_ALIMTALK_SEND_URL_TEST,
				httpParam,
				String.class);
		
		log.info("alimtalk send response ==> {}", response.toString());
		
		AlimTalk.Response responseBody = JSONUtils.fromJson(response.getBody(), AlimTalk.Response.class);
		
		responseBody.setMessage(getMessage(responseBody.getCode()));
		
		//알림톡 history
		historyService.insertAlimtalkHistory(httpParam.getBody(), responseBody, talk.getLink_url(), talk.getBranchId());
		
		return responseBody;
	}

	/**
	 * extract Alimtalk Request Parameter
	 * 
	 * @param talk
	 * @return
	 * @throws NumberParseException
	 */
	private AlimTalk.Request getAlimTalkParam(AlimTalk talk) throws NumberParseException {
		AlimTalk.Request req = new AlimTalk.Request();

		req.setResponse_method(KAKAO_ALIMTALK_SEND_TYPE);
		req.setTimeout(KAKAO_ALIMTALK_SEND_TIMEOUT);
		req.setSerial_number(TimeUtils.getCurrentTime_DATE_FORMAT() + "-"
				+ StringUtils.rightPad(String.valueOf(new Random().nextInt(999999999)), 6));
		
		req.setSender_key(talk.getSender_key());
		
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		PhoneNumber number = phoneUtil.parse(talk.getPhone_number(), "KR");
		String phoneNumber = StringUtils.replace(number.getCountryCode() + "" + number.getNationalNumber(), "-", "");
		req.setPhone_number(phoneNumber);

		req.setTemplate_code(talk.getTemplate_cd());
		req.setMessage(talk.getMessage());

		if (!StringUtils.isEmpty(talk.getLink_url())) {
			AlimTalk.Button button = new AlimTalk.Button(
					StringUtils.isEmpty(talk.getLink_nm()) ? "링크" : talk.getLink_nm(), talk.getLink_url());

			AlimTalk.Attachment attachment = new AlimTalk.Attachment();
			attachment.setButton(button);
			req.setAttachment(attachment);
		}

		return req;
	}

	/**
	 * extract Alimtalk Response Parameter
	 * 
	 * @return
	 */
	private HttpHeaders getAlimTalkHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8); // JSON
		return headers;
	}

	
	private String getMessage( String code ) {
		return codeMap.get(code);
	}
	
}
