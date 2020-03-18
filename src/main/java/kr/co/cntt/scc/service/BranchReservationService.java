package kr.co.cntt.scc.service;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.Constants.ReservationStatusType;
import kr.co.cntt.scc.alimTalk.TbAlimtalkTemplate;
import kr.co.cntt.scc.app.admin.model.AppAdminMember;
import kr.co.cntt.scc.app.admin.model.administer.DeskRatio;
import kr.co.cntt.scc.app.admin.model.register.RegisterReservation;
import kr.co.cntt.scc.app.admin.model.search.SearchMember;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.model.Page;
import kr.co.cntt.scc.model.Reservation;
import kr.co.cntt.scc.model.AdminStatistics;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.service.BranchMemberService.DataModelTest;
import kr.co.cntt.scc.service.BranchMemberService.UserDataModel;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.util.DateUtil;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reservation 예약
 *
 * Created by jslivane on 2016. 6. 14..
 */
@Service
@Transactional
@Slf4j
public class BranchReservationService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;
    
    @Autowired
    BranchService branchService;
    
	@Getter
	@Setter
	static class DataModelTest<T>{
		private String mode;
		private T data;
	}

    @Getter
    @Setter
    static class UserDataModel {
    	private String name;
    	private String userId;
    	private int startTime;
    	private int endTime;
    }

    /*******************************************************************************/      
    
    /**
     * 현재 예약별 남/여, 성인/학생 통계
     * @param branchId
     */
    
    public List<Branch> selectReservationStatisticsList(String branchId, String sDate) {
    	String s = " SELECT DISTINCT "
    							+ " b.name "
    							+ " , b.openDt "
    							+ ", DATE_FORMAT(p.payDt,'%m') m "
    							+ ", DATE_FORMAT(p.payDt,'%Y-%m') ym "
			    				+ ", COUNT(CASE WHEN m.gender = 10 THEN 1 END ) as 'man_cnt' " 
								+ ", COUNT(CASE WHEN m.gender = 20 THEN 1 END ) as 'woman_cnt' " 
								+ ", COUNT(CASE WHEN m.schoolGrade < 7 THEN 1 END ) as 'stu_cnt' "
								+ ", COUNT(CASE WHEN m.schoolGrade >=7 THEN 1 END ) as 'adult_cnt' "
								+ ", COUNT(CASE WHEN m.schoolGrade =1 THEN 1 END ) as 'mid1_cnt' "
								+ ", COUNT(CASE WHEN m.schoolGrade =2 THEN 1 END ) as 'mid2_cnt' "
								+ ", COUNT(CASE WHEN m.schoolGrade =3 THEN 1 END ) as 'mid3_cnt' "
								+ ", COUNT(CASE WHEN m.schoolGrade =4 THEN 1 END ) as 'high1_cnt' "
								+ ", COUNT(CASE WHEN m.schoolGrade =5 THEN 1 END ) as 'high2_cnt' "					
								+ ", COUNT(CASE WHEN m.schoolGrade =6 THEN 1 END ) as 'high3_cnt' "
								+ ", COUNT(CASE WHEN m.schoolGrade =7 THEN 1 END ) as 'orgAdult_cnt' "
								+ ", COUNT(CASE WHEN m.schoolGrade >7 THEN 1 END ) as 'etc_cnt' "										
				+ " FROM branch_pay p "
				+ " LEFT JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) "
				+ " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) "
				+ " JOIN branch b ON (p.branchId = b.branchId AND b.visibleYn = :useYn AND b.useYn = :useYn) "
				+ " WHERE 1=1 "
				+ " AND m.memberId != 'ec8e0219-8d51-49c8-971a-d9e275c6676e' ";
    	
				if(!StringUtils.isEmpty(branchId)) {
					s += " AND p.branchId = :branchId ";
				} else {
					s += " AND p.branchId IN (SELECT branchId FROM branch where visibleYn = 1 and useYn = 1) ";
				}
				
				s += " AND p.useYn = :useYn ";
				
				if(!StringUtils.isEmpty(sDate)) {
					s += " AND DATE_FORMAT(p.payDt, '%Y') = :sDate ";	
				} else {
					
				}
				
				s += " AND p.payStateType = 10 "
				+ " AND p.payAmount > 0 "
				+ " group by ym, p.branchId "
				
				+ " UNION " 
        		+ " SELECT DISTINCT "
        					+ " b.name "
        					+ " , b.openDt "
        					+ " , DATE_FORMAT(p.payDt,'%m') m "
        					+ " , DATE_FORMAT(p.payDt,'%Y-%m') ym "
				  			+ " , COUNT(CASE WHEN asm.gender = 10 THEN 1 END ) as 'man_cnt' "
							+ " , COUNT(CASE WHEN asm.gender = 20 THEN 1 END ) as 'woman_cnt' " 
							+ " , COUNT(CASE WHEN (date_format(now(), '%Y') - date_format(asm.birthDt, '%Y')) < 19 THEN 1 END ) as 'stu_cnt' "
							+ " , COUNT(CASE WHEN (date_format(now(), '%Y') - date_format(asm.birthDt, '%Y')) >=19 THEN 1 END ) as 'adult_cnt' "
							+ " , COUNT(CASE WHEN (date_format(now(), '%Y') - date_format(asm.birthDt, '%Y')) =13 THEN 1 END ) as 'mid1_cnt' "
							+ " , COUNT(CASE WHEN (date_format(now(), '%Y') - date_format(asm.birthDt, '%Y')) =14 THEN 1 END ) as 'mid2_cnt' "
							+ " , COUNT(CASE WHEN (date_format(now(), '%Y') - date_format(asm.birthDt, '%Y')) =15 THEN 1 END ) as 'mid3_cnt' "
							+ " , COUNT(CASE WHEN (date_format(now(), '%Y') - date_format(asm.birthDt, '%Y')) =16 THEN 1 END ) as 'high1_cnt' "
							+ " , COUNT(CASE WHEN (date_format(now(), '%Y') - date_format(asm.birthDt, '%Y')) =17 THEN 1 END ) as 'high2_cnt' "					
							+ " , COUNT(CASE WHEN (date_format(now(), '%Y') - date_format(asm.birthDt, '%Y')) =18 THEN 1 END ) as 'high3_cnt' "
							+ " , COUNT(CASE WHEN (date_format(now(), '%Y') - date_format(asm.birthDt, '%Y')) >18 THEN 1 END ) as 'orgAdult_cnt' "
							+ " , COUNT(CASE WHEN (date_format(asm.birthDt, '%Y')) = ''  THEN 1 END ) as 'etc_cnt' "		
						+ " FROM branch_pay p " 
		                + " LEFT JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = 1) "
		                + " JOIN app_branch_manager abm ON (abm.branchId = p.branchId AND abm.memberId = p.memberId AND abm.useYn = :useYn) "
		                + " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) "
		                + " JOIN branch b ON (p.branchId = b.branchId AND b.visibleYn = :useYn AND b.useYn = :useYn) "
				+ " WHERE 1=1 ";

				if(!StringUtils.isEmpty(branchId)) {
					s += " AND p.branchId = :branchId ";
				} else {
					s += " AND p.branchId IN (SELECT branchId FROM branch where visibleYn = 1 and useYn = 1) ";
				}
				
				
				s += " AND p.payStateType = 10 "
				+ " AND p.payAmount > 0 ";
		
				if(!StringUtils.isEmpty(sDate)) {
					s += " AND DATE_FORMAT(p.payDt, '%Y') = :sDate ";	
				} else {
					
				}
				
				s += " AND p.useYn = :useYn "

				+ " group by ym, p.branchId "
				+ " order by openDt, ym  ";
				
    	
    	Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("useYn", Constants.USE);
        args.put("sDate", sDate);
        args.put("reservationStatus", Constants.ReservationStatusType.CONFIRMED.getValue());        
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Branch.class));
    }
    
    
    /**
     * 만료된 예약 리스트 조회
     * @param branchId
     * @param startDate
     * @param endDate
     * @param page
     * @param reservationStatus
     * @return
     * MySQL Date Time Fuctions : http://dev.mysql.com/doc/refman/5.7/en/date-and-time-functions.html
     *
     */
    public List<Reservation> selectReservationAllList(String branchId, Constants.ReservationStatusType reservationStatus, String expireYn) {
        String s = " SELECT branchId, orderId, reservationId, memberId, " +
        		" deskId, reservationNum, deskStartDt, deskStartTm, deskEndDt, deskEndTm, " +
                " reservationDt, reservationTm, reservationNote, reservationStatus, insertDt, updateDt, checkoutYn " +
        		" FROM ( " +
        		
	        		" SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
	                " r.deskId, r.reservationNum, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, " +
	                " r.reservationDt, r.reservationTm, r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn" +
	                " FROM branch_reservation r " +
	                " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
	                " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = :useYn";
	                
	                if(!StringUtils.isEmpty(expireYn)) {
	                	s += " AND m.expireYn = :expireYn ";
	                }
	                s += " ) " +
	                
	                " WHERE r.branchId = :branchId ";                
			        if(reservationStatus != null) {
			            s += " AND r.reservationStatus = :reservationStatus";
			        }
			        s += " AND r.useYn = :useYn " +
			        " GROUP BY memberId, deskStartDt DESC, deskStartTm DESC " +
			        
			    " ) a GROUP BY memberId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("useYn", Constants.USE);
        args.put("reservationStatus", reservationStatus.getValue());    
        args.put("expireYn", expireYn);
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Reservation.class));

    }
    
    
    /**
     * 예약 리스트 조회 (사용일 기준)
     * @param branchId
     * @param startDate
     * @param endDate
     * @param page
     * @param reservationStatus
     * @return
     * MySQL Date Time Fuctions : http://dev.mysql.com/doc/refman/5.7/en/date-and-time-functions.html
     *
     */
    public List<Reservation> selectReservationList(String branchId, String startDate, String endDate, String memberId, String roomId, String deskId, Page page, Constants.ReservationStatusType reservationStatus, String expireYn) {
        String s = " SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
                " r.deskId, r.reservationNum, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, " +
                " r.reservationDt, r.reservationTm, r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn" +
                " FROM branch_reservation r " +
                " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = :useYn";
                
                if(!StringUtils.isEmpty(expireYn)) {
                	s += " AND m.expireYn = :expireYn ";
                }
                s += " ) " +
                
                " WHERE r.branchId = :branchId ";                

        if(!StringUtils.isEmpty(memberId)) {
            s += " AND r.memberId = :memberId ";

        }

        if(!StringUtils.isEmpty(roomId)) {
            s += " AND r.deskId IN (SELECT d.deskId FROM branch_room r JOIN branch_desk d " +
                    " ON (d.branchId = r.branchId AND d.roomId = r.roomId AND d.useYn = :useYn)" +
                    " WHERE r.branchId = :branchId AND r.roomId = :roomId AND r.useYn = :useYn) ";

        }

        if(!StringUtils.isEmpty(deskId)) {
            s += " AND r.deskId = :deskId ";

        }
        
        if(!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {       	
        	if (startDate.equals(endDate)) {
        		s += " AND r.deskStartDt <= :startDate AND r.deskEndDt >= :startDate";
        	}
        	else {        		
        		s += " AND ( ((:startDate BETWEEN r.deskStartDt and r.deskEndDt) or (:endDate BETWEEN r.deskStartDt and r.deskEndDt)) "
        		   + " OR (( r.deskStartDt BETWEEN :startDate and :endDate ) or( r.deskEndDt BETWEEN :startDate and :endDate )) )";
        	}

        }

        if (!StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate)) {
        	s += " AND ( (:startDate between r.deskStartDt and r.deskEndDt) or r.deskStartDt >= :startDate) ";
        }

        if (StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
        	s += " AND r.deskEndDt = :endDate ";
        }

        if(reservationStatus != null) {
            s += " AND r.reservationStatus = :reservationStatus";

        }
        s += " AND r.useYn = :useYn " +

        
		// app 회원 추가
        " UNION " +
        " SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
        " r.deskId, r.reservationNum, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, " +
        " r.reservationDt, r.reservationTm, r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn" +
        " FROM branch_reservation r " +
        " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
        " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = :useYn) " +
        " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
        " WHERE r.branchId = :branchId " ;
        
        if(!StringUtils.isEmpty(memberId)) {
            s += " AND r.memberId = :memberId ";

        }

        if(!StringUtils.isEmpty(roomId)) {
            s += " AND r.deskId IN (SELECT d.deskId FROM branch_room r JOIN branch_desk d " +
                    " ON (d.branchId = r.branchId AND d.roomId = r.roomId AND d.useYn = :useYn)" +
                    " WHERE r.branchId = :branchId AND r.roomId = :roomId AND r.useYn = :useYn) ";

        }

        if(!StringUtils.isEmpty(deskId)) {
            s += " AND r.deskId = :deskId ";

        }
        
        if(!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {       	
        	if (startDate.equals(endDate)) {
        		s += " AND r.deskStartDt <= :startDate AND r.deskEndDt >= :startDate";
        	}
        	else {        		
        		s += " AND ( ((:startDate BETWEEN r.deskStartDt and r.deskEndDt) or (:endDate BETWEEN r.deskStartDt and r.deskEndDt)) "
        		   + " OR (( r.deskStartDt BETWEEN :startDate and :endDate ) or( r.deskEndDt BETWEEN :startDate and :endDate )) )";
        	}

        }

        if (!StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate)) {
        	s += " AND ( (:startDate between r.deskStartDt and r.deskEndDt) or r.deskStartDt >= :startDate) ";
        }

        if (StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
        	s += " AND r.deskEndDt = :endDate ";
        }
        
        if(reservationStatus != null) {
            s += " AND r.reservationStatus = :reservationStatus";

        }
        s += " AND r.useYn = :useYn ";
        
        
        
        
        s += " ORDER BY reservationDt DESC, reservationTm DESC ";
        
        if(page == null) {}
        else {
	        int perPage = (page.getPage() -1) * page.getPerPageNum();
	    	s += " limit "+perPage+", "+page.getPerPageNum()+" ";
        }
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("deskId", deskId);
        args.put("roomId", roomId);
        args.put("useYn", Constants.USE);
        args.put("startDate", startDate);
        args.put("endDate", endDate);
        //args.put("startTime", DateUtil.MIN_TIME);
        //args.put("endTime", DateUtil.MAX_TIME);
        args.put("reservationStatus", reservationStatus.getValue());    
        args.put("expireYn", expireYn);
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Reservation.class));

    }

    /**
     * 예약 리스트 조회 (예약일 기준)
     * @param branchId
     * @param reservationStartDt
     * @param reservationEndDt
     * @param memberId
     * @param roomId
     * @param deskId
     * @return
     *
     * MySQL Date Time Fuctions : http://dev.mysql.com/doc/refman/5.7/en/date-and-time-functions.html
     *
     */
    public List<Reservation> selectReservationListByReservationDt(String branchId, String reservationStartDt, String reservationEndDt,
                                          String memberId, String roomId, String deskId) {

        String s = " SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
                " r.deskId, r.reservationNum, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, " +
                " r.reservationDt, r.reservationTm, r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn " +
                " FROM branch_reservation r " +
                " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn)" +
                " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = :useYn) " +
                " WHERE r.branchId = :branchId ";

        if(!StringUtils.isEmpty(memberId)) {
            s += " AND r.memberId = :memberId ";

        }

        if(!StringUtils.isEmpty(reservationStartDt)) {
            //reservationStartDt = DateUtil.getCurrentDateString();
            s += " AND r.reservationDt >= :reservationStartDt";

        }

        if(!StringUtils.isEmpty(reservationEndDt)) {
            //reservationEndDt = DateUtil.getCurrentDateString();
            s += " AND r.reservationDt <= :reservationEndDt ";

        }

        if(!StringUtils.isEmpty(roomId)) {
            s += " AND r.deskId IN (SELECT d.deskId FROM branch_room r JOIN branch_desk d " +
                    " ON (d.branchId = r.branchId AND d.roomId = r.roomId AND d.useYn = :useYn)" +
                    " WHERE r.branchId = :branchId AND r.roomId = :roomId AND r.useYn = :useYn) ";

        }

        if(!StringUtils.isEmpty(deskId)) {
            s += " AND r.deskId = :deskId ";

        }
        s += " AND r.useYn = :useYn " +

        
        
        " UNION " + //app 회원 추가
        " SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
        " r.deskId, r.reservationNum, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, " +
        " r.reservationDt, r.reservationTm, r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn" +
        " FROM branch_reservation r " +
        " JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
        " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = :useYn) " +
        " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
        " WHERE r.branchId = :branchId " ;
        
        if(!StringUtils.isEmpty(memberId)) {
            s += " AND r.memberId = :memberId ";

        }

        if(!StringUtils.isEmpty(reservationStartDt)) {
            //reservationStartDt = DateUtil.getCurrentDateString();
            s += " AND r.reservationDt >= :reservationStartDt";

        }

        if(!StringUtils.isEmpty(reservationEndDt)) {
            //reservationEndDt = DateUtil.getCurrentDateString();
            s += " AND r.reservationDt <= :reservationEndDt ";

        }

        if(!StringUtils.isEmpty(roomId)) {
            s += " AND r.deskId IN (SELECT d.deskId FROM branch_room r JOIN branch_desk d " +
                    " ON (d.branchId = r.branchId AND d.roomId = r.roomId AND d.useYn = :useYn)" +
                    " WHERE r.branchId = :branchId AND r.roomId = :roomId AND r.useYn = :useYn) ";

        }

        if(!StringUtils.isEmpty(deskId)) {
            s += " AND r.deskId = :deskId ";

        }
        s += " AND r.useYn = :useYn ";
        
        
        
        
        s += " ORDER BY reservationDt ASC, reservationTm ASC ";
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("roomId", roomId);
        args.put("deskId", deskId);
        args.put("useYn", Constants.USE);
        args.put("reservationStartDt", reservationStartDt);
        args.put("reservationEndDt", reservationEndDt);
        //args.put("startTime", DateUtil.MIN_TIME);
        //args.put("endTime", DateUtil.MAX_TIME);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Reservation.class));

    }

    /**
     * (지점의 특정 주문의) 예약 목록 조회
     * @param branchId
     * @param orderId
     * @return
     */
    public List<Reservation> selectReservationList(String branchId, String orderId) {

        String s = " SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
                " r.deskId, r.reservationNum, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, " +
                " r.reservationDt, r.reservationTm, r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn " +
                " FROM branch_reservation r " +
                " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = :useYn) " +
                " WHERE r.branchId = :branchId ";
        
                if(!StringUtils.isEmpty(orderId)) {
                    s += " AND r.memberId = :orderId  ";
                	//s += " AND r.orderId = :orderId  ";

                }
                s += " AND r.useYn = :useYn ";

				//app 회원 추가
		        s += " UNION " + 
		        " SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
		        " r.deskId, r.reservationNum, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, " +
		        " r.reservationDt, r.reservationTm, r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn " +
		        " FROM branch_reservation r " +
		        " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
		        " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = :useYn) " +
		        " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
		        " WHERE r.branchId = :branchId ";
		        
		        if(!StringUtils.isEmpty(orderId)) {
                    s += " AND r.memberId = :orderId  ";
		        	//s += " AND r.orderId = :orderId  ";

                }
                s += " AND r.useYn = :useYn ";
		        
		        s += " AND r.useYn = :useYn " ;
        
        //s += " ORDER BY reservationDt ASC, reservationTm ASC ";
        s += " ORDER BY reservationDt DESC, reservationTm DESC ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("orderId", orderId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Reservation.class));

    }

    public int selectReservationCount(String branchId, String DATE, String TIME) {

        String s = " SELECT count(r.reservationId) " +                
                " FROM branch_reservation r " +
                " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = :useYn) " +
                " where r.branchId = :branchId";

        if(!StringUtils.isEmpty(DATE)) {
            s += " AND r.deskStartDt <= :DATE AND r.deskEndDt >= :DATE";

        }

        if(!StringUtils.isEmpty(TIME)) {
            s += " AND r.deskStartTm <= :TIME AND r.deskEndTm >= :TIME ";

        }
        	s += " AND r.useYn = :useYn ";
        	s += " AND r.deskId = '' OR r.deskID = null";
        	s += " AND r.reservationStatus = '20' " ;
        
        	//app 회원 추가
        	s += " UNION " +
        	" SELECT count(r.reservationId) " +                
            " FROM branch_reservation r " +
            " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = :useYn) " +
	        " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
            " where r.branchId = :branchId";

		    if(!StringUtils.isEmpty(DATE)) {
		        s += " AND r.deskStartDt <= :DATE AND r.deskEndDt >= :DATE";
		
		    }
		
		    if(!StringUtils.isEmpty(TIME)) {
		        s += " AND r.deskStartTm <= :TIME AND r.deskEndTm >= :TIME ";
		
		    }
		    	s += " AND r.useYn = :useYn ";
		    	s += " AND r.deskId = '' OR r.deskID = null";
		    	s += " AND r.reservationStatus = '20' ";
		        	
        	
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("useYn", Constants.USE);
        args.put("DATE", DATE);
        args.put("TIME", TIME);
        //args.put("startTime", DateUtil.MIN_TIME);
        //args.put("endTime", DateUtil.MAX_TIME);
        
        return jdbcTemplate.queryForObject(s, args, int.class);
      }
    /*******************************************************************************/


    public Reservation selectReservation(String branchId, String reservationId) {
        String s = " SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
                " r.deskId, reservationNum, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, " +
                " r.reservationDt, r.reservationTm, r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn " +
                " FROM branch_reservation r " +
                " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = :useYn) " +                
                " WHERE r.branchId = :branchId AND r.reservationId = :reservationId AND r.useYn = :useYn " +            
        		" UNION " + //app 회원 추가
        		" SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
                " r.deskId, reservationNum, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, " +
                " r.reservationDt, r.reservationTm, r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn " + 
                " FROM branch_reservation r " +
                " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
                " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = :useYn) " +
                " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
                " WHERE r.branchId = :branchId AND r.reservationId = :reservationId AND r.useYn = :useYn ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("reservationId", reservationId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(Reservation.class));

    }

    public int insertReservation(String branchId, String reservationId, Reservation reservation) {
        String s = " INSERT INTO branch_reservation ( " +
                " branchId, orderId, reservationId, memberId, " +
                " deskId, reservationNum, deskStartDt, deskStartTm, deskEndDt, deskEndTm, " +
                " reservationDt, reservationTm, reservationNote, reservationStatus" +
                " ) VALUES ( " +
                " :branchId, :orderId, :reservationId, :memberId, " +
                " :deskId, :reservationNum, :deskStartDt, :deskStartTm, :deskEndDt, :deskEndTm, " +
                " :reservationDt, :reservationTm, :reservationNote, :reservationStatus " +
                " ) ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(reservation);
        source.addValue("branchId", branchId);
        source.addValue("reservationId", reservationId);
        source.addValue("reservationDt", DateUtil.getCurrentDateString());
        source.addValue("reservationTm", DateUtil.getCurrentTimeString());

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to create reservation");

        } else {
            History history = new History(branchId, Constants.HistoryType.RESERVATION_CREATE, reservation.toString());
            history.setOrderId(reservation.getOrderId());
            history.setReservationId(reservationId);
            history.setMemberId(reservation.getMemberId());
            history.setDeskId(reservation.getDeskId());
            historyService.insertHistory(history);
            
          //장기동 센터일 때만  || branchId.equals("28f6b3ca-1289-43f3-971a-9135120b0eb4")
            	Branch branch = branchService.selectBranch(branchId);
            	if (branch.getFingerprintYn() == 1) {
            		//지문인식기 데이터 삽입
    	            Socket socket = null;
    	    		BufferedReader in = null;
    	    		PrintWriter out = null;
    	    		try {
    	    			//socket = new Socket("220.73.9.56", 9000);
    	    			socket = new Socket(branch.getIp(), 9005);
    	    			out = new PrintWriter(socket.getOutputStream(), true);
    	    			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	    			UserDataModel user = new UserDataModel();
    	    			user.setUserId(reservation.getMemberNo());
    	    			user.setName("");
    	    			user.setStartTime(reservation.getDeskStartDtTimeStamp()); //335339736
    	    			user.setEndTime(reservation.getDeskEndDtTimeStamp());
    	    			DataModelTest<UserDataModel> userModel = new DataModelTest<UserDataModel>();
    	//    			userModel.setMode("EnrolUser");
    	    			userModel.setMode("UpdateUser");
    	//    			userModel.setMode("DeleteUser");
    	    			userModel.setData(user);
    	    			out.println(new Gson().toJson(userModel));
    	    			String response = in.readLine();
    	    			System.out.println(response);
    	    		}catch (Exception e) {
    	    			e.printStackTrace();
    	    		} finally {
    	    			try {
    	    				if(out !=null) out.close();
    	    				if(in != null) in.close();
    	    				if(socket != null) socket.close();
    	    			} catch (Exception e) {
    	    			}
    	    		}
            	}

            

        }

        return result;


    }

    public int updateReservation(String branchId, String reservationId, Reservation reservation) {
        String s = " UPDATE branch_reservation SET " +
                " deskId = :deskId, reservationNum = :reservationNum ,deskStartDt = :deskStartDt, deskStartTm = :deskStartTm, deskEndDt = :deskEndDt, deskEndTm = :deskEndTm, " +
                " reservationNote = :reservationNote, reservationStatus = :reservationStatus, updateDt = NOW() " +
                " WHERE branchId = :branchId AND orderId = :orderId AND reservationId = :reservationId ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(reservation);
        source.addValue("branchId", branchId);
        source.addValue("reservationId", reservationId);

        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to update reservation");

        } else {
            History history = new History(branchId, Constants.HistoryType.RESERVATION_UPDATE, reservation.toString());
            history.setOrderId(reservation.getOrderId());
            history.setReservationId(reservationId);
            history.setMemberId(reservation.getMemberId());
            history.setDeskId(reservation.getDeskId());
            historyService.insertHistory(history);
            
          //장기동 센터일 때만 || branchId.equals("28f6b3ca-1289-43f3-971a-9135120b0eb4")            
            	Branch branch = branchService.selectBranch(branchId);
	            if (branch.getFingerprintYn() == 1) {
	            	//지문인식기 데이터 삽입
		            Socket socket = null;
		    		BufferedReader in = null;
		    		PrintWriter out = null;
		    		try {
		    			
		    			//socket = new Socket("220.73.9.56", 9000);
		    			socket = new Socket(branch.getIp(), 9005);
		    			out = new PrintWriter(socket.getOutputStream(), true);
		    			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    			UserDataModel user = new UserDataModel();
		    			user.setUserId(reservation.getMemberNo());
		    			user.setName("");
		    			user.setStartTime(reservation.getDeskStartDtTimeStamp()); //335339736
		    			user.setEndTime(reservation.getDeskEndDtTimeStamp());
		    			DataModelTest<UserDataModel> userModel = new DataModelTest<UserDataModel>();
		//    			userModel.setMode("EnrolUser");
		    			userModel.setMode("UpdateUser");
		//    			userModel.setMode("DeleteUser");
		    			userModel.setData(user);
		    			out.println(new Gson().toJson(userModel));
		    			String response = in.readLine();
		    			System.out.println(response);
		    		}catch (Exception e) {
		    			e.printStackTrace();
		    		} finally {
		    			try {
		    				if(out !=null) out.close();
		    				if(in != null) in.close();
		    				if(socket != null) socket.close();
		    			} catch (Exception e) {
		    			}
		    		}
	            }


        }

        return result;

    }

    public int updateReservationStatus(String branchId, String orderId, String reservationId, Constants.ReservationStatusType reservationStatus) {
        String s = " UPDATE branch_reservation SET reservationStatus = :reservationStatus, updateDt = NOW() " +
                " WHERE branchId = :branchId AND orderId = :orderId AND reservationId = :reservationId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("orderId", orderId);
        args.put("reservationId", reservationId);
        args.put("reservationStatus", reservationStatus.getValue());

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to update reservation status");

        } else {
            History history = new History(branchId, Constants.HistoryType.RESERVATION_UPDATE, reservationStatus.getText());
            history.setOrderId(orderId);
            history.setReservationId(reservationId);
            historyService.insertHistory(history);

        }

        return result;
    }

    public int deleteReservation(String branchId, String reservationId, Reservation reservation) {
        String s = " UPDATE branch_reservation SET " +
                " reservationStatus = :reservationStatus, " +
                " useYn = :useYn, updateDt = NOW() " +
                " WHERE branchId = :branchId AND reservationId = :reservationId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("reservationId", reservationId);
        args.put("reservationStatus", Constants.ReservationStatusType.CANCELED.getValue());
        args.put("useYn", Constants.NOT_USE);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to delete reservation");

        } else {
            History history = new History(branchId, Constants.HistoryType.RESERVATION_DELETE, "");
            history.setReservationId(reservationId);
            historyService.insertHistory(history);

        }

        return result;

    }


    /*******************************************************************************/


    public Reservation selectCurrentReservationByMemberId(String branchId, String memberId) {

        String s = " SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
                " r.deskId, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, r.reservationDt, r.reservationTm, " +
                " r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn " +
                " FROM branch_reservation r " +
                " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = :useYn) " +
                " WHERE r.branchId = :branchId AND r.memberId = :memberId AND r.useYn = :useYn " +
                " AND :currentDateTime BETWEEN CONCAT(r.deskStartDt, ' ', r.deskStartTm) AND CONCAT(r.deskEndDt, ' ', r.deskEndTm) " +
                " AND r.reservationStatus = :reservationStatus " ;
           //app 회원 추가
           s += " UNION " + 
        		" SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
                " r.deskId, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, r.reservationDt, r.reservationTm, " +
                " r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn" + 
                " FROM branch_reservation r " +
                " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
                " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = :useYn) " +
                " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
                " WHERE r.branchId = :branchId AND r.memberId = :memberId AND r.useYn = :useYn " +
                " AND :currentDateTime BETWEEN CONCAT(r.deskStartDt, ' ', r.deskStartTm) AND CONCAT(r.deskEndDt, ' ', r.deskEndTm) " +
                " AND r.reservationStatus = :reservationStatus " ;
                
                
                
                
           s += " LIMIT 1 ";
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("useYn", Constants.USE);
        args.put("currentDateTime", DateUtil.getCurrentDateTimeString());
        args.put("reservationStatus", 20);
        
        return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(Reservation.class));


    }

    
    
    //현재 등록된 등록개수
    public List<Reservation> selectReservationCount(String branchId, String memberId) {
    	    	
        String s = " SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
                " r.deskId, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, r.reservationDt, r.reservationTm, " +
                " r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn " +
                " FROM branch_reservation r " +
                " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = :useYn) " +
                " WHERE r.branchId = :branchId AND r.memberId = :memberId AND r.useYn = :useYn " +
                " AND r.deskEndDt >= CURRENT_DATE " +
                " AND r.reservationStatus = :reservationStatus " ;
                //app 회원 추가
           s += " UNION " + 
        		" SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
                " r.deskId, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, r.reservationDt, r.reservationTm, " +
                " r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn " +
                " FROM branch_reservation r " +
                " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
                " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = :useYn) " +
                " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
                " WHERE r.branchId = :branchId AND r.memberId = :memberId AND r.useYn = :useYn " +
                " AND r.deskEndDt >= CURRENT_DATE " +
                " AND r.reservationStatus = :reservationStatus " ;

        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("reservationStatus", 20);
        args.put("useYn", Constants.USE);        
        	
        try {
        	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Reservation.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}


    }
    
    //등록이 3일 남은것들 select -> 7일로 변경
    public Reservation select3daysReservation(String branchId, String memberId) {

    	String startDt = DateUtil.getCurrentDateString();
    	String endDt = DateUtil.getCurrentDatePlusDaysString(6);
    	    	
        String s = " SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
                " r.deskId, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, r.reservationDt, r.reservationTm, " +
                " r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn," +
                " DATEDIFF(r.deskEndDt, CURDATE()) as dayGap " +
                " FROM branch_reservation r " +
                " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = :useYn) " +
                " WHERE r.branchId = :branchId AND r.memberId = :memberId AND r.useYn = :useYn " +
                " AND r.deskEndDt BETWEEN :startDt AND :endDt " +
                " AND r.reservationStatus = :reservationStatus " ;
                //app 회원 추가
           s += " UNION " + 
        		" SELECT r.branchId, r.orderId, r.reservationId, r.memberId, " +
                " r.deskId, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, r.reservationDt, r.reservationTm, " +
                " r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn, " +
                " DATEDIFF(r.deskEndDt, CURDATE()) as dayGap " +
                " FROM branch_reservation r " +
                " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
                " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = :useYn) " +
                " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
                " WHERE r.branchId = :branchId AND r.memberId = :memberId AND r.useYn = :useYn " +
                " AND r.deskEndDt BETWEEN :startDt AND :endDt " +
                " AND r.reservationStatus = :reservationStatus " ;
                
                s += " LIMIT 1 ";
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("startDt", startDt);
        args.put("endDt", endDt);
        args.put("reservationStatus", 20);
        args.put("useYn", Constants.USE);
        args.put("currentDateTime", DateUtil.getCurrentDateTimeString());
        	
        try {
			return jdbcTemplate. queryForObject(s, args, new BeanPropertyRowMapper<>(Reservation.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}


    }


    /******************************************
     * 
     * 홈화면 통계
     * 
     * *************************************/
    
    public List<Reservation> selectReservationCountList(String branchId, String dateFlag) {
        String s = " SELECT distinct r.branchId, r.orderId, r.reservationId, r.memberId, " +
                " r.deskId, r.reservationNum, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, " +
                " r.reservationDt, r.reservationTm, r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn " +
                " FROM branch_reservation r " +
                " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = :useYn) " +
                " WHERE r.branchId = :branchId " +                
                " AND r.reservationStatus = :reservationStatus ";
        		
        		if(!StringUtils.isEmpty(dateFlag)) {
        			if(dateFlag.equals("today")) {
        				s += " AND ( CASE " +
        					 " WHEN CURDATE() = r.deskStartDt THEN ( CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt ) AND CURTIME() >= r.deskStartTm " +
        					 " ELSE CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt " +
        					 " END ) ";
        				
        				//s += " AND CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt ";
        			}
        			else if (dateFlag.equals("yesterday")) {       				
        				s += " AND CURDATE() - INTERVAL 1 DAY BETWEEN r.deskStartDt AND r.deskEndDt ";
        			}
        			else if (dateFlag.equals("twodaysago")) {
        				s += " AND CURDATE() - INTERVAL 2 DAY BETWEEN r.deskStartDt AND r.deskEndDt ";
        			}        		        
        		}

                s += " AND (r.deskId != null or r.deskId != '') " ;
                s += " AND r.useYn = :useYn ";

                //app 회원 추가                
                s += " UNION " + 
            		 " SELECT distinct r.branchId, r.orderId, r.reservationId, r.memberId, " +
                     " r.deskId, r.reservationNum, r.deskStartDt, r.deskStartTm, r.deskEndDt, r.deskEndTm, " +
                     " r.reservationDt, r.reservationTm, r.reservationNote, r.reservationStatus, r.insertDt, r.updateDt, r.checkoutYn " +
                     " FROM branch_reservation r " +
                     " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
                     " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = :useYn) " +
                     " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
                     " WHERE r.branchId = :branchId " +                
                     " AND r.reservationStatus = :reservationStatus ";
             		
             		if(!StringUtils.isEmpty(dateFlag)) {
             			if(dateFlag.equals("today")) {
             				s += " AND ( CASE " +
             					 " WHEN CURDATE() = r.deskStartDt THEN ( CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt ) AND CURTIME() >= r.deskStartTm " +
             					 " ELSE CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt " +
             					 " END ) ";
             				
             				//s += " AND CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt ";
             			}
             			else if (dateFlag.equals("yesterday")) {       				
             				s += " AND CURDATE() - INTERVAL 1 DAY BETWEEN r.deskStartDt AND r.deskEndDt ";
             			}
             			else if (dateFlag.equals("twodaysago")) {
             				s += " AND CURDATE() - INTERVAL 2 DAY BETWEEN r.deskStartDt AND r.deskEndDt ";
             			}        		        
             		}

                     s += " AND (r.deskId != null or r.deskId != '') " ;
                     s += " AND r.useYn = :useYn ";
                
                

        //s += " ORDER BY r.reservationDt DESC, r.reservationTm DESC ";
        
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        //args.put("reservationStatus", Constants.ReservationStatusType.CONFIRMED.getValue());
        args.put("reservationStatus", 20);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Reservation.class));

    }

    //당일 전 등록된 등록
    public List<Integer> selectYesterDayReservationCount(String branchId) {
    	String s = " SELECT DATEDIFF(deskEndDt, deskStartDt) " +
    			" FROM branch_reservation r " +
    			" WHERE r.branchId = :branchId " +
    			" AND deskStartDt = DATE_ADD(curdate(), INTERVAL -1 DAY) " +
    			" AND r.reservationStatus = :reservationStatus " +
    			" AND r.useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("branchId", branchId);
    	args.put("reservationStatus", 20);
    	args.put("useYn", Constants.USE);
        
    	return (List<Integer>) jdbcTemplate.queryForList(s, args, Integer.class);
    }
    
    //월, 일 등록 분리
    public List<Integer> selectStatisticsReservationCount(String branchId, String dateFlag) {
    	String s = " SELECT DATEDIFF(deskEndDt, deskStartDt) " +
    			" FROM branch_reservation r " +
    			" WHERE r.branchId = :branchId " +
    			//" AND deskStartDt = DATE_ADD(curdate(), INTERVAL -1 DAY) " +
    			" AND r.reservationStatus = :reservationStatus ";
    			    	
		if(!StringUtils.isEmpty(dateFlag)) {
			if(dateFlag.equals("today")) {
				s += " AND CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt ";
			}
			else if (dateFlag.equals("yesterday")) {
				s += " AND CURDATE() - INTERVAL 1 DAY BETWEEN r.deskStartDt AND r.deskEndDt ";
			}
			else if (dateFlag.equals("twodaysago")) {
				s += " AND CURDATE() - INTERVAL 2 DAY BETWEEN r.deskStartDt AND r.deskEndDt ";
			}        		        
		}
		
		s += " AND (r.deskId != null or r.deskId != '') " ;
    	s += " AND r.useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("branchId", branchId);
    	args.put("reservationStatus", 20);
    	args.put("useYn", Constants.USE);
        
    	return (List<Integer>) jdbcTemplate.queryForList(s, args, Integer.class);
    }
    
    
    public int updateReservationCheckout(String branchId, String reservationId, Reservation reservation) {
        String s = " UPDATE branch_reservation SET " +
                " checkoutYn = :checkoutYn, " +
                " updateDt = NOW() " +
                " WHERE branchId = :branchId AND reservationId = :reservationId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("reservationId", reservationId);        
        args.put("checkoutYn", Constants.USE);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to update reservation");

        } else {
            History history = new History(branchId, Constants.HistoryType.RESERVATION_UPDATE, "");
            history.setReservationId(reservationId);
            historyService.insertHistory(history);

        }

        return result;

    }
    
    ///////////////////////////////////////연장데이터//////////////////////////
    //연장데이터(가공 전)
    public List<Reservation> selectReservationExtensionList(String branchId) {
    	
    	String s = " SELECT * FROM ( " +
    			" select br.* " +
				" from branch_reservation br " +
			    " join branch_pay bp on ( bp.orderId = br.orderId and ( bp.payAmount > 0  or bp.payAmount < 0) and bp.payStateType = 10 AND bp.payType <> 3 and bp.payDt = DATE_ADD(curdate(), INTERVAL -1 DAY) and bp.useYn = :useYn) " +
			    " where br.branchId = :branchId " +
			    " and br.reservationStatus != :reservationStatus " + 
			    " and br.useYn = :useYn " +
			    " group by br.reservationId " +
			    " HAVING count(br.reservationId) < 2 " +
			    " order by br.insertDt desc " +							
			    " ) test1 " +
			    " group by orderId " +
			    " HAVING count(orderId) > 1 ";

	
    	Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        //args.put("reservationStatus", Constants.ReservationStatusType.CONFIRMED.getValue());
        args.put("reservationStatus", 90);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Reservation.class));
    }
    
    //연장데이터(가공)
    public int selectReservationExtensionCount(String branchId, String reservationId) {

        String s = " SELECT COUNT(reservationId) " +                
                " FROM branch_pay bp" +
                " WHERE bp.branchId = :branchId " +
                " AND bp.reservationId = :reservationId " +
                " AND (bp.payAmount > 0  or bp.payAmount < 0) " +
                " AND bp.payStateType = 10 " + 
                " AND bp.payType <> 3 " +
                " AND bp.useYn = :useYn " +
                " GROUP BY bp.reservationId " +
                " HAVING COUNT(bp.reservationId) = 1 ";
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("reservationId", reservationId);
        args.put("useYn", Constants.USE);

        try {
        	return jdbcTemplate.queryForObject(s, args, int.class);
        } catch(EmptyResultDataAccessException e) {
        	return 0;
        } 


      }    	
    
    //연장데이터(가공2)
    public int selectReservationExtensionCount2(String branchId, String orderId) {

        String s = " SELECT COUNT(bp.reservationId) " +                
                " FROM branch_pay bp" +
                " WHERE bp.branchId = :branchId " +
                " AND bp.orderId = :orderId " +
                " AND (bp.payAmount > 0  or bp.payAmount < 0) " +
                " AND bp.payStateType = 10 " + 
                " AND bp.payType <> 3 " +
                " AND bp.useYn = :useYn " +
                " HAVING COUNT(bp.reservationId) > 1 ";
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("orderId", orderId);
        args.put("useYn", Constants.USE);

        try {
        	return jdbcTemplate.queryForObject(s, args, int.class);
        } catch(EmptyResultDataAccessException e) {
        	return 0;
        } 


      }    	
    
    //연장데이터(가공3)
    public int selectReservationExtensionDate(String branchId, String reservationId) {

        String s = " SELECT DATEDIFF(br.deskEndDt, br.deskStartDt) " +
        		" FROM branch_reservation br " +
        		" WHERE br.branchId = :branchId " +
        		" AND br.reservationId = :reservationId " +
        		" AND br.reservationStatus != :reservationStatus " + 
        		" AND br.useYn = useYn ";
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("reservationId", reservationId);
        args.put("reservationStatus", 90);
        args.put("useYn", Constants.USE);

        try {
        	return jdbcTemplate.queryForObject(s, args, int.class);
        } catch(EmptyResultDataAccessException e) {
        	return 0;
        } 


      }     
    
    //종료 데이터
    public List<Integer> selectReservationMonthEnd(String branchId) {

        String s = " SELECT DATEDIFF(br.deskEndDt, br.deskStartDt) " +
        " FROM branch_reservation br " +
        " WHERE br.branchId = :branchId " +
        " AND br.reservationStatus = :reservationStatus " + 
        " AND br.deskEndDt = DATE_ADD(curdate(), INTERVAL -1 DAY) " +
        " AND DATEDIFF(deskEndDt, deskStartDt) > 28 " +
        " AND br.useYn = 1 ";
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("reservationStatus", 20);
        args.put("useYn", Constants.USE);

        return (List<Integer>) jdbcTemplate.queryForList(s, args, Integer.class);


      }
    
    //신규
    public List<Reservation> selectReservationNewList(String branchId) {

    	String s = " SELECT * FROM ( " +
    			" select br.* " +
				" from branch_reservation br " +
			    " join branch_pay bp on ( bp.orderId = br.orderId and ( bp.payAmount > 0  or bp.payAmount < 0) and bp.payStateType = 10 AND bp.payType <> 3 and bp.payDt = DATE_ADD(curdate(), INTERVAL -1 DAY) and bp.useYn = :useYn) " +
			    " where br.branchId = :branchId " +
			    " and br.reservationStatus != :reservationStatus " + 
			    " and br.useYn = :useYn " +
			    " group by br.reservationId " +
			    " HAVING count(br.reservationId) < 2 " +
			    " order by br.insertDt desc " +							
			    " ) test1 " +
			    " group by orderId " +
			    " HAVING count(orderId) < 2 ";
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("reservationStatus", 90);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Reservation.class));


      }
    
    /***********************AppAdmin*****************************************/
    
    public List<SearchMember> selectAppUserSearchList(String branchId, String name, Integer roomType, Integer num) {
        String s = " SELECT r.memberId, r.reservationDt, " +
        		" (SELECT name FROM branch_member bm WHERE bm.memberId = r.memberId) AS name, " +
        		" (SELECT no FROM branch_member bm WHERE bm.memberId = r.memberId) AS no, " +
        		" brm.name AS roomName, bd.name AS deskName " +
                " FROM branch_reservation r " +
                " JOIN branch_desk bd on r.deskId = bd.deskId ";
                if(!StringUtils.isEmpty(roomType)) {
                	s += " JOIN branch_room brm on ( bd.roomId = brm.roomId AND brm.roomType = :roomType )";
                } else { 
                	s += " JOIN branch_room brm on ( bd.roomId = brm.roomId )";
                }
                s += " WHERE r.branchId = :branchId ";      
                
        		if(!StringUtils.isEmpty(name)) {
                	s += " AND memberId IN  (SELECT memberId FROM branch_member WHERE name LIKE :name AND useYn = :useYn "
					                	  + " UNION "
										  + " SELECT abm.memberId AS memberId " 
										  + " FROM app_student_member asm "
										  + " JOIN app_branch_manager abm ON ( abm.no = asm.no AND abm.useYn = :useYn ) "
										  + " WHERE asm.studentName LIKE :name "
										  + " AND asm.useYn = :useYn "
										  + " ) ";                	                
                	
                } else {
                	s += " AND memberId IN  (SELECT memberId FROM branch_member WHERE useYn = :useYn "
			                			  + " UNION "
										  + " SELECT abm.memberId AS memberId " 
										  + " FROM app_student_member asm "
										  + " JOIN app_branch_manager abm ON ( abm.no = asm.no AND abm.useYn = :useYn ) "
										  + " WHERE asm.studentName LIKE :name "
										  + " AND asm.useYn = :useYn "
										  + " ) ";     
                }
                
        		s += " AND r.reservationStatus = :reservationStatus " +

        		" AND ( CASE " +
        		" WHEN CURDATE() = r.deskStartDt THEN ( CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt ) AND CURTIME() >= r.deskStartTm " +
        		" ELSE CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt " +
        		" END ) " +

                " AND (r.deskId != null or r.deskId != '') " +
                " AND r.useYn = :useYn ";
        		
 
        
        
        
        if(!StringUtils.isEmpty(num)) {
        	s += " LIMIT :num, 10 ";
        }

        //s += " ORDER BY r.reservationDt DESC, r.reservationTm DESC ";
        
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("name", '%' + name + '%');
        //args.put("reservationStatus", Constants.ReservationStatusType.CONFIRMED.getValue());
        args.put("reservationStatus", 20);
        args.put("useYn", Constants.USE);
        args.put("roomType", roomType);
        args.put("num", num);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(SearchMember.class));

    }
    
    public AppAdminMember selectAppUserInfo(String branchId, String memberId) {
        String s = " SELECT bm.no AS no, bm.name AS name, brm.name AS roomName, bd.name AS deskName, " +
        		" bm.tel AS tel, bm.telParent AS telParent, bm.telEtc AS telEtc, sa.enterexitYes AS enterexitYes, sa.smsYes AS smsYes, " +
        		" bm.gender AS gender, bm.cabinet AS cabinet, bm.school AS school, bm.schoolGrade AS schoolGrade, bm.address1 AS address1, " +
        		" bm.memberNote AS memberNote, bm.examType AS examType " +
                " FROM branch_reservation r " +
                " JOIN branch_member bm ON (bm.branchId = r.branchId AND bm.memberId = r.memberId AND bm.useYn = :useYn) " +
                " JOIN branch_desk bd on r.deskId = bd.deskId " +
                " JOIN branch_room brm on bd.roomId = brm.roomId " +
                " JOIN sms_approve sa on r.memberId = sa.memberId " +
                " WHERE r.branchId = :branchId " +
                " AND r.memberId = :memberId " +
                " AND r.reservationStatus = :reservationStatus " +

        		" AND ( CASE " +
        		" WHEN CURDATE() = r.deskStartDt THEN ( CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt ) AND CURTIME() >= r.deskStartTm " +
        		" ELSE CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt " +
        		" END ) " +

                " AND (r.deskId != null or r.deskId != '') " +
                " AND r.useYn = :useYn ";
        	// App 회원 추가
        	s += " UNION " +
    			" SELECT asm.no AS no, asm.studentName AS name, brm.name AS roomName, bd.name AS deskName, " +
        		" asm.tel AS tel, asm.telParent AS telParent, asm.telParent AS telEtc, sa.enterexitYes AS enterexitYes, sa.smsYes AS smsYes, " +
        		" asm.gender AS gender, asm.cabinet AS cabinet, asm.cabinet AS school, asm.cabinet AS schoolGrade, asm.address1 AS address1, " +
        		" asm.cabinet AS memberNote, asm.cabinet AS examType " +
                " FROM branch_reservation r " +
                " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = :useYn) " +
                " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
                " JOIN branch_desk bd on r.deskId = bd.deskId " +
                " JOIN branch_room brm on bd.roomId = brm.roomId " +
                " JOIN sms_approve sa on r.memberId = sa.memberId " +
                " WHERE r.branchId = :branchId " +
                " AND r.memberId = :memberId " +
                " AND r.reservationStatus = :reservationStatus " +

        		" AND ( CASE " +
        		" WHEN CURDATE() = r.deskStartDt THEN ( CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt ) AND CURTIME() >= r.deskStartTm " +
        		" ELSE CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt " +
        		" END ) " +

                " AND (r.deskId != null or r.deskId != '') " +
                " AND r.useYn = :useYn ";
        
        
        
        
        
        s += "LIMIT 1";

        //s += " ORDER BY r.reservationDt DESC, r.reservationTm DESC ";
        
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("reservationStatus", 20);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppAdminMember.class));        
    }
    
    public List<RegisterReservation> selectAppRegisterReservationList(String branchId, String memberId) { 
	    String s = " SELECT r.reservationStatus, r.deskStartDt, r.deskEndDt, r.reservationNote " +
	            " FROM branch_reservation r " +
	            " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
	            " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = :useYn) " +
	            " WHERE r.branchId = :branchId " +
	            " AND r.memberId = :memberId " +
	            " AND r.reservationStatus != :reservationStatus " +
	            " AND (r.deskId != null or r.deskId != '') " +
	            " AND r.useYn = :useYn ";
	    
	       s += " UNION " +
    			" SELECT r.reservationStatus, r.deskStartDt, r.deskEndDt, r.reservationNote " +
	            " FROM branch_reservation r " +
	            " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = :useYn) " +
	            " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = :useYn) " +
                " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
	            " WHERE r.branchId = :branchId " +
	            " AND r.memberId = :memberId " +
	            " AND r.reservationStatus != :reservationStatus " +
	            " AND (r.deskId != null or r.deskId != '') " +
	            " AND r.useYn = :useYn ";
	    
	    
        
	    Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("reservationStatus", 90);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(RegisterReservation.class));     
    
    }
    
    public List<DeskRatio> selectAppRegisterRoomTypeCountList(String branchId) {
    	 String s = " SELECT distinct brm.roomType AS roomType, count(bd.deskType) AS count" +
    			 	" FROM branch_reservation r " +
         " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = 1) " +
         " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = 1)" +
		 " JOIN branch_desk bd on r.deskId = bd.deskId " +
         " JOIN branch_room brm on bd.roomId = brm.roomId " + 
         " WHERE r.branchId = :branchId " +        
         " AND r.reservationStatus = :reservationStatus " +
         " AND ( CASE " +
 					 " WHEN CURDATE() = r.deskStartDt THEN ( CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt ) AND CURTIME() >= r.deskStartTm " +
 					 " ELSE CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt " +
 					 " END ) " +
 		 " AND (r.deskId != null or r.deskId != '') " +
         " AND r.useYn = :useYn " ;
         
         s += " UNION " +
        	  " SELECT distinct brm.roomType AS roomType, count(bd.deskType) AS count" +
 			  " FROM branch_reservation r " +
 			  " LEFT JOIN branch_order o ON (o.branchId = r.branchId AND o.orderId = r.orderId AND o.useYn = 1) " +
 			  " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = :useYn) " +
              " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
 			  " JOIN branch_desk bd on r.deskId = bd.deskId " +
 			  " JOIN branch_room brm on bd.roomId = brm.roomId " + 
 			  " WHERE r.branchId = :branchId " +        
 			  " AND r.reservationStatus = :reservationStatus " +
 			  " AND ( CASE " +
					 " WHEN CURDATE() = r.deskStartDt THEN ( CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt ) AND CURTIME() >= r.deskStartTm " +
					 " ELSE CURDATE() BETWEEN r.deskStartDt AND r.deskEndDt " +
					 " END ) " +
			  " AND (r.deskId != null or r.deskId != '') " +
			  " AND r.useYn = :useYn " +
		 
         
         " group by roomType ";
    	 
    	 Map<String, Object> args = new HashMap<>();
         args.put("branchId", branchId);
         args.put("reservationStatus", 20);
         args.put("useYn", Constants.USE);

         return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(DeskRatio.class));   
    	 
    }
}
