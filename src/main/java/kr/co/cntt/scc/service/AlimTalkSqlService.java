package kr.co.cntt.scc.service;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.alimTalk.TbAlimtalkTemplate;
import kr.co.cntt.scc.model.BranchDesign;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.model.Reservation;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.model.Desk;
import kr.co.cntt.scc.model.Room;
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
public class AlimTalkSqlService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;
   
	//@Autowired
	//TbAlimtalkTemplate tbAlimtalkTemplate;

	public TbAlimtalkTemplate getAlimtalkTemplate(String type) {
	
		String s = " SELECT template_type, template_cd, template_message " +
				" FROM alimtalk_template " +
				" WHERE template_type = :template_type LIMIT 1";
		
		Map<String, Object> args = new HashMap<>();
		args.put("template_type", type);
		
		return  jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(TbAlimtalkTemplate.class));
		
		
	}
	
	public String getArvApplyId(String branchId, String authChannelCd) {

		String s = " SELECT arvApplyId " +
				" FROM branch_auth_info " +
 				" WHERE branchId = :branchId " +
				" AND authChannelCd = :authChannelCd ";
		
		Map<String, Object> args = new HashMap<>();
		args.put("branchId", branchId);
		args.put("authChannelCd", authChannelCd);	
		
		return jdbcTemplate.queryForObject(s, args, String.class);

	}
	
}
