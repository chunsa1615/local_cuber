package kr.co.cntt.scc.app.student.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.app.student.model.AppClientMember;
import kr.co.cntt.scc.service.HistoryService;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class SeatApplicationService {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;
    
    public int insertSeatApplication(String applicationId, String branchId, Integer type, Integer roomType, String startDt, AppClientMember appClientMember, Integer status) {
        String s = " INSERT INTO app_seat_application ( " +
                " applicationId, branchId, type, roomType, startDt, appId, no, role, name, gender, birthDt, tel, email, status " +
                " ) VALUES ( " +
                " :applicationId, :branchId, :type, :roomType, :startDt, :appId, :no, :role, :name, :gender, :birthDt, :tel, :email, :status " +
                " ) ";
                
        
        CombinedSqlParameterSource source = new CombinedSqlParameterSource(appClientMember);
        source.addValue("applicationId", applicationId);
        source.addValue("branchId", branchId);        
        source.addValue("type", type);
        source.addValue("roomType", roomType);
        source.addValue("startDt", startDt);
        source.addValue("status", status);
        
    	int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to create SeatApplication");

        } else {
//            History history = new History(branchId, Constants.HistoryType.PAY_CREATE, pay.toString());
//            history.setOrderId(pay.getOrderId());
//            history.setPayId(payId);
//            history.setMemberId(pay.getMemberId());
//            historyService.insertHistory(history);

        }

        return result;
        
    }
    
    
    public int schedulerSeatApplication() {
        String s = " UPDATE app_seat_application " + 
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

