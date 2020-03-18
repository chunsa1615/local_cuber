package kr.co.cntt.biostar.service;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.alimTalk.TbAlimtalkTemplate;
import kr.co.cntt.scc.model.BranchDesign;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.model.Reservation;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.model.Desk;
import kr.co.cntt.scc.model.Room;
import kr.co.cntt.scc.model.User;
import kr.co.cntt.scc.service.HistoryService;
import kr.co.cntt.scc.util.DateUtil;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jslivane on 2016. 4. 5..
 */
@Service
@Transactional
@Slf4j
public class BiostarService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;
   
    @Autowired
    HistoryService historyService;
    
    public int insertUser(String branchId, String cookie) {
    	
    	
        String s = " INSERT INTO biostar_cookie ( "
        		+ " branchId, cookie, insertDt, updateDt ) " 
        		+ " VALUES ('', '', now(), now() ) " 
                + " ON DUPLICATE KEY UPDATE "
                + " branchId = :branchId, cookie = '' ";
       
        Map<String, Object> args = new HashMap<>();
    	args.put("branchId", branchId);
    	args.put("cookie", cookie);
                
        
        
        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to create biostarCookie");

        } else {
            //History history = new History(null, Constants.HistoryType.USER_CREATE, user.toString());
            //history.setUserId(userId);
            //historyService.insertHistory(history);

            
        }

        return result;

    }
	
}
