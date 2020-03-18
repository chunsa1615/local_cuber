package kr.co.cntt.scc.app.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.cntt.scc.app.admin.model.AppClientMember;
import kr.co.cntt.scc.app.admin.model.AppClientUserInfo;
import kr.co.cntt.scc.app.admin.model.smsCertify;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class AppSmsCertify {
	
	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;
	
	// TB:history_sms - 매장명, 매정 전화번호
	public List<Branch> getBranchInfo(String branchId) {
		
		String s =  " SELECT * FROM branch " +
					" WHERE branchId = :branchId " ;
		
		Map<String, Object> args = new HashMap<>();
		args.put("branchId", branchId);
		
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Branch.class));
	}
	
	// TB:sms_certify 전화번호 중복여부 (중복이면 UPDATE, 아니면 INSERT)
	public String chkTel(String tel) {
		
		String id = " SELECT id FROM sms_certify WHERE tel=:tel ";
		
		Map<String, Object> args = new HashMap<>();
		args.put("tel", tel);
		
	    try {
	    	
            return jdbcTemplate.queryForObject(id, args, String.class);
            
        } catch (EmptyResultDataAccessException e) {
        	
            return null;
        }
		
	}
	
	// TB:sms_certify - 신규 인증번호 등록 INSERT 키 중복 시 업데이트
	public int insertSmsDB(String tel, String sendAuthNum, String description, int type) {
		
				String s = " INSERT INTO sms_certify ( tel, authNum, description, type) " +
						   " VALUES ( :tel, :authNum, :description, :type) ON DUPLICATE KEY UPDATE " + 
						   " tel=:tel, authNum=:authNum, description=:description, type=:type, updateDt=now() ";
				Map<String, Object> args = new HashMap<>();
				
				args.put("tel", tel);
				args.put("authNum", sendAuthNum);
				args.put("description", description);
				args.put("type", type);
		
			int result = jdbcTemplate.update(s, args);	
			
			 if (result == 0) {
			     throw new InternalServerError("Failed to update user");
			
			 } else {}
			
			 return result;
			 
	}

	// TB:sms_certify - 기존 번호의 row에 UPDATE
	public void updateSmsDB(String idResult, String authNum, String description, Integer type) {
		
		String s = " UPDATE sms_certify SET authNum = :authNum, description=:description, type=:type , updateDt = now() where id = :id ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("id", idResult);
		args.put("authNum", authNum);
		args.put("description", description);
		args.put("type", type);
		
		jdbcTemplate.update(s, args);
		
	}
	
	//insertDt와 now의 시간차이가 3분 이상이면 '인증시간 만료'
	public List<String> chkAuthNum(String tel, String authNum) {
	
		String s = " SELECT CASE " 
						 + " WHEN TIMESTAMPDIFF(SECOND, updateDt, now()) < 180 "
						 + " THEN true "
						 + " END AS authYn "
				 + " FROM sms_certify "
				 + " WHERE tel = :tel "
				 + " AND authNum = :authNum ";
	

		Map<String, Object> args = new HashMap<>();
		
		args.put("tel", tel);
		args.put("authNum", authNum);
	
		return jdbcTemplate.queryForList(s, args, String.class);

	}

	public String selectAuthNum(String tel, String authNum, String description, Integer authType) {
		
		String s = " SELECT authNum FROM sms_certify " +
				   " WHERE tel=:tel " +
				   " AND authNum=:authNum " +
				   " ORDER BY insertDt desc, updateDt desc LIMIT 1 ";		
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("tel", tel);
		args.put("authNum", authNum);
		
		try {
			
			 return jdbcTemplate.queryForObject(s, args, String.class);
			 
		} catch(EmptyResultDataAccessException e) {
			
			return null;
		}
	}
}
