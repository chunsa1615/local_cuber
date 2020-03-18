package kr.co.cntt.scc.kakaoPay;

import lombok.Data;

@Data
public class KakaoPayResponse {

  private String partner_order_id;
  private String partner_user_id;
  private String payment_method_type; // 결제 수단. CARD, MONEY 중 하나
  private String item_name;
  private Integer quantity;
  
  private String status;
  private String cid;
  private String tid;
  private String sid;
  private Boolean tms_result;
  private String next_redirect_app_url;
  private String next_redirect_mobile_url;
  private String next_redirect_pc_url;
  private String android_app_scheme;
  private String ios_app_scheme;
  private String created_at;
  private String approved_at; // 결제 승인 시각
  
  
  private String payload; // approve, cancel, subscription에서 aid와 매핑하여 저장할 수 있는 값, request, response에 포함
  private String aid;     // approve, cancel, subscription 행위와 매핑되는 id, response에 포함
  
  private Amount amount;
  private CardInfo card_info;
  private CanceledAmount canceled_amount;
  private String canceled_at;

  private String cancel_id;
  
  // 에러 관련
  private String code;
  private String msg;
  private Extras extras;
  
  @Data
  public class Extras {
    String method_result_code;
    String method_result_message;
  }
  
  @Data
  public class Amount{
    Integer total;
    Integer tax_free;
    Integer vat;
    Integer point;
  }
  
  @Data
  public class CanceledAmount{
    Integer total; // 전체 결제 금액
    Integer tax_free;
    Integer vat;
    Integer point; // 사용한 포인트 금액
  }
  
  @Data
  public class CardInfo{
    String purchase_corp; // 매입카드사 한글명
    String issuer_corp; // 카드발급사 한글명
    String bin; // 카드 BIN
    String card_type; // 카드타입
    String install_month; // 할부개월
    String approved_id; // 카드사 승인번호
  }
}
