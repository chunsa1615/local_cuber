package kr.co.cntt.scc.service;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.model.Notification;
import kr.co.cntt.scc.model.Page;
import kr.co.cntt.scc.util.AuthUtil;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 시스템 사용이력 서비스
 * Created by jslivane on 2016. 7. 7..
 */
@Service
@Slf4j
public class BranchNotificationService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    /*******************************************************************************/
 
    /**
     * 알림 목록 조회
     * @param branchId
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Notification> selectNotificationList(String branchId, String startDate,
                                String endDate, String memberId, Page page) {
        String s = " SELECT DISTINCT s.id, s.branchId, s.smsDt, s.userId, " +
        		" s.fromNumber, s.toNumber, s.msg, s.resultCode, s.cmid " +
                " FROM history_sms s " +
                " JOIN branch_member m ON (m.branchId = s.branchId AND m.memberId = s.userId AND m.useYn = :useYn) " +
                " WHERE s.branchId = :branchId " +
                " AND SUBSTRING(s.smsDt, 1, 10) >= :startDate AND SUBSTRING(s.smsDt, 1, 10) <= :endDate ";
        		if(!StringUtils.isEmpty(memberId)) {
        			s += " AND s.userId = :memberId ";
        		}
                
                s += " UNION " +
            		 " SELECT DISTINCT s.id, s.branchId, s.smsDt, s.userId, " +
            		 " s.fromNumber, s.toNumber, s.msg, s.resultCode, s.cmid " +
                     " FROM history_sms s " +
                     " JOIN app_branch_manager abm ON (abm.branchId = s.branchId AND abm.memberId = s.userId AND abm.useYn = :useYn) " +
         	         " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
                     " WHERE s.branchId = :branchId " +
                     " AND SUBSTRING(s.smsDt, 1, 10) >= :startDate AND SUBSTRING(s.smsDt, 1, 10) <= :endDate ";
            		 if(!StringUtils.isEmpty(memberId)) {
            			 s += " AND s.userId = :memberId ";
            		 }
        		
        		
        		
        		
        		if(page == null) {}
                else {
        	        int perPage = (page.getPage() -1) * page.getPerPageNum();
        	    	s += " limit "+perPage+", "+page.getPerPageNum()+" ";
                }
                
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("startDate", startDate);
        args.put("endDate", endDate);
        args.put("useYn", Constants.USE);
        
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Notification.class) );

    }

    /*******************************************************************************/

}
