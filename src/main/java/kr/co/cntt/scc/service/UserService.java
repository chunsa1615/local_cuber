package kr.co.cntt.scc.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.app.admin.model.AppAdminUserInfo;
import kr.co.cntt.scc.app.admin.model.AppClientMain;
import kr.co.cntt.scc.app.admin.model.AppClientMain.CenterInfo;
import kr.co.cntt.scc.app.admin.model.AppClientMain.MainRank;
import kr.co.cntt.scc.app.admin.model.AppClientMain.NoticeList;
import kr.co.cntt.scc.app.admin.model.AppClientReservation;
import kr.co.cntt.scc.app.admin.model.AppClientReservation.ReservationHistory;
import kr.co.cntt.scc.app.admin.model.AppClientReservation.UserBranchInfo;
import kr.co.cntt.scc.app.admin.model.AppClientStudy;
import kr.co.cntt.scc.app.admin.model.AppClientStudy.StudyPlan;
import kr.co.cntt.scc.app.admin.model.AppClientStudy.GoalStatus;
import kr.co.cntt.scc.app.admin.model.AppClientStudy.StudyList;
import kr.co.cntt.scc.app.admin.model.AppClientMember;
import kr.co.cntt.scc.app.admin.model.AppClientReport;
import kr.co.cntt.scc.app.admin.model.AppClientUserInfo;
import kr.co.cntt.scc.app.admin.model.forPassword;
import kr.co.cntt.scc.app.admin.model.notice;
import kr.co.cntt.scc.app.admin.model.AppClientUserInfo.SettingInfo;
import kr.co.cntt.scc.app.admin.model.AppClientUserInfo.StudentList;
import kr.co.cntt.scc.app.admin.model.main.Notice;
import kr.co.cntt.scc.app.admin.model.notice.NoticeDetail;
import kr.co.cntt.scc.app.admin.model.settingSearch.ResponseVersion;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.model.User;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by jslivane on 2016. 4. 20..
 */
@Service
@Transactional
@Slf4j
public class UserService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;
    
    //@Autowired
    PasswordEncoder passwordEncoder;

    public UserService() {
    	this.passwordEncoder = new StandardPasswordEncoder(); 
    }

    /*******************************************************************************/


    /**
     * (캐시 제거) 사용자 리스트
     */
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void resetUserList() { }

    /**
     * (캐시) 사용자 리스트 조회
     * @return
     * Cache : http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html
     */
    @Cacheable(cacheNames = "users")
    public List<User> selectUserList() {
        String s = " SELECT u.id, u.userId, u.name, u.password, u.role, u.insertDt, u.updateDt, " +
                "   (SELECT GROUP_CONCAT(bm.branchId SEPARATOR :separator) FROM branch_manager bm " +
                "   WHERE bm.userId = u.userId AND bm.useYn = :useYn) AS branches, " +
                "   (SELECT GROUP_CONCAT(b.name SEPARATOR :spacedSeparator) FROM branch_manager bm JOIN branch b ON (b.branchId = bm.branchId AND b.useYn = :useYn) " +
                "   WHERE bm.userId = u.userId AND bm.useYn = :useYn) AS branchesNames " +
                " FROM user u " +
                " WHERE u.useYn = :useYn ";

        Map<String, Object> args = new HashMap<>();
        args.put("separator", Constants.SEPERATOR);
        args.put("spacedSeparator", Constants.SPACED_SEPERATOR);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(User.class));

    }


    public User selectUser(String userId) {

        List<User> users = selectUserList();

        for(User user: users) {
            if(userId.equals(user.getUserId())) return user;

        }

        return null;

    }

    public User selectUserByName(String name) {

        List<User> users = selectUserList();

        for(User user: users) {
            if(name.equals(user.getName())) return user;

        }

        return null;

    }

    @CacheEvict(cacheNames = "users", allEntries = true)
    public int insertUser(String userId, User user) {
    	
    	
        String s = " INSERT INTO user ( " +
                " userId, name, password, role " +
                " ) VALUES ( " +
                " :userId, :name, :encoded_password, :role " +
                " ) ";
       
        CombinedSqlParameterSource source = new CombinedSqlParameterSource(user);
        source.addValue("userId", userId);        
        source.addValue("encoded_password", passwordEncoder.encode(user.getEncoded_password()));
        
        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to create user");

        } else {
            History history = new History(null, Constants.HistoryType.USER_CREATE, user.toString());
            history.setUserId(userId);
            historyService.insertHistory(history);

            upsertBranchManager(userId, user.getBranches());
        }

        return result;

    }

    @CacheEvict(cacheNames = "users", allEntries = true)
    public int updateUser(String userId, User user) {
        String s = " UPDATE user SET " +
                " name = :name, password = :encoded_password, role = :role, updateDt = NOW() " + //  password = :password,
                " WHERE userId = :userId ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(user);
        source.addValue("userId", userId);
        source.addValue("encoded_password", passwordEncoder.encode(user.getEncoded_password()));
        
        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to update user");

        } else {
            History history = new History(null, Constants.HistoryType.USER_UPDATE, user.toString());
            history.setUserId(userId);
            historyService.insertHistory(history);

            deleteBranchManager(userId);
            upsertBranchManager(userId, user.getBranches());

        }

        return result;

    }

    @CacheEvict(cacheNames = "users", allEntries = true)
    public int deleteUser(String userId) {
        String s = " UPDATE user SET " +
                " useYn = :useYn, updateDt = NOW() " +
                " WHERE userId = :userId ";

        Map<String, Object> args = new HashMap<>();
        args.put("userId", userId);
        args.put("useYn", Constants.NOT_USE);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to delete user");

        } else {
            History history = new History(null, Constants.HistoryType.USER_DELETE, "");
            history.setUserId(userId);
            historyService.insertHistory(history);

            deleteBranchManager(userId);

        }

        return result;

    }


    /**************************************************************************************/


    public String selectPrimaryBranchIdByUserId(String userId) {

        String s = " SELECT bm.branchId " +
                " FROM user u" +
                " JOIN branch_manager bm ON (bm.userId = u.userId) " +
                " WHERE u.userId = :userId AND u.useYn = :useYn" +               
                " AND bm.branchId NOT IN (:TEST_BRANCHID) " +
                " LIMIT 1 ";

        Map<String, Object> args = new HashMap<>();
        args.put("userId", userId);
        args.put("useYn", Constants.USE);
        args.put("TEST_BRANCHID", Constants.TEST_BRANCHID);
        
        return jdbcTemplate.queryForObject(s, args, String.class);

    }

    private int[] upsertBranchManager(String userId, String branches) {
        String[] b = StringUtils.isEmpty(branches) ? new String[]{} : branches.split(Constants.SEPERATOR);
        int l = b.length;

        if (l > 0) {
            String s = " INSERT INTO branch_manager ( " +
                    " userId, branchId " +
                    " ) VALUES ( " +
                    " :userId, :branchId " +
                    " ) ON DUPLICATE KEY UPDATE  " +
                    " userId = :userId, branchId = :branchId, useYn = :useYn ";

            Map<String, Object>[] args = new HashMap[b.length];
            for(int i = 0; i < l; i++) {
                Map<String, Object> arg = new HashMap<>();
                arg.put("userId", userId);
                arg.put("branchId", b[i]);
                arg.put("useYn", Constants.USE);

                args[i] = arg;

            }

            int[] results = jdbcTemplate.batchUpdate(s, args);

            for (int i = 0; i < l; i++) {
                String branchId = b[i];

                if (results[i] == 1) {
                    History history = new History(branchId, Constants.HistoryType.BRANCH_MANAGER_CREATE, "");
                    history.setUserId(userId);
                    historyService.insertHistory(history);

                } else if (results[i] == 2) {
                    History history = new History(branchId, Constants.HistoryType.BRANCH_MANAGER_UPDATE, "");
                    history.setUserId(userId);
                    historyService.insertHistory(history);

                }

            }

            return results;

        } else {
            return new int[0];

        }
    }

    private int deleteBranchManager(String userId) {
        String s = " DELETE FROM branch_manager WHERE userId = :userId ";

        Map<String, Object> args = new HashMap<>();
        args.put("userId", userId);

        int result = jdbcTemplate.update(s, args);

        History history = new History(null, Constants.HistoryType.BRANCH_MANAGER_DELETE, "");
        history.setUserId(userId);
        historyService.insertHistory(history);

        /*
        String s = " UPDATE branch_manager SET " +
                " useYn = :useYn, updateDt = NOW() " +
                " WHERE userId = :userId ";

        Map<String, Object> args = new HashMap<>();
        args.put("userId", userId);
        args.put("useYn", Constants.NOT_USE);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to delete branch manager");

        } else {
            History history = new History(null, Constants.HistoryType.BRANCH_MANAGER_DELETE, "");
            history.setUserId(userId);
            historyService.insertHistory(history);

        }
        */

        return result;

    }
    
    //**********************************************App admin 
    public List<User> findUser(String name) {
    	String s = " SELECT u.id, u.userId, u.name, u.password, u.role, u.insertDt, u.updateDt, " +
                 "   (SELECT GROUP_CONCAT(bm.branchId SEPARATOR :separator) FROM branch_manager bm " +
                "   WHERE bm.userId = u.userId AND bm.useYn = :useYn ) AS branches, " +
                "   (SELECT GROUP_CONCAT(b.name SEPARATOR :spacedSeparator) FROM branch_manager bm JOIN branch b ON (b.branchId = bm.branchId AND b.useYn = :useYn) " +
                "   WHERE bm.userId = u.userId AND bm.useYn = :useYn ) AS branchesNames " +
                " FROM user u " +
                " WHERE u.name = :name " +
                " AND u.useYn = :useYn ";

        Map<String, Object> args = new HashMap<>();
        args.put("separator", Constants.SEPERATOR);
        args.put("spacedSeparator", Constants.SPACED_SEPERATOR);
        args.put("name", name);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(User.class));

    }
    
    
	public int insertAppAdmin(String branchId, String userId, String name, Boolean auto, String uuid, String os, String version, String device, String pushId) {    	
	    	    	
	        String s = " INSERT INTO app_admin_userinfo ( " +
	                " branchId, userId, name, auto, uuid, os, version, device, pushId " +
	                " ) VALUES ( " +
	                " :branchId, :userId, :name, :auto, :uuid, :os, :version, :device, :pushId " +
	                " ) " +
	                " ON DUPLICATE KEY UPDATE auto = :auto, uuid = :uuid, os = :os, version = :version, device = :device, pushId = :pushId, updateDt=now() ";
	       
	        
	        Map<String, Object> args = new HashMap<>();	        
	        args.put("branchId", branchId);
	        args.put("userId", userId);
	        args.put("name", name);
	        args.put("auto", auto);
	        args.put("uuid", uuid);
	        args.put("os", os);
	        args.put("version", version);
	        args.put("device", device);
	        args.put("pushId", pushId);
	        
	
	        int result = jdbcTemplate.update(s, args);        
	
	        if (result == 0) {
	            throw new InternalServerError("Failed to create AppAdminUser");
	
	        } else {
	//            History history = new History(null, Constants.HistoryType.USER_CREATE, user.toString());
	//            history.setUserId(userId);
	//            historyService.insertHistory(history);
	//
	//            upsertBranchManager(userId, user.getBranches());
	        }
	
	        return result;
	
	    }

	public List<AppAdminUserInfo> selectPushYn(String name, String branchId, String type) {
		String s = null;
		
		if (type.equals("LOGIN")) {
			s = " SELECT aau.name AS userId, aau.branchId AS branchId, aau.uuid AS uuid, aau.os AS os, aau.version, " +
        		" aau.device, aau.pushId, aau.pushYn " +
                " FROM app_admin_userinfo aau" +
                " WHERE aau.name = :name " +
                " AND aau.branchId = :branchId " +
                " AND aau.useYn = :useYn ";
		} else if (type.equals("SETTING")) {
			s = " SELECT aau.name AS userId, aau.branchId AS branchId, aau.uuid AS uuid, aau.os AS os, aau.version, " +
	        		" aau.device, aau.pushId, aau.pushYn " +
	                " FROM app_admin_userinfo aau" +
	                " WHERE aau.userId = :userId " +
	                " AND aau.branchId = :branchId " +
	                " AND aau.useYn = :useYn ";
		}
		
        Map<String, Object> args = new HashMap<>();
        args.put("name", name);
        args.put("userId", name);
        args.put("branchId", branchId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppAdminUserInfo.class));

    }
	
	 public int updatePushYn(String userId, String branchId, boolean pushYn) {
	        String s = " UPDATE app_admin_userinfo SET " +
	                " pushYn = :pushYn, updateDt = NOW() " + //  password = :password,
	                " WHERE userId = :userId " + 
	                " AND branchId = :branchId " +
	                " AND useYn = :useYn " ;
	 
	        Map<String, Object> args = new HashMap<>();
	        args.put("userId", userId);
	        args.put("branchId", branchId);
	        args.put("pushYn", pushYn);
	        args.put("useYn", Constants.USE);
	
	        int result = jdbcTemplate.update(s, args);        
	
	        if (result == 0) {
	            throw new InternalServerError("Failed to create AppAdminUser");
	
	        } else {
	        
	        }
	        
	        return result;	 
	 }
	 
		public List<ResponseVersion> selectVersion(String os, Integer type) {

	        String s = " SELECT aav.version, aav.updateYn " +
	                " FROM app_admin_version aav" +
	                " WHERE aav.os = :os " +	                
	                " AND aav.useYn = :useYn AND aav.type=:type ";
	        s += " LIMIT 1 ";
	        //" AND aav.type = :type ";
	        
	        Map<String, Object> args = new HashMap<>();
	        args.put("os", os);
	        args.put("useYn", Constants.USE);
	        args.put("type", type);

	        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(ResponseVersion.class));

	    }
		
		
		public int insertLogHistory(String branchId, String userId, String logType) {    	
	    	
	        String s = " INSERT INTO app_admin_login_history ( " +
	                " branchId, userId, logType " +
	                " ) VALUES ( " +
	                " :branchId, :userId, :logType ) ";
	        
	        Map<String, Object> args = new HashMap<>();
	        args.put("branchId", branchId);
	        args.put("userId", userId);	        
	        args.put("logType", logType);
	
	        int result = jdbcTemplate.update(s, args);        
	
	        if (result == 0) {
	            throw new InternalServerError("Failed to insert AppAdminLogType");
	
	        } else {
	//            History history = new History(null, Constants.HistoryType.USER_CREATE, user.toString());
	//            history.setUserId(userId);
	//            historyService.insertHistory(history);
	//
	//            upsertBranchManager(userId, user.getBranches());
	        }
	
	        return result;
	
	    }
		
		
		public int insertAppLogHistory(String branchId, String userId, String appId, String logType) {    	
	    	
	        String s = " INSERT INTO app_admin_login_history ( " +
	                " branchId, userId, appId, logType " +
	                " ) VALUES ( " +
	                " :branchId, :userId, :appId, :logType ) ";
	        
	        Map<String, Object> args = new HashMap<>();
	        args.put("branchId", branchId);
	        args.put("appId", appId);
	        args.put("userId", userId);	        
	        args.put("logType", logType);
	
	        int result = jdbcTemplate.update(s, args);        
	
	        if (result == 0) {
	        	
	            throw new InternalServerError("Failed to insert AppAdminLogType");
	
	        } else {

	        }
	
	        return result;
	
	    }
		
		public List<Notice> getNoticeList(String branchId) {
			String s = " SELECT aan.noticeId AS noticeId, aan.title AS title, aan.noticeDt AS noticeDt " +
	                " FROM app_admin_notice aan" +
	                " WHERE ( aan.branchId = :branchId OR aan.branchId = 'ALL' ) " +
	                " AND aan.useYn = :useYn ";
			s += " ORDER BY aan.noticeDt DESC " +
	             " LIMIT 3 " ;
		
		
	        Map<String, Object> args = new HashMap<>();
	        args.put("branchId", branchId);
	        args.put("useYn", Constants.USE);

	        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Notice.class));
		}
		
		public List<NoticeDetail> getNoticeList2(String branchId) {
			String s = " SELECT aan.noticeId AS noticeId, aan.title AS title, aan.noticeDt AS noticeDt " +
	                " FROM app_admin_notice aan" +
	                " WHERE ( aan.branchId = :branchId OR aan.branchId = 'ALL' ) " +
	                " AND aan.useYn = :useYn " ;
			s += " ORDER BY aan.noticeDt DESC " ;
		
		
	        Map<String, Object> args = new HashMap<>();
	        args.put("branchId", branchId);
	        args.put("useYn", Constants.USE);

	        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(NoticeDetail.class));
		}
		
		
		public List<notice.Response> getNoticeDetail(String branchId, String noticeId) {
			String s = " SELECT aan.title AS title, aan.noticeDt AS noticeDt, aan.content AS content, aan.branchId as branchId" +
	                " FROM app_admin_notice aan" +
	                " WHERE ( aan.branchId = :branchId OR aan.branchId = 'ALL' ) " +
	                " AND aan.noticeId = :noticeId " +
	                " AND aan.useYn = :useYn ";
			s += " LIMIT 1 ";
		
		
	        Map<String, Object> args = new HashMap<>();
	        args.put("branchId", branchId);
	        args.put("noticeId", noticeId);
	        args.put("useYn", Constants.USE);

	        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(notice.Response.class));
		}
		
		
		public List<AppClientMember> findAppUser_student(String userId) {
			
			String s = " SELECT count(*) as result, appId, no, studentPw as userPw, role  FROM app_student_member " + 
				       " WHERE studentId = :userId AND useYn=:useYn ";
			
	        Map<String, Object> args = new HashMap<>();
	        args.put("userId", userId);
	        args.put("useYn", Constants.USE);
	        
	        
	        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
		}

		
		public List<AppClientMember> findAppUser_parents(String userId) {
			
			String s = " SELECT count(*) as result_count, appId, no, parentsPw as userPw , role, IFNULL(mainChildNo, '') as mainChildNo FROM app_parents_member " + 
				       " WHERE parentsId = :userId AND useYn=:useYn ";
			
	        Map<String, Object> args = new HashMap<>();
	        args.put("userId", userId);
	        args.put("useYn", Constants.USE);
	        
	        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
		}

		public int insertAppStudent(String no, String userId, Boolean auto, 
				String uuid, String os, String version, String device, String pushId) {
			
			 String s = " INSERT INTO app_student_userinfo ( " +
		                " no,  name , auto, uuid, os, version, device, pushId, insertDt" +
		                " ) VALUES ( " +
		                " :no, :userId, :auto, :uuid, :os, :version, :device, :pushId, now()) " +
		                " ON DUPLICATE KEY UPDATE auto = :auto, uuid = :uuid, os = :os, version = :version, device = :device, pushId = :pushId, updateDt=now() ";
		       
		        
		        Map<String, Object> args = new HashMap<>();	   
		        
		        args.put("no", no);
		        args.put("userId", userId);
		        args.put("auto", auto);
		        args.put("uuid", uuid);
		        args.put("os", os);
		        args.put("version", version);
		        args.put("device", device);
		        args.put("pushId", pushId);
		        
		
		        int result = jdbcTemplate.update(s, args);        
		
		        if (result == 0) {
		            throw new InternalServerError("Failed to create AppAdminUser");
		
		        } else {

		        }
		
			return result;
		}
		
	public int insertAppClientLogHistory( String no, String logType) {    	
	    	
	        String s = " INSERT INTO app_admin_login_history ( " +
	                " branchId, userId, no, logType " +
	                " ) VALUES ( " +
	                " '', '', :no, :logType ) ";
	        
	        Map<String, Object> args = new HashMap<>();
	        args.put("no", no);
	        args.put("logType", logType);
	
	        int result = jdbcTemplate.update(s, args);        
	
	        if (result == 0) {
	            throw new InternalServerError("Failed to insert AppAdminLogType");
	
	        } else {

	        }
	
	        return result;
	
	    }

	public List<AppClientMember> selectStudentUserNo(String no, Integer role) {
		
		String s = " SELECT count(*) as result_count from app_student_member where no=:no AND role=:role AND useYn=:useYn ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("role", role);
		args.put("useYn", Constants.USE);
		
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
	}
	
	public List<AppClientMember> selectParentsUserNo(String no, Integer role) {
		
		String s = " SELECT count(*) as result_count from app_parents_member where no=:no AND role=:role AND useYn=:useYn";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("role", role);
		args.put("useYn", Constants.USE);
		
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
	}

	public AppClientUserInfo.StudentsUserInfo selectUserinfoStudent(String no, Integer role) {

		String s = " SELECT asm.no, asm.role, asm.studentName as name, asm.studentId as id , b.name as branchNm , " + 
				   " asm.imgUrl, asm.tel, asm.telParent  , sa.enterexitYes, " +
				   //" IF(sa.smsYes=1, 'Y', 'N') AS smsYes, IF(sa.enterexitYes=1, 'Y', 'N') AS enterexitYes, asm.gender, " +
				   "  sa.smsYes , sa.enterexitYes , asm.gender, " +
			       " asm.address1 , asm.address2 , asm.addressDetail , IFNULL(asm.addressType, 10) AS addressType, " +
			       " asm.job, asm.interest, asm.email, asm.birthDt " +
			       " FROM app_student_member asm LEFT OUTER JOIN branch b ON asm.mainBranchId = b.branchId " +
				   "							 LEFT OUTER JOIN sms_approve sa ON asm.no = sa.no " +
				   " WHERE asm.no=:no AND asm.useYn=:useYn ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		//args.put("role", role);
		args.put("useYn", Constants.USE);
		
		
		try {
			
			 return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientUserInfo.StudentsUserInfo.class));
			 
		} catch(EmptyResultDataAccessException e) {
			
			return null;
		}
		
	}

	public AppClientUserInfo.ParentsUserInfo selectUserinfoParents(String no, Integer role) {
		
		String s = " SELECT apm.no as no, apm.role, apm.parentsName as name , " + 
				   " apm.parentsId as id , apm.birthDt as birthDt ,apm.imgUrl, apm.tel, apm.email, " +
				   " apm.gender, apm.mainChildno, " +
				   " apm.address1 , apm.address2 , apm.addressDetail , IFNULL(apm.addressType, 10) AS addressType " +
				   " FROM app_parents_member apm  " +
				   " WHERE apm.no=:no AND apm.useYn=:useYn ";		
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		//args.put("role", role);
		args.put("useYn", Constants.USE); 
		
		try {
			
			 return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientUserInfo.ParentsUserInfo.class));
			 
		} catch(EmptyResultDataAccessException e) {
			
			return null;
		}
		
	}

	public List<AppClientUserInfo.StudentList> selectStudentList(String no) {
		
		String s = " SELECT studentNo , studentName FROM app_parents_safeservice " +
				   " WHERE parentsNo=:no AND useYn=:useYn GROUP BY studentNo, studentName ORDER BY studentName, studentNo  ASC ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("useYn", Constants.USE);
		
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientUserInfo.StudentList.class));
		
	}

	public int updateUserinfoStudent(String no, String tel, Integer smsYes, Integer enterexitYes,
			String address, String job, String interest) {
		
		 /* String s = " UPDATE app_student_member asm JOIN sms_approve sa on asm.no=sa.no " +
				  	 " SET asm.tel=:tel, sa.smsYes=:smsYes, sa.enterexitYes=:enterexitYes, " +
				  	 " asm.address1=:address1, asm.address2=:address2 , asm.job=:job , asm.interest=:interest " + 
				  	 " , asm.updateDt=now() WHERE sa.no=:no ";*/
		
		String s = ""; String address1 = ""; String address2 = "";
		Map<String, Object> args = new HashMap<>();
		
		
		if(address.contains("로") || address.contains("길")){
			address1 = address;
			 s = " UPDATE app_student_member asm JOIN sms_approve sa on asm.no=sa.no " +
			  	 " SET asm.tel=:tel, sa.smsYes=:smsYes, sa.enterexitYes=:enterexitYes, " +
			  	 " asm.address1=:address1, asm.job=:job , asm.interest=:interest " + 
			  	 " , asm.updateDt=now() WHERE sa.no=:no AND sa.useYn=:useYn ";
			 
			 args.put("address1", address1);
			 
		} else { 
			address2 = address;
			s = " UPDATE app_student_member asm JOIN sms_approve sa on asm.no=sa.no " +
			    " SET asm.tel=:tel, sa.smsYes=:smsYes, sa.enterexitYes=:enterexitYes, " +
			    " asm.address2=:address2, asm.job=:job , asm.interest=:interest " + 
			    " , asm.updateDt=now() WHERE sa.no=:no AND sa.useYn=:useYn ";
			args.put("address2", address2);
		} 
		
		
		args.put("no",no);
		args.put("tel",tel);
		args.put("smsYes",smsYes);
		args.put("enterexitYes",enterexitYes);
		args.put("job",job);
		args.put("interest",interest);
		args.put("useYn", Constants.USE);  
		
		
		int updResult = jdbcTemplate.update(s, args);
		
		 if (updResult == 0) {
			 
	            throw new InternalServerError("Failed to insert AppAdminLogType");
	
	        } else {

	        }
		 
	        return updResult;
		
	}

	public int updateUserinfoParents(String no, String tel, String address) {
		
	/*	String s = " UPDATE app_parents_member " +
				   " SET tel=:tel, address1=:address1 , updateDt=now() " +
				   " WHERE no=:no";*/
		
		/*StringBuffer s = new StringBuffer();
		String address1 = ""; String address2 = "";
		
			s.append(" UPDATE app_parents_member SET tel=:tel ");
		
		if(address.contains("로") || address.contains("길")){
			address1 = address;
			s.append(" ,address1=:address1 ");
		} else { 
			address2 = address;
			s.append(" ,address2=:address2 ");
		} 
		
			s.append(" ,updateDt=now() WHERE no=:no ");*/
		
		
		String s = ""; String address1 = ""; String address2 = "";
		Map<String, Object> args = new HashMap<>();
		
		if(address.contains("로") || address.contains("길")){
			address1 = address;
			s = " UPDATE app_parents_member " +
			    " SET tel=:tel, address1=:address1 , updateDt=now() " +
			    " WHERE no=:no AND useYn=:useYn ";
			
			args.put("address1", address1);
			
		} else { 
			address2 = address;
			s = " UPDATE app_parents_member " +
			    " SET tel=:tel, address2=:address2 , updateDt=now() " +
				" WHERE no=:no AND useYn=:useYn ";
			
			args.put("address2", address2);
		} 
		
		args.put("tel", tel);
		args.put("no", no);
		args.put("useYn", Constants.USE);
		int updResult = jdbcTemplate.update(s, args);
		
		 if (updResult == 0) {
			 
	            throw new InternalServerError("Failed to insert AppAdminLogType");
	
	        } else {

	        }
		
		return updResult;
	}

	public int updateStudentImage(String no, String newImg) {
		
		String s = " UPDATE app_student_member SET imgUrl=:imgUrl, updateDt=now() where no=:no AND useYn=:useYn ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("imgUrl", newImg);
		args.put("no", no);
		args.put("useYn", Constants.USE);
		
		int result = jdbcTemplate.update(s, args);
		
		return result;
	}
	
	public int updateParentsImage(String no, String newImg) {
		
		String s = " UPDATE app_parents_member SET imgUrl=:imgUrl, updateDt=now() where no=:no AND useYn=:useYn ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("imgUrl", newImg);
		args.put("no", no);
		args.put("useYn", Constants.USE);
		
		int result = jdbcTemplate.update(s, args);
		
		return result;
	}

	public List<AppClientUserInfo> selectOldStudentImg(String no, Integer pathLength) {
		
		String s = " SELECT substring(imgUrl, :pathLength) as imgUrl FROM app_student_member WHERE no=:no AND useYn=:useYn ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("pathLength", pathLength);
		args.put("no", no);
		args.put("useYn", Constants.USE);
		
		try {
			
			return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientUserInfo.class));
			 
		} catch(EmptyResultDataAccessException e) {
			
			return null;
		}
	}
	
	public List<AppClientUserInfo> selectOldParentsImg(String no, Integer pathLength) {
		
		String s = " SELECT substring(imgUrl, :pathLength) as imgUrl FROM app_parents_member WHERE no=:no ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("pathLength", pathLength);
		args.put("no", no);
		
		try {
			
			return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientUserInfo.class));
			 
		} catch(EmptyResultDataAccessException e) {
			
			return null;
		}
		
	}

	public List<AppClientReservation.ReservationHistory> selectSeatReservationList(String no, Integer role, Integer type) {
		
		String s = " SELECT DISTINCT  asa.id, asa.no as no, asa.role as role, asa.branchId, b.name AS branchName , DATE_FORMAT(asa.startDt , '%Y.%m.%d') as startDt, asa.status, " +
				   " CASE asa.status " +
				   " WHEN 10 THEN '예약요청' " +
				   " WHEN 20 THEN '예약완료' " +
                   " WHEN 90 THEN '예약취소' " +
                   " WHEN 100 THEN '자동취소' " +
                   " END AS statusDesc " +	
					" FROM app_seat_application asa JOIN branch b ON asa.branchId = b.branchId " +
				   " WHERE asa.no=:no " +
				   " AND role=:role " +
				   " AND asa.useYn=:useYn " +
				   " AND asa.type=:type " +
				   " AND asa.status in ( 10, 20, 90, 100 ) " +
				   " ORDER BY asa.startDt asc " ;
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("role", role);
		args.put("type", type);
		args.put("useYn", Constants.USE);
		
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientReservation.ReservationHistory.class));
		
	}

	public List<AppClientReservation.UserBranchInfo> selectUserInfo(String no) {
		
		String s = " SELECT asm.mainBranchId AS mainBranchId , bm.memberId as memberId, asm.transferYn,  " +
				   " asm.studentName as name FROM app_student_member asm JOIN branch_member bm ON asm.no=bm.appNo " +
				   " WHERE asm.no=:no AND asm.useYn=:useYn";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("useYn", Constants.USE);
		
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientReservation.UserBranchInfo.class));
		
	}

	public List<AppClientMember> validChk(String no, Integer role, String userPw) {
		
		String s = " SELECT  "; 
		
		if(role == 10) {
			s += " studentPw AS userPw FROM app_student_member WHERE ";
		} else if(role == 20) {
			s += " parentsPw AS userPw FROM app_parents_member WHERE ";
		} else {}
		
		s += " no=:no AND useYn=:useYn ";

		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("userPw", passwordEncoder.encode(userPw));
		args.put("useYn", Constants.USE);
		
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
		
	}

	public int changePw(String no, Integer role, String newPw ) {
		
		String s = " UPDATE ";
		
		if(role == 10) {
			s += " app_student_member SET studentPw=:newPw ";
		} else if(role == 20) {
			s += " app_parents_member SET parentsPw=:newPw ";
		} else {}
		
		s += " WHERE no=:no AND role=:role AND useYn=:useYn ";
		
		   
			Map<String, Object> args = new HashMap<>();
			
			args.put("no", no);
			args.put("role", role);
			args.put("newPw", passwordEncoder.encode(newPw));
			args.put("useYn", Constants.USE);
			
			int result = jdbcTemplate.update(s, args);
		
				 if (result == 0) {
				     throw new InternalServerError("Failed to update user");
				
				 } else {}
				
				 return result;
	}

	public int insertStudySet(String mainBranchId, String memberId, String no, Integer role,  String startDt, String endDt, long goalTotal) {
		
		String s = " INSERT INTO branch_learning_time (branchId, memberId , no, role, startDt, endDt,  goalTime, " +
				   " studyStatus, useYn, insertDt) VALUE " +
				   " (:mainBranchId, :memberId, :no, :role, :startDt, :endDt, :goalTime , :studySatus , :useYn, now()) ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("mainBranchId", mainBranchId);
		args.put("memberId", memberId);
		args.put("no", no);
		args.put("role", role);
		args.put("startDt", startDt);
		args.put("endDt", endDt);
		args.put("goalTime", goalTotal*60);
		args.put("studySatus", 20);
		args.put("useYn", Constants.USE);
		
		int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to insert studySet");

        } else {}

        return result;
		
	}


	public int updateStatus(String mainBranchId, String memberId, String no, Integer role  ) {
		
			String s = " UPDATE branch_learning_time " +  
					   " SET studyStatus=CASE WHEN (endDt <= curdate()) AND (learningTime >= goalTime) THEN 10 " + 
					   " WHEN (endDt >= curdate()) AND (learningTime >= goalTime) THEN 10 " + 
					   " WHEN (endDt  < curdate()) AND learningTime < goalTime THEN 30 " + 
					   " ELSE 20 END " + 
					   " ,updateDt=now() " +
					   // " WHERE endDt < curdate() " +
					   " WHERE 1=1 " +
					   " AND branchId=:branchId " +
					   " AND memberId=:memberId " +
					   " AND studyStatus=20 AND no=:no AND role=:role AND useYn=:useYn ";

				   
		Map<String, Object> args = new HashMap<>();
		
		args.put("branchId", mainBranchId);
		args.put("memberId", memberId);
		args.put("no", no);
		args.put("role", role);
		args.put("useYn", Constants.USE);
		
		int result = 0;
        
	        try {
				
				 result = jdbcTemplate.update(s, args);
				
			} catch (EmptyResultDataAccessException e) {
				e.printStackTrace();
			}
		        
        return result;
        
	}

	public List<ReservationHistory> selectParentsSeatReservationList(String no, Integer role, Integer type) {

		String s = " SELECT DISTINCT asa.id, asa.no as no, asa.role as role, asa.branchId, b.name AS branchName , asa.startDt, asa.status, " +
				   " CASE asa.status " +
				   " WHEN 10 THEN '예약요청' " +
				   " WHEN 20 THEN '예약완료' " +
                   " WHEN 90 THEN '예약취소' " +
                   " WHEN 100 THEN '자동취소' " +
	               " END AS statusDesc " +	
				   " FROM app_seat_application asa JOIN branch b ON asa.branchId = b.branchId " +
				   " WHERE asa.no=:no " +
				   " AND asa.role=:role " +
				   " AND asa.useYn=:useYn " +
				   " AND asa.type=:type " +
				   " AND asa.status in ( 10, 20, 90, 100 ) " +
				   " ORDER BY asa.startDt asc " ;
		 
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("role", role);
		args.put("type", type);
		args.put("useYn", Constants.USE);
		
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientReservation.ReservationHistory.class));
	}

	public List<UserBranchInfo> selectMappingTable(String no) {
		
		String s = " SELECT memberId , branchId as mainBranchId " + 
				   " FROM app_branch_manager " +
				   " WHERE no=:no AND useYn=:useYn "; 
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("useYn", Constants.USE);
		
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(UserBranchInfo.class));
	}

	//public List<AppClientStudy.StudyPlan> recentStudySet(String mainBranchid, String no, Integer role, String startDt, String endDt) {
	public AppClientStudy.StudyPlan recentStudySet(String mainBranchid, String no, Integer role, String startDt, String endDt) {
		
		String s = " SELECT studyStatus, DATE_FORMAT(startDt, '%y.%m.%d') AS startDt, DATE_FORMAT(endDt, '%y.%m.%d') as endDt, " +
				   " IF(TRUNCATE(((goalTime)/(datediff(endDt, startDt)+1))/3600, 1) = '0.5', " +
				   " 30, TRUNCATE(((goalTime)/(datediff(endDt, startDt)+1))/3600, 0)) as goalTime " + 				
				   " FROM branch_learning_time WHERE useYn=:useYn AND no=:no AND role=:role " +    
				   " AND branchId=:mainBranchId " +
				   " AND startDt <= :startDt " + 
				   " AND endDt >= :endDt " +
				   " ORDER BY insertDt DESC LIMIT 1 ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("useYn", Constants.USE);
		args.put("role", role);
		args.put("no", no);
		args.put("startDt", startDt);
		args.put("mainBranchId", mainBranchid);
		args.put("endDt", endDt);
		
		//return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientStudy.StudyPlan.class));
		
		try {
			
			return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientStudy.StudyPlan.class));
			
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}

		
	}

	public int updateLearningTime( String mainBranchId, String memberId , String no, Integer role, String learningTime) {
		
			String s = " UPDATE branch_learning_time set " + 
					   //" learningTime=IFNULL((learningTime), 0) + (TIME_TO_SEC(:learningTime)) " +
					   " learningTime=(TIME_TO_SEC(:learningTime)) " +
					   " , updateDt=now() WHERE 1=1" +
					   // " AND id=:id " +
					   " AND branchId=:branchId " +
					   " AND memberId=:memberId " +
					   " AND useYn=:useYn " +
					   " AND no=:no " +
					   " AND role=:role " +
					   " AND studyStatus=20 ";
			
			Map<String, Object> args = new HashMap<>();
			
			//args.put("id", id);
			args.put("branchId", mainBranchId);
			args.put("memberId", memberId);
			args.put("no", no);
			args.put("role", role);
			args.put("learningTime", learningTime);
			args.put("useYn", Constants.USE);
			
			int result = jdbcTemplate.update(s, args);
	
	     if (result == 0) {
	         throw new InternalServerError("Failed to insert LearningTimeSet");
	
	     } else {}
	
	     return result;
	}

	public int chkSuccesYn(String mainBranchId, String no, Integer role, Integer type, String startDt, String endDt) {
		
		String s = " SELECT IF(SUM(TIME_TO_SEC(learningTime)) > goalTime*(datediff(:endDt,:startDt))+1, 1, 0) AS updYn " +
				   " FROM branch_learning_time " +
				   " WHERE branchId=:branchId " +
				   " AND useYn=:useYn AND no=:no AND role=:role " +
				   " AND startDt <= :startDt " + 
				   " AND endDt >= :endDt " +	
				   " AND type=:type AND studyStatus=:studyStatus " + 
				   " order by insertDt desc limit 1 ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("branchId", mainBranchId );
		args.put("useYn", Constants.USE);
		args.put("no", no);
		args.put("role", role);
		args.put("type", type);
		args.put("startDt", startDt);
		args.put("studyStatus", 20);
		args.put("endDt", endDt);
		
		return jdbcTemplate.queryForObject(s, args, int.class);
		
	}

	public int updateClear(String mainBranchId, String no, Integer role, Integer type, String startDt, String endDt) {
		
	/*	String s = " UPDATE branch_learning_time SET studyStatus=10, " + 
				   "  learningTime=sec_to_time(goalTime), updateDt=now() " +
				   " WHERE branchId=:branchId And role=:role " +
				   " AND startDt <= :startDt " + 
				   " AND endDt >= :endDt " +
				   " AND type=10 AND studyStatus=:studyStatus " + 
				   " AND useYn=:useYn AND no=:no " +
				   " order by insertDt desc limit 1";	*/

		
		String s =  " UPDATE branch_learning_time SET studyStatus= " + 
					" CASE WHEN IFNULL((SELECT * FROM (SELECT SUM(TIME_TO_SEC(learningTime)) " + 
					" FROM branch_learning_time WHERE branchId=:branchId " +  
					"				AND startDt >= :startDt " + 
					"				AND endDt <= :endDt " + 
					" 				AND studyStatus=:studyStatus " +
					" 				AND type=20 AND useYn=:useYn AND no=:no AND role=:role " +
					" 				order by insertDt desc limit 1) a) , 0 )  >= goalTime THEN 10 " +
					" WHEN IFNULL((SELECT * FROM (SELECT SUM(TIME_TO_SEC(learningTime)) " +
					" FROM branch_learning_time WHERE branchId=:branchId " +  
					"				AND startDt >= :startDt " + 
					"				AND endDt <= :endDt " + 
                    "       		AND endDt >= curdate() " +
					"				AND studyStatus=:studyStatus " +
					"				AND type=20 AND useYn=:useYn AND no=:no AND role=:role " +
					"				order by insertDt desc limit 1) a), 0) < goalTime THEN 20 " +
					" WHEN IFNULL((SELECT * FROM (SELECT SUM(TIME_TO_SEC(learningTime)) " +
					" FROM branch_learning_time WHERE branchId=:branchId " +  
					"				AND startDt >= :startDt " + 
					"				AND endDt <= :endDt " + 
					"               AND endDt < curdate() " +
					"				AND studyStatus=:studyStatus " +
					"				AND type=20 AND useYn=:useYn AND no=:no AND role=:role " +
					"				order by insertDt desc limit 1) a), 0) < goalTime THEN 30 END" + 
					" WHERE branchId=:branchId  " +  
					" AND startDt >= :startDt " + 
					" AND endDt <= :endDt " + 
					// " AND endDt < curdate()  " +
					" AND studyStatus NOT IN (10, 30) " +
					" AND type=10  " +
					" AND useYn=1 AND no=:no AND role=:role  " +
					" order by insertDt desc limit 1  ";

								
		
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("studyStatus", 20);
		args.put("role", role);
		args.put("branchId", mainBranchId);
		args.put("no", no);
		args.put("startDt", startDt);
		args.put("endDt", endDt);
		args.put("useYn", Constants.USE);

		int result = 0;
		
		try {
			
			 result = jdbcTemplate.update(s, args);
			
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		}

		return result;
		
	}

	public List<StudyPlan> selectSuccessInfo(String mainBranchId, String no, Integer role, Integer type, String startDt, String endDt) {
		
		String s = " SELECT studyStatus, role, (goalTime)/3600 as goalTime , " +
				   " DATE_FORMAT(startDt, '%Y.%m.%d') AS startDt " +
				   "  , DATE_FORMAT(endDt ,'%Y.%m.%d') AS endDt " +   
				   " FROM branch_learning_time " + 
				   " WHERE type=:type AND studyStatus=:studyStatus " +
				   " AND branchId=:branchId AND role=:role " + 
				   " AND startDt >= :startDt " + 
				   " AND endDt <= :endDt " +
				   " AND useYn=:useYn AND no=:no ORDER BY insertDt desc limit 1 ";
		
			Map<String, Object> args = new HashMap<>();
			
			args.put("type", 10);
			args.put("role", role);
			args.put("branchId", mainBranchId);
			args.put("studyStatus", 10);
			args.put("startDt", startDt);
			args.put("endDt", endDt);
			args.put("no", no);
			args.put("useYn", Constants.USE);
			
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientStudy.StudyPlan.class));	
			
	}

	public List<StudyPlan> selectGoalAchi(String no, String mainBranchId ) {
		
		String s = " SELECT branchId, no, role, startDt, endDt, type, goalTime, studyStatus " + 
				   "  learningTime, studyStatus, useYn " +
				   " FROM branch_learning_time " +
				   " WHERE type=10 AND useYn=:useYn AND no=:no " +
				   " AND branchId=:branchId " +
				   " AND studyStatus=10  " +
				   " LIMIT 1 "; 
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("useYn", Constants.USE);
		args.put("branchId", mainBranchId);
		
		
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientStudy.StudyPlan.class));	
	}

	public List<AppClientStudy.GoalStatus> sumLearningTime(String mainBranchId, String no, String recStartDt, String recEndDt) {
		
		String s = " SELECT  IFNULL(SEC_TO_TIME(SUM(TIME_TO_SEC(learningTime))), 0) AS accuTime, " + 
			       " IFNULL(SEC_TO_TIME(SUM(TIME_TO_SEC(learningTime))) , 0) AS conAccuTime, " + 
			       " SEC_TO_TIME(IF( " + 
			       " (goalTime*(datediff(:endDt,:startDt)+1)) - SUM(TIME_TO_SEC(learningTime)) > 0, " + 
			       " (goalTime*(datediff(:endDt,:startDt)+1)) - SUM(TIME_TO_SEC(learningTime)), 0)) " +
				   " as remainTime " + 
			       " FROM branch_learning_time WHERE branchId=:branchId " +
			       " AND startDt >= :startDt " +
			       " AND endDt <= :endDt " +
			       " AND no=:no AND type=20 " +
			       " AND studyStatus=20 " +
			       " AND useYn=:useYn order by insertDt desc LIMIT 1 ";
				
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("branchId", mainBranchId);
		args.put("startDt", recStartDt);
		args.put("endDt", recEndDt);
		args.put("useYn", Constants.USE);
		
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientStudy.GoalStatus.class));
		
		
	}

	public int updateFalse(String mainBranchId, String no, Integer role, Integer type, String startDt, String endDt) {
		
			String s = " UPDATE branch_learning_time SET studyStatus= IF( (SELECT * FROM (SELECT SUM(TIME_TO_SEC(learningTime)) " +
					   " 						FROM branch_learning_time WHERE branchId=:branchId  " +
					   " 							AND startDt >= :startDt " +
					   " 							AND endDt <= :endDt " +
					   " 							AND endDt < curdate() " +
                       " 							AND studyStatus=:studyStatus " +
					   " 							AND type=20 AND useYn=:useYn AND no=:no AND role=:role " +
					   " order by insertDt desc limit 1) a) >= goalTime , 10, 30) " +
					   " WHERE branchId= :branchId  " +
					   " AND endDt <= curdate() " +
					   " AND studyStatus NOT IN (10, 30) " + 
					   " AND type=10  " + 
					   " AND useYn=:useYn AND no=:no AND role=:role " +
					   " order by insertDt desc limit 1 ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("studyStatus", 20);
		args.put("role", role);
		args.put("branchId", mainBranchId);
		args.put("no", no);
		args.put("startDt", startDt);
		args.put("endDt", endDt);
		args.put("useYn", Constants.USE);
	
		int result = 0;
		
		try {
			
			 result = jdbcTemplate.update(s, args);
			
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		}
	
		return result;
			
		}


	public int presentProgressYn(String no, Integer role, String mainBranchId, String memberId) {
		
		String s = " SELECT count(*) FROM branch_learning_time " + 
				   " where branchId=:branchId AND memberId=:memberId  " +
				   " AND no=:no AND role=:role AND studyStatus=20 AND useYn=:useYn ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("role", role);
		args.put("branchId", mainBranchId);
		args.put("memberId", memberId);
		args.put("useYn", Constants.USE);
		
		
		return jdbcTemplate.queryForObject(s, args, int.class);
	}

	public int updateStudySet(String no, Integer role, String startDt, String endDt, long goalTotal, String mainBranchId, String memberId) {
		
		   String s = " UPDATE branch_learning_time SET startDt=:startDt, endDt=:endDt, goalTime=:goalTime " +
					  " WHERE studyStatus=20 " +
                      " AND branchId=:branchId " +
                      " AND memberId=:memberId " +
					  " AND no=:no AND role=:role AND useYn=:useYn ";
	 
	        Map<String, Object> args = new HashMap<>();
	        
	        args.put("startDt", startDt);
	        args.put("endDt", endDt);
	        args.put("goalTime", goalTotal*60);
	        args.put("role", role);
	        args.put("no", no);
	        args.put("memberId", memberId);
	        args.put("branchId", mainBranchId);
	        args.put("useYn", Constants.USE);
	
	        int result = jdbcTemplate.update(s, args);        
	
	        if (result == 0) {
	            throw new InternalServerError("Failed to update StudySet");
	
	        } else {
	        
	        }
	        
	        return result;	 
	}
	
	
	
	public AppClientMember findIdInStudent(String name, String tel, Integer role) {
		
		
		String s = " SELECT studentId as id FROM app_student_member " +
				   " WHERE studentName = :name AND tel = :tel and role=:role";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("tel", tel);
		args.put("name", name);
		args.put("role", role);
		
		
		try {
			
			 return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
			 
		} catch(EmptyResultDataAccessException e) {
			
			return null;
		}
		
	}

	public AppClientMember findIdParents(String name, String tel, Integer role) {
		
		String s = " SELECT parentsId as id FROM app_parents_member " +
				   " WHERE parentsName=:name AND tel=:tel and role=:role";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("tel", tel);
		args.put("name", name);
		args.put("role", role);
		
		try {
			
			return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
			 
		} catch(EmptyResultDataAccessException e) {
			
			return null;
		}
	}

	public AppClientMember findStudentPw(String name, String userId, String tel , Integer role) {
		
		String s = " SELECT count(*) as result_count FROM app_student_member "
				+ " WHERE studentName=:name AND studentId=:userId AND tel=:tel and role=:role ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("name", name);
		args.put("userId", userId);
		args.put("tel", tel);
		args.put("role", role);
		
		
		return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
		
	}

		public AppClientMember findParentsPw(String name, String userId, String tel , Integer role) {
		
		String s = " SELECT count(*) as result_count FROM app_parents_member "
				+ " WHERE parentsName=:name AND parentsId=:userId AND tel=:tel and role=:role ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("name", name);
		args.put("userId", userId);
		args.put("tel", tel);
		args.put("role", role);
		
		
		return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
		
	}
	
	public AppClientMember selectStudentTable(String name, String userId, String tel) {
	
		String s = " SELECT count(*) as result_count FROM app_student_member " +
				   " WHERE studentName=:name and studentId=:userId and tel=:tel ";
		
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("name", name);
		args.put("userId", userId);
		args.put("tel", tel);
		
		return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
				
	}
	
	
	
	public AppClientMember selectParentsTable(String name, String userId, String tel) {
	
		String s = " SELECT count(*) result_count FROM app_parents_member " +
				   " WHERE parentsName = :name and parentsId = :userId and tel=:tel";
		
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("name", name);
		args.put("userId", userId);
		args.put("tel", tel);
		
		return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
				
	}

	/*public int changeStudentPw(String name, String userId, String tel, String newPw , Integer role) {*/
		public int changeStudentPw(forPassword forPassword) {
		
		String s = "UPDATE app_student_member SET studentPw=:encoded_password where studentName=:name " +
				   " and studentId=:userId and tel=:tel and role=:role  ";
		
		CombinedSqlParameterSource source = new CombinedSqlParameterSource(forPassword);
        source.addValue("encoded_password", passwordEncoder.encode(forPassword.getNewPw()));
		//source.addValue("encoded_password", forPassword.getNewPw());
		
        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to create user");

        } else {
            //History history = new History(null, Constants.HistoryType.USER_CREATE, forPassword.toString());
            //history.setUserId(userId);
            //historyService.insertHistory(history);

            //upsertBranchManager(userId, user.getBranches());
        }

        return result;
		
/*		
		String s = "UPDATE app_student_member SET studentPw=:newPw where studentName=:name " +
				   " and studentId=:userId and tel=:tel and role=:role  ";
		

		Map<String, Object> args = new HashMap<>();
		
		args.put("name", name);
		args.put("userId", userId);
		args.put("tel", tel);
		args.put("newPw", passwordEncoder.encode(newPw));
		args.put("role", role);
		
		return jdbcTemplate.update(s, args);*/
	}
	

	/*public int changeParentsPw(String name, String userId, String tel, String newPw, Integer role) {*/
	public int changeParentsPw(forPassword forPassword) {
		
		String s = " UPDATE app_parents_member SET parentsPw=:encoded_password where parentsName=:name " +
				   " and parentsId=:userId and tel=:tel and role=:role";
		
		
		 	CombinedSqlParameterSource source = new CombinedSqlParameterSource(forPassword);
	        /*source.addValue("encoded_password", forPassword.getNewPw());*/
	        source.addValue("encoded_password", passwordEncoder.encode(forPassword.getNewPw()));
	        //KeyHolder keyHolder = new GeneratedKeyHolder();
	        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
	        int result = jdbcTemplate.update(s, source);

	        if (result == 0) {
	            throw new InternalServerError("Failed to create user");

	        } else {
	            History history = new History(null, Constants.HistoryType.USER_CREATE, forPassword.toString());
	            //history.setUserId(userId);
	            //historyService.insertHistory(history);

	            //upsertBranchManager(userId, user.getBranches());
	        }

	        return result;

/*		Map<String, Object> args = new HashMap<>();
		
		args.put("name", name);
		args.put("userId", userId);
		args.put("tel", tel);
		args.put("newPw", passwordEncoder.encode(newPw));
		args.put("role", role);
		
		return jdbcTemplate.update(s, args);*/
		
	}

	public List<AppClientMember> duplicateChkStudent(String userId) {
		
		String s = " SELECT count(*) as result_count FROM app_student_member WHERE studentId=:userId ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("userId", userId);
		
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
	}

	public List<AppClientMember> duplicateChkParents(String userId) {
		
		String s = " SELECT count(*) as result_count FROM app_parents_member WHERE parentsId=:userId ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("userId", userId);
		
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
	}

	public String selectCurStudy(String no, Integer role) {
		
		String s =  " SELECT id FROM branch_learning_time " +
					" WHERE studyStatus=20 " +
					" AND no=:no AND role= :role AND useYn=:useYn ORDER BY insertDt LIMIT 1 ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("role", role);
		args.put("useYn", Constants.USE);
		
		try {
			
			return jdbcTemplate.queryForObject(s, args, String.class);
			 
		} catch(EmptyResultDataAccessException e) {
			
			return null;
		}
		
	}

	public AppClientStudy.GoalStatus selectGoalYn(String no, String mainBranchId, String memberId) {
		
		String s =  " SELECT " +
					" IF(endDt > DATE_FORMAT(sysdate(), '%Y-%m-%d'), 1, 0) AS progYn, " +
					" id, branchId, no, role, " + 
					" IFNULL(learningTime, 0) AS unConvLTime, " +
					" goalTime AS unConvGTime," +
					" IF((goalTime)-(IFNULL(learningTime, 0)) < 0, " + 
		        	" 0, (goalTime)-(IFNULL(learningTime, 0)) " + 
		        	"            ) AS diffHours, " +
		        	" SEC_TO_TIME(IF((goalTime)-(IFNULL(learningTime, 0)) < 0, " + 
					" 		 0, (goalTime)-(IFNULL(learningTime, 0)) " +
					"    )) AS convDiff, " +
					" SEC_TO_TIME(IFNULL(learningTime, 0)) AS learningTime, " + 
					" CONVERT(SEC_TO_TIME(goalTime),  CHAR CHARACTER SET utf8 ) AS goalTime, " +
					" DATE_FORMAT(startDt, '%Y.%m.%d') AS startDt, " +
					" DATE_FORMAT(endDt, '%Y.%m.%d') AS endDt, " +
					" DATEDIFF(endDt, startDt)+1 AS diffDays, " +
					" studyStatus, " + 
					" useYn " +
				    " FROM branch_learning_time " +
				    " WHERE useYn=:useYn AND no=:no " +
				    " AND branchId=:branchId AND studyStatus=20 LIMIT 1 ";
	
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("branchId", mainBranchId);
		args.put("memberId", memberId);
		args.put("useYn", Constants.USE);
		
		
	try {
			
			return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientStudy.GoalStatus.class));
			 
		} catch(EmptyResultDataAccessException e) {
			
			return null;
		}
		
	}
	

	public List<AppClientStudy.StudyList> selectStudyPlanList(String mainBranchId, String memberId, String no, Integer role, Integer num) {
		
		String s = " SELECT IF(TRUNCATE(goalTime/((IF(DATEDIFF(endDt, startDt) < 0 , " +
				   " 		DATEDIFF(endDt, startDt)*-1, " +  
				   " 		DATEDIFF(endDt, startDt)))+1)/3600 ,1) = 0.5 " +
	               " 		, CONCAT(30 , '분') " +
	               " 		, CONCAT(TRUNCATE(goalTime/((IF(DATEDIFF(endDt, startDt) < 0 , " +  
				   " 		DATEDIFF(endDt, startDt)*-1, " +  
				   " 		DATEDIFF(endDt, startDt)))+1)/3600 ,0) , '시간')) AS goalPerDay, " +
				   " 		DATE_FORMAT(startDt, '%y.%m.%d') AS startDt , DATE_FORMAT(endDt, '%y.%m.%d') AS endDt, " +
				   " 		IFNULL(CONCAT(TRUNCATE((IF((learningTime/goalTime)*100 > 100 " + 
				   " 		, 100 , (learningTime/goalTime)*100 " +
				   " 		)) ,0),'%'), 1) AS timeRate, " + 
				   " 	CASE studyStatus  " +
				   " 		WHEN 10 THEN '달성' " +
				   " 		WHEN 20 THEN '진행 중' " +
				   " 		WHEN 30 THEN '미달성' " +
				   " 	END AS studyStatus " +
				   " FROM branch_learning_time " +
				   " WHERE branchId=:branchId " +
				   " AND memberId=:memberId " +
				   " AND no=:no AND role=:role AND useYn=:useYn " +
				   " ORDER BY endDt DESC, insertDt DESC , updateDt desc LIMIT :num, 10 ";	
				
		
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("role", role);
		args.put("branchId", mainBranchId);
		args.put("memberId", memberId);
		args.put("num", num);
		args.put("useYn", Constants.USE);
		
		try {
			
			return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientStudy.StudyList.class));
			
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		}
			
			return null;
		
		
	}

	public AppClientReport selectReportInfo(String paramBranchId, String paramMemberId) {
		
		String s =  " SELECT a.* FROM (SELECT  br.deskStartDt AS deskStartDt " +
					" 		,br.deskEndDt AS deskEndDt " +
					" 		,DATE_FORMAT(br.reservationDt, '%Y.%m.%d') AS reservationDt " +
					" 		,DATEDIFF(br.deskEndDt,br.deskStartDt) AS regPeriod " +
					" 		,b.name as branchNm " +
                    " 		,asm.studentName as name " + 
					" 		,bd.name as seatNo " +
					// "		,apm.parentsName as parentsName " + 
					" 		,brm.name as roomName " +
					" 		,asm.job as job " +
					" 		,IFNULL(asm.imgUrl, '') AS imgUrl " +
					" FROM 	branch_reservation br JOIN branch_pay bp ON br.reservationId = bp.reservationId " +
					" 				  	  JOIN branch_desk bd ON br.deskId = bd.deskId " +
					"                     JOIN branch_room brm ON bd.roomId = brm.roomId " +
					" 					  JOIN app_branch_manager abm ON (br.memberId=abm.memberId AND br.branchId = abm.branchId) " +
					//"                     JOIN branch_member bm ON br.memberId = bm.memberId " +
					"					  JOIN branch b ON br.branchId = b.branchId " +
					//"					  LEFT OUTER JOIN app_parents_member apm ON abm.no = apm.mainChildNo " + 
					"                     LEFT OUTER JOIN app_student_member asm ON abm.no = asm.no " +
					" WHERE	br.branchId=:branchId " +
					" AND	br.memberId=:memberId " +
					" AND	br.useYn=:useYn " ;
					
				
					
					s += " ORDER BY  br.reservationDt desc LIMIT 1 ) a ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("branchId", paramBranchId);
		args.put("memberId", paramMemberId);
		args.put("useYn", Constants.USE);
		
		//return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientReport.class));
		
				try {
						
						return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientReport.class));
						
					} catch (EmptyResultDataAccessException e) {
						e.printStackTrace();
					}
						
						return null;
		
	}

	public String selectAttendanceRate(String paramBranchId, String paramMemberId, Integer regPeriod,
											String deskStartDt, String deskEndDt ) {
		
		String s = " SELECT TRUNCATE(IF((count(distinct bme.businessDt)/30)*100 >= 100, " +  
				   " 100, (count(distinct bme.businessDt)/30)*100), 0) AS attendanceRate " +  
				   " FROM branch_member_entry bme JOIN branch_member bm ON bme.memberId=bm.memberId " + 
				   "  WHERE bme.businessDt BETWEEN :deskStartDt AND :deskEndDt " +
				   " 	AND bme.branchId=:branchId " +
				   "	AND bme.memberId=:memberId  " +
                   "    AND bme.useYn=:useYn AND bme.useYn=:useYn AND bme.entryType=1 ";

		
			Map<String, Object> args = new HashMap<>();
			
			args.put("branchId", paramBranchId);
			args.put("memberId", paramMemberId);
			args.put("regPeriod", regPeriod);
			args.put("deskStartDt", deskStartDt);
			args.put("deskEndDt", deskEndDt);
			args.put("useYn", Constants.USE);
		
		try {
			
			return jdbcTemplate.queryForObject(s, args, String.class);
			 
		} catch(EmptyResultDataAccessException e) {
			
			return null;
		}
		
	}

	public String getEntryInfo(String paramBranchId, String paramMemberId) {
		
		String s = " SELECT CONCAT(DATE_FORMAT(entryDt, '%Y.%m.%d %H:%h'), ' ', " +
				   " CASE WHEN e.entryType=1 THEN '입실' " + 
				   " 	  WHEN e.entryType=2 THEN '퇴실' " +
				   " 	  WHEN e.entryType=3 THEN '외출' " +
				   " 	  WHEN e.entryType=4 THEN '복귀' " +
				   " END) AS entryInfo " +
				   " FROM branch_member_entry e WHERE 1=1 " +  
				   " AND e.branchId=:branchId " +
				   " AND e.memberId=:memberId AND e.useYn=:useYn " +
				   " order by entryDt desc, insertDt desc limit 1 ";
		
		Map<String,Object> args = new HashMap<>();
		
		args.put("branchId", paramBranchId);
		args.put("memberId", paramMemberId);
		args.put("useYn", Constants.USE);
		
		//return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(String.class));
		
		try {
			
			return jdbcTemplate.queryForObject(s, args, String.class);
			
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		}
			
			return null;
			
	}

	public List<AppClientMain.NoticeList> selectNotice(String paramBranchId ) {
		
		/*String s = "   SELECT   a.rowNum AS noticeNo " +
				   " 		 	,a.title AS title " +
				   " FROM ( SELECT @rownum:=@rownum+1 AS rowNum " + 
				   " ,branchId, title, subtitle, imgUrl, visibleYn " +
				   " ,CONCAT(startDt,' ', startTm) as startDt " +
				   " ,CONCAT(endDt, ' ',endTm) as endTM " +
				   " ,insertDt,updateDt,useYn " +
				   " FROM   branch_ad_or_banner, (SELECT @rownum:=0) tempNum " +
				   " WHERE  branchId=:branchId " +
				   " AND	  adOrNoticeType=20 " + 
				   " AND 	  useYn=1) a " +
				   " WHERE a.useYn=1 " +
				   " ORDER BY a.insertDt DESC LIMIT 3 ";    */   
		
			String s = " SELECT aan.noticeId AS noticeId, " +
						"			aan.title AS title, " +
						"			aan.noticeDt AS noticeDt " +
						" FROM app_admin_notice aan" +
						" WHERE ( aan.branchId = :branchId OR aan.branchId = 'ALL' ) " +
						" AND aan.useYn = :useYn " ;
						s += " ORDER BY aan.noticeDt DESC LIMIT 3 " ;
		
			Map<String, Object> args = new HashMap<>();
			
			args.put("branchId", paramBranchId);
			args.put("useYn", Constants.USE);
			
			return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMain.NoticeList.class));
	}

	public List<AppClientMember> selectBranchId(String no, Integer role) {
		
		String s = " SELECT mainBranchId , studentName as name, job, IFNULL(imgUrl, '') AS imgUrl " +
				   " FROM app_student_member WHERE useYn=:useYn " +
				   " AND no=:no AND role=:role ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("role", role);
		args.put("useYn", Constants.USE);
		
			try {
			
			return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
			
			} catch (EmptyResultDataAccessException e) {
				e.printStackTrace();
			}
			
			return null;
			
	}

	public List<AppClientMember> getMainChildNo(String no, Integer role) {

		String s = " SELECT mainChildNo, parentsName as name FROM app_parents_member WHERE no=:no AND role=:role " +
				   " AND useYn=:useYn ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("role", role);
		args.put("useYn", Constants.USE);
		
		try {
			
			return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
			
			} catch (EmptyResultDataAccessException e) {
				e.printStackTrace();
			}
			
		return null;
		
	}
	
	public List<AppClientMember> selectBranchMatch(String no, String mainBranchId) {	
		
			String s = " SELECT abm.no, " + 
					   " 		abm.memberId, " +  
					   " 		abm.branchId, " + 
					   " 		b.name as branchNm " +
					   " FROM app_branch_manager abm join branch b on abm.branchId = b.branchId " +
					   " WHERE 1 = 1 ";  
				if(mainBranchId != null) {
					s += " AND abm.branchId=:branchId ";
				} else {}
				
				if(no != null) {
					s += " AND abm.no=:no  ";
				} else {}
				
				s += "AND abm.useYn=:useYn ";
			
			
			Map<String, Object> args = new HashMap<>();
			args.put("no", no);
			args.put("branchId", mainBranchId);
			args.put("useYn", Constants.USE);
			args.put("visibleYn", Constants.USE);
			
		
		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(AppClientMember.class));
	}

	public String selectCenterRank(String paramBranchId, String paramMemberId) {
		
		String s = " SELECT  a.total as cntrRank " +
				   " FROM (SELECT @rownum:=@rownum+1 as total ,asub.* " +
				   " 		FROM   (SELECT branchNm, memberNm AS name, SUM(time) AS time, memberId " + 
				   " 				FROM statistics_entry_day where 1=1  " +
				   " 				AND branchId = :branchId " +
				   //" 				AND branchId NOT IN ('033f8817-71a0-4feb-bf7b-f9f184da7317') AND useYn=:useYn " +
				   " 				AND useYn=:useYn " +
				   " 				GROUP BY memberId " +
				   " 				ORDER BY time desc ) asub, (SELECT @rownum:=0) tempNum " + 
				   " 	   ) a " +
				   " WHERE a.memberId=:memberId ";
				
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("branchId", paramBranchId);
		args.put("memberId", paramMemberId);
		args.put("useYn", Constants.USE);
		
		try {
			
			return jdbcTemplate.queryForObject(s, args, String.class);
			
			} catch (EmptyResultDataAccessException e) {
				e.printStackTrace();
			}
			
		return null;
	
}

	public AppClientUserInfo.SettingInfo selectSetInfo(String no) {
		
		String s = " SELECT no, name as id , auto, pushYn, " + 
				   " (SELECT version " +  
				   " FROM app_admin_version WHERE type=20 " +
				   " ORDER BY insertDt desc LIMIT 1 " +
				   " ) AS version " +
				   " FROM app_student_userinfo " +
				   " WHERE no=:no AND useYn=:useYn ";
				
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("useYn", Constants.USE);
		
		
		try {
			
			return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientUserInfo.SettingInfo.class));
		
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		}

		return null;
		
	
	}

	public int updateSetting(String no, Integer role, Integer auto, Integer pushYn) {
		
		String s = " UPDATE app_student_userinfo SET auto=:auto, pushYn=:pushYn WHERE no=:no AND useYn=:useYn ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("auto", auto);
		args.put("pushYn", pushYn);
		args.put("no", no);
		args.put("useYn", Constants.USE);
		
			int result = 0;
		
			try {
				
				 result = jdbcTemplate.update(s, args);
				
				} catch (EmptyResultDataAccessException e) {
					e.printStackTrace();
				}
	
			return result;
		
	}
	
	public AppClientUserInfo.AfterSet selectAfterSetInfo(String no) {
		
		String s = " SELECT no, name as id ,auto,pushYn,version " +
				   " FROM app_student_userinfo " +
				   " WHERE no=:no AND useYn=:useYn ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("no", no);
		args.put("useYn", Constants.USE);

		return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientUserInfo.AfterSet.class));
	
	}

	public String selectCountryRank(String paramBranchId, String paramMemberId) {
			
		String s = " SELECT  a.total as countryRanking " +
				   " FROM (SELECT @rownum:=@rownum+1 as total ,asub.* " +
				   " 		FROM   (SELECT branchNm, memberNm AS name, SUM(time) AS time, memberId " + 
				   " 				FROM statistics_entry_day where 1=1  " +
				   //" 				AND branchId NOT IN ('033f8817-71a0-4feb-bf7b-f9f184da7317') AND useYn=:useYn " +
				   " 				AND useYn=:useYn " +
				   " 				GROUP BY memberId " +
				   " 				ORDER BY time desc ) asub, (SELECT @rownum:=0) tempNum " + 
				   " 	   ) a " +
				   " WHERE a.memberId=:memberId ";
				
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("branchId", paramBranchId);
		args.put("memberId", paramMemberId);
		args.put("useYn", Constants.USE);
		
		try {
			
				return jdbcTemplate.queryForObject(s, args, String.class);
			
			} catch (EmptyResultDataAccessException e) {
				e.printStackTrace();
			}
			
		return null;
		
	}

	public String selectCntrTotalMember(String paramBranchId, String paramMemberId) {
		
		String s = " SELECT IF(COUNT(DISTINCT memberid) = 0, null, COUNT(DISTINCT memberid)) AS totalmember FROM statistics_entry_day " + 
				   " WHERE branchId=:branchId ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("branchId", paramBranchId);
		args.put("memberId", paramMemberId);
		args.put("useYn", Constants.USE);
		
		try {
			
				return jdbcTemplate.queryForObject(s, args, String.class);
			
			} catch (EmptyResultDataAccessException e) {
				e.printStackTrace();
			}
			
		return null;
}

	/*public List<AppClientStudy.StudyPlan> selectSuccessChk(String planId, String mainBranchId, String memberId, String no,
			Integer role) {*/
	
	public AppClientStudy.StudyPlan selectSuccessChk( String mainBranchId, String memberId, String no,
			Integer role) {
		
		String s = " SELECT studyStatus, DATE_FORMAT(startDt, '%y.%m.%d') AS startDt, DATE_FORMAT(endDt, '%y.%m.%d') AS endDt, " + 
				   " CONVERT(SEC_TO_TIME(goalTime),  CHAR CHARACTER SET utf8 ) AS goalTime " +
				   " FROM branch_learning_time " + 
				   " WHERE 1=1 " +
				   //" AND id=:id " +
				   " and branchId=:branchId " +
				   " and memberId=:memberId " +
				   " AND no=:no AND role=:role AND useYn=:useYn " + 
				   " ORDER BY insertDt desc, updateDt desc LIMIT 1 ";				
		
			Map<String, Object> args = new HashMap<>();
			
			//args.put("id", planId);
			args.put("branchId", mainBranchId);
			args.put("memberId", memberId);
			args.put("no", no);
			args.put("role", role);
			args.put("useYn", Constants.USE);
		
			
			try {
				
				return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientStudy.StudyPlan.class));
			
			} catch (EmptyResultDataAccessException e) {
				e.printStackTrace();
			}
			
		return null;
	}


		public int selectUserStudent(String tel, String name, String id) {
		
		String s = " SELECT count(*) " + 
				   " FROM app_student_member " + 
				   " WHERE 1=1 " +
				   " AND studentName=:name " +
				   " AND studentId=:id " +
				   " AND tel=:tel AND useYn=:useYn ";
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("tel", tel);
		args.put("name", name);
		args.put("id", id);
		args.put("useYn", Constants.USE);
		
        try {
			
			return jdbcTemplate.queryForObject(s, args, int.class);
			 
		} catch(EmptyResultDataAccessException e) {
			
			return 0;
		}
	}       
        
    	public int selectUserParents(String tel, String name, String id) {
    		
    		String s = " SELECT  count(*) " +
    				   " FROM app_parents_member " +
    				   " WHERE 1=1 " + 
    				   " AND parentsName=:name " +
    				   " AND parentsId=:id " +
    				   " AND tel=:tel ";
    		
    		Map<String, Object> args = new HashMap<>();
    		
    		args.put("tel", tel);
    		args.put("name", name);
    		args.put("id", id);
    		args.put("useYn", Constants.USE);
    		
            try {
    			
    			return jdbcTemplate.queryForObject(s, args, int.class);
    			 
    		} catch(EmptyResultDataAccessException e) {
    			
    			return 0;
    		}
        
	}


		public AppClientStudy.GoalStatus selectStudyGet(String mainBranchId, String memberId, String no, Integer latestYn) {
			
			String s = " SELECT  id, branchId, no, role, " + 
				       " IFNULL(learningTime, 0) AS unConvLTime, " + 
				       " goalTime AS unConvGTime, " +
				       " IF((goalTime)-(IFNULL(learningTime, 0)) < 0, " +  
				       " 0, (goalTime)-(IFNULL(learningTime, 0)) " + 
					   "  ) AS diffHours, " + 
					   " CONVERT(SEC_TO_TIME(IF((goalTime)-(IFNULL(learningTime, 0)) < 0, " +  
					   " 0, (goalTime)-(IFNULL(learningTime, 0)) " + 
					   " )) , CHAR CHARACTER SET utf8 ) AS convDiff, " + 
					   " SEC_TO_TIME(IFNULL(learningTime, 0)) AS learningTime, " +  
					   " IF(TRUNCATE(((goalTime)/(datediff(endDt, startDt)+1))/3600, 1) = '0.5', " + 
					   " 30, TRUNCATE(((goalTime)/(datediff(endDt, startDt)+1))/3600, 0)) as goalTime, " +
					   " DATE_FORMAT(startDt, '%y.%m.%d') AS startDt, " + 
					   " DATE_FORMAT(endDt, '%y.%m.%d') AS endDt, " + 
					   " DATEDIFF(endDt, startDt)+1 AS diffDays, " + 
					   " studyStatus " +  
					   " FROM branch_learning_time " + 
					   " WHERE useYn=:useYn AND no=:no " + 
					   " AND branchId=:branchId ";
					   if(latestYn == 1) {
						   s += " ORDER BY insertDt desc, updateDt desc ";
					   } else {
						   s += " AND studyStatus=20 ORDER BY insertDt desc, updateDt desc ";
					   }
                       
					   s += " LIMIT 1 ";
			
			Map<String, Object> args = new HashMap<>();
			
			args.put("branchId", mainBranchId);
			//args.put("memberId", memberId);
			args.put("no", no);
			args.put("useYn", Constants.USE);
			
			try {
				
				return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientStudy.GoalStatus.class));
				 
			} catch(EmptyResultDataAccessException e) {
				
				return null;
			}
			
		}

		
		
		public String selectGetName(String no, Integer role) {
			
			String s = " SELECT studentName AS name FROM app_student_member WHERE no=:no AND role=:role " +
					   " ANd useYn=:useYn ";
		
			
			Map<String, Object> args = new HashMap<>();
			
			args.put("no", no);
			args.put("role", role);
			args.put("useYn", Constants.USE);
			
			try {
				
				return jdbcTemplate.queryForObject(s, args, String.class);
				 
			} catch(EmptyResultDataAccessException e) {
				
				return null;
			}

		}

		public AppClientStudy.GoalStatus selectLatestStudyGet(String mainBranchId, String memberId, String no) {
			
			String s = "	SELECT   branchId ,memberId ,no ,role ,startDt " +
					   " ,endDt ,SEC_TO_TIME(IFNULL(learningTime, 0)) AS learningTime ,studyStatus " +
					   " ,IF(TRUNCATE(((goalTime)/(datediff(endDt, startDt)+1))/3600, 1) = '0.5', " + 
					   " 30, TRUNCATE(((goalTime)/(datediff(endDt, startDt)+1))/3600, 0)) as goalTime " +  		
					   " FROM branch_learning_time " +
					   " WHERE useYn=:useYn AND no=:no  " +
					   " ORDER BY insertDt desc, updateDt desc LIMIT 1 ";
			
			Map<String, Object> args = new HashMap<>();
			
			args.put("no", no);
			args.put("useYn", Constants.USE);
			
			try {
				
					return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(AppClientStudy.GoalStatus.class));
				 
			} catch(EmptyResultDataAccessException e) {
				
					return null;
			}

		}
}