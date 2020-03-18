package kr.co.cntt.scc.app.student.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.app.student.model.AppClientBranch;
import kr.co.cntt.scc.app.student.model.ReservationBranch;
import kr.co.cntt.scc.app.student.model.join;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.service.HistoryService;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class AppClientBranchService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;
    
    public List<AppClientBranch.CenterList> selectAppClientBranch(String no) {
    	String s = "SELECT abm.branchId AS branchId, b.name AS branchNm " 
    			+ " FROM app_branch_manager abm "
    			+ " JOIN branch b ON (abm.branchId = b.branchId AND b.useYn = :useYn)"
    			+ " WHERE abm.no = :no "
    			+ " AND abm.useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("no", no);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientBranch.CenterList.class));
    }
    
    
    //no와 branchId를 이용한 나의 센터목록
    public List<AppClientBranch.CenterList> selectAppClientBranchBybranchId(String no, String branchId) {
    	String s = "SELECT abm.branchId AS branchId, abm.memberId AS memberId, b.name AS branchNm " 
    			+ " FROM app_branch_manager abm "
    			+ " JOIN branch b ON (abm.branchId = b.branchId AND b.useYn = :useYn)"
    			+ " WHERE abm.no = :no "
    			+ " AND abm.branchId = :branchId "
    			+ " AND abm.useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("no", no);
    	args.put("branchId", branchId);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientBranch.CenterList.class));
    }
    
    
    
    //main센터 변경/선택 시, app_branch_manager에 데이터 추가
    public int insertAppBranchManager(String appId, String no, String memberId, String branchId) {
        String s = " INSERT INTO app_branch_manager ( "
                + " appId, no, memberId, branchId "
                + " ) VALUES ( "
                + " :appId, :no, :memberId, :branchId "
                + " ) ";        
        
    	Map<String, Object> args = new HashMap<>();    	
    	args.put("appId", appId);
    	args.put("no", no);    	
    	args.put("memberId", memberId);
    	args.put("branchId", branchId);
    	    	

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to create AppStudent");

        } else {
//            History history = new History(branchId, Constants.HistoryType.PAY_CREATE, pay.toString());
//            history.setOrderId(pay.getOrderId());
//            history.setPayId(payId);
//            history.setMemberId(pay.getMemberId());
//            historyService.insertHistory(history);

        }

        return result;

    }    
    
    public List<AppClientBranch.CenterList> selectBranchList() {

        String s = " SELECT b.branchId, b.name AS branchNm " +
                " FROM branch b " +
                " WHERE b.branchId NOT IN (:TEST_BRANCHID) " +
                " AND b.useYn = :useYn " +
        		" AND b.visibleYn = :visibleYn " +
                " AND b.branchType = :branchType" +
        		" ORDER BY openDt asc ";


        Map<String, Object> args = new HashMap<>();
        args.put("useYn", Constants.USE);        
        args.put("branchType", 10);
        args.put("visibleYn", Constants.USE);
        args.put("TEST_BRANCHID", Constants.TEST_BRANCHID);
        
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientBranch.CenterList.class));

    }
    
    public List<ReservationBranch> selectBranchReservationYn(String branchId) {
    	String s = "SELECT b.branchId AS branchId, b.name AS branchNm, b.reservationYn, b.multiYn, b.singleYn, b.privateYn " 
    			+ " FROM branch b "
    			+ " WHERE b.branchId = :branchId "
    			+ " AND b.branchId NOT IN (:TEST_BRANCHID) "
    			+ " AND b.useYn = :useYn "
    			+ " AND b.visibleYn = :visibleYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("branchId", branchId);
    	args.put("useYn", Constants.USE);
    	args.put("visibleYn", Constants.USE);
    	args.put("TEST_BRANCHID", Constants.TEST_BRANCHID);
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(ReservationBranch.class));
    }
    
    
    // api /reservation 을 위한 select
    public List<AppClientBranch.Branch> selectBranchListForReservation(String branchId) {
    	String s = "SELECT b.branchId as branchId, b.name AS branchNm, b.address1, b.openDt, b.tel, b.weekdayOpen, b.weekendOpen, "
    			+ " b.reservationYn, b.multiYn, b.singleYn, b.privateYn " 
    			+ " FROM branch b "
    			+ " WHERE b.branchId = :branchId "
    			+ " AND b.branchId NOT IN (:TEST_BRANCHID) "
    			+ " AND b.useYn = :useYn "
    			+ " AND b.visibleYn = :visibleYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("branchId", branchId);
    	args.put("useYn", Constants.USE);
    	args.put("visibleYn", Constants.USE);
    	args.put("visibleYn", Constants.USE);
    	args.put("TEST_BRANCHID", Constants.TEST_BRANCHID);
    	
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientBranch.Branch.class));
    }
    
    // api /reservation 을 위한 센터리스트
    public List<AppClientBranch.CenterList> selectCenterListForReservation() {
    	String s = "SELECT b.name AS branchNm, b.branchId, b.reservationYn AS registYn " 
    			+ " FROM branch b "
    			+ " WHERE 1=1 "
    			+ " AND b.branchId NOT IN (:TEST_BRANCHID) "
    			+ " AND b.useYn = :useYn "
    			+ " AND b.visibleYn = :visibleYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("useYn", Constants.USE);
    	args.put("visibleYn", Constants.USE);
    	args.put("TEST_BRANCHID", Constants.TEST_BRANCHID);
    	    	
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientBranch.CenterList.class));
    }
    
    
}
