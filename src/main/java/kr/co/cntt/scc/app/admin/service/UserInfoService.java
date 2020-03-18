package kr.co.cntt.scc.app.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.model.ApiResponse;
import kr.co.cntt.scc.model.Desk;
import kr.co.cntt.scc.model.Entry;
import kr.co.cntt.scc.model.Pay;
import kr.co.cntt.scc.model.Reservation;
import kr.co.cntt.scc.model.Entry.Response;
import kr.co.cntt.scc.service.BranchPayService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class UserInfoService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;
    
    public List<Pay> selectUserInfo(String branchId) {
    	String s = " SELECT u.branchId, u.userId, u.password, u.uuid, u.os, u.version, u.device, u.pushId, " +
                " u.useYn, u.insertDt, u.updateDt " +
                " FROM app_admin_userinfo u " +
                " WHERE p.branchId = :branchId AND p.orderId = :orderId AND p.useYn = :useYn ";
    	
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        //args.put("orderId", orderId);
        args.put("useYn", Constants.USE);
        
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Pay.class));
    }
    

}
