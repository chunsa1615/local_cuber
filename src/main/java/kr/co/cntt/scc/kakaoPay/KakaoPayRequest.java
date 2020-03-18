package kr.co.cntt.scc.kakaoPay;

 import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KakaoPayRequest {

  private String brand_cd;
  private String branch_id;
  private String order_id;
  private String access_token;
  
  private String cid;                               // 가맹점 코드
  private String partner_order_id;                  // 가맹점 주문번호
  private String partner_user_id;                   // 가맹점 회원 ID
  private String item_name;                         // 상품명
  private String item_code;                         // 상품코드 (선택)
  private Integer quantity;                         // 상품 수량
  private Integer total_amount;                     // 상품 금액
  private Integer tax_free_amount;                  // 상품 비과세 금액
  private Integer vat_amount;                       // 상품 부가세 금액
  private String approval_url;                      // 결제성공 Redirect URL
  private String cancel_url;                        // 결제취소 Redirect URL
  private String fail_url;                          // 결제실패 Redirect URL
  private String user_phone_number;                 // 사용자 전화번호(결제요청 TMS 발송용)
  private String[] available_cards;                 // 카드사 제한 목록(없을 경우 전체) - ex)["HANA", "BC"], 목록: SHINHAN, KB, HYUNDAI, LOTTE, SAMSUNG, NH, BC, HANA
  private String payment_method_type;               // 결제 수단 제한(없을 경우 전체)   - 목록 : CARD, MONEY..
  private Integer install_month;                    // 카드할부개월수
  
  private String pg_token;                          // 결제 승인시 필요한 PG TOKEN(결제성공 Redirect URL의 Query String으로 넘어옴)
  private String tid;                               // 결제 트랜잭션 ID
  
  private String payload;                           // approve, cancel, subscription에서 aid와 매핑하여 저장할 수 있는 값, request, response에 포함
  private String aid;                               // approve, cancel, subscription 행위와 매핑되는 id, response에 포함
  
  private Integer cancel_amount;
  private Integer cancel_tax_free_amount;
  private Integer cancel_vat_amount;
  
}
