package kr.co.cntt.scc.kakaoPay;

import lombok.Data;

@Data
public class KakaoReceiptResponse {

  private String cid;                               // 가맹점 코드
  private String aid;                               // approve, cancel, subscription 행위와 매핑되는 id, response에 포함
  private String tid;                               // 결제 트랜잭션 ID

  private String partner_order_id;                  // 가맹점 주문번호
  private String partner_user_id;                   // 가맹점 회원 ID
  private String status;                            // 상태

  
}
