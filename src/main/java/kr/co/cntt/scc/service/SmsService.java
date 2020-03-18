package kr.co.cntt.scc.service;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.util.AuthUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * SMS 서비스
 * Created by jslivane on 2016. 7. 8..
 *
 * API STORE 대용량 SMS 서비스 : http://www.apistore.co.kr/api/apiViewPrice.do?service_seq=151
 *
 */
@Service
@Slf4j
public class SmsService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    /*******************************************************************************/

    /**
     * SMS 발신번호 등록
     * @param number
     * @param comment
     * @return
     */
    @Async
    public Future<SmsResultSaveSendNumber> saveSmsSendNumber(String number, String comment) {
        if(StringUtils.isEmpty(number)) return null;

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(new FormHttpMessageConverter()); // FORM_URLENCODED

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("x-waple-authorization", Constants.SMS_SERVER_KEY);

        try {
            String url = Constants.SMS_SAVE_SEND_NUMBER_SERVER_URL;

            // FORM_URLENCODED
            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
            param.add("sendnumber", number.replaceAll("-", ""));
            param.add("comment", comment);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(param, headers);

            ResponseEntity<SmsResultSaveSendNumber> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, SmsResultSaveSendNumber.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                SmsResultSaveSendNumber result = responseEntity.getBody();

                return new AsyncResult<>(result);

            } else {
                log.error("error in saveSmsSendNumber", responseEntity.getBody().toString());

            }

        } catch (RestClientException e) {
            log.error("error in saveSmsSendNumber", e);

        }

        return null;

    }

    /**
     * SMS 발신번호 리스트 조회
     * @return
     */
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


    /*******************************************************************************/
    @Async
    public Future<SmsResult> sendSmsTest(int i) {
    	System.out.println(i);
    	return null;
    }

    /**
     * SMS 발송
     * @param branchId
     * @param userId
     * @param from
     * @param fromNumber
     * @param to
     * @param toNumber
     * @param msg
     * @return
     */
    @Async
    public Future<SmsResult> sendSms(String branchId, String userId,
                                     String from, String fromNumber, String to, String toNumber, String msg) {
        if(StringUtils.isEmpty(fromNumber) || StringUtils.isEmpty(toNumber)) return null;

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(new FormHttpMessageConverter()); // FORM_URLENCODED

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("x-waple-authorization", Constants.SMS_SERVER_KEY);

        try {
            String url = Constants.SMS_SEND_SERVER_URL;

            // FORM_URLENCODED
            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
            param.add("send_name", from);
            param.add("send_phone", fromNumber.replaceAll("-", ""));
            param.add("dest_name", to);
            param.add("dest_phone", toNumber.replaceAll("-", ""));
            param.add("msg_body", msg);


            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(param, headers);

            ResponseEntity<SmsResult> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, SmsResult.class);
            //ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {

                SmsResult result = responseEntity.getBody();

                // SMS 발송 이력 기록
                insertHistorySms(branchId, userId, fromNumber, toNumber, msg,
                        result.getResult_code(), result.getCmid());

                return new AsyncResult<>(result);

            } else {
                log.error("error in sendSms", responseEntity.getBody().toString());

            }

        } catch (RestClientException e) {
            log.error("error in sendSms", e);

        }

        return null;

    }

    
    /**
     * SMS 발송
     * @param branchId
     * @param userId
     * @param from
     * @param fromNumber
     * @param to
     * @param toNumber
     * @param msg
     * @return
     */
    public Future<SmsResult> sendSms2(String branchId, String userId,
                                     String from, String fromNumber, String to, String toNumber, String msg) {
        if(StringUtils.isEmpty(fromNumber) || StringUtils.isEmpty(toNumber)) return null;

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(new FormHttpMessageConverter()); // FORM_URLENCODED

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("x-waple-authorization", Constants.SMS_SERVER_KEY);

        try {
            String url = Constants.SMS_SEND_SERVER_URL;

            // FORM_URLENCODED
            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
            param.add("send_name", from);
            param.add("send_phone", fromNumber.replaceAll("-", ""));
            param.add("dest_name", to);
            param.add("dest_phone", toNumber.replaceAll("-", ""));
            param.add("msg_body", msg);


            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(param, headers);

            ResponseEntity<SmsResult> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, SmsResult.class);
            //ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {

                SmsResult result = responseEntity.getBody();

                // SMS 발송 이력 기록
                insertHistorySms(branchId, userId, fromNumber, toNumber, msg,
                        result.getResult_code(), result.getCmid());

                return new AsyncResult<>(result);

            } else {
                log.error("error in sendSms", responseEntity.getBody().toString());

            }

        } catch (RestClientException e) {
            log.error("error in sendSms", e);

        }

        return null;

    }

    /*******************************************************************************/


    @Data
    public static class Sms {
        /**
         * 발송시간(없을 경우 즉시발송)
         * “20130529171111”(2013-05-29-17:11:11)
         */
        private String send_time;

        /**
         * 등록된 발신자 전화번호(“-“제외/숫자만 입력)
         * “01012345678”
         */
        private String send_phone;

        /**
         * 수신자 전화번호(동보 발송 시 콤마”,”구분자사용) (“-“제외)
         * “01012345678”
         */
        private String dest_phone;

        /**
         * 발신자 이름(32byte 미만)
         */
        private String send_name;

        /**
         * 수신자 이름(32byte 미만)
         */
        private String dest_name;

        /**
         * 메시지의 내용(90byte 이하)
         * - 90byte가 넘을 경우 LMS로 자동 변환 발송
         */
        private String msg_body;

    }

    @Data
    public static class SmsResult {
        /**
         * 처리 결과 코드 (result_code)
         * 100 : User Error
         * 200 : OK
         * 300 : Parameter Error
         * 400 : Etc Error
         * 500 : 발신번호 사전 등록제에 의한 미등록 차단
         */
        private String result_code;

        /**
         * 서버에서 생성한 request를 식별할 수 있는 유일한 키 (cmid)
         */
        private String cmid;

    }


    @Data
    public static class SmsSendNumber {
        /**
         * 발신번호(“-“ 제외)
         * - 기본 : 8자리 ~ 11자리 발신번호
         * - 유선 전화번호 : 지역 번호를 포함
         * - 이동통신 전화번호
         * - 대표 전화번호: 15xx, 16xx, 18xx(번호 앞에 지역번호 사용금지)
         * 위 규칙 외 등록 필요 시 담당자(tel: 02-3289-2888, e-mail: apistore.help@kt.com)에게 문의
         */
        private String sendnumber;

        /**
         * 코멘트(200자)
         */
        private String comment;

    }

    @Data
    public static class SmsResultSaveSendNumber {
        /**
         * 처리 결과 코드
         * 100 : User Error
         * 200 : OK
         * 300 : Parameter Error
         * 400 : Etc Error
         * 500 : 중복등록
         */
        private String result_code;

        /**
         * 발신번호
         */
        private String sendnumber;

    }

    @Data
    public static class SmsResultListSendNumber {
        /**
         * 처리 결과 코드 (result_code)
         * 100 : User Error
         * 200 : OK
         * 300 : Parameter Error
         * 400 : Etc Error
         */
        private String result_code;

        /**
         * 발신번호 리스트
         */
        private List<SmsSendNumber> numberList;

    }


    /*******************************************************************************/


    /**
     * SMS 발송 이력 기록
     * @param branchId
     * @param userId
     * @param fromNumber
     * @param toNumber
     * @param msg
     * @param resultCode
     * @param cmid
     * @return
     */
    public int insertHistorySms(String branchId, String userId,
                                String fromNumber, String toNumber, String msg,
                                String resultCode, String cmid) {
        String s = " INSERT INTO history_sms ( " +
                " branchId, smsDt, userId, fromNumber, toNumber, msg, resultCode, cmid, insertId " +
                " ) VALUES ( " +
                " :branchId, NOW(), :userId, :fromNumber, :toNumber, :msg, :resultCode, :cmid, :insertId " +
                " ) ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("userId", userId);
        args.put("fromNumber", fromNumber);
        args.put("toNumber", toNumber);
        args.put("msg", msg);

        args.put("resultCode", resultCode);
        args.put("cmid", cmid);

        args.put("insertId", AuthUtil.getCurrentUserId());

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        return jdbcTemplate.update(s, args);

    }


    /*******************************************************************************/


}
