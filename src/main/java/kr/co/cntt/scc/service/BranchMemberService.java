package kr.co.cntt.scc.service;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.app.admin.model.AppClientUserInfo;
import kr.co.cntt.scc.app.admin.model.statusList.StatusListDeskInfo;
import kr.co.cntt.scc.app.admin.model.statusList.StatusListEntry;
import kr.co.cntt.scc.app.student.model.AppClientMember;
import kr.co.cntt.scc.app.student.model.Rank;
import kr.co.cntt.scc.app.student.model.ReportingTime;
import kr.co.cntt.scc.app.student.model.StatisticsEntry;
import kr.co.cntt.scc.app.student.service.AppClientMemberService;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.model.BranchMember;
import kr.co.cntt.scc.model.Entry;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.model.MembershipCard;
import kr.co.cntt.scc.model.Page;
import kr.co.cntt.scc.model.Reservation;
import kr.co.cntt.scc.service.BranchReservationService.DataModelTest;
import kr.co.cntt.scc.service.BranchReservationService.UserDataModel;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.util.DateUtil;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jslivane on 2016. 4. 5..
 */
@Service
@Transactional
@Slf4j
public class BranchMemberService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;
    
    @Autowired
    AppClientMemberService appClientMemberService;

    @Autowired
    BranchService branchService;
    
	@Getter
	@Setter
	static class DataModelTest<T>{
		private String mode;
		private T data;
	}

    @Getter
    @Setter
    static class UserDataModel {
    	private String name;
    	private String userId;
    	private int startTime;
    	private int endTime;
    }
    
    public BranchMemberService() {

    }


    /**************************************************************************************/

    //멤버쉽 카드 번호 조회
    public List<MembershipCard> selectMembershipNoList() {

        String s = " SELECT m.membershipId " +
                " FROM membershipcard m " +
                " WHERE useYn = :useYn ";


        Map<String, Object> args = new HashMap<>();
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(MembershipCard.class));

    }
    
    @CacheEvict(cacheNames = "members", key = "#branchId")
    public int updateMembership(String branchId, String memberId, BranchMember member) {
        String s = " UPDATE membershipcard SET " +
                " no = :no, memberId = :memberId, registerBranchId = :branchId ,updateDt = NOW() " +
                " WHERE membershipId = :membershipNo ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(member);
        source.addValue("memberId", memberId);
        source.addValue("branchId", branchId);

        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to update membership");

        } else {
            History history = new History(branchId, Constants.HistoryType.MEMBER_UPDATE, member.toString());
            history.setMemberId(memberId);
            historyService.insertHistory(history);

        }

        return result;


    }
    
    
    
    /**
     * (캐시 제거) 지점회원 리스트
     */
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void resetUserList() { }
    
    /**
     * (캐시) 지점회원 리스트 조회
     * @param branchId
     * @return
     * Cache : http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html
     */

    @Cacheable(cacheNames = "members", key = "#branchId")
    public List<BranchMember> selectMemberList(String branchId){
    	List<BranchMember> memberList = selectMemberList(branchId, null, null); 
    	
    	//appMember 가져오기
//    	List<BranchMember> appMemberList = appClientMemberService.selectAppBranchManagerForCodi(branchId, null);
//		if (appMemberList.size() > 0) {
//			memberList.addAll(appMemberList);
//		}
    	
    	return memberList;
    	
    }
    
    public List<BranchMember> selectMemberList(String branchId, String memberId, Page page) {

        String s = " SELECT m.branchId, m.memberId, substring(m.insertDt, 1, 10) as regDt, m.no, m.membershipNo ,m.name, m.tel, m.telParent, m.email, m.school, m.schoolGrade, " +
                " m.gender, m.birthDt, m.postcode, m.address1, m.address2, m.addressDetail, m.checkoutYn, m.examType, m.jobType, m.interestType, m.expireYn, m.timeYn, m.remainTime, " +                
                " if ( isnull(sms.enterexitYes), '0', sms.enterexitYes ) as enterexitYes, " +
                " if ( isnull(sms.smsYes), '0', sms.smsYes ) as smsYes, " +
                " if ( isnull(sms.promotionYes), '0', sms.promotionYes ) as promotionYes, " +                
                " if ( isnull(sms.personalYn), '0', sms.personalYn ) as personalYn, " +
                " if ( isnull(sms.utilYn), '0', sms.utilYn ) as utilYn, " +                              
                " memberNote,m.insertDt, m.updateDt, m.cabinet," +
		               
                " ( " +
		                " SELECT name " +
		                " FROM branch b " +
		                " WHERE m.branchId = b.branchId " +
		                " AND b.useYn = :useYn " +
		                " AND b.visibleYn = :visibleYn " +    
		        " ) AS branchNm " +
		                
                " FROM branch_member m " +
                " LEFT OUTER JOIN sms_approve as sms ON m.memberId = sms.memberId" +
                " WHERE m.branchId = :branchId AND m.useYn = :useYn ";
        
        if(!StringUtils.isEmpty(memberId)) {
            s += " AND m.memberId = :memberId ";

        }
        //s += "ORDER BY m.insertDt";
        
        //앱회원 추가
        s += " UNION " + 
        		" SELECT abm.branchId, abm.memberId, substring(m.insertDt, 1, 10) as regDt, m.no, NULL AS membershipNo ,m.studentName as name, m.tel, m.telParent, m.email, 0 as school, 0 as schoolGrade, " +
                " m.gender, m.birthDt, m.postcode, m.address1, m.address2, m.addressDetail, m.checkoutYn, 0 as examType, m.job as jobType, m.interest as interestType, '0' as expireYn, 0 AS timeYn, 0 AS remainTime," +                
                " if ( isnull(sms.enterexitYes), '0', sms.enterexitYes ) as enterexitYes, " +
                " if ( isnull(sms.smsYes), '0', sms.smsYes ) as smsYes, " +
                " if ( isnull(sms.promotionYes), '0', sms.promotionYes ) as promotionYes, " +
                " if ( isnull(sms.personalYn), '0', sms.personalYn ) as personalYn, " +
                " if ( isnull(sms.utilYn), '0', sms.utilYn ) as utilYn, " +
                " NULL as memberNote,m.insertDt, m.updateDt, m.cabinet, " +
		               
                " ( " +
		                " SELECT name " +
		                " FROM branch b " +
		                " WHERE abm.branchId = b.branchId " +
		                " AND b.useYn = :useYn " +
		                " AND b.visibleYn = :visibleYn " +    
		        " ) AS branchNm " +
		                
        " FROM app_student_member m " +
        " JOIN app_branch_manager abm ON (abm.no = m.no AND abm.useYn = :useYn) " +
        " LEFT OUTER JOIN sms_approve as sms ON m.no = sms.no" +
        
        " WHERE abm.branchId = :branchId " +
        " AND m.useYn = :useYn " ;
        
        
        if(!StringUtils.isEmpty(memberId)) {
            s += " AND abm.memberId = :memberId ";

        }
        s += "ORDER BY insertDt";
        
        if(page == null) {}
        else {
	        int perPage = (page.getPage() -1) * page.getPerPageNum();
	    	s += " limit "+perPage+", "+page.getPerPageNum()+" ";
        }

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("useYn", Constants.USE);
        args.put("visibleYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(BranchMember.class));

    }
    
    public List<BranchMember> selectAllMemberList(String branchId) {

        String s = " SELECT m.branchId, m.memberId, substring(m.insertDt, 1, 10) as regDt, m.no, m.membershipNo ,m.name, m.tel, m.telParent, m.email, m.school, m.schoolGrade," +
                " m.gender, m.birthDt, m.postcode, m.address1, m.address2, m.addressDetail, m.checkoutYn, m.examType, m.jobType, m.interestType, m.expireYn, m.timeYn, m.remainTime," +
                " if ( isnull(sms.enterexitYes), '0', sms.enterexitYes ) as enterexitYes, " +
                " if ( isnull(sms.smsYes), '0', sms.smsYes ) as smsYes, " +
                " if ( isnull(sms.promotionYes), '0', sms.promotionYes ) as promotionYes, " +
                " if ( isnull(sms.personalYn), '0', sms.personalYn ) as personalYn, " +
                " if ( isnull(sms.utilYn), '0', sms.utilYn ) as utilYn, " +
                " memberNote,m.insertDt, m.updateDt, cabinet " +
        		" FROM branch_member m " +
                " LEFT OUTER JOIN sms_approve as sms ON m.memberId = sms.memberId" +
                " WHERE m.branchId = :branchId AND m.useYn = :useYn ";
        
      //앱회원 추가
        s += " UNION " + 
        		" SELECT abm.branchId, abm.memberId, substring(m.insertDt, 1, 10) as regDt, m.no, NULL AS membershipNo ,m.studentName as name, m.tel, m.telParent, m.email, 0 as school, 0 as schoolGrade, " +
                " m.gender, m.birthDt, m.postcode, m.address1, m.address2, m.addressDetail, m.checkoutYn, 0 as examType, m.job as jobType, m.interest as interestType, '0' as expireYn, 0 as timeYn, 0 as remainTime, " +                
                " if ( isnull(sms.enterexitYes), '0', sms.enterexitYes ) as enterexitYes, " +
                " if ( isnull(sms.smsYes), '0', sms.smsYes ) as smsYes, " +
                " if ( isnull(sms.promotionYes), '0', sms.promotionYes ) as promotionYes, " +
                " if ( isnull(sms.personalYn), '0', sms.personalYn ) as personalYn, " +
                " if ( isnull(sms.utilYn), '0', sms.utilYn ) as utilYn, " +
                " NULL as memberNote,m.insertDt, m.updateDt, m.cabinet " +
		                
        " FROM app_student_member m " +
        " JOIN app_branch_manager abm ON (abm.no = m.no AND abm.useYn = :useYn) " +
        " LEFT OUTER JOIN sms_approve as sms ON m.no = sms.no" +
        
        " WHERE abm.branchId = :branchId " +
        " AND m.useYn = :useYn " ;
        
   
        
        s += "ORDER BY insertDt";


        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("useYn", Constants.USE);

        //jdbcTemplate.
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(BranchMember.class));

    }
    

    public BranchMember selectMember(String branchId, String memberId) {
        List<BranchMember> members = selectMemberList(branchId, null, null);

        for(BranchMember member: members) {
            if(memberId.equals(member.getMemberId())) return member;

        }

        return null;

    }

    public BranchMember selectMemberByNo(String branchId, String no) { //memberid, page = null값
        List<BranchMember> members = selectMemberList(branchId, null, null);

        for(BranchMember member: members) {
            
        	if(no.equals(member.getNo())) { 
        		System.out.println("==============================" + member);
        		return member; }
            
        }

        return null;

    }

    @CacheEvict(cacheNames = "members", key = "#branchId")
    public int insertMember(String branchId, String memberId, BranchMember member) {
        String s = " INSERT INTO branch_member ( " +
                " branchId, memberId, no, membershipNo, name, tel, telParent, telEtc, email, school, schoolGrade, " +
                " gender, birthDt, postcode, address1, address2, addressDetail, memberNote, cabinet, examType, jobType, interestType, timeYn, remainTime " +
                " ) VALUES ( " +
                " :branchId, :memberId, :no, :membershipNo, :name, :tel, :telParent, :telEtc, :email, :school, :schoolGrade, " +
                " :gender, :birthDt, :postcode, :address1, :address2, :addressDetail, :memberNote, :cabinet, :examType, :jobType, :interestType, :timeYn, :remainTime " +
                " ) ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(member);
        source.addValue("branchId", branchId);
        source.addValue("memberId", memberId);

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to create member");

        } else {
            History history = new History(branchId, Constants.HistoryType.MEMBER_CREATE, member.toString());
            history.setMemberId(memberId);
            historyService.insertHistory(history);
            
            //장기동 센터일 때만  || branchId.equals("28f6b3ca-1289-43f3-971a-9135120b0eb4")
            	Branch branch = branchService.selectBranch(branchId);
	            if (branch.getFingerprintYn() == 1) {
	            	//지문인식기 데이터 삽입
		            Socket socket = null;
		    		BufferedReader in = null;
		    		PrintWriter out = null;
		    		try {
		    			socket = new Socket(branch.getIp(), 9005);
		//    			socket = new Socket("192.168.170.191", 9005);
		    			out = new PrintWriter(socket.getOutputStream(), true);
		    			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    			UserDataModel user = new UserDataModel();
		    			user.setUserId(member.getNo());
		    			user.setName("");
		    			//user.setStartTime(123456789);
		    			//user.setEndTime(145678912);
		    			DataModelTest<UserDataModel> userModel = new DataModelTest<UserDataModel>();
		    			userModel.setMode("EnrolUser");
		//    			userModel.setMode("UpdateUser");
		    			//userModel.setMode("DeleteUser");
		    			userModel.setData(user);
		    			out.println(new Gson().toJson(userModel));
		    			String response = in.readLine();
		    			System.out.println(response);
		    		}catch (Exception e) {
		    			e.printStackTrace();
		    		} finally {
		    			try {
		    				if(out !=null) out.close();
		    				if(in != null) in.close();
		    				if(socket != null) socket.close();
		    			} catch (Exception e) {
		    			}
		    		}
		
		        }
	            
            	
            	

        }

        return result;

    }
    @CacheEvict(cacheNames = "members", key = "#branchId")
    public int insertMemberApprove(String branchId, String memberId, BranchMember member) {
        String s = " INSERT INTO sms_approve ( " +
                " branchId, memberId, enterexitYes, smsYes, promotionYes, personalYn, utilYn " +
                " ) VALUES ( " +
                " :branchId, :memberId, :enterexitYes, :smsYes, :promotionYes, :personalYn, :utilYn " +
                " ) ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(member);
        source.addValue("branchId", branchId);
        source.addValue("memberId", memberId);

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to create member");

        } else {
            History history = new History(branchId, Constants.HistoryType.MEMBER_CREATE, member.toString());
            history.setMemberId(memberId);
            historyService.insertHistory(history);

        }

        return result;

    }

    @CacheEvict(cacheNames = "members", key = "#branchId")
    public int updateMember(String branchId, String memberId, BranchMember member) {
        String s = " UPDATE branch_member SET " +
                " no = :no, membershipNo = :membershipNo ,name = :name, tel = :tel, telParent = :telParent, "
                + " telEtc = :telEtc, email = :email, school = :school, schoolGrade = :schoolGrade, timeYn = :timeYn, remainTime = :remainTime,"
                + " gender = :gender, birthDt = :birthDt, postcode = :postcode, address1 = :address1, address2 = :address2, addressDetail = :addressDetail, "
                + " memberNote =:memberNote, cabinet = :cabinet, examType = :examType, jobType = :jobType, interestType = :interestType, updateDt = NOW() "
                + " WHERE branchId = :branchId AND memberId = :memberId ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(member);
        source.addValue("branchId", branchId);
        source.addValue("memberId", memberId);

        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to update member");

        } else {
            History history = new History(branchId, Constants.HistoryType.MEMBER_UPDATE, member.toString());
            history.setMemberId(memberId);
            historyService.insertHistory(history);
    		           
        }
        return result;


    }

   // @CacheEvict(cacheNames = "members", key = "#branchId")
    public int updateMemberApprove(String branchId, String memberId, BranchMember member) {
        String s = " INSERT INTO sms_approve ( " +
                " branchId, memberId, enterexitYes, smsYes, promotionYes " +
                " ) VALUES ( " +
                " :branchId, :memberId, :enterexitYes, :smsYes, :promotionYes " +
                " ) " +
                "ON DUPLICATE KEY UPDATE " +
                "enterexitYes = :enterexitYes, smsYes = :smsYes, promotionYes = :promotionYes";
        

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(member);
        source.addValue("branchId", branchId);
        source.addValue("memberId", memberId);

        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to update member");

        } else {
            History history = new History(branchId, Constants.HistoryType.MEMBER_UPDATE, member.toString());
            history.setMemberId(memberId);
            historyService.insertHistory(history);

        }

        return result;


    }

    @CacheEvict(cacheNames = "members", key = "#branchId")
    public int deleteMember(String branchId, String memberId) {
    	 BranchMember branchMember = selectMember(branchId, memberId);
    	
    	String s = " UPDATE branch_member SET " +
                " useYn = :useYn, updateDt = NOW() " +
                " WHERE branchId = :branchId AND memberId = :memberId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("useYn", Constants.NOT_USE);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to delete member");

        } else {
            History history = new History(branchId, Constants.HistoryType.MEMBER_DELETE, "");
            history.setMemberId(memberId);
            historyService.insertHistory(history);
            
           
            Branch branch = branchService.selectBranch(branchId);
            if (branch.getFingerprintYn() == 1) {
            	//지문인식기 데이터 삽입
	            Socket socket = null;
	    		BufferedReader in = null;
	    		PrintWriter out = null;
	    		try {
	    			socket = new Socket(branch.getIp(), 9005);
	//    			socket = new Socket("192.168.170.191", 9005);
	    			out = new PrintWriter(socket.getOutputStream(), true);
	    			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    			UserDataModel user = new UserDataModel();
	    			user.setUserId(branchMember.getNo());
	    			user.setName("");
	    			//user.setStartTime(123456789);
	    			//user.setEndTime(145678912);
	    			DataModelTest<UserDataModel> userModel = new DataModelTest<UserDataModel>();
	//    			userModel.setMode("EnrolUser");
	//    			userModel.setMode("UpdateUser");
	    			userModel.setMode("DeleteUser");
	    			userModel.setData(user);
	    			out.println(new Gson().toJson(userModel));
	    			String response = in.readLine();
	    			System.out.println(response);
	    		}catch (Exception e) {
	    			e.printStackTrace();
	    		} finally {
	    			try {
	    				if(out !=null) out.close();
	    				if(in != null) in.close();
	    				if(socket != null) socket.close();
	    			} catch (Exception e) {
	    			}
	    		}
	
	        }
            

        }

        return result;

    }


    /**************************************************************************************/


    /**
     * 다음 지점회원 번호 조회
     * @param branchId
     * @return
     */
    public String selectMemberNextNo(String branchId) {
        String s = " SELECT IFNULL(MAX(m.no), 0) + 1 " +
                " FROM branch_member m " +
                " WHERE m.branchId = :branchId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);

        return jdbcTemplate.queryForObject(s, args, String.class);

    }


    /**************************************************************************************/

    /**
     * 지점회원 번호 조회
     * @param branchId
     * @return
     */
    public List<String> selectMemberNo(String branchId) {
        String s = " SELECT no" +
                " FROM branch_member m " +
                " WHERE m.branchId = :branchId " +
                " AND m.useYn = '1' ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);

        return jdbcTemplate.queryForList(s, args, String.class);

    }


    /**************************************************************************************/
    
    
    /**
     * 지점회원 출입기록 리스트 조회
     * @param branchId
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Entry> selectMemberEntryList(String branchId, String startDate, String endDate,
                                             String memberId, String roomId, String deskId, Page page) {            	
    	    	
    	Integer CurrentTime = Integer.parseInt(DateUtil.getCurrentTimeShortString().replaceAll(":", ""));
    	
    	if(StringUtils.isEmpty(startDate)) {
            if (CurrentTime >= 0000 && CurrentTime <= 0300 ) {
            	startDate = DateUtil.getCurrentDatePlusDaysString(-1);
            }
            else {
            	startDate = DateUtil.getCurrentDateString();
            }
    		
            startDate = startDate + " " + DateUtil.getStartTime();
        }

        if(StringUtils.isEmpty(endDate)) {
        	if (CurrentTime >= 0000 && CurrentTime <= 0300 ) {
        		endDate = DateUtil.getCurrentDateString();
        	}
        	else {
        		endDate = DateUtil.getCurrentDatePlusDaysString(1);
        	}            
            
        	endDate = endDate + " " + DateUtil.getEndTime();
        }
    	
        
        String s = " SELECT e.branchId, e.memberId, e.entryDt, e.entryType, " +
                " e.reservationId, e.deskId, e.insertDt, e.updateDt " +
                " FROM branch_member_entry e " +
                " JOIN branch_member m ON (m.branchId = e.branchId AND m.memberId = e.memberId AND m.useYn = :useYn) " +
                " WHERE e.branchId = :branchId " +                
                " AND e.entryDt >= :startDate AND e.entryDt <= :endDate " +
                " AND e.useYn = :useYn ";

        if(!StringUtils.isEmpty(memberId)) {
            s += " AND e.memberId = :memberId ";

        }

//    	if(!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
//    		s += " AND e.businessDt BETWEEN :startDt AND :endDt ";
//    	}
        
        if(!StringUtils.isEmpty(roomId)) {
            s += " AND e.deskId IN (SELECT d.deskId FROM branch_room r JOIN branch_desk d " +
                    " ON (d.branchId = r.branchId AND d.roomId = r.roomId AND d.useYn = :useYn)" +
                    " WHERE r.branchId = :branchId AND r.roomId = :roomId AND r.useYn = :useYn) ";

        }

        if(!StringUtils.isEmpty(deskId)) {
            s += " AND e.deskId = :deskId ";

        }

        
        
        //앱회원 추가
        s += " UNION " +
        	 " SELECT e.branchId, e.memberId, e.entryDt, e.entryType, " +
                " e.reservationId, e.deskId, e.insertDt, e.updateDt " +
                " FROM branch_member_entry e " +                

		   " JOIN app_branch_manager abm ON (abm.branchId = e.branchId AND abm.memberId = e.memberId AND abm.useYn = :useYn) " +
		   " WHERE e.branchId = :branchId " +                
		   " AND e.entryDt >= :startDate AND e.entryDt <= :endDate " +
		   " AND e.useYn = :useYn ";
        
        if(!StringUtils.isEmpty(memberId)) {
            s += " AND e.memberId = :memberId ";

        }
        
//    	if(!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
//    		s += " AND e.businessDt BETWEEN :startDt AND :endDt ";
//    	}

        if(!StringUtils.isEmpty(roomId)) {
            s += " AND e.deskId IN (SELECT d.deskId FROM branch_room r JOIN branch_desk d " +
                    " ON (d.branchId = r.branchId AND d.roomId = r.roomId AND d.useYn = :useYn)" +
                    " WHERE r.branchId = :branchId AND r.roomId = :roomId AND r.useYn = :useYn) ";

        }

        if(!StringUtils.isEmpty(deskId)) {
            s += " AND e.deskId = :deskId ";

        }
        
        s += "ORDER BY entryDt DESC";
        
        if(page == null) {}
        else {
	        int perPage = (page.getPage() -1) * 10;
	    	s += " limit "+perPage+", "+page.getPerPageNum()+" ";
        }
        
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("roomId", roomId);
        args.put("deskId", deskId);
        args.put("useYn", Constants.USE);
        args.put("startDate", startDate);
        args.put("endDate", endDate);
        //args.put("startDate", startDate + " " + DateUtil.MIN_TIME);
        //args.put("endDate", endDate + " " + DateUtil.MAX_TIME);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Entry.class));

    }


    public int insertMemberEntry(String branchId, String memberId, String entryType, Reservation reservation) {
        String s = " INSERT INTO branch_member_entry ( " +
                " branchId, memberId, entryDt, entryType, reservationId, deskId, businessDt " +
                " ) VALUES ( " +
                " :branchId, :memberId, NOW(), :entryType, :reservationId, :deskId,  CASE " +
																					" WHEN STR_TO_DATE(substring(NOW(), 11, 18), '%H:%i:%s') >= STR_TO_DATE('00:00:00', '%H:%i:%s') " +
																					" AND STR_TO_DATE(substring(NOW(), 11, 18), '%H:%i:%s') <= STR_TO_DATE('03:00:00', '%H:%i:%s') " +
																					" THEN date_add(substring(NOW(), 1, 10), interval -1 day) " +
																					" ELSE substring(NOW(), 1, 10) " +
																					" END " +
                " ) ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("entryType", entryType);
        if (reservation == null) {
        	args.put("reservationId", null);
        	args.put("deskId", null);
        } else {
        	args.put("reservationId", reservation.getReservationId());
        	args.put("deskId", reservation.getDeskId());
        }
        
        

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to create entry");

        } else {
            History history = new History(branchId, Constants.HistoryType.MEMBER_ENTRY_CREATE, entryType);
            history.setMemberId(memberId);
            if (reservation == null) {
            	history.setReservationId(null);
                history.setDeskId(null);
            } else {
            	history.setReservationId(reservation.getReservationId());
                history.setDeskId(reservation.getDeskId());	
            }
            
            historyService.insertHistory(history);

        }

        return result;

    }

    public List<Entry> selectRecentMemberEntryListByMemberId(String branchId, String memberId) {
        String s = " SELECT e.branchId, e.memberId, e.entryDt, e.entryType, " +
                " e.reservationId, e.deskId, e.businessDt, e.insertDt, e.updateDt " +
                " FROM branch_member_entry e " +
                " WHERE e.branchId = :branchId AND e.memberId = :memberId " +
                " AND e.useYn = :useYn " + 

        " ORDER BY entryDt DESC LIMIT 3";
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
//        args.put("startDate", startDate + " " + DateUtil.MIN_TIME);
//        args.put("endDate", endDate + " " + DateUtil.MAX_TIME);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Entry.class));

    }
    
    public List<StatusListEntry> selectMemberEntryListByBusinessDt(String branchId, String memberId, String businessDt) {
        String s = " SELECT SUBSTRING(e.entryDt, 1, 10) as entryDt, SUBSTRING(e.entryDt, 12, 17) as entryTm, "
        		+ "e.entryType, e.entryDt as entryDtOg, e.businessDt " +
                " FROM branch_member_entry e " +
                " WHERE e.branchId = :branchId AND e.memberId = :memberId " +
                " AND e.businessDt = :businessDt " +
                " AND e.useYn = :useYn " + 

        " ORDER BY SUBSTRING(entryDt, 1, 10), SUBSTRING(entryDt, 12, 17), entryType";
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("businessDt", businessDt);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(StatusListEntry.class));

    }

    /**
     * 지점회원 직업 / 학교 리스트 조회
     * @param branchId
     * @return
     */

    public List<String> selectSchoolList(String branchId) {
        String s = " SELECT DISTINCT m.school " +
                " FROM branch_member m " +
                " WHERE m.branchId = :branchId AND useYn = :useYn ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.queryForList(s, args, String.class);

    }

    /**************************************************************************************/

    /**
     * 현재 모든지점, 모든회원 entry 상태 조회
     * 
     *     	if(StringUtils.isEmpty(startDate)) {
            startDate = DateUtil.getCurrentDateString();
            startDate = startDate + " " + DateUtil.getStartTime();
        }    	

        if(StringUtils.isEmpty(endDate)) {
            endDate = DateUtil.getCurrentDatePlusDaysString(1);
            endDate = endDate + " " + DateUtil.getEndTime();
        }
     */
    public List<Entry> selectCurrentMemberEntryList(){
    	String s = " SELECT e.branchId, e.memberId, e.entryDt, " +
    			" substr(max(concat(date_format(e.entryDt, '%Y%m%d%H%i%S'),e.entryType)),15,1) as entryType, " +
    	        " e.reservationId, e.deskId, e.insertDt, e.updateDt " +
    	        " FROM branch_member_entry e" +
    	        //" WHERE entryType not in(:entryType)" +
    	        " WHERE e.entryDt >= :startDate AND e.entryDt <= :endDate " +
    	        " AND e.useYn = :useYn" +
    	        " GROUP BY e.memberId";
    	        //" ORDER BY e.insertDt DESC ";
    	        //" LIMIT 1 ";
    	
    	Map<String, Object> args = new HashMap<>();
    	//Constants.EntryType.OUT.getValue()
    	//args.put("entryType", 2);
    	args.put("startDate", DateUtil.getCurrentDatePlusDaysString(-1) + " " + DateUtil.getStartTime());
    	args.put("endDate", DateUtil.getCurrentDateString() + " " + DateUtil.getEndTime());
    	args.put("useYn", Constants.USE);
    	    	
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Entry.class));
    }
    
    /**
     * 
     * 
     * 
     */
    public int insertMemberEntryOUT(String branchId, String memberId, String reservationId , String deskId) {
        String s = " INSERT INTO branch_member_entry ( " +
                " branchId, memberId, entryDt, entryType, reservationId, deskId, businessDt " +
                " ) VALUES ( " +
                " :branchId, :memberId, NOW(), :entryType, :reservationId, :deskId, DATE_ADD(CURDATE(), INTERVAL -1 DAY) " +
                " ) ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("entryType", Constants.EntryType.OUT.getValue());
        args.put("reservationId", reservationId);
        args.put("deskId", deskId);
        

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to create entry");

        } else {
        	History history = new History(branchId, Constants.HistoryType.MEMBER_ENTRY_CREATE, Constants.EntryType.OUT.getValue());

            history.setMemberId(memberId);
            history.setReservationId(reservationId);
            history.setDeskId(deskId);
            historyService.insertHistory(history);

        }

        return result;

    }
    
    /*******************************AppAdmin****************************************/
    public StatusListDeskInfo selectAppStatusListDeskInfo(String branchId, String memberId) {
    	String s = " SELECT IFNULL(( SELECT entryType " +
								  " FROM branch_member_entry e " +
								  " WHERE e.memberId = r.memberId " +
								  " AND e.branchId = r.branchId " +
								  " AND e.useYn = :useYn " +
								  " ORDER BY e.entryDt DESC LIMIT 1 " +
								  " ), '2') AS entryType," +
								  " bm.name AS name, bm.gender AS gender , brm.name AS roomName, bd.name AS deskName, bm.jobType AS school, r.reservationDt " +                 
				  " FROM branch_reservation r " +					                
				  " JOIN branch_desk bd on r.deskId = bd.deskId " +
				  " JOIN branch_room brm on bd.roomId = brm.roomId " +
				  " JOIN branch_member bm on r.memberId = bm.memberId " +						 
				  " WHERE r.branchId = :branchId " +
				  " AND r.memberId = :memberId " +
				  " AND r.reservationStatus = :reservationStatus " +
				  " AND r.useYn = :useYn " ;
				  //앱 회원 추가
				  s += " UNION " +
					  " SELECT IFNULL(( SELECT entryType " +
					  " FROM branch_member_entry e " +
					  " WHERE e.memberId = r.memberId " +
					  " AND e.branchId = r.branchId " +
					  " AND e.useYn = :useYn " +
					  " ORDER BY e.entryDt DESC LIMIT 1 " +
					  " ), '2') AS entryType," +
					  " asm.studentName AS name, asm.gender AS gender , brm.name AS roomName, bd.name AS deskName, asm.job AS school, r.reservationDt " +                 
					  " FROM branch_reservation r " +					                
					  " JOIN branch_desk bd on r.deskId = bd.deskId " +
					  " JOIN branch_room brm on bd.roomId = brm.roomId " +
					  //" JOIN branch_member bm on r.memberId = bm.memberId " +
		              " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = :useYn) " +
		              " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
					  " WHERE r.branchId = :branchId " +
					  " AND r.memberId = :memberId " +
					  " AND r.reservationStatus = :reservationStatus " +
					  " AND r.useYn = :useYn " ;
						  
				  
				  
				  s += " ORDER BY reservationDt DESC LIMIT 1 "; 

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("reservationStatus", 20);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(StatusListDeskInfo.class));

    }
    
    
    /*******************************AppAdmin****************************************/
    public StatusListDeskInfo reportingAppStatusListDeskInfo(String branchId, String memberId) {
    	String s = " SELECT IFNULL(( SELECT entryType " +
								  " FROM branch_member_entry e " +
								  " WHERE e.memberId = r.memberId " +
								  " AND e.branchId = r.branchId " +
								  " AND e.useYn = :useYn " +
								  " ORDER BY e.entryDt DESC LIMIT 1 " +
								  " ), '2') AS entryType," +
								  " bm.name AS name, bm.gender AS gender , brm.name AS roomName, bd.name AS deskName, bm.jobType AS school, r.reservationDt " +                 
				  " FROM branch_reservation r " +					                
				  " JOIN branch_desk bd on r.deskId = bd.deskId " +
				  " JOIN branch_room brm on bd.roomId = brm.roomId " +
				  " JOIN branch_member bm on r.memberId = bm.memberId " +						 
				  " WHERE r.branchId = :branchId " +
				  " AND r.memberId = :memberId " +
				  " AND r.reservationStatus = :reservationStatus " +
				  " AND r.useYn = :useYn " ;
				  //앱 회원 추가
				  s += " UNION " +
					  " SELECT IFNULL(( SELECT entryType " +
					  " FROM branch_member_entry e " +
					  " WHERE e.memberId = r.memberId " +
					  " AND e.branchId = r.branchId " +
					  " AND e.useYn = :useYn " +
					  " ORDER BY e.entryDt DESC LIMIT 1 " +
					  " ), '2') AS entryType," +
					  " asm.studentName AS name, asm.gender AS gender , brm.name AS roomName, bd.name AS deskName, asm.job AS school, r.reservationDt " +                 
					  " FROM branch_reservation r " +					                
					  " JOIN branch_desk bd on r.deskId = bd.deskId " +
					  " JOIN branch_room brm on bd.roomId = brm.roomId " +
					  //" JOIN branch_member bm on r.memberId = bm.memberId " +
		              " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = :useYn) " +
		              " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = :useYn AND asm.transferYn = 0) " +
					  " WHERE r.branchId = :branchId " +
					  " AND r.memberId = :memberId " +
					  " AND r.reservationStatus = :reservationStatus " +
					  " AND r.useYn = :useYn " ;
						  
				  
				  
				  s += " ORDER BY reservationDt DESC LIMIT 1 "; 

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("reservationStatus", 20);
        args.put("useYn", Constants.USE);
        
    	try {
			
    		return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(StatusListDeskInfo.class));
			 
		} catch(EmptyResultDataAccessException e) {
			
			return null;
		}
        
    }
    
    
    public List<StatusListEntry> selectStatusListEntryList (String branchId, String memberId, Integer num) {
    	String s = " SELECT SUBSTRING(e.entryDt, 1, 10) AS entryDt, SUBSTRING(e.entryDt, 11, 17) AS entryTm, e.entryType, e.entryDt AS entryDtOg " +    			
                " FROM branch_member_entry e " +
                " WHERE 1=1 " ;

    	if(!StringUtils.isEmpty(branchId)) {
    		s += " AND e.branchId = :branchId ";
    	}        
        
    	if(!StringUtils.isEmpty(memberId)) {
    		s += " AND e.memberId = :memberId ";
    	}                       

    		s += " AND e.useYn = :useYn "
    		  + " order by SUBSTRING(entryDt, 1, 10), SUBSTRING(entryDt, 11, 17), entryType " ;
    		
    	if(!StringUtils.isEmpty(num)) {
    		s += " LIMIT :num, 10 ";
    	}
    	 
    	Map<String, Object> args = new HashMap<>();
	    args.put("branchId", branchId);
	    args.put("memberId", memberId);
	    args.put("num", num);
	    args.put("useYn", Constants.USE);
	
	    return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(StatusListEntry.class));
    }
    
    public List<StatusListEntry> MainSelectStatusListEntryList (String branchId, String memberId, Integer num) {
    	String s = " SELECT SUBSTRING(e.entryDt, 1, 10) AS entryDt, TIME_FORMAT(substring(entryDt, 12, 13), '%H:%i') as entryTm, e.entryType, e.entryDt AS entryDtOg " +    			
                " FROM branch_member_entry e " +
                " WHERE 1=1 " ;

    	if(!StringUtils.isEmpty(branchId)) {
    		s += " AND e.branchId = :branchId ";
    	}        
        
    	if(!StringUtils.isEmpty(memberId)) {
    		s += " AND e.memberId = :memberId ";
    	}                       

    		s += " AND e.useYn = :useYn "
    		  + " order by SUBSTRING(entryDt, 1, 10), SUBSTRING(entryDt, 11, 17), entryType " ;
    		
    	if(!StringUtils.isEmpty(num)) {
    		s += " LIMIT :num, 10 ";
    	}
    	 
    	Map<String, Object> args = new HashMap<>();
	    args.put("branchId", branchId);
	    args.put("memberId", memberId);
	    args.put("num", num);
	    args.put("useYn", Constants.USE);
	
	    return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(StatusListEntry.class));
    }
    
    public List<StatusListEntry> selectStatusListEntryList2 (String branchId, String memberId, Integer num, String startDt, String endDt) {
    	String s = " SELECT SUBSTRING(e.entryDt, 1, 10) AS entryDt, SUBSTRING(e.entryDt, 12, 17) AS entryTm, "
    			+ " e.entryType, e.entryDt AS entryDtOg, e.businessDt AS businessDt " +    			
                " FROM branch_member_entry e " +
                " WHERE 1=1 " ;

    	if(!StringUtils.isEmpty(branchId)) {
    		s += " AND e.branchId = :branchId ";
    	}        
        
    	if(!StringUtils.isEmpty(memberId)) {
    		s += " AND e.memberId = :memberId ";
    	}                       
        
    	if(!StringUtils.isEmpty(startDt) && !StringUtils.isEmpty(endDt)) {
    		s += " AND e.businessDt BETWEEN :startDt AND :endDt ";
    	}


    			s += " AND e.useYn = :useYn "
    			  + " order by SUBSTRING(entryDt, 1, 10), SUBSTRING(entryDt, 12, 17), entryType " ;
    		
    	if(!StringUtils.isEmpty(num)) {
    		s += " LIMIT :num, 10 ";
    	}
    	 
    	Map<String, Object> args = new HashMap<>();
	    args.put("branchId", branchId);
	    args.put("memberId", memberId);
	    args.put("num", num);
	    args.put("startDt", startDt);
	    args.put("endDt", endDt);
	    args.put("useYn", Constants.USE);
	
	    return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(StatusListEntry.class));
    }
    
    //statistics_entry를 위한 쿼리
    public List<ReportingTime.Temp> selectEntryMemberId (String startDt, String endDt) {
    	String s = " SELECT e.memberId, e.branchId " +    			
                " FROM branch_member_entry e " +
                " WHERE 1=1 " ;
    	
    	if(!StringUtils.isEmpty(startDt) && !StringUtils.isEmpty(endDt)) {
    		s += " AND e.businessDt BETWEEN :startDt AND :endDt ";
    	}
         
    			s += " AND e.useYn = :useYn " 
    					//TEST
    					//+ " AND e.memberId = 'f4b081ff-5b8f-4fc2-9959-053fa9256753' "
    			  + " GROUP BY e.memberId, e.branchId ";
    		
    	
    	 
    	Map<String, Object> args = new HashMap<>();
	    args.put("startDt", startDt);
	    args.put("endDt", endDt);
	    args.put("useYn", Constants.USE);
	
	    return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(ReportingTime.Temp.class));
    	
    }

    
    //입퇴실 (학습시간) 스케줄러
    public int insertStatisticsEntry(ReportingTime.TimeTest myTime) {
	    String s = " INSERT INTO statistics_entry_day ( " +
	            " branchId, branchNm, no, memberId, memberNm, businessDt, time, autoYn " +
	            " ) VALUES ( " +
	            " :branchId, :branchNm, :no, :memberId, :memberNm, :businessDt, :time, :autoYn " +
	            " ) "  +
                "ON DUPLICATE KEY UPDATE " +
                "branchId = :branchId, branchNm = :branchNm, no = :no, memberId = :memberId, memberNm = :memberNm, businessDt = :businessDt, time = :time, autoYn = :autoYn ";
	    
	    CombinedSqlParameterSource source = new CombinedSqlParameterSource(myTime);


        int result = jdbcTemplate.update(s, source);
        
	    if (result == 0) {
	        throw new InternalServerError("Failed to create StatisticsEntry");
	
	    } else {
	    	
	
	    }
	
	    return result;
    }
    
    //statistics_entry select
    public List<StatisticsEntry.Entry> selectStatisticsEntry (String startDt, String endDt, String branchId) {
    	String s = " SELECT branchId, branchNm, no, memberId, memberNm, businessDt, "
    			+ " SUM(time) AS totalTime, COUNT(time) AS totalCount "    			
                + " FROM statistics_entry_day "
                + " WHERE 1=1 " ;
    	
    	if(!StringUtils.isEmpty(branchId)) {
    			s += " AND branchId = :branchId ";
    	}
    			
    	if(!StringUtils.isEmpty(startDt) && !StringUtils.isEmpty(endDt)) {
    		s += " AND businessDt BETWEEN :startDt AND :endDt ";
    	}
         
    			s += " AND useYn = :useYn "     			  
    			  + " GROUP BY businessDt "
    			  + " ORDER BY businessDt ";
    		
    	
    	 
    	Map<String, Object> args = new HashMap<>();
	    args.put("startDt", startDt);
	    args.put("endDt", endDt);
	    args.put("branchId", branchId);
	    args.put("useYn", Constants.USE);
	
	    return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(StatisticsEntry.Entry.class));
    	
    }
    
    
  //statistics_entry select rank    
    public List<Rank.RankInfo> selectStatisticsEntryRank (String startDt, String endDt, List<String> branchIdList, String memberId) {
    String s = " SELECT branchNm, name, time, "
    		 + " memberId, @ROWNUM:=@ROWNUM+1 AS rank "    			    				  
			 + " FROM " 
			 + " ( "
			 + " SELECT branchNm, memberNm AS name, SUM(time) AS time, memberId "
			 + " FROM statistics_entry_day " 
			 + " where 1=1 "
			 + " AND branchId IN ( :branchId ) "
			 + " OR memberId = :memberId ";    				
			 
    		if(!StringUtils.isEmpty(startDt) && !StringUtils.isEmpty(endDt)) {
				 s += " AND businessDt BETWEEN :startDt AND :endDt ";
			 }
			
			 s += " AND useYn =1 "      			  
			 + " GROUP BY memberId "
			 + " ORDER BY time desc "    		 
			 + " ) Sub1 "
			
			 + " CROSS JOIN (select @ROWNUM:=0) Sub2 ";
    	
    		s += " LIMIT 10 ";
    	
    	 
    	Map<String, Object> args = new HashMap<>();
	    args.put("startDt", startDt);
	    args.put("endDt", endDt);
	    args.put("branchId", branchIdList);
	    args.put("memberId", memberId);
	    args.put("useYn", Constants.USE);
	
	    return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Rank.RankInfo.class));
    	
    }
    
    
    
    //해당 월 센터 내 랭킹
    public List<Rank.RankInfo> selectStatisticsEntryRank2 (String branchId, String memberId) {
    	String s = " SELECT * "
    	+ " FROM "
    	+ " (SELECT @ROWNUM := @ROWNUM+1 as rank, Sub1.* FROM ( "
    		+ " SELECT sum(time) AS time, ( CASE " 
    												 + " WHEN m.schoolGrade = 0 THEN '성인' "
    												 + " ELSE '학생' "								 										 										 
    												 + " END ) "
    												 + " AS schoolGrade , sed.memberId AS memberId, sed.branchId AS branchId "
    		+ " FROM statistics_entry_day sed "
    		+ " JOIN branch_member m on (sed.memberId = m.memberId) "
    		+ " where sed.branchId = :branchId "
    		+ " AND DATE_FORMAT(sed.businessDt, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m') "
    		+ " AND sed.autoYn = 0 "
    		
    		+ " GROUP BY sed.memberId "
    		+ " ORDER BY sum(time) DESC "
    	+ " ) Sub1 "
    	+ " CROSS JOIN (SELECT @ROWNUM:=0) Sub2 "
    	+ " ) Sub3"
    	+ " where Sub3.memberId = :memberId ";
    	
    	

    	    	 
    	Map<String, Object> args = new HashMap<>();
	    args.put("branchId", branchId);
	    args.put("memberId", memberId);
	    args.put("useYn", Constants.USE);
	
	    return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Rank.RankInfo.class));
    	
    }
    
    
    public int updateMemberExpire(String branchId, String memberId, Boolean expireYn) {
        String s = " UPDATE branch_member SET expireYn = :expireYn, updateDt = NOW() " +
                " WHERE branchId = :branchId AND memberId = :memberId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("expireYn", expireYn);
        
        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to update memberExpire");

        } else {
            

        }

        return result;
    }
    
}
