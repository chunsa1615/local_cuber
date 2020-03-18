package kr.co.cntt.scc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.model.FreeApplication;
import kr.co.cntt.scc.model.Page;
import kr.co.cntt.scc.model.PageMaker;
import kr.co.cntt.scc.model.SeatReservation;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.util.DateUtil;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;

/**
 * Reservation 예약
 *
 * Created by jslivane on 2016. 6. 14..
 */
@Service
@Transactional
@Slf4j
public class BranchSeatReservationService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;

    /*******************************************************************************/      
    /**
     * (지점의) 무료 신청서 조회(관리자 페이지)
     * @param branchId
     * @param todayDt
     * @return
     */
    
    public List<SeatReservation> selectSeatReservationListToday(String branchId, String no, Integer role, Integer type) {
    	 
    	String s = " SELECT id, applicationId, appId, branchId, startDt, roomType, role, name, IFNULL(gender, 0) AS gender, birthDt, tel,  " +
                 " email, status , type, insertDt, updateDt " +
                 " FROM app_seat_application " +
                 " WHERE branchId = :branchId " +
                 " AND no = :no " +
                 " AND role = :role " +
                 " AND type = :type " +
                 " AND ( status = :status OR status = :status2 ) " +
                 " AND startDt = CURRENT_DATE() " +
                 " AND useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("no", no);
        args.put("role", role);
        args.put("type", type);
        args.put("status", 10);
        args.put("status2", 20);
        args.put("useYn", Constants.USE);

        
        try {
	    	
        	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(SeatReservation.class));
            
        } catch (EmptyResultDataAccessException e) {
        	
            return null;
        }
        
    }
    
    //중복체크
    public List<SeatReservation> selectSeatReservationListDupl(String branchId, String no, Integer role, Integer type) {
   	 
    	String s = " SELECT id, applicationId, appId, branchId, startDt, roomType, role, name, IFNULL(gender, 0) AS gender, birthDt, tel,  " +
                 " email, status , type, insertDt, updateDt " +
                 " FROM app_seat_application " +
                 " WHERE branchId = :branchId " +
                 " AND no = :no " +
                 " AND role = :role " +
                 " AND type = :type " +
                 " AND status = :status " +
                 " AND useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("no", no);
        args.put("role", role);
        args.put("type", type);
        args.put("status", 20);
        args.put("useYn", Constants.USE);

        
        try {
	    	
        	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(SeatReservation.class));
            
        } catch (EmptyResultDataAccessException e) {
        	
            return null;
        }
        
    }
    
    
    
    public List<SeatReservation> selectSeatReservationList(String branchId, String seatReservationStartDt, String seatReservationEndDt, String memberId, String tel, Integer roomType, Page page) {
    	
        String s = " SELECT id, applicationId, appId, branchId, startDt, roomType, role, name, IFNULL(gender, 0) AS gender, birthDt, tel  " +
                "  ,email, status , type, insertDt, updateDt ";
                
                if(!StringUtils.isEmpty(memberId)) {
                    //s += " AND memberId = :memberId ";
                	//s += " AND a.name like "+sName+"";
                    s += " , (SELECT memberId FROM app_branch_manager WHERE memberId = :memberId) AS memberId ";

                }
                
                s += " FROM app_seat_application " +
                " WHERE branchId = :branchId " +                
                " AND useYn = :useYn ";

        if(!StringUtils.isEmpty(seatReservationStartDt)) {
            s += " AND startDt >= :seatReservationStartDt";
        	//s += " AND a.startDt >= "+sFreeApplicationStartDt+"";

        }
        
        if(!StringUtils.isEmpty(seatReservationEndDt)) {
            s += " AND startDt <= :seatReservationEndDt";
        	//s += " AND a.startDt <= "+sFreeApplicationEndDt+"";

        }
        
        if(!StringUtils.isEmpty(tel)) {
        	s += " AND tel like :tel";
        	//s += " AND a.tel like "+sTel+"";

        }

        if(!StringUtils.isEmpty(roomType)) {
        	s += " AND roomType = :roomType";
        	//s += " AND a.tel like "+sTel+"";

        }
        
        s += " ORDER BY startDt desc, insertDt asc ";

        if(page == null) {}
        else {
	        int perPage = (page.getPage() -1) * page.getPerPageNum();
	    	s += " limit "+perPage+", "+page.getPerPageNum()+" ";
        }
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);        
        args.put("memberId", memberId);
        args.put("useYn", Constants.USE);
        args.put("seatReservationStartDt", seatReservationStartDt);
        args.put("seatReservationEndDt", seatReservationEndDt);
        args.put("tel", "%" + tel + "%");
        args.put("roomType", roomType);
        
        try {
	    	
        	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(SeatReservation.class));
            
        } catch (EmptyResultDataAccessException e) {
        	
            return null;
        }
        

    }
    

	public List<SeatReservation> selectSeatReservationListCount(String branchId, String seatReservationStartDt, String seatReservationEndDt, String memberId, String tel, Integer roomType, Page page) {
		
		   String s = " SELECT id, applicationId, appId, branchId, startDt, roomType, role, name, IFNULL(gender, 0) AS gender, birthDt, tel  " +
	                "  ,email, status , type, insertDt, updateDt " ;
	                
	                if(!StringUtils.isEmpty(memberId)) {
	                    //s += " AND memberId = :memberId ";
	                	//s += " AND a.name like "+sName+"";
	                    s += " , (SELECT memberId FROM app_branch_manager WHERE memberId = :memberId) AS memberId ";

	                }
	                
	                s += " FROM app_seat_application " +
	                " WHERE branchId = :branchId " +
	                " AND useYn = :useYn ";

	        if(!StringUtils.isEmpty(seatReservationStartDt)) {
	            s += " AND startDt >= :seatReservationStartDt";
	        	//s += " AND a.startDt >= "+sFreeApplicationStartDt+"";

	        }
	        
	        if(!StringUtils.isEmpty(seatReservationEndDt)) {
	            s += " AND startDt <= :seatReservationEndDt";
	        	//s += " AND a.startDt <= "+sFreeApplicationEndDt+"";

	        }	        
	        
	        if(!StringUtils.isEmpty(tel)) {
	        	s += " AND tel like :tel";
	        	//s += " AND a.tel like "+sTel+"";

	        }

	        if(!StringUtils.isEmpty(roomType)) {
	        	s += " AND roomType = :roomType";
	        	//s += " AND a.tel like "+sTel+"";

	        }
	        
	        s += " ORDER BY startDt desc, insertDt asc ";

	        if(page == null) {}
	        else {
		        int perPage = (page.getPage() -1) * page.getPerPageNum();
		    	s += " limit "+perPage+", "+page.getPerPageNum()+" ";
	        }
	        
	        Map<String, Object> args = new HashMap<>();
	        args.put("branchId", branchId);
	        args.put("useYn", Constants.USE);
	        args.put("seatReservationStartDt", seatReservationStartDt);
	        args.put("seatReservationEndDt", seatReservationEndDt);
	        args.put("memberId", memberId);
	        args.put("tel", "%" + tel + "%");
	        args.put("roomType", roomType);
	        
	        
	        try {
		    	
	        	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(SeatReservation.class));
	            
	        } catch (EmptyResultDataAccessException e) {
	        	
	            return null;
	        }
	        
	}
    
	public List<SeatReservation> selectSeatReservationList(String branchId) {
		
	    String s = " SELECT id, applicationId, appId, branchId, startDt, roomType, role, name, IFNULL(gender, 0) AS gender, birthDt, tel  " +
                "  ,email, status , type, insertDt, updateDt " +
                
                " FROM app_seat_application " +
                " WHERE branchId = :branchId " +     
          //      " AND startDt = :todayDt " +
                " AND useYn = :useYn " +
	    		" ORDER BY startDt desc, insertDt asc ";
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        //args.put("todayDt", DateUtil.getCurrentDateString());
        args.put("useYn", Constants.USE);
        
        try {
	    	
        	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(SeatReservation.class));
            
        } catch (EmptyResultDataAccessException e) {
        	
            return null;
        }
        
	}

	public int insertedSeatReservation(SeatReservation seatReservation) {
		
		String s = " INSERT INTO app_seat_application " + 
				   " ( applicationId, appId, branchId, startDt, roomType, role, name, gender, birthDt, tel, email, status, type, useYn) VALUES " +
				   " ( :applicationId, :appId, :branchId, :startDt, :roomType, :role, :name, :gender, :birthDt, :tel, :email, 20 , :type, :useYn )";
		  
	        CombinedSqlParameterSource source = new CombinedSqlParameterSource(seatReservation);
	        // source.addValue("branchId", freeApplication.getBranchId());
	        source.addValue("useYn", Constants.USE);
	        
	        int result = jdbcTemplate.update(s, source);
	        
	        if(result == 0) {
	        	throw new InternalServerError("Failed to insert seatReservation");
	        }
	        
	        return result;
	}


	public SeatReservation  selectSeatReservation(String branchId, String applicationId) {
		
		String s = 
				" SELECT id, applicationId, appId, branchId, startDt, roomType, role, name, IFNULL(gender, 0) AS gender, birthDt, tel  " +
                " ,email, status , type, insertDt, updateDt FROM app_seat_application " + 
                " WHERE branchId = :branchId " +
                " AND useYn = :useYn " +
                " AND applicationId = :applicationId " + 
                " ORDER BY startDt desc, insertDt asc ";
		
		
	      	Map<String, Object> args = new HashMap<>();
	      	
	        args.put("branchId", branchId);
	        args.put("applicationId", applicationId);
	        args.put("useYn", Constants.USE);

	        return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(SeatReservation.class));
	}


	public int updateSeatReservation(String applicationId, SeatReservation seatReservation) {
		
		String s = 
				" UPDATE app_seat_application SET  " +
				"startDt = :startDt, name = :name, role= :role, type=:type, roomType = :roomType, gender = :gender," +
        		"  tel = :tel, email = :email, status = :status " +
        		" WHERE branchId = :branchId AND applicationId = :applicationId ";
			
		 	CombinedSqlParameterSource source = new CombinedSqlParameterSource(seatReservation);
	        
	        source.addValue("branchId", seatReservation.getBranchId());
	        source.addValue("applicationId", applicationId);
	        
	        int result = jdbcTemplate.update(s, source);
	        
	        if(result == 0) {
	        	throw new InternalServerError("Failed to update seatReservation");
	        }
	        
	        return result;
	}


	public int deleteSeatReservation(String branchId, String applicationId, SeatReservation seatReservation) {
		
		  String s = " UPDATE app_seat_application SET " +
	                " useYn = :useYn, updateDt = NOW() " +
	                " WHERE branchId = :branchId " +
	                " AND applicationId = :applicationId ";

	        Map<String, Object> args = new HashMap<>();
	        args.put("useYn", Constants.NOT_USE);
	        args.put("branchId", branchId);
	        args.put("applicationId", applicationId);
	        
	        int result = jdbcTemplate.update(s, args);

	        if (result == 0) {
	            throw new InternalServerError("Failed to delete seatReservation");

	        } else {}

	        return result;

	}
}
