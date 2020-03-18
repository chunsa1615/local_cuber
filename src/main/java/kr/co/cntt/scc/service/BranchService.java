package kr.co.cntt.scc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.util.DateUtil;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jslivane on 2016. 4. 5..
 */
@Service
@Transactional
@Slf4j
public class BranchService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;

    @Autowired
    SmsService smsService;

    public BranchService() {
    }


    /*******************************************************************************/


    /**
     * (캐시 제거) 지점 리스트
     */
    @CacheEvict(cacheNames = "branches", allEntries = true)
    public void resetBranchList() { }

    /**
     * (캐시) 지점 리스트 조회
     * @return
     * Cache : http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html
     */
    @Cacheable(cacheNames = "branches")
    public List<Branch> selectBranchList() {

        String s = " SELECT b.id, b.branchId, b.branchType ,b.name, b.tel, b.telEtc, " +
                " b.postcode, b.address1, b.address2, " +
                " b.hpUrl, b.locNote, b.locUrl, b.opNote, b.openDt, b.ip, b.visibleYn, b.fingerprintYn, b.standYn,  b.insertDt, b.updateDt " +
                " FROM branch b " +
                " WHERE b.useYn = :useYn " +
        		" AND b.visibleYn = :visibleYn " +
        		" ORDER BY openDt asc ";


        Map<String, Object> args = new HashMap<>();
        args.put("useYn", Constants.USE);
        args.put("visibleYn", Constants.USE);
        
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Branch.class));

    }

    public List<Branch> selectBranchListOne() {

        String s = " SELECT b.id, b.branchId, b.branchType ,b.name, b.tel, b.telEtc, " +
                " b.postcode, b.address1, b.address2, " +
                " b.hpUrl, b.locNote, b.locUrl, b.opNote, b.openDt, b.ip, b.visibleYn, b.insertDt, b.updateDt " +
                " FROM branch b " +
                " WHERE b.branchId NOT IN (:TEST_BRANCHID) " +
                " AND b.useYn = :useYn " +
        		" AND b.visibleYn = :visibleYn " +
                " AND b.branchType = :branchType " +
        		" ORDER BY openDt asc " +
        		" LIMIT 1 ";


        Map<String, Object> args = new HashMap<>();        
        args.put("branchType", 10);
        args.put("TEST_BRANCHID", Constants.TEST_BRANCHID);
        args.put("useYn", Constants.USE);
        args.put("visibleYn", Constants.USE);
        
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Branch.class));

    }
    
    public List<Branch> selectVisibleBranchList() {
        List<Branch> branchList = selectBranchList();
//        for (Branch b : branchList) {
//        	if (b.getVisibleYn() != Constants.VISIBLE) {
//        		branchList.remove(branchList.indexOf(b));
//        	}
//        }
//        return branchList;
        return branchList.stream().filter(b -> b.getVisibleYn() == Constants.VISIBLE).collect(Collectors.toList());

    }

    public Branch selectBranch(String branchId) {

        List<Branch> branches = selectBranchList();

        for(Branch branch: branches) {
            if(branchId.equals(branch.getBranchId())) return branch;

        }

        return null;

    }

    public Branch selectBranchByIdOrName(String idOrName) {

        List<Branch> branches = selectBranchList();
        
        // 1. id
        for(Branch branch: branches) {
            if(idOrName.equals(branch.getId().toString())) { 
            	branch.getOpenDt();
            	return branch;
            
            }

        }

        // 2. name
        for(Branch branch: branches) {
            if(branch.getName().contains(idOrName)) {
            	branch.getOpenDt();
            	return branch;
            
            }

        }

        return null;

    }

    
    @CacheEvict(cacheNames = "branches", allEntries = true)
    public int insertBranch(String branchId, Branch branch) {
        String s = " INSERT INTO branch ( " +
                " branchId, branchType, name, tel, telEtc, postcode, address1, address2," +
                " hpUrl, locNote, locUrl, opNote, openDt, visibleYn, areaType " +
                " ) VALUES ( " +
                " :branchId, :branchType, :name, :tel, :telEtc, :postcode, :address1, :address2," +
                " :hpUrl, :locNote, :locUrl, :opNote, :openDt, :visibleYn,  CASE " +
																					" WHEN address1 LIKE CONCAT('%', '서울', '%') " +
																					" THEN 20 " +
																					" WHEN address1 LIKE CONCAT('%', '부산', '%') " +
																					" THEN 30 " +
																					" WHEN address1 LIKE CONCAT('%', '대구', '%') " +
																					" THEN 40 " +
																					" WHEN address1 LIKE CONCAT('%', '인천', '%') " +
																					" THEN 50 " +																					
																					" WHEN address1 LIKE CONCAT('%', '광주', '%') " +
																					" THEN 60 " +
																					" WHEN address1 LIKE CONCAT('%', '대전', '%') " +
																					" THEN 70 " +																					
																					" WHEN address1 LIKE CONCAT('%', '울산', '%') " +
																					" THEN 80 " +
																					" WHEN address1 LIKE CONCAT('%', '세종', '%') " +
																					" THEN 90 " +
																					" WHEN address1 LIKE CONCAT('%', '경기', '%') " +
																					" THEN 100 " +
																					" WHEN address1 LIKE CONCAT('%', '강원', '%') " +
																					" THEN 110 " +																					
																					" WHEN address1 LIKE CONCAT('%', '충북', '%') " +
																					" THEN 120 " +
																					" WHEN address1 LIKE CONCAT('%', '충남', '%') " +
																					" THEN 130 " +																					
																					" WHEN address1 LIKE CONCAT('%', '전북', '%') " +
																					" THEN 140 " +
																					" WHEN address1 LIKE CONCAT('%', '전남', '%') " +
																					" THEN 150 " +
																					" WHEN address1 LIKE CONCAT('%', '경북', '%') " +
																					" THEN 160 " +
																					" WHEN address1 LIKE CONCAT('%', '경남', '%') " +
																					" THEN 170 " +																					
																					" WHEN address1 LIKE CONCAT('%', '제주', '%') " +
																					" THEN 180 " +																				
																					" ELSE 10 " +
																					" END " + 
                " ) ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(branch);
        source.addValue("branchId", branchId);

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to create branch");

        } else {
            // 이력 등록
            History history = new History(branchId, Constants.HistoryType.BRANCH_CREATE, branch.toString());
            historyService.insertHistory(history);

            // SMS 발신번호 등록
            if(!StringUtils.isEmpty(branch.getTel())) {
                smsService.saveSmsSendNumber(branch.getTel(), branch.getName() + " " + DateUtil.getCurrentDateString());

            }

        }

        return result;

    }

    @CacheEvict(cacheNames = "branches", allEntries = true)
    public int updateBranch(String branchId, Branch branch) {
        String s = " UPDATE branch SET " +
                " name = :name, branchType = :branchType, tel = :tel, telEtc = :telEtc, postcode = :postcode, " +
                " address1 = :address1, address2 = :address2, " +
                " hpUrl = :hpUrl, locNote = :locNote, locUrl = :locUrl, opNote = :opNote, visibleYn = :visibleYn, " +
                " updateDt = NOW() " +
                " WHERE branchId = :branchId ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(branch);
        source.addValue("branchId", branchId);

        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to update branch");

        } else {
            // 이력 등록
            History history = new History(branchId, Constants.HistoryType.BRANCH_UPDATE, branch.toString());
            historyService.insertHistory(history);

            // SMS 발신번호 등록
            if(!StringUtils.isEmpty(branch.getTel())) {
                smsService.saveSmsSendNumber(branch.getTel(), branch.getName() + " " + DateUtil.getCurrentDateString());

            }

        }

        return result;

    }

    @CacheEvict(cacheNames = "branches", allEntries = true)
    public int deleteBranch(String branchId) {
        String s = " UPDATE branch SET " +
                " useYn = :useYn, updateDt = NOW() " +
                " WHERE branchId = :branchId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("useYn", Constants.NOT_USE);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to delete branch");

        } else {
            History history = new History(branchId, Constants.HistoryType.BRANCH_DELETE, "");
            historyService.insertHistory(history);

        }

        return result;

    }

    /*******************************************************************************/


    public List<Branch.AdOrNotice> getAdOrNoticeList(String branchId) {
        String s = " SELECT b.id, b.branchId, b.adOrNoticeType, b.title, b.subtitle, b.imgUrl, " +
                " b.visibleYn, b.startDt, b.startTm, b.endDt, b.endTm, b.insertDt, b.updateDt " +
                " FROM branch_ad_or_banner b " +
                " WHERE b.branchId = :branchId AND b.useYn = :useYn ";


        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Branch.AdOrNotice.class));

    }


    public List<Branch> selectRoomType(String branchId) {
        String s =  " select " 
        		  + " b.branchId , b.name , aa.multi_sum , aa.single_sum , aa.private_sum , aa.cur_seat , sum(bd.deskMax) as tot_count "
        		  + " from branch_desk bd inner join branch b on bd.branchId = b.branchId "
        		  + " 					join branch_room brm on bd.roomId = brm.roomId "
        		  + " 					join (  select "
        		  + " 								 br.branchId "
        		  + " 								, count(br.reservationId) as cur_seat "
        		  + " 								, count(case when brm.roomType=10 then 'desk_cnt' end) as 'multi_sum' "
        		  + " 								, count(case when brm.roomType=20 then 'desk_cnt' end) as 'single_sum' "
        		  + " 								, count(case when brm.roomType=30 then 'desk_cnt' end) as 'private_sum' "
        		  + " 							from branch_reservation br join branch b on br.branchId = b.branchId " 
        		  + " 													   join branch_desk bd on br.deskId = bd.deskId "
        		  + " 													   join branch_room brm on bd.roomId = brm.roomId "
        		  + " 							where br.reservationStatus = 20 "
        		  + " 							and b.branchId = :branchId "
        		  + " 							and br.useYn = 1 "
        		  + " 							and sysdate() between concat(deskStartDt,' ',deskStartTm) and concat(deskEndDt,' ',deskEndTm) "
        		  + " 							group by br.branchId "
        		  + " 						  ) aa on bd.branchId = aa.branchId "
        		  + " where (bd.roomId != null or bd.roomId != '') "
        		  + " AND b.name not like '%test%' "
        		  + " AND bd.useYn = 1 "
        		  + " AND b.useYn = 1 "
        		  + " group by b.branchId, b.name, aa.multi_sum, aa.single_sum, aa.private_sum, aa.cur_seat ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Branch.class));

    }


    /***************AppAdmin****************************************************************/
    public String selectBranchName(String branchId) {

        String s = " SELECT b.name " +
                " FROM branch b " +
                " WHERE b.branchId = :branchId" +
                //" AND branchId NOT IN ('033f8817-71a0-4feb-bf7b-f9f184da7317') " +
                " AND b.useYn = :useYn " +
        		" AND b.visibleYn = :visibleYn ";


        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("useYn", Constants.USE);
        args.put("visibleYn", Constants.USE);
        
        return jdbcTemplate.queryForObject(s, args, String.class);

    }
    
    //지역으로 매장찾기
    public List<String> selectBranchArea(String area) {

        String s = " SELECT branchId " +
                " FROM branch " +
                " WHERE 1=1 ";
        
        if (!StringUtils.isEmpty(area)) {
                s += " AND address1 like :area";
		}
                s += " AND useYn = :useYn " +
                	 " AND visibleYn = :visibleYn ";


        Map<String, Object> args = new HashMap<>();
        args.put("area", "%" + area + "%" );
        args.put("useYn", Constants.USE);
        args.put("visibleYn", Constants.USE);

        //return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Branch.class));
        return jdbcTemplate.queryForList(s, args, String.class);
    }
    
    //branch의 areaType 가져오기
    public List<Branch> selectBranchArea() {

        String s = " SELECT areaType " +
                " FROM branch " +
                " WHERE 1=1 " +
        		" AND branchId != '033f8817-71a0-4feb-bf7b-f9f184da7317' "  +        
                " AND useYn = :useYn " +
                " AND visibleYn = :visibleYn ";
        		
        	s += " GROUP BY areaType ";

        Map<String, Object> args = new HashMap<>();
        args.put("useYn", Constants.USE);
        args.put("visibleYn", Constants.USE);

        //return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Branch.class));
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Branch.class));
    }    
    
    
}
