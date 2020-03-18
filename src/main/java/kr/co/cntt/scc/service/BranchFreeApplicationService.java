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
import kr.co.cntt.scc.model.FreeApplication;
import kr.co.cntt.scc.model.Page;
import kr.co.cntt.scc.model.PreReservation;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.util.DateUtil;
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
public class BranchFreeApplicationService {

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
    public List<FreeApplication> selectFreeApplicationListAll(String branchId, String sFreeApplicationStartDt, String sFreeApplicationEndDt, String sName, String sTel, Integer sRoomType, Page page) {

        String s = " SELECT a.branchId, a.startDt, a.roomType ,a.name, a.gender, " +
                " a.birthDt, a.tel, a.roomType , a.school, a.email, a.cmpRoute, a.insertDt, a.updateDt, a.freeApplicationId " +
                " FROM free_application a " +
                " WHERE a.branchId = :branchId " +                
                " AND a.useYn = :useYn ";

        if(!StringUtils.isEmpty(sFreeApplicationStartDt)) {
            s += " AND a.startDt >= :sFreeApplicationStartDt";
        	//s += " AND a.startDt >= "+sFreeApplicationStartDt+"";

        }
        
        if(!StringUtils.isEmpty(sFreeApplicationEndDt)) {
            s += " AND a.startDt <= :sFreeApplicationEndDt";
        	//s += " AND a.startDt <= "+sFreeApplicationEndDt+"";

        }
        
        if(!StringUtils.isEmpty(sName)) {
            s += " AND a.name like :sName";
        	//s += " AND a.name like "+sName+"";

        }
        
        if(!StringUtils.isEmpty(sTel)) {
        	s += " AND a.tel like :sTel";
        	//s += " AND a.tel like "+sTel+"";

        }

        if(!StringUtils.isEmpty(sRoomType)) {
        	s += " AND a.roomType = :sRoomType";
        	//s += " AND a.tel like "+sTel+"";

        }
        
        s += " ORDER BY a.startDt ASC ";

        if(page == null) {}
        else {
	        int perPage = (page.getPage() -1) * page.getPerPageNum();
	    	s += " limit "+perPage+", "+page.getPerPageNum()+" ";
        }
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("useYn", Constants.USE);
        args.put("sFreeApplicationStartDt", sFreeApplicationStartDt);
        args.put("sFreeApplicationEndDt", sFreeApplicationEndDt);
        args.put("sName", "%" + sName + "%");
        args.put("sTel", "%" + sTel + "%");
        args.put("sRoomType", sRoomType);
        
        
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(FreeApplication.class));

    }
    
    public int selectFreeApplicationCount(String branchId, String sFreeApplicationStartDt, String sFreeApplicationEndDt, String sName, String sTel, Integer sRoomType) {
        String s = " SELECT a.branchId, a.startDt, a.roomType ,a.name, a.gender, " +
                " a.birthDt, a.tel, a.roomType ,a.email, a.cmpRoute, a.insertDt, a.updateDt, a.freeApplicationId " +
                " FROM free_application a " +
                " WHERE a.branchId = :branchId " +
                " AND a.useYn = :useYn ";

        if(!StringUtils.isEmpty(sFreeApplicationStartDt)) {
            s += " AND a.startDt >= :sFreeApplicationStartDt";
        	//s += " AND a.startDt >= "+sFreeApplicationStartDt+"";

        }
        
        if(!StringUtils.isEmpty(sFreeApplicationEndDt)) {
            s += " AND a.startDt <= :sFreeApplicationEndDt";
        	//s += " AND a.startDt <= "+sFreeApplicationEndDt+"";

        }
        
        if(!StringUtils.isEmpty(sName)) {
            s += " AND a.name like :sName";
        	//s += " AND a.name like "+sName+"";

        }
        
        if(!StringUtils.isEmpty(sTel)) {
        	s += " AND a.tel like :sTel";
        	//s += " AND a.tel like "+sTel+"";

        }

        if(!StringUtils.isEmpty(sRoomType)) {
        	s += " AND a.roomType = :sRoomType";
        	//s += " AND a.tel like "+sTel+"";

        }
                
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("useYn", Constants.USE);
        args.put("sFreeApplicationStartDt", sFreeApplicationStartDt);
        args.put("sFreeApplicationEndDt", sFreeApplicationEndDt);
        args.put("sName", "%" + sName + "%");
        args.put("sTel", "%" + sTel + "%");
        args.put("sRoomType", sRoomType);
                
        return jdbcTemplate.queryForObject(s, args, int.class);
      }
    
    /**
     * (지점의) 무료 신청서 조회
     * @param branchId
     * @param todayDt
     * @return
     */
    public List<FreeApplication> selectFreeApplicationList(String branchId) {

        String s = " SELECT a.branchId, a.startDt, a.roomType ,a.name, a.gender, " +
                " a.birthDt, a.tel, a.school ,a.email, a.cmpRoute, a.insertDt, a.updateDt, a.freeApplicationId " +
                " FROM free_application a " +
                " WHERE a.branchId = :branchId " +
                //" AND a.startDt >= " + "'" + ""+todayDt+"" + "'" +
                " AND a.startDt >= :todayDt " +
                " AND a.useYn = :useYn ";


        s += " ORDER BY a.startDt ASC ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("todayDt", DateUtil.getCurrentDateString());
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(FreeApplication.class));

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
