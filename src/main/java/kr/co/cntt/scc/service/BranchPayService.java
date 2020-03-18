package kr.co.cntt.scc.service;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.app.admin.model.AppAdminMember;
import kr.co.cntt.scc.app.admin.model.accounting.AccountingPay;
import kr.co.cntt.scc.app.admin.model.accountingMonth.AccountingMonthPay;
import kr.co.cntt.scc.app.admin.model.accountingMonth.Year;
import kr.co.cntt.scc.app.admin.model.register.RegisterPay;
import kr.co.cntt.scc.app.admin.model.register.RegisterReservation;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.model.Page;
import kr.co.cntt.scc.model.Pay;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.util.DateUtil;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pay 결제
 *
 * Created by jslivane on 2016. 6. 14..
 */
@Service
@Transactional
@Slf4j
public class BranchPayService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;

    /*******************************************************************************/   
    public List<Pay> selectPayListforReservationDelete(String branchId, String orderId) {
    	String s = " SELECT p.branchId, p.orderId, p.reservationId ,p.payId, p.memberId, p.payDt, p.payTm, p.payInOutType ,p.payType, p.payAmount, " +
                " p.payStateType, p.payNote, p.insertDt, p.updateDt " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +                
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " WHERE p.branchId = :branchId AND p.orderId = :orderId AND p.useYn = :useYn " +
                " UNION " + //앱회원 추가
                " SELECT p.branchId, p.orderId, p.reservationId ,p.payId, p.memberId, p.payDt, p.payTm, p.payInOutType ,p.payType, p.payAmount, " +
                " p.payStateType, p.payNote, p.insertDt, p.updateDt " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                " JOIN app_branch_manager abm ON (abm.branchId = p.branchId AND abm.memberId = p.memberId AND abm.useYn = :useYn) " +
                " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
                " WHERE p.branchId = :branchId AND p.orderId = :orderId AND p.useYn = :useYn " ;
    	
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("orderId", orderId);
        args.put("useYn", Constants.USE);
        
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Pay.class));
    }
    /**
     * 결제 리스트 조회 (결제일 기준)
     * @param branchId
     * @param payStartDt
     * @param payEndDt
     * @param memberId
     * @return
     *
     * MySQL Date Time Fuctions : http://dev.mysql.com/doc/refman/5.7/en/date-and-time-functions.html
     *
     */
    public List<Pay> selectPayList(String branchId, String payStartDt, String payEndDt,
                                          String memberId, String payType, Page page) {

        String s = " SELECT p.branchId, p.orderId, p.reservationId, p.payId, p.memberId, p.payDt, p.payTm, p.payInOutType ,p.payType, p.payAmount, " +
                " p.payStateType, p.payNote, p.insertDt, p.updateDt " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +                
                //" JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " WHERE p.branchId = :branchId and p.useYn = :useYn " ;
                               
                //" AND p.payTm >= :startTime AND p.payTm <= :endTime " +


        if(!StringUtils.isEmpty(payStartDt)) {
            //payStartDt = DateUtil.getCurrentDateString();
            s += " AND p.payDt >= :startDate ";

        }

        if(!StringUtils.isEmpty(payEndDt)) {
            //payEndDt = DateUtil.getCurrentDateString();
            s += " AND p.payDt <= :endDate ";

        }

        if(!StringUtils.isEmpty(memberId)) {
            s += " AND p.memberId = :memberId ";

        }
        
        if(!StringUtils.isEmpty(payType)) {
            s += " AND p.payType = :payType ";

        }
        s += " AND p.useYn = :useYn ";
        
        
        //앱회원 추가
        s += " UNION " +
           	 " SELECT p.branchId, p.orderId, p.reservationId ,p.payId, p.memberId, p.payDt, p.payTm, p.payInOutType ,p.payType, p.payAmount, " +
           	 " p.payStateType, p.payNote, p.insertDt, p.updateDt " +
           	 " FROM branch_pay p " +
     	         " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
   	         " JOIN app_branch_manager abm ON (abm.branchId = p.branchId AND abm.memberId = p.memberId AND abm.useYn = :useYn) " +
   	         " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
   			 " WHERE p.branchId = :branchId and p.useYn = :useYn ";
           
           if(!StringUtils.isEmpty(payStartDt)) {
               //payStartDt = DateUtil.getCurrentDateString();
               s += " AND p.payDt >= :startDate ";

           }

           if(!StringUtils.isEmpty(payEndDt)) {
               //payEndDt = DateUtil.getCurrentDateString();
               s += " AND p.payDt <= :endDate ";

           }

           if(!StringUtils.isEmpty(memberId)) {
               s += " AND p.memberId = :memberId ";

           }
           
           if(!StringUtils.isEmpty(payType)) {
               s += " AND p.payType = :payType ";

           }
           
           s += " AND p.useYn = :useYn ";        
                
        
           s += " ORDER BY payDt DESC, payTm DESC ";        
        //s += " ORDER BY p.payDt ASC, p.payTm ASC ";

        if(page == null) {}
        else {
	        int perPage = (page.getPage() -1) * page.getPerPageNum();
	    	s += " limit "+perPage+", "+page.getPerPageNum()+" ";
        }
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("startDate", payStartDt);
        args.put("endDate", payEndDt);
        //args.put("startTime", DateUtil.MIN_TIME);
        //args.put("endTime", DateUtil.MAX_TIME);
        args.put("memberId", memberId);
        args.put("payType", payType);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Pay.class));

    }

    /**
     * 결제 리스트 조회 (결제일 기준)
     * @param branchId
     * @param payStartDt
     * @param payEndDt
     * @param memberId
     * @return
     *
     * MySQL Date Time Fuctions : http://dev.mysql.com/doc/refman/5.7/en/date-and-time-functions.html
     *
     */
    public List<Pay> selectTotalPay(String branchId, String payStartDt, String payEndDt,
                                          String memberId, String payType) {

        String s = " SELECT p.payType, p.payStateType, p.payAmount, p.payDt, p.payTm " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                //" JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " WHERE p.branchId = :branchId and p.useYn = :useYn ";

                //" AND p.payTm >= :startTime AND p.payTm <= :endTime " +


        if(!StringUtils.isEmpty(payStartDt)) {
            //payStartDt = DateUtil.getCurrentDateString();
            s += " AND p.payDt >= :startDate ";

        }

        if(!StringUtils.isEmpty(payEndDt)) {
            //payEndDt = DateUtil.getCurrentDateString();
            s += " AND p.payDt <= :endDate ";

        }

        if(!StringUtils.isEmpty(memberId)) {
            s += " AND p.memberId = :memberId ";

        }
        
        if(!StringUtils.isEmpty(payType)) {
            s += " AND p.payType = :payType ";

        }

        s += " AND p.useYn = :useYn ";
        
        
        //앱회원 추가
        s += " UNION " +
           	 " SELECT p.payType, p.payStateType, p.payAmount, p.payDt, p.payTm " +
           	 " FROM branch_pay p " +
     	     " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
   	         " JOIN app_branch_manager abm ON (abm.branchId = p.branchId AND abm.memberId = p.memberId AND abm.useYn = :useYn) " +
   	         " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
   			 " WHERE p.branchId = :branchId and p.useYn = :useYn ";
           
           if(!StringUtils.isEmpty(payStartDt)) {
               //payStartDt = DateUtil.getCurrentDateString();
               s += " AND p.payDt >= :startDate ";

           }

           if(!StringUtils.isEmpty(payEndDt)) {
               //payEndDt = DateUtil.getCurrentDateString();
               s += " AND p.payDt <= :endDate ";

           }

           if(!StringUtils.isEmpty(memberId)) {
               s += " AND p.memberId = :memberId ";

           }
           
           if(!StringUtils.isEmpty(payType)) {
               s += " AND p.payType = :payType ";

           }
           
           s += " AND p.useYn = :useYn ";
       
        
        
        s += " ORDER BY payDt DESC, payTm DESC ";
        //s += " ORDER BY p.payDt ASC, p.payTm ASC ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("startDate", payStartDt);
        args.put("endDate", payEndDt);
        //args.put("startTime", DateUtil.MIN_TIME);
        //args.put("endTime", DateUtil.MAX_TIME);
        args.put("memberId", memberId);
        args.put("payType", payType);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Pay.class));

    }
    
    /**
     * (지점의 특정 주문의) 결제 목록 조회
     * @param branchId
     * @param orderId
     * @return
     */
    public List<Pay> selectPayList(String branchId, String orderId) {

        String s = " SELECT p.branchId, p.orderId, p.reservationId, p.payId, p.memberId, p.payDt, p.payTm, p.payInOutType ,p.payType, p.payAmount, " +
                " p.payStateType, p.payNote, p.insertDt, p.updateDt " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " WHERE p.branchId = :branchId ";
                
				if(!StringUtils.isEmpty(orderId)) {
				    s += " AND p.memberId = :orderId  ";
				
				}
				
                
                s += " AND p.useYn = :useYn ";
        
	        //앱회원 추가
	        s += " UNION " +
	        	 " SELECT p.branchId, p.orderId, p.reservationId, p.payId, p.memberId, p.payDt, p.payTm, p.payInOutType ,p.payType, p.payAmount, " +
	             " p.payStateType, p.payNote, p.insertDt, p.updateDt " +
	           	 " FROM branch_pay p " +
	     	     " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
	   	         " JOIN app_branch_manager abm ON (abm.branchId = p.branchId AND abm.memberId = p.memberId AND abm.useYn = :useYn) " +
	   	         " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
	   	         " WHERE p.branchId = :branchId AND p.orderId = :orderId ";
		   	      
	        		if(!StringUtils.isEmpty(orderId)) {
					    s += " AND p.memberId = :orderId  ";
					
					}
					
	              
	              s += " AND p.useYn = :useYn ";
        
        
        
        
        //s += " ORDER BY payDt ASC, payTm ASC ";
	        s += " ORDER BY payDt DESC, payTm DESC ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("orderId", orderId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Pay.class));

    }

    /**
     * (지점의 특정 주문의) 결제 삭제
     * @param branchId
     * @param orderId
     * @return
     */
    public int deleteBranchOrderPay(String branchId, String orderId, String payId, Boolean paramFlag) {

    	String s = null;
    	
    	//등록내역에서 개별 등록삭제 시에는 useYn = 0으로 변경
    	if (paramFlag == true) {
            s = " UPDATE branch_pay SET " +
                " useYn = :useYn, updateDt = NOW() " +
                " WHERE branchId = :branchId AND payId = :payId ";    		
    	} else {
            s = " UPDATE branch_pay SET " +
                " payStateType = :payStateType, updateDt = NOW() " +
                " WHERE branchId = :branchId AND payId = :payId ";
    	}
        

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("payId", payId);
        args.put("useYn", Constants.NOT_USE);        
        args.put("payStateType", Constants.CANCEL);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to delete pay");

        } else {
            History history = new History(branchId, Constants.HistoryType.PAY_DELETE, "");
            history.setPayId(payId);
            historyService.insertHistory(history);

        }

        return result;

    }
    
    /**
     * (지점의 특정 주문의) 결제 수정
     * @param branchId
     * @param orderId
     * @return
     */
    public int updateBranchOrderPay(String branchId, String orderId, String payId, Pay pay) throws SQLException {
    	System.out.println("=====================" + branchId);
    	System.out.println("=====================" + orderId);
    	System.out.println("=====================" + payId);
    	System.out.println("=====================" + pay);
    	
        String s = " UPDATE branch_pay SET " +
                " payInOutType = :payInOutType ,payType = :payType, payAmount = :payAmount, memberId = :memberId, " +
                " payDt = :payDt ,payNote = :payNote, updateDt = NOW() " +
                " WHERE 1=1 ";
                
        if(!StringUtils.isEmpty(branchId)) {
        	s += " AND branchId = :branchId ";
        }
        
        if(!StringUtils.isEmpty(orderId)) {
        	s += " AND orderId = :orderId ";
        }
        
        if(!StringUtils.isEmpty(payId)) {
        	s += " AND payId = :payId ";
        }
        
//        if(!StringUtils.isEmpty(reservationId)) {
//			s += " AND reservationId = :reservationId ";
//		}
        
        CombinedSqlParameterSource source = new CombinedSqlParameterSource(pay);
        source.addValue("branchId", branchId);
        source.addValue("orderId", orderId);
        source.addValue("payId", payId);
        //source.addValue("reservationId", reservationId);

        int result = jdbcTemplate.update(s, source);
        
        
        

        if (result == 0) {
        	
        	throw new InternalServerError("Failed to update pay");

        } else {
            History history = new History(branchId, Constants.HistoryType.PAY_UPDATE, pay.toString());
            history.setOrderId(pay.getOrderId());
            history.setPayId(payId);
            history.setMemberId(pay.getMemberId());
            historyService.insertHistory(history);

        }

        return result;

    }  
    
    /*******************************************************************************/


    public Pay selectPay(String branchId, String payId) {
        String s = " SELECT p.branchId, p.orderId, p.reservationId, p.payId, p.memberId, p.payDt, p.payTm, p.payInOutType ,p.payType, p.payAmount, " +
                " p.payStateType, p.payNote, p.insertDt, p.updateDt " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " WHERE p.branchId = :branchId AND p.payId = :payId AND p.useYn = :useYn " +
		        " UNION " + //app 회원 추가
		        " SELECT p.branchId, p.orderId, p.reservationId, p.payId, p.memberId, p.payDt, p.payTm, p.payInOutType ,p.payType, p.payAmount, " +
                " p.payStateType, p.payNote, p.insertDt, p.updateDt " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
		        " JOIN app_branch_manager abm ON (abm.branchId = p.branchId AND abm.memberId = p.memberId AND abm.useYn = :useYn) " +
		        " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
		        " WHERE p.branchId = :branchId AND p.payId = :payId AND p.useYn = :useYn " ;
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("payId", payId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(Pay.class));

    }

    public int insertPay(String branchId, String payId, Pay pay) {
        String s = " INSERT INTO branch_pay ( " +
                " branchId, orderId, reservationId, payId, memberId, payDt, payTm, payInOutType ,payType, payAmount, payNote " +
                " ) VALUES ( " +
                " :branchId, :orderId, :reservationId, :payId, :memberId, :payDt, :payTm, :payInOutType ,:payType, :payAmount, :payNote " +
                " ) ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(pay);
        source.addValue("branchId", branchId);
        source.addValue("payId", payId);
        //source.addValue("payDt", DateUtil.getCurrentDateString());
        source.addValue("payTm", DateUtil.getCurrentTimeString());

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to create pay");

        } else {
            History history = new History(branchId, Constants.HistoryType.PAY_CREATE, pay.toString());
            history.setOrderId(pay.getOrderId());
            history.setPayId(payId);
            history.setMemberId(pay.getMemberId());
            historyService.insertHistory(history);

        }

        return result;


    }

    public int updatePay(String branchId, String payId, Pay pay) {
        String s = " UPDATE branch_pay SET " +
                " payInOutType = :payInOutType ,payType = :payType, payAmount = :payAmount, memberId = :memberId, " +
                " payNote = :payNote, payDt = :payDt, updateDt = NOW() " +
                " WHERE branchId = :branchId AND payId = :payId ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(pay);
        source.addValue("branchId", branchId);
        source.addValue("payId", payId);

        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to update pay");

        } else {
            History history = new History(branchId, Constants.HistoryType.PAY_UPDATE, pay.toString());
            history.setOrderId(pay.getOrderId());
            history.setPayId(payId);
            history.setMemberId(pay.getMemberId());
            historyService.insertHistory(history);

        }

        return result;

    }

    public int deletePay(String branchId, String payId) {
        String s = " UPDATE branch_pay SET " +
                " useYn = :useYn, updateDt = NOW() " +
                " WHERE branchId = :branchId AND payId = :payId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("payId", payId);
        args.put("useYn", Constants.NOT_USE);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to delete pay");

        } else {
            History history = new History(branchId, Constants.HistoryType.PAY_DELETE, "");
            history.setPayId(payId);
            historyService.insertHistory(history);

        }

        return result;

    }


    /*
     * 통계를 위한 쿼리
     * 
     * */

    public String selectMonthPayAmount(String branchId) {
        String s = " SELECT SUM(p.payAmount) " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                //" JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId ) " +
                " WHERE p.branchId = :branchId AND p.useYn = :useYn " +
                " AND payDt >= DATE_ADD(LAST_DAY(CURDATE() - INTERVAL 1 MONTH), INTERVAL 1 DAY) " +
                " AND payDt <= CURDATE() " +
                " AND payStateType = :payStateType " +
                " AND payType != :payType ";
                

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("payStateType", 10);
        args.put("payType", 3); //미수금
        args.put("useYn", Constants.USE);
        
        return jdbcTemplate.queryForObject(s, args, String.class);

    }
    
    public String selectTodayPayAmount(String branchId) {
        String s = " SELECT SUM(p.payAmount) " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                //" JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId ) " +
                " WHERE p.branchId = :branchId AND p.useYn = :useYn " +
                " AND payDt = curdate() " +
                " AND payStateType = :payStateType " +
                " AND payType != :payType ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("payStateType", 10);
        args.put("payType", 3); //미수금
        args.put("useYn", Constants.USE);
        
        return jdbcTemplate.queryForObject(s, args, String.class);

    }    
    
    public String selectYesterdayPayAmount(String branchId) {
        String s = " SELECT SUM(p.payAmount) " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                //" JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId ) " +
                " WHERE p.branchId = :branchId AND p.useYn = :useYn " +
                " AND payDt = DATE_ADD(curdate(), INTERVAL -1 DAY) " +
                " AND payStateType = :payStateType " +
                " AND payType != :payType ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("payStateType", 10);
        args.put("payType", 3); //미수금
        args.put("useYn", Constants.USE);
        
        return jdbcTemplate.queryForObject(s, args, String.class);

    }    

    public String selectPayTypeAmount(String branchId, String payStartDt, String payEndDt, Integer payType) {
        String s = " SELECT SUM(p.payAmount) " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                //" JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId ) " +
                " WHERE p.branchId = :branchId AND p.useYn = :useYn ";
                
	        if(!StringUtils.isEmpty(payStartDt)) {
	            //payStartDt = DateUtil.getCurrentDateString();
	            s += " AND p.payDt >= :startDate ";
	
	        }
	
	        if(!StringUtils.isEmpty(payEndDt)) {
	            //payEndDt = DateUtil.getCurrentDateString();
	            s += " AND p.payDt <= :endDate ";
	
	        }

           s += " AND payStateType = :payStateType " +
                " AND payType = :payType ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("startDate", payStartDt);
        args.put("endDate", payEndDt);
        args.put("payStateType", 10);
        args.put("payType", payType); 
        args.put("useYn", Constants.USE);
        
        return jdbcTemplate.queryForObject(s, args, String.class);

    }
    
    public List<Pay> selectMonthPay(String branchId) {
        String s = " SELECT  p.branchId, p.orderId, p.reservationId, p.payId, p.memberId, p.payDt, p.payTm, p.payInOutType ,p.payType, SUM(p.payAmount) as payAmount, " +
                " p.payStateType, p.payNote, p.insertDt, p.updateDt " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                //" JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId ) " +
                " WHERE p.branchId = :branchId AND p.useYn = :useYn " +
                " AND payDt >= DATE_ADD(LAST_DAY(CURDATE() - INTERVAL 1 MONTH), INTERVAL 1 DAY) " +
                " AND payDt <= CURDATE() " +
                " AND payStateType = :payStateType " +
                " group by p.orderId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("payStateType", 10);
        args.put("payType", 3); //미수금
        args.put("useYn", Constants.USE);
        
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Pay.class));

    }
    
    
    public Integer selectPayAmount(String branchId, String orderId) {
        String s = " SELECT SUM(p.payAmount) " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                //" JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId ) " +
                " WHERE p.branchId = :branchId AND p.useYn = :useYn " +
                " AND p.orderId = :orderId " +
                " AND p.payStateType = :payStateType " +
        		" AND payType != :payType ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("orderId", orderId);
        args.put("payStateType", 10);
        args.put("useYn", Constants.USE);
        
        return jdbcTemplate.queryForObject(s, args, Integer.class);

    }   

    /*******************************AppAdmin************************************************/
    public List<RegisterReservation> selectAppRegisterPayList(String branchId, String memberId, Integer num) {
        String s = " SELECT distinct IFNULL(p.payDt, SUBSTRING(p.updateDt, 1, 10)) AS payDt, payTm , m.name AS name, p.payType AS payType, p.payAmount AS payAmount, brm.name AS roomName, bd.name AS deskName, p.payStateType AS payStateType, p.payId AS orderId, " +
        		"  r.reservationStatus AS reservationStatus, r.deskStartDt AS deskStartDt, r.deskEndDt AS deskEndDt, r.reservationNote AS reservationNote "+
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +                
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +                
                " JOIN branch_reservation r on (r.branchId = o.branchId AND r.reservationId = p.reservationId) " +
                " JOIN branch_desk bd on (bd.branchId = r.branchId AND r.deskId = bd.deskId) " +
                " JOIN branch_room brm on (brm.branchId = bd.branchId AND bd.roomId = brm.roomId) " +                
                " WHERE r.reservationStatus != :reservationStatus AND r.deskId != null or r.deskId != '' AND r.useYn = :useYn" +
                " AND p.branchId = :branchId " +
                " AND p.memberId = :memberId " +
                " AND p.useYn = :useYn "+
                //app 회원 추가
        		" UNION " +
        		" SELECT distinct IFNULL(p.payDt, SUBSTRING(p.updateDt, 1, 10)) AS payDt, payTm, asm.studentName AS name, p.payType AS payType, p.payAmount AS payAmount, brm.name AS roomName, bd.name AS deskName, p.payStateType AS payStateType, p.payId AS orderId, " +
        		"  r.reservationStatus AS reservationStatus, r.deskStartDt AS deskStartDt, r.deskEndDt AS deskEndDt, r.reservationNote AS reservationNote "+
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +                
		        " JOIN app_branch_manager abm ON (abm.branchId = p.branchId AND abm.memberId = p.memberId AND abm.useYn = :useYn) " +
		        " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +                
                " JOIN branch_reservation r on (r.branchId = o.branchId AND r.reservationId = p.reservationId) " +                
                " JOIN branch_desk bd on (bd.branchId = r.branchId AND r.deskId = bd.deskId) " +
                " JOIN branch_room brm on (brm.branchId = bd.branchId AND bd.roomId = brm.roomId) " +                
                " WHERE r.reservationStatus != :reservationStatus AND r.deskId != null or r.deskId != '' AND r.useYn = :useYn" +
                " AND p.branchId = :branchId " +
                " AND p.memberId = :memberId " +
                " AND p.useYn = :useYn ";
        
        s += " ORDER BY payDt ASC, payTm ASC ";
        
        if(!StringUtils.isEmpty(num)) {
        	s += " LIMIT :num, 10 ";
        }
        //s += " ORDER BY r.reservationDt DESC, r.reservationTm DESC ";
        
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("reservationStatus", 90);
        args.put("useYn", Constants.USE);
        args.put("num", num);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(RegisterReservation.class));        
    }
    
    //app client /parents API에서 사용
    public List<RegisterReservation> selectAppRegisterPayList2(String branchId, String memberId) {
        String s = " SELECT distinct IFNULL(p.payDt, SUBSTRING(p.updateDt, 1, 10)) AS payDt, p.payTm AS payTm, p.payType AS payType, p.payAmount AS payAmount, brm.name AS roomName, bd.name AS deskName, p.payStateType AS payStateType, p.payId AS orderId, " +
        		"  r.reservationStatus AS reservationStatus, r.deskStartDt AS deskStartDt, r.deskEndDt AS deskEndDt, r.reservationNote AS reservationNote"+
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +                                
                " JOIN branch_reservation r on (r.branchId = o.branchId AND r.reservationId = p.reservationId) " +
                " JOIN branch_desk bd on (bd.branchId = r.branchId AND r.deskId = bd.deskId) " +
                " JOIN branch_room brm on (brm.branchId = bd.branchId AND bd.roomId = brm.roomId) " +                
                " WHERE r.reservationStatus != :reservationStatus AND r.deskId != null or r.deskId != '' AND r.useYn = :useYn" +
                " AND p.branchId = :branchId " +
                " AND p.memberId = :memberId " +
                " AND p.useYn = :useYn " +
		        //app 회원 추가
				" UNION " +
				" SELECT distinct IFNULL(p.payDt, SUBSTRING(p.updateDt, 1, 10)) AS payDt, p.payTm AS payTm, p.payType AS payType, p.payAmount AS payAmount, brm.name AS roomName, bd.name AS deskName, p.payStateType AS payStateType, p.payId AS orderId, " +
        		"  r.reservationStatus AS reservationStatus, r.deskStartDt AS deskStartDt, r.deskEndDt AS deskEndDt, r.reservationNote AS reservationNote"+
		        " FROM branch_pay p " +
		        " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +                
		        " JOIN app_branch_manager abm ON (abm.branchId = p.branchId AND abm.memberId = p.memberId AND abm.useYn = :useYn) " +
		        " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +                
		        " JOIN branch_reservation r on (r.branchId = o.branchId AND r.reservationId = p.reservationId) " +                
		        " JOIN branch_desk bd on (bd.branchId = r.branchId AND r.deskId = bd.deskId) " +
		        " JOIN branch_room brm on (brm.branchId = bd.branchId AND bd.roomId = brm.roomId) " +                
		        " WHERE r.reservationStatus != :reservationStatus AND r.deskId != null or r.deskId != '' AND r.useYn = :useYn" +
		        " AND p.branchId = :branchId " +
		        " AND p.memberId = :memberId " +
		        " AND p.useYn = :useYn ";
        
        
        
        s += " ORDER BY payDt ASC, payTm ASC "
          + " LIMIT 1 ";

        
        //s += " ORDER BY r.reservationDt DESC, r.reservationTm DESC ";
        
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("reservationStatus", 90);
        args.put("useYn", Constants.USE);


        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(RegisterReservation.class));        
    }
    
    public List<AccountingPay> selectAccountingPayList(String branchId, String payStartDt, String payEndDt, String searchType, Integer num) {
        String s = null;
    	
        if(searchType.equals("EXPENSE")) {
        	s = " SELECT e.expenseDt AS payDt, e.expenseTm AS payTm, e.payInOutType AS payInOutType, e.expenseAmount AS payAmount, " +
        			" e.expenseGroup AS expenseGroup, e.expenseOption AS expenseOption, (SELECT SUM(ee.expenseAmount) " +
																		        			" FROM branch_expense ee " +
																							" WHERE ee.branchId = e.branchId " + 
																							" AND ee.expenseDt >= :payStartDt AND ee.expenseDt <= :payEndDt " +
																							" AND ee.payStateType = :payStateType " +
																							" AND ee.payType != :payType " +
																							" AND ee.useYn = :useYn " +
																							" ) AS totalAmount, " + 
					" e.expenseNote AS payNote" +
			" FROM branch_expense e " +
			" WHERE e.branchId = :branchId " +               
			" AND e.payStateType = :payStateType " +
			//" AND e.payInOutType = :payInOutType " +
			" AND e.payType != :payType " ;
			if(!StringUtils.isEmpty(payStartDt)) {
	            //payStartDt = DateUtil.getCurrentDateString();
	            s += " AND e.expenseDt >= :payStartDt ";
	
	        }
	
	        if(!StringUtils.isEmpty(payEndDt)) {
	            //payEndDt = DateUtil.getCurrentDateString();
	            s += " AND e.expenseDt <= :payEndDt ";
	
	        }
			
        	
			s += " AND e.useYn = :useYn ";
	        s += " ORDER BY e.expenseDt ASC, e.expenseTm ASC ";
	        
        } else {
        
	    	s = " SELECT p.payDt, p.payTm, m.name AS name, p.payInOutType, p.payAmount, (SELECT SUM(pp.payAmount) " +
	        																					" FROM branch_pay pp " +
	        																					" WHERE pp.branchId = p.branchId " +
	        																					" AND pp.memberId = p.memberId " +
	        																					" AND pp.payDt >= :payStartDt AND pp.payDt <= p.payDt " +        																					
	        																					" AND p.payStateType = :payStateType " +
	        																					" AND payType != :payType " +
	        																					" AND pp.useYn = :useYn" +
	        																					" ) AS totalAmount, " +
	        		 " p.payNote " +
	                " FROM branch_pay p " +
	                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +                
	                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
	                " WHERE p.branchId = :branchId " +
	                //" AND p.memberId = :memberId " +
	                " AND p.payStateType = :payStateType ";
	                
			        if(!StringUtils.isEmpty(searchType)) {
		                if(searchType.equals("INCOME")) { //매출
		                	s += " AND p.payType != :payType ";
		                	s+= " AND p.payInOutType = :payInOutType ";
		                }
//		                else if (searchType.equals("EXPENSE")) { //지출
//		                	s += " AND p.payType != :payType ";
//		                	s+= " AND p.payInOutType = :payInOutType ";
//		                }
		                else if (searchType.equals("RECEIVABLE")) { //미수금
		                	s += " AND p.payType = :payType ";
		                }
			        }
			        
	        		if(!StringUtils.isEmpty(payStartDt)) {
			            //payStartDt = DateUtil.getCurrentDateString();
			            s += " AND p.payDt >= :payStartDt ";
			
			        }
			
			        if(!StringUtils.isEmpty(payEndDt)) {
			            //payEndDt = DateUtil.getCurrentDateString();
			            s += " AND p.payDt <= :payEndDt ";
			
			        }
	
			s += " AND p.useYn = :useYn " +
	        
			
			
			//app회원 추가
			" UNION " +
			" SELECT p.payDt, p.payTm, asm.studentName AS name, p.payInOutType, p.payAmount, (SELECT SUM(pp.payAmount) " +
																					" FROM branch_pay pp " +
																					" WHERE pp.branchId = p.branchId " +
																					" AND pp.memberId = p.memberId " +
																					" AND pp.payDt >= :payStartDt AND pp.payDt <= p.payDt " +        																					
																					" AND p.payStateType = :payStateType " +
																					" AND payType != :payType " +
																					" AND pp.useYn = :useYn" +
																					" ) AS totalAmount, " +
			" p.payNote " +
			" FROM branch_pay p " +
			" LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +                			
			" JOIN app_branch_manager abm ON (abm.branchId = p.branchId AND abm.memberId = p.memberId AND abm.useYn = :useYn) " +
	        " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +              
			" WHERE p.branchId = :branchId " +
			//" AND p.memberId = :memberId " +
			" AND p.payStateType = :payStateType ";
			
			if(!StringUtils.isEmpty(searchType)) {
				if(searchType.equals("INCOME")) { //매출
					s += " AND p.payType != :payType ";
					s+= " AND p.payInOutType = :payInOutType ";
				}
				//else if (searchType.equals("EXPENSE")) { //지출
				//s += " AND p.payType != :payType ";
				//s+= " AND p.payInOutType = :payInOutType ";
				//}
				else if (searchType.equals("RECEIVABLE")) { //미수금
					s += " AND p.payType = :payType ";
				}
			}
			
			if(!StringUtils.isEmpty(payStartDt)) {
				s += " AND p.payDt >= :payStartDt ";
			
			}
			
			if(!StringUtils.isEmpty(payEndDt)) {
				s += " AND p.payDt <= :payEndDt ";
			
			}
			
			s += " AND p.useYn = :useYn " ;
			
			
			
			
			
			
			
			s += " ORDER BY payDt ASC, payTm ASC ";
	        
        }
        
        
        if(!StringUtils.isEmpty(num)) {
        	s += " LIMIT :num, 10 ";
        }

        //s += " ORDER BY r.reservationDt DESC, r.reservationTm DESC ";
        
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        //args.put("memberId", memberId);
        args.put("payStartDt", payStartDt);
        args.put("payEndDt", payEndDt);
        args.put("payStateType", 10);
        if(!StringUtils.isEmpty(searchType)) {
            if(searchType.equals("INCOME")) { //매출
            	args.put("payInOutType", 20); 
            }
            else if (searchType.equals("EXPENSE")) { //지출
            	args.put("payInOutType", 10);             	
            }
            else if (searchType.equals("RECEIVABLE")) { //미수금
            	//args.put("payInOutType", 30);
            }
        }
        args.put("payType", 3); //미수금
        args.put("useYn", Constants.USE);
        args.put("num", num);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AccountingPay.class));        
    }
    
    
    public List<AccountingMonthPay> selectaccountingMonthPayAmount(String branchId) {
        String s = " SELECT IFNULL(SUBSTRING(p.payDt, 1, 4), SUBSTRING(p.updateDt, 1, 4)) AS year, IFNULL(SUBSTRING(p.payDt, 6, 2), SUBSTRING(p.updateDt, 1, 4)) AS month, " +
        		" SUM(p.payAmount) AS amount " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " WHERE p.branchId = :branchId AND p.useYn = :useYn " +
                " AND p.payStateType = :payStateType " +
                " AND p.payType != :payType " +
                //app 회원 추가
                " UNION " +
                " SELECT IFNULL(SUBSTRING(p.payDt, 1, 4), SUBSTRING(p.updateDt, 1, 4)) AS year, IFNULL(SUBSTRING(p.payDt, 6, 2), SUBSTRING(p.updateDt, 1, 4)) AS month, " +
        		" SUM(p.payAmount) AS amount " +
                " FROM branch_pay p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                " JOIN app_branch_manager abm ON (abm.branchId = p.branchId AND abm.memberId = p.memberId AND abm.useYn = :useYn) " +
    	        " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +        
                " WHERE p.branchId = :branchId AND p.useYn = :useYn " +
                " AND p.payStateType = :payStateType " +
                " AND p.payType != :payType " +
                
                
                " GROUP BY IFNULL(DATE_FORMAT(p.payDt,'%Y-%m'), DATE_FORMAT(p.updateDt, '%Y-%m')) ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("payStateType", 10);
        args.put("payType", 3); //미수금
        args.put("useYn", Constants.USE);
        
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AccountingMonthPay.class));

    }    
    
    public int updatePayForSync(String branchId, String orderId, String reservationId, String newReservationId) {
        String s = " UPDATE branch_pay SET reservationId = :newReservationId, updateDt = NOW() " +
                " WHERE branchId = :branchId AND orderId = :orderId AND reservationId = :reservationId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("orderId", orderId);
        args.put("reservationId", reservationId);
        args.put("newReservationId", newReservationId);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to update pay");

        } else {
            

        }

        return result;
    }
}
