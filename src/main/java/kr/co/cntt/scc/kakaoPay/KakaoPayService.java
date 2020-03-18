package kr.co.cntt.scc.kakaoPay;

import kr.co.cntt.scc.util.HttpUtils;
import kr.co.cntt.scc.util.JSONUtils;
import kr.co.cntt.scc.exception.CancelPayProcessError;

import kr.co.cntt.scc.util.LoggingRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import kr.co.cntt.scc.kakaoPay.*;
import kr.co.cntt.scc.service.BranchDesignService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class KakaoPayService {

  public static final String KAKAO_API_ADMIN_KEY = "KakaoAK 6175c2f63b753de08beb4785c7ef0bbb";

  public static final String KAKAO_HOST_URL = "https://kapi.kakao.com";
  public static final String KAKAO_READY_URL = "/v1/payment/ready";
  public static final String KAKAO_APPROVE_URL = "/v1/payment/approve";
  public static final String KAKAO_CANCEL_URL = "/v1/payment/cancel";

  // FIXME 버전에 따른 URL 체크
  public static final String ORDER_APPROVE_URL = "/pay/kakaopay/approve";
  public static final String ORDER_CANCEL_URL = "/pay/kakaopay/cancel";
  public static final String ORDER_FAIL_URL = "/pay/kakaopay/fail";

  @Autowired
  HttpServletRequest request;

  @Autowired
  KakaoPayDao kakaoPayDao;

  /**
   * 1단계 카카오 결제 요청 API
   * 
   * @param payRequest
   * @return
   */
  public KakaoPayResponse paymentReady(KakaoPayRequest payRequest) throws Exception {

    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

    HttpEntity<MultiValueMap<String, String>> httpParam =
        new HttpEntity<>(getKakaopayReadyParam(payRequest), getKakaopayHttpHeader());
    log.info("kakaopay ready request : {}", httpParam.toString());

    // Debugging
    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
    interceptors.add(new LoggingRequestInterceptor());
    restTemplate.setInterceptors(interceptors);
    restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory())); // Response 로깅을 위해서

    ResponseEntity<KakaoPayResponse> response = restTemplate
        .exchange(KAKAO_HOST_URL + KAKAO_READY_URL,
                HttpMethod.POST,
                httpParam,
                KakaoPayResponse.class);

    if (response.getStatusCode() == HttpStatus.OK) {
      log.info("kakaopay ready response : {}", response);
      response.getBody().setCode("200");
    } else {
      log.error("kakaopay ready response : {}", response);
    }

    kakaoPayDao.saveKakaopayPg(payRequest, response.getBody());

    return response.getBody();
  }

  /**
   * 8단계 카카오 결제 승인 요청 API
   * 
   * @param order_id
   * @param pg_token
   * @return
   */
  public KakaoPayResponse paymentApprove(String order_id, String pg_token) throws Exception {

    RestTemplate restTemplate = new RestTemplate();

    HttpEntity<MultiValueMap<String, String>> httpParam =
        new HttpEntity<>(getKakaopayApproveParam(order_id, pg_token), getKakaopayHttpHeader());

    // Debugging
    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
    interceptors.add(new LoggingRequestInterceptor());
    restTemplate.setInterceptors(interceptors);
    restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory())); // Response 로깅을 위해서

    // 에러 응답에 대해서 예외가 발생하지 않도록  http://stackoverflow.com/a/26990665
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
      protected boolean hasError(HttpStatus statusCode) {
        return false;
      }});

    ResponseEntity<KakaoPayResponse> response = restTemplate
        .exchange(KAKAO_HOST_URL + KAKAO_APPROVE_URL,
                HttpMethod.POST,
                httpParam,
                KakaoPayResponse.class);

    response.getBody().setCode(String.valueOf(response.getStatusCode()));

    kakaoPayDao.updateKakaoPayApprove(response.getBody());

    return response.getBody();
  }

  /**
   * 8단계 카카오 결제 전체 취소 요청 API
   * 
   * @param orderId
   * @return
   */
  public KakaoPayResponse paymentCancel(String orderId, String payload, String cancel_id) throws CancelPayProcessError {

    RestTemplate restTemplate = new RestTemplate();

    HttpEntity<MultiValueMap<String, String>> httpParam =
        new HttpEntity<>(getKakaopayCancelParam(orderId, payload), getKakaopayHttpHeader());

    // Debugging
    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
    interceptors.add(new LoggingRequestInterceptor());
    restTemplate.setInterceptors(interceptors);
    restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory())); // Response 로깅을 위해서

    // 에러 응답에 대해서 예외가 발생하지 않도록  http://stackoverflow.com/a/26990665
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
      protected boolean hasError(HttpStatus statusCode) {
        return false;
      }});

      ResponseEntity<KakaoPayResponse> response = restTemplate
              .postForEntity(KAKAO_HOST_URL + KAKAO_CANCEL_URL, httpParam, KakaoPayResponse.class);

      KakaoPayResponse body = response.getBody();

      if (response.getStatusCode() == HttpStatus.OK) {
        // 취소 성공
        body.setCode("200");
        body.setCancel_id(cancel_id);

        kakaoPayDao.updateKakaoPayCancel(body);

        return body;

      } else if ("-784".equals(body.getCode()) && body.getMsg().contains("status : CANCEL_PAYMENT")) {
        // 취소 실패 - 기 취소건 { "msg": "can not request cancel!(status : CANCEL_PAYMENT)", "code": -784 }
        //
        // https://developers.kakao.com/docs/restapi#간편한-참조-응답-코드
        // -784	취소요청을 할 수 없는 상태에 결제취소 API를 호출한 경우	400

        // 취소 성공으로 리턴
        body.setCode("200");
        body.setCancel_id(cancel_id);

        //kakaoPayDao.updateKakaoPayCancel(body);

        return body;

      } else {
        // 취소 실패
        // 기 취소건이외
        throw new CancelPayProcessError(body.getMsg());

      }
  }

  /**
   * 1단계 카카오 결제 요청용 파라미터
   * 
   * @param payRequest
   * @return
   */
  private MultiValueMap<String, String> getKakaopayReadyParam(KakaoPayRequest payRequest) {

    MultiValueMap<String, String> param = new LinkedMultiValueMap<>();

    // 필수 값
    param.add("cid", payRequest.getCid());
    param.add("aid", payRequest.getAid());
    param.add("partner_order_id", payRequest.getPartner_order_id());
    param.add("partner_user_id", payRequest.getPartner_user_id());
    param.add("item_name", payRequest.getItem_name());
    param.add("item_code", payRequest.getItem_code());
    param.add("quantity", String.valueOf(payRequest.getQuantity()));
    param.add("total_amount", String.valueOf(payRequest.getTotal_amount()));
    param.add("tax_free_amount", String.valueOf(payRequest.getTax_free_amount()));
    param.add("vat_amount", String.valueOf(payRequest.getVat_amount()));

    /*PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    String phoneNumber = "";

    // 전화번호 포맷팅
    try {
      phoneNumber = phoneUtil.format(phoneUtil.parse(payRequest.getUser_phone_number(), "KR"),
          PhoneNumberFormat.NATIONAL);
    } catch (NumberParseException e) {
      phoneNumber = payRequest.getUser_phone_number();
      log.warn(phoneNumber + " issue " + e.getMessage());
    }

    param.add("user_phone_number", phoneNumber.replaceAll("-", ""));*/

    // 선택 값
    for (String card : payRequest.getAvailable_cards())
      param.add("available_cards", card);

    if (!StringUtils.isEmpty(payRequest.getPayment_method_type()))
      param.add("payment_method_type", payRequest.getPayment_method_type());

    if (payRequest.getInstall_month() != null)
      param.add("install_month", String.valueOf(payRequest.getInstall_month()));

    if (!StringUtils.isEmpty(payRequest.getPayload()))
      param.add("payload", payRequest.getPayload());
    
    // 필수 고정 값
    param.add("approval_url", HttpUtils.getRootURL(request) + ORDER_APPROVE_URL);
    param.add("cancel_url", HttpUtils.getRootURL(request) + ORDER_CANCEL_URL);
    param.add("fail_url", HttpUtils.getRootURL(request) + ORDER_FAIL_URL);

    Map<String, Object> custom_json = new HashMap<>();
    custom_json.put("custom_message", "카카오톡 주문하기의 결제 서비스는 씨엔티테크(주)에서 운영하며, 승인 문자 및 매출전표에 씨엔티테크(주)로 표시됩니다.");
    //custom_json.put("auto_close", true); // 결제창이 자동으로 닫히는 기능 (마사용)

    param.add("custom_json", JSONUtils.toJson(custom_json));
    
    return param;
  }

  /**
   * 8단계 카카오 결제 승인용 파라미터
   * 
   * @param order_id
   * @param pg_token
   * @return
   */
  private MultiValueMap<String, String> getKakaopayApproveParam(String order_id, String pg_token) {
    KakaoPayRequest payRequest = kakaoPayDao.getKakaopayReady(order_id);
    //null체크 추가가ㅏㅏㅏ가가ㅏㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱ
    
    
    MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
    param.add("cid", payRequest.getCid());
    param.add("aid", payRequest.getAid());
    param.add("tid", payRequest.getTid());
    param.add("partner_order_id", payRequest.getPartner_order_id());
    param.add("partner_user_id", payRequest.getPartner_user_id());
    param.add("pg_token", pg_token);

    payRequest.setPg_token(pg_token);
    
    // PG_TOKEN Update
    kakaoPayDao.updateKakaoPayPgToken(payRequest);

    return param;
  }

  /**
   * 카카오 결제 취소용 파라미터
   * 
   * @param order_id
   * @return
   */
  private MultiValueMap<String, String> getKakaopayCancelParam(String order_id, String payload) {
    KakaoPayRequest payRequest = kakaoPayDao.getKakaopayApprove(order_id);
    //null체크 추가가ㅏㅏㅏ가가ㅏㄱㄱㄱㄱㄱㄱㄱㄱㄱㄱ
    
    
    MultiValueMap<String, String> param = new LinkedMultiValueMap<>();

    param.add("cid", payRequest.getCid());
    param.add("aid", payRequest.getAid());
    param.add("tid", payRequest.getTid());
    param.add("cancel_amount", String.valueOf(payRequest.getTotal_amount()));
    param.add("cancel_tax_free_amount", String.valueOf(payRequest.getTax_free_amount()));
    param.add("cancel_vat_amount", String.valueOf(payRequest.getVat_amount()));

    if (!StringUtils.isEmpty(payRequest.getPayload()))
      param.add("payload", payRequest.getPayload());
    
    return param;
  }

  /**
   * 카카오 API 헤더
   * 
   * @return
   */
  private HttpHeaders getKakaopayHttpHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE,
        MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
    headers.add(HttpHeaders.AUTHORIZATION, KAKAO_API_ADMIN_KEY);
    return headers;
  }

  /**
   * 주문완료 업데이트
   * @param brand_cd
   * @param branch_id
   * @param order_id
   * @return
   */
    public int updateIsOrdered(String brand_cd, String branch_id, String order_id) {
     return kakaoPayDao.updateIsOrdered(brand_cd, branch_id, order_id);

    }
}
