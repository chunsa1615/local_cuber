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
import kr.co.cntt.scc.model.Page;
import kr.co.cntt.scc.model.SafeService;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
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
public class BranchSafeServiceService {

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
    
    public List<SafeService> selectSafeServiceList(String branchId, String safeServiceStartDt, String safeServiceEndDt, String parentsName, String studentName, String parentsTel, String studentTel, Integer status, Page page) {
    	
        String s = " SELECT id, parentsAppId, parentsName, parentsNo, parentsId, studentId, studentAppId, studentName, "
        		+ " parentsTel, studentTel, studentNo, branchId, status, startDt, " +
                " insertDt, updateDt " +
                " FROM app_parents_safeservice " +
                " WHERE branchId = :branchId ";
               

        if(!StringUtils.isEmpty(safeServiceStartDt)) {
            s += " AND startDt >= :safeServiceStartDt";
        	//s += " AND a.startDt >= "+sFreeApplicationStartDt+"";

        }
        
        if(!StringUtils.isEmpty(safeServiceEndDt)) {
            s += " AND startDt <= :safeServiceEndDt";
        	//s += " AND a.startDt <= "+sFreeApplicationEndDt+"";

        }
        
        if(!StringUtils.isEmpty(parentsName)) {
            s += " AND parentsName like :parentsName";
        	//s += " AND a.name like "+sName+"";

        }

        if(!StringUtils.isEmpty(studentName)) {
            s += " AND studentName like :studentName";
        	//s += " AND a.name like "+sName+"";

        }
        
        
        if(!StringUtils.isEmpty(parentsTel)) {
        	s += " AND parentsTel like :parentsTel";

        }

        if(!StringUtils.isEmpty(studentTel)) {
        	s += " AND studentTel like :studentTel";
        }
        
        if(!StringUtils.isEmpty(status)) {
        	s += " AND status = :status";
        }
        
        s += " AND useYn = :useYn ";
        
        s += " ORDER BY startDt desc, insertDt asc ";

        if(page == null) {}
        else {
	        int perPage = (page.getPage() -1) * page.getPerPageNum();
	    	s += " limit "+perPage+", "+page.getPerPageNum()+" ";
        }
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);        
        args.put("safeServiceStartDt", safeServiceStartDt);
        args.put("safeServiceEndDt", safeServiceEndDt);
        args.put("parentsName", "%" + parentsName + "%");
        args.put("studentName", "%" + studentName + "%");
        args.put("parentsTel", "%" + parentsTel + "%");
        args.put("studentTel", "%" + studentTel + "%");
        
        args.put("status", status);
        args.put("useYn", Constants.USE);
        
        try {
	    	
        	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(SafeService.class));
            
        } catch (EmptyResultDataAccessException e) {
        	
            return null;
        }
        

    }
    

    public List<SafeService> selectSafeServiceList(String branchId) {
		
        String s = " SELECT id, parentsAppId, parentsName, parentsNo, studentId, studentAppId, studentName, "
        		+ " parentsTel, studentTel, studentNo, branchId, status, startDt, " 
                + " insertDt, updateDt " 
                + " FROM app_parents_safeservice "
                + " WHERE branchId = :branchId "
                + " AND useYn = :useYn "
	    		+ " ORDER BY startDt desc, insertDt asc ";
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        //args.put("todayDt", DateUtil.getCurrentDateString());
        args.put("useYn", Constants.USE);
        
        try {
	    	
        	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(SafeService.class));
            
        } catch (EmptyResultDataAccessException e) {
        	
            return null;
        }
        
	}
    
    //안심서비스 select (app에서 사용)
    public List<String> selectSafeServiceList(String no, Integer role, Integer status) {
        String s = " SELECT studentNo "
    			+ " FROM app_parents_safeservice ";
    			if(role == 10) {
    				s += " WHERE studentNo = :no";
    			} else if(role == 20) {
    				s += " WHERE parentsNo = :no";
    			}
    			
    			s += " AND status = :status " 
    			  + " AND useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("no", no);    	
    	args.put("role", role);
    	args.put("status", status);    	
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.queryForList(s, args, String.class);
    }

    //학부모 안심 서비스 insert
//    public int insertParentsSafeService(String parentsAppId, String parentsName, String parentsNo, String parentsId, String parentsTel, 
//    									String studentAppId, String studentName, String studentNo, String studentId, 
//    									String studentTel, String branchId, String startDt ) {
	
	public int insertParentsSafeService(SafeService safeService) {
        String s = " INSERT INTO app_parents_safeservice ( " +
                " parentsAppId, parentsName, parentsNo, parentsId, parentsTel,  " +
                " studentId, studentAppId, studentTel, studentName, studentNo, " +
                " branchId, startDt, status " +
                " ) VALUES ( " +
                " :parentsAppId, :parentsName, :parentsNo, :parentsId, :parentsTel,  " +
                " :studentId, :studentAppId, :studentTel, :studentName, :studentNo, " +
                " :branchId, :startDt, :status " +
                " ) ";
                
        
//    	Map<String, Object> args = new HashMap<>();
//    	args.put("parentsAppId", parentsAppId);
//    	args.put("parentsName", parentsName);    	
//    	args.put("parentsNo", parentsNo);
//    	args.put("parentsId", parentsId);    	
//    	args.put("parentsTel", parentsTel);
//    	
//    	args.put("studentAppId", studentAppId);
//    	args.put("studentName", studentName);
//    	args.put("studentNo", studentNo);
//    	args.put("studentId", studentId);
//    	args.put("studentTel", studentTel);
//    	args.put("branchId", branchId);
//    	args.put("startDt", startDt);
//    	args.put("status", 10);

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(safeService);
    	
        int result = jdbcTemplate.update(s, source);

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
    
    
    public List<SafeService> selectSafeServiceList(String branchId, String parentsAppId, String studentAppId) {
		
        String s = " SELECT id, parentsAppId, parentsName, parentsNo, studentId, studentAppId, studentName, "
        		+ " parentsTel, studentTel, studentNo, branchId, status, startDt, " 
                + " insertDt, updateDt " 
                + " FROM app_parents_safeservice "
                + " WHERE 1 = 1 "
                //+ " WHERE branchId = :branchId "
                + " AND parentsAppId = :parentsAppId "
                + " AND studentAppId = :studentAppId "
                + " AND status = :status "
                + " AND useYn = :useYn ";

        
        Map<String, Object> args = new HashMap<>();
        //args.put("branchId", branchId);
        args.put("status", 20);
        args.put("parentsAppId", parentsAppId);
        args.put("studentAppId", studentAppId);
        args.put("useYn", Constants.USE);
        
        try {
	    	
        	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(SafeService.class));
            
        } catch (EmptyResultDataAccessException e) {
        	
            return null;
        }
        
	}
    
    
    //오늘 날짜 기준 중복데이터 select
    public List<SafeService> selectSafeServiceListToday(String branchId, String parentsAppId, String studentAppId) {
		
        String s = " SELECT id, parentsAppId, parentsName, parentsNo, studentId, studentAppId, studentName, "
        		+ " parentsTel, studentTel, studentNo, branchId, status, startDt, " 
                + " insertDt, updateDt " 
                + " FROM app_parents_safeservice "
                + " WHERE 1 = 1 "
                //+ " WHERE branchId = :branchId "
                + " AND parentsAppId = :parentsAppId "
                + " AND studentAppId = :studentAppId "
                + " AND ( status = :status OR status = :status2 )"
                + " AND startDt = CURRENT_DATE() "
                + " AND useYn = :useYn ";

        
        Map<String, Object> args = new HashMap<>();
        //args.put("branchId", branchId);
        args.put("status", 10);
        args.put("status2", 20);
        args.put("parentsAppId", parentsAppId);
        args.put("studentAppId", studentAppId);
        args.put("useYn", Constants.USE);
        
        try {
	    	
        	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(SafeService.class));
            
        } catch (EmptyResultDataAccessException e) {
        	
            return null;
        }
        
	}
    
    
    
    //안심서비스 수정
    public int updateSafeService(String branchId, SafeService safeService) {
    	String s = " UPDATE app_parents_safeservice SET "
                 + " status = :status, startDt = :startDt, updateDt = NOW() "
        		 + " WHERE branchId = :branchId "
        		 + " AND parentsAppId = :parentsAppId " 
    			 + " AND studentAppId = :studentAppId ";
                
    			 s += " AND useYn = :useYn ";
                
        
    	CombinedSqlParameterSource source = new CombinedSqlParameterSource(safeService);
    	source.addValue("useYn", Constants.USE);

        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to create UpdateSafeService");

        } else {
//            History history = new History(branchId, Constants.HistoryType.PAY_CREATE, pay.toString());
//            history.setOrderId(pay.getOrderId());
//            history.setPayId(payId);
//            history.setMemberId(pay.getMemberId());
//            historyService.insertHistory(history);

        }

        return result;
    }
    

    //안심서비스 정보변경
    public int updateSafeService(String no, Integer role, String tel) {
    	String s = " UPDATE app_parents_safeservice SET "
                 + " updateDt = NOW(),  ";
        		//+ " branchId = :branchId, memberId = :memberId " 
    			if(role == 10) {
    				s += " studentTel = :tel "
    				   + " WHERE studentNo = :no";
    			} else if(role == 20) {
    				s += " parentsTel = :tel "
    				   + " WHERE parentsNo = :no";
    			}
                
    			s += " AND useYn = :useYn ";
                
        
    	Map<String, Object> args = new HashMap<>();
    	args.put("no", no);
    	args.put("role", role);
    	args.put("tel", tel);
    	args.put("useYn", Constants.USE);

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
    
    
    
    
    //안심서비스 삭제
    public int deleteSafeService(String no, Integer role) {
    	String s = " UPDATE app_parents_safeservice SET "
                + " useYn = :notUseYn, updateDt = NOW() ";
        		//+ " branchId = :branchId, memberId = :memberId " 
    			if(role == 10) {
    				s += " WHERE studentNo = :no";
    			} else if(role == 20) {
    				s += " WHERE parentsNo = :no";
    			}
                s += " AND useYn = :useYn ";
                
        
    	Map<String, Object> args = new HashMap<>();
    	args.put("no", no);
    	args.put("role", role);
    	args.put("notUseYn", Constants.NOT_USE);
    	args.put("useYn", Constants.USE);

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
    
    
    
    public int schedulerParentsSafeservice() {
        String s = " UPDATE app_parents_safeservice " + 
				   " SET	status = 100 " +
				   " WHERE	startDt < CURRENT_DATE " +
				   " AND	status = 10 " +
				   " AND	useYn = :useYn ";
						   
                        
    	Map<String, Object> args = new HashMap<>();
    	args.put("useYn", Constants.USE);
    	
        
    	int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            //throw new InternalServerError("Failed to Scheuler SeatApplication");

        } else {
//            History history = new History(branchId, Constants.HistoryType.PAY_CREATE, pay.toString());
//            history.setOrderId(pay.getOrderId());
//            history.setPayId(payId);
//            history.setMemberId(pay.getMemberId());
//            historyService.insertHistory(history);

        }

        return result;
        
    }
}
