package kr.co.cntt.scc.app.student.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.app.student.model.AppClientMember;
import kr.co.cntt.scc.app.student.model.AppClientBranch;
import kr.co.cntt.scc.app.student.model.join;
import kr.co.cntt.scc.model.BranchMember;
import kr.co.cntt.scc.service.HistoryService;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class AppClientMemberService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    //학생 아이디 중복체크
    public List<String> selectAppStudentCheckIdDupl(String studentId) {
    	String s = "SELECT asm.studentId " 
    			+ " FROM app_student_member asm "
    			+ " WHERE studentId = :studentId "
    			+ " AND useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("studentId", studentId);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.queryForList(s, args, String.class);
    }
    //학부모 아이디 중복체크
    public List<String> selectAppParentsCheckIdDupl(String parentsId) {
    	String s = "SELECT apm.parentsId " 
    			+ " FROM app_parents_member apm "
    			+ " WHERE parentsId = :parentsId "
    			+ " AND useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("parentsId", parentsId);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.queryForList(s, args, String.class);
    }
    

    
    public List<join.RegistYnResponse> selectAppStudentMember(String studentName, String tel) {
    	String s = "SELECT asm.studentId AS id, asm.insertDt AS joinDt " 
    			+ " FROM app_student_member asm "
    			+ " WHERE studentName = :studentName "
    			+ " AND  tel = :tel "
    			+ " AND useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("studentName", studentName);
    	args.put("tel", tel);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(join.RegistYnResponse.class));
    }
    
    public List<join.RegistYnResponse> selectAppParentsMember(String parentsName, String tel) {
    	String s = "SELECT asm.parentsId AS id, asm.insertDt AS joinDt " 
    			+ " FROM app_parents_member asm "
    			+ " WHERE parentsName = :parentsName "
    			+ " AND  tel = :tel "
    			+ " AND useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("parentsName", parentsName);
    	args.put("tel", tel);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(join.RegistYnResponse.class));
    }
   
    //appId로 학생회원 찾기
    public List<join.RegistYnResponse> selectAppStudentMemberByAppId(String appId) {
    	String s = "SELECT id AS autoIncrement " 
    			+ " FROM app_student_member asm "
    			+ " WHERE appId = :appId "
    			+ " AND useYn = :useYn "
    			+ " limit 1 ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("appId", appId);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(join.RegistYnResponse.class));
    }
    //appId로 학부모회원 찾기
    public List<join.RegistYnResponse> selectAppParentsMemberByAppId(String appId) {
    	String s = "SELECT id AS autoIncrement " 
    			+ " FROM app_parents_member asm "
    			+ " WHERE appId = :appId "
    			+ " AND useYn = :useYn "
    			+ " limit 1 ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("appId", appId);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(join.RegistYnResponse.class));
    }
    
    public int insertAppStudentMember(String appId) {
        String s = " INSERT INTO app_student_member ( " +
                " appId " +
                " ) VALUES ( " +
                " :appId " +
                " ) ";

    	Map<String, Object> args = new HashMap<>();    	
    	args.put("appId", appId);


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
    
    public int insertAppParentsMember(String appId) {
        String s = " INSERT INTO app_parents_member ( " +
                " appId " +
                " ) VALUES ( " +
                " :appId " +
                " ) ";

    	Map<String, Object> args = new HashMap<>();    	
    	args.put("appId", appId);


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
    
    

    public int updateAppStudentMemberForNo(String appId, AppClientMember appMember) {
        String s = " UPDATE app_student_member SET "
                + " studentName = :name, gender = :gender, birthDt = :birthDt, tel = :tel,"
                + " address1 = :address1, address2 = :address2, addressDetail = :addressDetail, addressType = :addressType, "
                + " postcode = :postcode, email = :email, role = :role, studentId = :id, studentPw = :encoded_password,"
                + " job = :job, interest = :interest, no = :no, transferYn = :transferYn, updateDt = NOW() "
                + " WHERE appId = :appId "
                + " AND useYn = :useYn ";
        
        CombinedSqlParameterSource source = new CombinedSqlParameterSource(appMember);
        source.addValue("appId", appId);
        source.addValue("encoded_password", passwordEncoder.encode(appMember.getPw()));
        source.addValue("useYn", Constants.USE);    	


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
    
    public int updateAppParentsMemberForNo(String appId, AppClientMember appMember) {
        String s = " UPDATE app_parents_member SET "
                + " parentsName = :name, gender = :gender, birthDt = :birthDt, tel = :tel,"
                + " address1 = :address1, address2 = :address2, addressDetail = :addressDetail, addressType = :addressType, "
                + " postcode = :postcode, email = :email, role = :role, "
                + " parentsId = :id, parentsPw = :encoded_password,"
                + " no = :no, updateDt = NOW() "
                + " WHERE appId = :appId "
                + " AND useYn = :useYn ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(appMember);
        source.addValue("appId", appId);
        source.addValue("encoded_password", passwordEncoder.encode(appMember.getPw()));
        source.addValue("useYn", Constants.USE);    	


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
    
    //ID, PW 입력
    public int updateAppStudentMemberForId(String no, String studentId, String paramPw) {
        String s = " UPDATE app_student_member SET " 
        		+ " studentId = :studentId, studentPw = :studentPw, updateDt = NOW() "
                + " WHERE no = :no "
                + " AND useYn = :useYn ";
        
    	Map<String, Object> args = new HashMap<>();
    	args.put("no", no);
    	args.put("studentId", studentId);
    	args.put("studentPw", paramPw);    
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
    
    //ID, PW 입력
    public int updateAppParentsMemberForId(String no, String parentsId, String parentsPw) {
        String s = " UPDATE app_parents_member SET "
        		+ " parentsId = :parentsId, parentsPw = :parentsPw, updateDt = NOW() " 
        		+ " WHERE no = :no "
        		+ " AND useYn = :useYn ";
        
    	Map<String, Object> args = new HashMap<>();
    	args.put("no", no);
    	args.put("parentsId", parentsId);
    	args.put("parentsPw", parentsPw);	
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
    
    //주자식 정보 변경
    public int updateAppParentsMemberMainChild(String parentsNo, String studentNo) {
        String s = " UPDATE app_parents_member SET "
        		+ " mainChildNo = :studentNo, updateDt = NOW() " 
        		+ " WHERE no = :parentsNo "
        		+ " AND useYn = :useYn ";
        
    	Map<String, Object> args = new HashMap<>();
    	args.put("parentsNo", parentsNo);
    	args.put("studentNo", studentNo);	
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
    
    
    
    //추가 정보 입력
    public int updateAppStudentMemberForAddInfo(String no, String job, String interest) {
        String s = " UPDATE app_student_member SET "
                + " job = :job, interest = :interest, transferYn = :transferYn, updateDt = NOW() "
        		//+ " branchId = :branchId, memberId = :memberId " 
                + " WHERE no = :no "
                + " AND useYn = :useYn ";
        
    	Map<String, Object> args = new HashMap<>();
    	args.put("no", no);
    	args.put("job", job);
    	args.put("interest", interest);
    	//args.put("branchId", branchId);
    	//args.put("memberId", memberId);
    	args.put("transferYn", Constants.USE);
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
    
    
    //추가 정보 입력
    public int insertSmsApproveApp(String no, Boolean smsYes, Boolean enterexitYes, Boolean personalYn, Boolean utilYn ) {
        String s = " INSERT INTO sms_approve ( " +
                " no, enterexitYes, smsYes, personalYn, utilYn " +
                " ) VALUES ( " +
                " :no, :enterexitYes, :smsYes, :personalYn, :utilYn " +
                " ) " +
                " ON DUPLICATE KEY UPDATE " +
                " no = :no, enterexitYes = :enterexitYes, smsYes = :smsYes, personalYn = :personalYn, utilYn = :utilYn ";
        
    	Map<String, Object> args = new HashMap<>();
    	args.put("no", no);
    	args.put("smsYes", smsYes);
    	args.put("enterexitYes", enterexitYes);
    	args.put("personalYn", personalYn);
    	args.put("utilYn", utilYn);
    	

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
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
    
    // sms_approve update
    public int updateSmsApproveApp(String branchId ,String memberId ,String no, Boolean smsYes, Boolean enterexitYes, Boolean personalYn, Boolean utilYn) {
        String s = " UPDATE sms_approve SET "
                + " no = :no, smsYes = :smsYes, enterexitYes = :enterexitYes, personalYn = :personalYn, utilYn = :utilYn, updateDt = NOW() "
        		//+ " branchId = :branchId, memberId = :memberId " 
                + " WHERE branchId = :branchId "
        		+ " AND memberId = :memberId "
                + " AND useYn = :useYn ";
        
    	Map<String, Object> args = new HashMap<>();    	
    	args.put("branchId", branchId);
    	args.put("memberId", memberId);
    	args.put("no", no);
    	args.put("smsYes", smsYes);
    	args.put("enterexitYes", enterexitYes);
    	args.put("personalYn", personalYn);
    	args.put("utilYn", utilYn);
    	args.put("transferYn", Constants.USE);
    	args.put("useYn", Constants.USE);
    	

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
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
    
    
    // sms_approve update
    public int updateSmsApproveAppByNo(String no, Boolean smsYes, Boolean enterexitYes) {
        String s = " UPDATE sms_approve SET "
                + " smsYes = :smsYes, enterexitYes = :enterexitYes, updateDt = NOW() "
        		//+ " branchId = :branchId, memberId = :memberId " 
                + " WHERE no = :no "
                + " AND useYn = :useYn ";
        
    	Map<String, Object> args = new HashMap<>();    	
    	args.put("no", no);
    	args.put("smsYes", smsYes);
    	args.put("enterexitYes", enterexitYes);
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
    
    
  //id와 tel을 이용한 회원정보 검색
    public List<AppClientMember> selectAppStudentListById(String id, String tel) {
    	String s = " SELECT appId, no, mainBranchId, studentId AS id, studentPw AS pw,  studentName AS name, tel " 
    			+ " FROM app_student_member ";
    	
    		s +=  " WHERE studentId = :id ";
    			
			if(!StringUtils.isEmpty(tel)) {
				s += " AND tel = :tel ";
			}
    			
    			s += " AND useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("id", id);
    	args.put("tel", tel);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));

    }
    
    
    //no를 이용한 회원정보 검색
    public List<AppClientMember> selectAppMemberList(String no, Integer role) {
    	String s = null;
    	
    	//role에 따른 select 테이블 변경
    	if (role == 10) {
	    	s = " SELECT appId, no, mainBranchId, studentId AS id, studentPw AS pw,  studentName AS name, tel, "
	    			+ " gender, birthDt, email "
	    			+ " transferYn "
	    			+ " FROM app_student_member ";
    	} else if (role == 20) {
    		s = " SELECT appId, no, parentsId AS id, parentsPw AS pw, parentsName AS name, tel, mainChildNo, "
    				+ " gender, birthDt, email "
    				+ " FROM app_parents_member ";
	    			
    	}
    	
    	s +=  " WHERE no = :no "
    			+ " AND role = :role "
    			+ " AND useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("no", no);
    	args.put("role", role);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));

    }
    
    public List<join> selectAppStudentMemberByNo(String studentName, String tel) {
    	String s = "SELECT asm.studentName AS name, asm.tel AS tel " 
    			+ " FROM app_student_member asm "
    			+ " WHERE studentName = :studentName "
    			+ " AND  tel = :tel "
    			+ " AND useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("studentName", studentName);
    	args.put("tel", tel);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(join.class));
    }
    
    public List<join> selectAppParentsMemberByNo(String parentsName, String tel) {
    	String s = "SELECT asm.parentsName AS name, asm.tel AS tel " 
    			+ " FROM app_parents_member asm "
    			+ " WHERE parentsName = :parentsName "
    			+ " AND  tel = :tel "
    			+ " AND useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("parentsName", parentsName);
    	args.put("tel", tel);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(join.class));
    }
    
    
    public List<AppClientBranch.CenterList> selectCodiMember(String name, String tel) {
    	String s = "SELECT bm.branchId AS branchId, bm.memberId AS memberId, "
																		+ " ( "
																		  + " SELECT b.name "
																		  + " FROM branch b "
																		  + " WHERE b.branchId = bm.branchId "
																		  + " ) AS branchNm "
    			+ " FROM branch_member bm "
    			+ " WHERE 1=1 "
    			+ " AND branchId NOT IN (:TEST_BRANCHID) "
    			+ " AND name = :name "
    			+ " AND replace(tel, '-', '') = :tel "
    			+ " AND useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
        args.put("TEST_BRANCHID", Constants.TEST_BRANCHID);
    	args.put("name", name);
    	args.put("tel", tel);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientBranch.CenterList.class));
    }
    
    
    public int updateBranchMemberAppNo(String branchId ,String memberId, String appNo) {
        String s = " UPDATE branch_member SET "
                + " appNo = :appNo, updateDt = NOW() "
        		//+ " branchId = :branchId, memberId = :memberId " 
                + " WHERE branchId = :branchId "
        		+ " AND memberId = :memberId "
                + " AND useYn = :useYn ";
        
    	Map<String, Object> args = new HashMap<>();    	
    	args.put("branchId", branchId);
    	args.put("memberId", memberId);
    	args.put("appNo", appNo);
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
    
    
    
    //가입된 센터 정보 insert
    public int insertAppBranchManager(String appId, String no, String branchId, String memberId ) {
        String s = " INSERT INTO app_branch_manager ( " +
                " appId, no, branchId, memberId " +
                " ) VALUES ( " +
                " :appId, :no, :branchId, :memberId " +
                " ) ";
                
        
    	Map<String, Object> args = new HashMap<>();
    	args.put("appId", appId);
    	args.put("no", no);    	
    	args.put("branchId", branchId);
    	args.put("memberId", memberId);
    	

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
    
    
    //가입된 센터 정보 select
    public List<AppClientMember> selectAppBranchManager(String no, String branchId) {
                
    	String s = "SELECT no, memberId, branchId, ( "
    											 + " SELECT b.name "
    											 + " FROM branch b "
    											 + " WHERE b.branchId = :branchId "
    											 + " AND b.useYn = :useYn "
    											 + " AND b.visibleYn = :visibleYn "
    											 + ") AS branchNm "
    			+ " FROM app_branch_manager "
    			+ " WHERE 1 = 1 ";
    				
    	
    		if(!StringUtils.isEmpty(branchId)) {
    			s += " AND branchId = :branchId ";
	    	}
    	
    		if(!StringUtils.isEmpty(no)) {
    			s += " AND no = :no ";
			}
    			
    			s += " AND useYn = :useYn ";
        
    	Map<String, Object> args = new HashMap<>();
    	args.put("no", no);
    	args.put("branchId", branchId);
    	args.put("useYn", Constants.USE);
    	args.put("visibleYn", Constants.USE);

    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));

    }
    
    //가입된 센터 정보 select only no
    public List<String> selectAppBranchManagerForNo(String branchId) {
                
    	String s = " SELECT no, memberId "    			
    			+ " FROM app_branch_manager "
    			+ " WHERE 1 = 1 ";
    				    	
    		if(!StringUtils.isEmpty(branchId)) {
    			s += " AND branchId = :branchId ";
	    	}    	
    			
    			s += " AND useYn = :useYn ";
        
    	Map<String, Object> args = new HashMap<>();
    	args.put("branchId", branchId);
    	args.put("useYn", Constants.USE);

    	
    	return jdbcTemplate.queryForList(s, args, String.class);

    }
    
    public List<BranchMember> selectAppBranchManagerForCodi(String branchId, String memberId) {
        
    	String s = " SELECT b.no, b.branchId, b.memberId, m.studentName AS name, if ( isnull(m.gender), '0', m.gender) AS gender, m.birthDt AS birthDt, "
    			+ " m.bfTel AS bfTel , m.tel AS tel, m.telParent AS telParent, m.address1 AS address1, m.address2 AS address2, "
    	        + " if ( isnull(m.job), '0', m.job ) AS jobType, if ( isnull(m.interest), '0', m.interest) AS interestType, "
    	        
    	        + " if ( isnull(sms.enterexitYes), '0', sms.enterexitYes ) as enterexitYes, " 
                + " if ( isnull(sms.smsYes), '0', sms.smsYes ) as smsYes, " 
                + " if ( isnull(sms.personalYn), '0', sms.personalYn ) as personalYn, " 
                + " if ( isnull(sms.utilYn), '0', sms.utilYn ) as utilYn " 
    	        
                + " FROM app_branch_manager b "
    	        + " LEFT OUTER JOIN app_student_member as m ON ( b.no = m.no AND m.role = 10 AND m.useYn = :useYn ) "    	        
    	        + " JOIN sms_approve as sms ON b.no = sms.no " 
    			+ " WHERE 1 = 1 "
    			+ " AND b.branchId = :branchId ";
		
    	if(!StringUtils.isEmpty(memberId)) {
            s += " AND m.memberId = :memberId ";

        }
    			s += " AND b.useYn = :useYn "
        		
    			+ " GROUP BY b.memberId ";
    			s += "ORDER BY m.insertDt"; 
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("branchId", branchId);
    	args.put("memberId", memberId);
    	args.put("useYn", Constants.USE);

    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(BranchMember.class));

    }
    
    public List<BranchMember> selectAppParentsForCodi() {
        
    	String s = " SELECT pm.no, pm.parentsName AS name, IFNULL(pm.gender, 0) AS gender , pm.birthDt AS birthDt, "
    			+ " pm.bfTel AS bfTel , pm.tel AS tel, pm.address1 AS address1, pm.address2 AS address2 "
    	        
                + " FROM app_parents_member as pm "    	         
    			+ " WHERE 1 = 1 ";

    			s += " AND pm.useYn = :useYn ";        		
    			
    			s += " ORDER BY pm.insertDt"; 
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("useYn", Constants.USE);

    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(BranchMember.class));

    }
    
    
    
    
    //가입된 센터 정보 select
    public List<AppClientMember> selectAppBranchManagerForMemberId(String memberId) {
                
    	String s = "SELECT abm.no, abm.memberId, abm.branchId, ( "
    											 + " SELECT b.name "
    											 + " FROM branch b "
    											 + " WHERE abm.branchId = b.branchId "
    											 + " AND b.useYn = :useYn "
    											 + " AND b.visibleYn = :visibleYn "
    											 + ") AS branchNm " 
    											 /*
    											 + "( "
    											 + " SELECT asm.studentName "
    											 + " FROM app_student_member asm"
    											 + " WHERE asm.memberId = :memberId "
    											 + " AND asm.useYn = :useYn "
    											 + ") AS name "
    											 */
    											 
    			+ " FROM app_branch_manager abm"
    			+ " WHERE 1 = 1 ";
    				
    	
    		if(!StringUtils.isEmpty(memberId)) {
    			s += " AND abm.memberId = :memberId ";
	    	}
    	
    			
    			s += " AND abm.useYn = :useYn ";
        
    	Map<String, Object> args = new HashMap<>();
    	args.put("memberId", memberId);
    	args.put("useYn", Constants.USE);
    	args.put("visibleYn", Constants.USE);

    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));

    }    
    
    
    //main센터 변경
    public int updateMainBranch(String no, Integer role, String branchId, String memberId) {
        String s = " UPDATE app_student_member SET "
                + " mainBranchId = :branchId, mainMemberId = :memberId, updateDt = NOW() "
        		//+ " branchId = :branchId, memberId = :memberId " 
                + " WHERE no = :no "
                + " AND role = :role ";
        
    	Map<String, Object> args = new HashMap<>();    	
    	args.put("no", no);
    	args.put("role", role);
    	args.put("branchId", branchId);
    	args.put("memberId", memberId);
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
    

    
    
        
    
    //mypage 정보 변경
    public int updateAppMember(String no, Integer role, AppClientMember appMember) {
        String s = null;
    	if (role == 10) {
    		s = " UPDATE app_student_member SET "
    				+ " address1 = :address1, "
    				+ " address2 = :address2, "
    				+ " addressDetail = :addressDetail, "
    				+ " addressType = :addressType, "
    				+ " postcode = :postcode, "
    				+ " birthDt = :birthDt, "
    				+ " email = :email, ";
    		
    		if (appMember.getJob() != null) {
            	s += " job = :job, ";
            }
    		
    		if (appMember.getInterest() != null) {
    			s += " interest = :interest, ";
    		}
    		
    	} else if (role == 20) {
    		s = " UPDATE app_parents_member SET "
    				+ " address1 = :address1, "
    				+ " address2 = :address2, "
    				+ " addressDetail = :addressDetail, "
    				+ " addressType = :addressType, "
    				+ " postcode = :postcode, "
    				+ " birthDt = :birthDt, "
    				+ " email = :email, ";
    	}
    	
		
	    	if (appMember.getTel() != null) {
				s += " tel = :tel, "
				  + " bfTel = :bfTel, ";
			}
                        		
	        s += " updateDt = NOW() "
	        + " WHERE no = :no "
	        + " AND role = :role "
	        + " AND useYn = :useYn ";
        
        CombinedSqlParameterSource source = new CombinedSqlParameterSource(appMember);
        source.addValue("no", no);
        source.addValue("role", role);
        source.addValue("useYn", Constants.USE);    	


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
    
    
    //연동된 회원들은 mypage 정보 변경이 되면, 코디 정보도 변경
    public int updateCodiInfo(String appNo, String tel, String bfTel) {
	    String s = " UPDATE branch_member SET "
	            + " tel = :tel, bfTel = :bfTel, updateDt = NOW() " 
	            + " WHERE appNo = :appNo "
    			+ " AND useYn = :useYn ";
	    
		Map<String, Object> args = new HashMap<>();
		args.put("appNo", appNo);
		args.put("tel", tel);
		args.put("bfTel", bfTel);
		args.put("useYn", Constants.USE);
			
		
	    int result = jdbcTemplate.update(s, args);
	
	    if (result == 0) {
	        throw new InternalServerError("Failed to create AppStudent");
	
	    } else {
	//        History history = new History(branchId, Constants.HistoryType.PAY_CREATE, pay.toString());
	//        history.setOrderId(pay.getOrderId());
	//        history.setPayId(payId);
	//        history.setMemberId(pay.getMemberId());
	//        historyService.insertHistory(history);
	
	    }
	
	    return result;
	
	}
    
    
    public List<BranchMember> selectCodiMemberByAppNo(String appNo) {
        String s = " SELECT m.branchId, m.memberId, m.no, m.membershipNo ,m.name, m.tel, m.telParent, m.telEtc, m.email, m.school, m.schoolGrade, "
        		+ " m.gender, m.birthDt, m.postcode, m.address1, m.address2, m.checkoutYn, m.examType "
    			+ " FROM branch_member m "
    			+ " WHERE m.appNo = :appNo "
    			+ " AND m.useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("appNo", appNo);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(BranchMember.class));
    }
    
    public int updateBranchStudentMemberFortelParent(String no ,Integer role, String telParent) {
        String s = " UPDATE app_student_member SET "
                + " telParent = :telParent, updateDt = NOW() "
        		//+ " branchId = :branchId, memberId = :memberId " 
                + " WHERE no = :no "
        		+ " AND role = :role " 
                + " AND useYn = :useYn ";
        
    	Map<String, Object> args = new HashMap<>();    	
    	args.put("no", no);
    	args.put("role", role);
    	args.put("telParent", telParent);
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
    
    
    //회원탈퇴
    public int updateAppMemberLeave(String no, Integer role) {
        String s = null;
    	
        if(role == 10) {
        	s = " UPDATE app_student_member SET "
              + " useYn = :notUseYn, updateDt = NOW() "
              + " WHERE no = :no "
              + " AND useYn = :useYn ";
        	
        } else if(role == 20) {
        	s = " UPDATE app_parents_member SET "
              + " useYn = :notUseYn, updateDt = NOW() "
              + " WHERE no = :no "
              + " AND useYn = :useYn ";        	
        }
        
       
        
    	Map<String, Object> args = new HashMap<>();    	
    	args.put("no", no);
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
    
    
    // 학생 회원에서 학부모 번호 지우기(탈퇴시) or 학부모 번호 변경시 학생 telParent 변경해주기
    public int updateAppStudentMembertTelParent(String no, String tel) {
        String s = " UPDATE app_student_member SET "
                + " telParent = :telParent, updateDt = NOW() "
                + " WHERE no = :no "
                + " AND useYn = :useYn ";
               
        
    	Map<String, Object> args = new HashMap<>();    	
    	args.put("no", no);
    	args.put("telParent", tel);
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
    
    
    
    /////코디에서 사용하기 위한 memberSelct (회원만 select -> role=10)
    public List<BranchMember> selectAppMemberForCodi(List<String> no, String branchId) {
        String s = " SELECT no, studentName AS name, gender, birthDt, bfTel, tel, telParent, address1, address2, "
        		+ " job AS jobType, interest AS interestType, "
        												+ " ( "
        												+ "  SELECT memberId  "
        												+ "  FROM app_branch_manager "
        												+ "  WHERE no IN ( :no ) "
        												+ "  AND branchId = :branchId "
        												+ " ) AS memberId"
    			+ " FROM app_student_member "
    			+ " WHERE no IN ( :no )"
    			+ " AND role = :role "
    			+ " AND transferYn = :transferYn "
    			+ " AND useYn = :useYn ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("no", no);
    	args.put("role", 10);
    	args.put("branchId", branchId);
    	args.put("transferYn", Constants.NOT_USE);
    	args.put("useYn", Constants.USE);
    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(BranchMember.class));
    }

    
    
}
