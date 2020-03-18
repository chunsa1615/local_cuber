package kr.co.cntt.scc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.model.EntryRank;
import kr.co.cntt.scc.model.FreeApplication;
import kr.co.cntt.scc.model.Page;
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
public class BranchEntryRankService {

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
    public List<EntryRank> selectEntryRankListAll(String sDate) {

   	 String s = " SELECT branchNm, name, time, Floor(time/60) as 'hour', + time-Floor(time/60)*60 as 'min', " +
		" memberId, @ROWNUM:=@ROWNUM+1 AS rank " +
		 " FROM " +
		 " ( " +
		 " SELECT branchNm, memberNm AS name, SUM(s.time) AS time, s.memberId as memberId " +		 
		 " FROM statistics_entry_day s " +		  
		 " JOIN branch_reservation r ON (r.branchId = s.branchId and r.memberId = s.memberId) " +
     	 " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = 1) " +
		 " where 1=1 ";								

		 if (!StringUtils.isEmpty(sDate)) {
			 s+= " AND s.businessDt between :sDate and CURDATE() ";
		 }
   	 
		 s += " AND s.branchId != :branchId " +
		 " AND s.useYn = :useYn " +      	
		 " AND s.autoYn = :autoYn " +
		 
		 " AND r.useYn = 1 " +
		 " AND CURDATE() BETWEEN  r.deskStartDt AND r.deskEndDt " +
		 " AND r.reservationStatus = 20 " +
		 
		 " GROUP BY s.memberId " +
		 
		" UNION ALL " +
		" SELECT branchNm, memberNm AS name, SUM(s.time) AS time, s.memberId as memberId " +
		" FROM statistics_entry_day s " +
		" JOIN branch_reservation r ON (r.branchId = s.branchId and r.memberId = s.memberId) " +
		" JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = 1) " +
		" JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = 1 AND asm.transferYn = 0) " +
		" WHERE 1=1 ";
		
		if (!StringUtils.isEmpty(sDate)) {
			 s+= " AND s.businessDt between :sDate and CURDATE() ";
		}
		
		s += " AND s.branchId != :branchId " +
		" AND s.useYn = 1 " +  
		" AND s.autoYn = 0 " +  			 
			
		" AND r.useYn = 1 " +
		" AND CURDATE() BETWEEN  r.deskStartDt AND r.deskEndDt " +
		" AND r.reservationStatus = 20 " +
		
		" GROUP BY s.memberId " +  			 
		 
		 
		 //" ORDER BY time desc " +    		 
		 " ORDER BY time desc " +
		 " ) Sub1 " +
		
		" CROSS JOIN (select @ROWNUM:=0) Sub2 ";
       
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", Constants.TEST_BRANCHID);
        args.put("sDate", sDate);        
        args.put("useYn", Constants.USE);
        args.put("autoYn", Constants.NOT_USE);        

        
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(EntryRank.class));

    }
    
    
    /**
     * (지점의) 회원 랭크 조회
     * @param branchId
     * @param todayDt
     * @return
     */
    public List<EntryRank> selectEntryRankList(String branchId, String sDate, Page page) {
 		//s += " AND DATE_FORMAT(businessDt, '%Y-%m') <= DATE_FORMAT(CURDATE(), '%Y-%m')";
    	 
    	int rankPage = 0;
    	 if(page == null) {}
         else {
 	        if (page.getPage() > 1) {
 	        	rankPage = (page.getPage() -1) * page.getPerPageNum();
 	        }
 	    	
         }
    	
    	String s = " SELECT branchNm, CONCAT( SUBSTRING(name, 1, 1), '*', SUBSTRING(name, 3, length(name))) as name, time, " +
    	" Floor(time/60) as 'hour', + time-Floor(time/60)*60 as 'min', " +
		" memberId, (@ROWNUM:=@ROWNUM+1) + :rankPage AS rank, no, cnt, Floor(Floor(allTime/totalCnt)/60) as 'allHour',  Floor(allTime/totalCnt)-Floor(Floor(allTime/totalCnt)/60)*60 as 'allMin' " +
		" , Floor(Floor(time/cnt)/60) as 'avgHour' " +
		" , Floor(time/cnt) - Floor( Floor(time/cnt) /60 )*60 as 'avgMin' " +    			 
		 " FROM " +
		 " ( " +
		 " SELECT branchNm, memberNm AS name, SUM(time) AS time, s.memberId as memberId, s.no as no , count(s.no) as cnt " +
		 
		 " ,( SELECT SUM(time) " +
		 " FROM " +
		 " ( SELECT SUM(time) as time FROM statistics_entry_day s " +
		 " JOIN branch_reservation r ON (r.branchId = s.branchId and r.memberId = s.memberId) " +
     	 " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = 1) " +
		 " WHERE 1=1 ";							 			 
		 if (!StringUtils.isEmpty(sDate)) {
			 s+= " AND s.businessDt between :sDate and CURDATE() ";
		 }
		 s += " AND s.branchId != :TEST_branchId " +
			     								 
		 " AND s.useYn = :useYn " +
		 " AND s.autoYn = :autoYn " +
		 " AND r.useYn = 1 " +
		 " AND CURDATE() BETWEEN  r.deskStartDt AND r.deskEndDt " +
		 " AND r.reservationStatus = 20 " +		 			 				 
		 
		  " UNION ALL " +
		  " SELECT SUM(time) as time FROM statistics_entry_day s " + 
		  " JOIN branch_reservation r ON (r.branchId = s.branchId and r.memberId = s.memberId) " +  
		  " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = 1) " +  
		  " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = 1 AND asm.transferYn = 0) " +  
		  " WHERE 1=1 ";

		  if (!StringUtils.isEmpty(sDate)) {
			  s+= " AND s.businessDt between :sDate and CURDATE() ";
		  }
		  s += " AND s.branchId != :TEST_branchId " +
		
		  " AND s.useYn = 1 " +    
		  " AND s.autoYn = 0 " +    			 
			
		  " AND r.useYn = 1 " +  
		  " AND CURDATE() BETWEEN  r.deskStartDt AND r.deskEndDt " +  
		  " AND r.reservationStatus = 20 " +
		 
		 
		 
		 " ) as T ) as allTime " +

		" ,( SELECT count(time) " +
		" FROM " +
		" ( " +
			" SELECT time FROM statistics_entry_day s " +
			" JOIN branch_reservation r ON (r.branchId = s.branchId and r.memberId = s.memberId) " +
	     	" JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = 1) " +
			" WHERE 1=1 ";			 			
			if (!StringUtils.isEmpty(sDate)) {
				s+= " AND s.businessDt between :sDate and CURDATE() ";
			}
			s += " AND s.branchId != :TEST_branchId " +
			" AND s.useYn = :useYn " +
			" AND s.autoYn = :autoYn " +			 
			" AND r.useYn = 1 " +
			" AND CURDATE() BETWEEN  r.deskStartDt AND r.deskEndDt " +
			" AND r.reservationStatus = 20 " +
			" GROUP BY s.memberId " +
			
			" ) AS T ) as totalCnt " +
		 
		
		 " FROM statistics_entry_day s " +		  
		 " JOIN branch_reservation r ON (r.branchId = s.branchId and r.memberId = s.memberId) " +
	     " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = 1) " +
		 
		 " where 1=1 " ;										 
    	 if (!StringUtils.isEmpty(branchId)) {
             s += " AND s.branchId = :branchId ";
		 }

		 if (!StringUtils.isEmpty(sDate)) {
			 s+= " AND s.businessDt between :sDate and CURDATE() ";
		 }
		 
		 s += " AND s.branchId != :TEST_branchId " +
		 " AND s.useYn = :useYn " +      	
 		 " AND s.autoYn = :autoYn " +
		 
 		 " AND r.useYn = :useYn " +
		" AND CURDATE() BETWEEN  r.deskStartDt AND r.deskEndDt " +
		" AND r.reservationStatus = 20 " +
		
		 " GROUP BY s.memberId " +
		 " ORDER BY time desc " +    		 
		 " ) Sub1 " +
		
		" CROSS JOIN (select @ROWNUM:=0) Sub2 ";
        
		 //앱회원
		 s +=  "UNION ALL " +
				 " SELECT branchNm, CONCAT( SUBSTRING(name, 1, 1), '*', SUBSTRING(name, 3, length(name))) as name, time, " +
			    	" Floor(time/60) as 'hour', + time-Floor(time/60)*60 as 'min', " +
					" memberId, (@ROWNUM:=@ROWNUM+1) + :rankPage AS rank, no, cnt, Floor(Floor(allTime/totalCnt)/60) as 'allHour',  Floor(allTime/totalCnt)-Floor(Floor(allTime/totalCnt)/60)*60 as 'allMin' " +
					" , Floor(Floor(time/cnt)/60) as 'avgHour' " +
					" , Floor(time/cnt) - Floor( Floor(time/cnt) /60 )*60 as 'avgMin' " +    			 
					 " FROM " +
					 " ( " +
					 " SELECT branchNm, memberNm AS name, SUM(time) AS time, s.memberId as memberId, s.no as no , count(s.no) as cnt " +
					 
			" ,( SELECT SUM(time) " +
			" FROM " +
			" ( SELECT SUM(time) as time FROM statistics_entry_day s " +
			" JOIN branch_reservation r ON (r.branchId = s.branchId and r.memberId = s.memberId) " +
			 " JOIN branch_member m ON (m.branchId = r.branchId AND m.memberId = r.memberId AND m.useYn = 1) " +
			" WHERE 1=1 ";							 			 
			if (!StringUtils.isEmpty(sDate)) {
				 s+= " AND s.businessDt between :sDate and CURDATE() ";
			}
			s += " AND s.branchId != :TEST_branchId " +
				     								 
			" AND s.useYn = :useYn " +
			" AND s.autoYn = :autoYn " +
			" AND r.useYn = 1 " +
			" AND CURDATE() BETWEEN  r.deskStartDt AND r.deskEndDt " +
			" AND r.reservationStatus = 20 " +		 			 				 
			
			 " UNION ALL " +
			 " SELECT SUM(time) as time FROM statistics_entry_day s " + 
			 " JOIN branch_reservation r ON (r.branchId = s.branchId and r.memberId = s.memberId) " +  
			 " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = 1) " +  
			 " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = 1 AND asm.transferYn = 0) " +  
			 " WHERE 1=1 ";
			
			 if (!StringUtils.isEmpty(sDate)) {
				  s+= " AND s.businessDt between :sDate and CURDATE() ";
			 }
			 s += " AND s.branchId != :TEST_branchId " +
			
			 " AND s.useYn = 1 " +    
			 " AND s.autoYn = 0 " +    			 
				
			 " AND r.useYn = 1 " +  
			 " AND CURDATE() BETWEEN  r.deskStartDt AND r.deskEndDt " +  
			 " AND r.reservationStatus = 20 " +						
			
			" ) as T ) as allTime " +

					" ,( SELECT count(time) " +
					" FROM " +
					" ( " +
						" SELECT time FROM statistics_entry_day s " +
						 " JOIN branch_reservation r ON (r.branchId = s.branchId and r.memberId = s.memberId) " +
						 " JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = 1) " +
						 " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = 1 AND asm.transferYn = 0) " +
						 " WHERE 1=1 ";

						if (!StringUtils.isEmpty(sDate)) {
							s+= " AND s.businessDt between :sDate and CURDATE() ";
						}
						s += " AND s.branchId != :TEST_branchId " +
						 " AND s.useYn = 1 " +  
						 " AND s.autoYn = 0 " +  			 
							
						 " AND r.useYn = 1 " +
						 " AND CURDATE() BETWEEN  r.deskStartDt AND r.deskEndDt " +
						 " AND r.reservationStatus = 20 " +
						 
					" ) AS T ) as totalCnt " +
					 
					
					" FROM statistics_entry_day s " +		  
					" JOIN branch_reservation r ON (r.branchId = s.branchId and r.memberId = s.memberId) " +
					" JOIN app_branch_manager abm ON (abm.branchId = r.branchId AND abm.memberId = r.memberId AND abm.useYn = 1) " +
		            " JOIN app_student_member asm ON (abm.no = asm.no AND asm.useYn = 1 AND asm.transferYn = 0) " + 
					 
					 " where 1=1 " ;										 
			    	 if (!StringUtils.isEmpty(branchId)) {
			             s += " AND s.branchId = :branchId ";
					 }

					 if (!StringUtils.isEmpty(sDate)) {
						 s+= " AND s.businessDt between :sDate and CURDATE() ";
					 }
					 
					 s += " AND s.branchId != :TEST_branchId " +
					 " AND s.useYn = :useYn " +      	
			 		 " AND s.autoYn = :autoYn " +
					 
			 		 " AND r.useYn = :useYn " +
					" AND CURDATE() BETWEEN  r.deskStartDt AND r.deskEndDt " +
					" AND r.reservationStatus = 20 " +
					
					 " GROUP BY s.memberId " +
					 " ORDER BY time desc " +    		 
					 " ) Sub1 " +
					
					" CROSS JOIN (select @ROWNUM:=0) Sub2 " ;		 
					  
		 
		 
         if(page == null) {}
         else {
 	        int perPage = (page.getPage() -1) * page.getPerPageNum();
 	    	s += " limit "+perPage+", "+page.getPerPageNum()+" ";
         }

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("TEST_branchId", Constants.TEST_BRANCHID);
        args.put("sDate", sDate);
        args.put("useYn", Constants.USE);
        args.put("autoYn", Constants.NOT_USE);        
        args.put("rankPage", rankPage);
        

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(EntryRank.class));

    }
    
    
    /*******************************************************************************/    
    
    public FreeApplication selectFreeApplication(String branchId) {

    	
        String s = " SELECT a.branchId, a.startDt, a.roomType ,a.name, a.gender, " +
                " a.birthDt, a.tel, a.school ,a.email, a.cmpRoute, a.insertDt, a.updateDt, a.freeApplicationId " +
                " FROM free_application a " +
                " where a.useYn = :useYn " +
                " order by a.id desc limit 1";


        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(FreeApplication.class));

    }    
    /**
     * (지점의) 무료 신청서 추가
     * 
     * @return
     */
    public int insertedFreeApplication(FreeApplication freeApplication) {
    	
        String s = " INSERT INTO free_application ( " +
        		"branchId, startDt, roomType ,name, gender, " +
                "school, birthDt, tel, email, cmpRoute, freeApplicationId " +
                " ) VALUES ( " +
                //" :branchId, FROM_UNIXTIME(:startDt, '%Y-%m-%d'), :name, :gender, " +
                " :branchId, :startDt, :roomType, :name, :gender, " +
                ":school, :birthDt, :tel, :email, :cmpRoute, :freeApplicationId " +
                " ) ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(freeApplication);
        // source.addValue("branchId", freeApplication.getBranchId());

        int result = jdbcTemplate.update(s, source);
        
        if(result == 0) {
        	throw new InternalServerError("Failed to update freeApplication");
        }
        
        return result;
    }
    /**
     * (지점의) 무료 신청서 수정
     * 
     * @return
     */
    public int updateFreeApplication(String freeApplicationId, FreeApplication freeApplication) {
    	
        String s = " UPDATE free_application SET " +
        		"startDt = :startDt, name = :name, roomType = :roomType, gender = :gender," +
        		"school = :school, birthDt = :birthDt, tel = :tel, email = :email, cmpRoute = :cmpRoute " +
        		" WHERE branchId = :branchId AND freeApplicationId = :freeApplicationId ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(freeApplication);
        
        source.addValue("branchId", freeApplication.getBranchId());
        source.addValue("freeApplicationId", freeApplicationId);
        
        int result = jdbcTemplate.update(s, source);
        
        if(result == 0) {
        	throw new InternalServerError("Failed to update freeApplication");
        }
        
        return result;
    }
    
    /**
     * (지점의) 무료 신청서 삭제
     * 
     * @return
     */
    public int deleteFreeApplication(String branchId, String freeApplicationId, FreeApplication freeApplication) {
        String s = " UPDATE free_application SET " +
                " useYn = :useYn, updateDt = NOW() " +
                " WHERE branchId = :branchId AND freeApplicationId = :freeApplicationId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("freeApplicationId", freeApplicationId);
        args.put("useYn", Constants.NOT_USE);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to delete FreeApplication");

        } else {
            //History history = new History(branchId, Constants.HistoryType.MEMBER_DELETE, "");
            //history.
            //historyService.insertHistory(history);

        }

        return result;

    }
    
}
