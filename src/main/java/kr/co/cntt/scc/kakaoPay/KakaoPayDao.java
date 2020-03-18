package kr.co.cntt.scc.kakaoPay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.app.admin.model.statusList.StatusListDeskInfo;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.model.MembershipCard;
import kr.co.cntt.scc.service.HistoryService;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;

//import com.cntt.kakaoorder.config.annotation.KakaoMapper;
//import com.cntt.kakaoorder.web.v15.pay.kakaoReceipt.KakaoReceiptResponse;
//import org.apache.ibatis.annotations.Param;
//import org.springframework.stereotype.Repository;

@Service
@Transactional
@Slf4j
public class KakaoPayDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;
    
  
  
  
  //카카오 결제 요청 데이터 조회 -- 최근 1건
  public KakaoPayRequest getKakaopayReady(String order_id) {
  
	  String s = " SELECT TOP (1) " +
	  		   " cid, " +
	  		   " aid, " +
	  		   " tid, " +
	  		   " partner_order_id, " +
	           " partner_user_id " +
	           " FROM TB_PG_KAKAO " +
	           " WHERE   partner_order_id = :order_id " +
	  
	           " ORDER BY created_at DESC ";
	  
	  Map<String, Object> args = new HashMap<>();
      args.put("useYn", Constants.USE);
      args.put("order_id", order_id);
      
      return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(KakaoPayRequest.class));
	  
  }
  
  
  
  //카카오 결제 승인 데이터 조회
  public KakaoPayRequest getKakaopayApprove(String order_id) {
  
  String s = "SELECT    top 1 cid " +
          " , aid " +
          " , tid " +
          " , total_amount " +
          " , tax_free_amount " +
          " , vat_amount " +
          " FROM      TB_PG_KAKAO " +
          " WHERE   partner_order_id = #{order_id} " +
          " order by created_at desc ";
	
	Map<String, Object> args = new HashMap<>();
    args.put("useYn", Constants.USE);
    args.put("order_id", order_id);

    return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(KakaoPayRequest.class));
  
  }
  
  
  //카카오 결제 요청 데이터 입력(최초 단계)
  public int saveKakaopayPg(KakaoPayRequest payRequest, KakaoPayResponse kakaoPayResponse) {  
  String s = " INSERT INTO TB_PG_KAKAO ( " +
		  	" BRAND_CD " +
		  	" ,BRANCH_ID " +
		  	" ,ORDER_ID " +
		  	" ,cid " +
		  	" ,aid " +
		  	" ,partner_order_id " +
		  	" ,partner_user_id " +
		  	" ,item_name " +
		  	" ,quantity " +
		  	" ,total_amount " +
		  	" ,tax_free_amount " +
		  	" ,vat_amount " +
		  	" ,user_phone_number " +
		  	" ,install_month " +
		  	" ,tid " +
		  	" ,created_at " +
		  	" ,reg_date " +
		  	" ,reg_time " +
		  	" ,upd_date " +
    " ,upd_time " +

  " ) values ( " +
     " #{payRequest.brand_cd} " +
    " ,#{payRequest.branch_id} " +
    " ,#{payRequest.order_id} " +
    " ,#{payRequest.cid} " +
    " ,#{payRequest.aid} " +
    " ,#{payRequest.partner_order_id} " +
    " ,#{payRequest.partner_user_id} " +
    " ,#{payRequest.item_name} " +
    " ,#{payRequest.quantity} " +
    " ,#{payRequest.total_amount} " +
    " ,#{payRequest.tax_free_amount} " +
    " ,#{payRequest.vat_amount} " +
    " ,#{payRequest.user_phone_number} " +
    " ,#{payRequest.install_month} " +
    " ,#{payResponse.tid} " +
    " ,#{payResponse.created_at} " +
    " ,CONVERT([varchar](8),getdate(),(112)) " +
    " ,replace(CONVERT([varchar](8),getdate(),(114)),':','') " +
    " ,CONVERT([varchar](8),getdate(),(112)) " +
    " ,replace(CONVERT([varchar](8),getdate(),(114)),':','') " +
    " ) ";
  
  
  CombinedSqlParameterSource source = new CombinedSqlParameterSource(payRequest);
  source.addValue("tid", kakaoPayResponse.getTid());
  source.addValue("created_at", kakaoPayResponse.getCreated_at());

  //KeyHolder keyHolder = new GeneratedKeyHolder();
  //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
  int result = jdbcTemplate.update(s, source);

  if (result == 0) {
      throw new InternalServerError("Failed to create member");

  } else {
//      History history = new History(branchId, Constants.HistoryType.MEMBER_CREATE, member.toString());
//      history.setMemberId(memberId);
//      historyService.insertHistory(history);

  }

  return result;
  
  }
  
  //카카오 PG_TOKEN UPDATE
  public int updateKakaoPayPgToken(KakaoPayRequest payRequest) {
  String s = " UPDATE TB_PG_KAKAO " +  
		  	" SET " +
		  	" pg_token = #{pg_token} " +
		  	" , status = 'READY' " +
		  	" WHERE partner_order_id = #{partner_order_id} " +
		  	" AND   tid = #{tid} ";
  
	  CombinedSqlParameterSource source = new CombinedSqlParameterSource(payRequest);
	  //source.addValue("branchId", branchId);
	  //source.addValue("memberId", memberId);
	
	  int result = jdbcTemplate.update(s, source);
	
	  if (result == 0) {
	      throw new InternalServerError("Failed to update member");
	
	  } else {
//	      History history = new History(branchId, Constants.HistoryType.MEMBER_UPDATE, member.toString());
//	      history.setMemberId(memberId);
//	      historyService.insertHistory(history);
	
	  }
	
	  return result;
  }
  
  
  //카카오 결제 승인 정보 업데이트
  public int updateKakaoPayApprove(KakaoPayResponse body) {
  String s = " UPDATE TB_PG_KAKAO " +
		  	" SET " +
		  	" sid = #{sid} " +
		  	" ,aid = #{aid} " +
		  	" ,payment_method_type = #{payment_method_type} " +
		  	" ,point = #{amount.point} " +
		  	//" <if test="card_info != null"> " +
		  	" ,purchase_corp = #{card_info.purchase_corp} " +
		  	" ,issuer_corp = #{card_info.issuer_corp} " +
		  	" ,bin = #{card_info.bin} " +
		  	" ,card_type = #{card_info.card_type} " +
		  	" ,approved_id = #{card_info.approved_id} " +
		  	" </if> " +
		  	" ,approved_at = #{approved_at} " +
		  	" ,status = 'APPROVE' " +
		  	" ,upd_date = CONVERT([varchar](8),getdate(),(112)) " +
		  	" ,upd_time = replace(CONVERT([varchar](8),getdate(),(114)),':','') " +
		  	" WHERE partner_order_id = #{partner_order_id} " +
		  	" AND   tid = #{tid} ";
  
  
	  CombinedSqlParameterSource source = new CombinedSqlParameterSource(body);
//	  source.addValue("branchId", branchId);
//	  source.addValue("memberId", memberId);
	
	  int result = jdbcTemplate.update(s, source);
	
	  if (result == 0) {
	      throw new InternalServerError("Failed to update member");
	
	  } else {
//	      History history = new History(branchId, Constants.HistoryType.MEMBER_UPDATE, member.toString());
//	      history.setMemberId(memberId);
//	      historyService.insertHistory(history);
	
	  }
	
	  return result;
  }
  
  
  
  //카카오 결제 취소 정보 업데이트
  public int updateKakaoPayCancel(KakaoPayResponse body) {
  String s = " UPDATE TB_PG_KAKAO " +
		  	" SET " +
		  	" sid = #{sid} " +
		  	" ,cancel_amount = #{canceled_amount.total} " +
		  	" ,cancel_tax_free_amount = #{canceled_amount.tax_free} " +
		  	" ,cancel_vat_amount = #{canceled_amount.vat} " +
		  	" ,canceled_at = #{canceled_at} " +
		  	" ,status = 'CANCEL' " +
	        " ,upd_date = CONVERT([varchar](8),getdate(),(112)) " +
	        " ,upd_time = replace(CONVERT([varchar](8),getdate(),(114)),':','') " +
	        " ,cancel_date = convert(varchar(10),getdate(),112) " +
	        " ,cancel_time = replace(convert(varchar(8),getdate(),108),':','') " +
	        " ,cancel_id = #{cancel_id} " +
	        " WHERE partner_order_id = #{partner_order_id} " +
	        " AND   tid = #{tid} ";
	  
  	CombinedSqlParameterSource source = new CombinedSqlParameterSource(body);
	//  source.addValue("branchId", branchId);
	//  source.addValue("memberId", memberId);
	
	  int result = jdbcTemplate.update(s, source);
	
	  if (result == 0) {
	      throw new InternalServerError("Failed to update member");
	
	  } else {
	//      History history = new History(branchId, Constants.HistoryType.MEMBER_UPDATE, member.toString());
	//      history.setMemberId(memberId);
	//      historyService.insertHistory(history);
	
	  }
	
	  return result;
  
  }
  
  
  
  public List<KakaoReceiptResponse> getKakaopayReceipt(String partner_order_id) {
	  
	  String s = " SELECT TOP (1) " +
			  	" cid " +
			  	" , aid " +
			  	" , tid " +
			  	" , partner_order_id " +
			  	" , partner_user_id " +
			  	" , status " +
			  	" FROM      TB_PG_KAKAO " +
			  	" WHERE     partner_order_id = #{partner_order_id} " +
			  	" ORDER BY created_at DESC ";
	  
	  Map<String, Object> args = new HashMap<>();
	  args.put("useYn", Constants.USE);
	  args.put("partner_order_id", partner_order_id);

	  return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(KakaoReceiptResponse.class));
      
      
  }

  
  
  /**
   * 주문완료 업데이트
   * @param brand_cd
   * @param branch_id
   * @param order_id
   * @return
   */
  public int updateIsOrdered(String brand_cd, String branch_id, String order_id) {
	  
      String s = " update TB_PG_KAKAO " +
      			" set " +
      			" IS_ORDERED = 'Y' " +
      			" where BRAND_CD = #{brand_cd} " +
      			" and   BRANCH_ID = #{branch_id} " +
      			" and   ORDER_ID = #{order_id} ";
      
      
      Map<String, Object> args = new HashMap<>();
      args.put("brand_cd", brand_cd);
      args.put("branch_id", branch_id);
      args.put("order_id", order_id);

    	
    	  int result = jdbcTemplate.update(s, args);
    	
    	  if (result == 0) {
    	      throw new InternalServerError("Failed to update member");
    	
    	  } else {
    	//      History history = new History(branchId, Constants.HistoryType.MEMBER_UPDATE, member.toString());
    	//      history.setMemberId(memberId);
    	//      historyService.insertHistory(history);
    	
    	  }
    	
    	  return result;
	  
  }
  
  
  
  
  
  
}
