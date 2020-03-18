package kr.co.cntt.scc.service;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.Constants.RentalStateType;
import kr.co.cntt.scc.model.Goods;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.model.Page;
import kr.co.cntt.scc.model.Rental;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.util.DateUtil;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Rental
 *
 * Created by jslivane on 2016. 6. 14..
 */
@Service
@Transactional
@Slf4j
public class BranchRentalService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;

    /*******************************************************************************/   
    public List<Rental> selectRentalListforReservationDelete(String branchId, String orderId) {
    	String s = " SELECT p.branchId, p.orderId, p.rentalId, p.rentalDt, p.rentalTm, p.memberId, " +
                " p.rentalStateType, p.rentalNote, p.insertDt, p.updateDt " +
                " FROM branch_rental p " +
                " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +                
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " WHERE p.branchId = :branchId AND p.orderId = :orderId AND p.useYn = :useYn ";
    	
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("orderId", orderId);
        args.put("useYn", Constants.USE);
        
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Rental.class));
    }
    
    // 물품 리스트 가져오기
    public List<Rental> selectGoodsList(String branchId){
    	
    	String s = "SELECT goodsId, rentalType" +
    			" FROM goods" +
    			" WHERE branchId = :branchId";
    	
    	Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Rental.class));
    }
    
    // 물품 목록 가져오기
    public List<Goods> selectGoodsList2(String branchId){ //TO DO : 위에것과 수정
    	
    	String s = "SELECT g.branchId, g.goodsId, g.rentalType, g.goodsNote, g.insertDt, g.updatedt" +
    			" FROM goods g" +
    			" WHERE g.branchId = :branchId AND useYn = :useYn";
    	
    	Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("useYn", Constants.USE);
    	return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Goods.class));
    }    
    
    public Goods selectGoods(String branchId, String goodsId) {
    	String s = "SELECT g.branchId, g.rentalType, g.goodsNote, g.insertDt, g.updatedt" +
    			" FROM goods g" +
    			" WHERE g.branchId = :branchId AND g.goodsId = :goodsId AND useYn = :useYn";
    	
    	Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("goodsId", goodsId);
        args.put("useYn", Constants.USE);
        
    	return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(Goods.class));

    }
    // 새 물품 목록 추가
    public int insertGoodsList(String branchId, String goodsId, Goods goods){ 
    	
        String s = " INSERT INTO goods ( " +
                " goodsId, branchId, rentalType, goodsNote " +
                " ) VALUES ( " +
                " :goodsId, :branchId, :rentalType, :goodsNote " +
                " ) "; 
    	
        CombinedSqlParameterSource source = new CombinedSqlParameterSource(goods);
    	source.addValue("branchId", branchId);
    	source.addValue("goodsId", goodsId);
    	
    	int result = jdbcTemplate.update(s, source);
        
    	if (result == 0) {
    		throw new InternalServerError("Failed to insert Goods");
    	}
    	
    	
    	return result;
    }
    
    //특정 물품 수정
    public int updateGoods(String branchId, String goodsId, Goods goods) {
    	
    	String s = " UPDATE goods SET " +
    			" goodsId = :goodsId, branchId = :branchId, rentalType = :rentalType, goodsNote = :goodsNote, updateDt = NOW() " +
    			" WHERE branchId = :branchId AND goodsId = :goodsId ";
    	
    	CombinedSqlParameterSource source = new CombinedSqlParameterSource(goods);
    	source.addValue("branchId", branchId);
    	source.addValue("goodsId", goodsId);
    	
    	int result = jdbcTemplate.update(s, source);
    	
    	if (result == 0) {
    		throw new InternalServerError("Failed to update Goods");
    	}
    	
    	return result;
    	
    }
    
    //특정 물품 삭제
    public int deleteGoods(String branchId, String goodsId) {
    	
    	String s = " UPDATE goods SET " +
    			" useYn = :useYn updateDt = NOW() " +
    			" WHERE branchId = :branchId AND goodsId = :goodsId ";
    	
    	Map<String, Object> args = new HashMap<>();
    	args.put("branchId", branchId);
    	args.put("goodsId", goodsId);
    	args.put("useYn", Constants.NOT_USE);
    	
    	int result = jdbcTemplate.update(s, args);
    	
    	if (result == 0) {
    		throw new InternalServerError("Failed to delete Goods");
    	}
    	
    	return result;
    	
    }
    
    /**
     * rental 리스트 조회 (결제일 기준)
     * @param branchId
     * @param rentalStartDt
     * @param rentalEndDt
     * @param memberId
     * @return
     *
     * MySQL Date Time Fuctions : http://dev.mysql.com/doc/refman/5.7/en/date-and-time-functions.html
     *
     */
    public List<Rental> selectRentalList(String branchId, String rentalStartDt, String rentalEndDt,
                                          String memberId, String goodsId, Page page) {

       /* String s = " SELECT p.branchId, p.rentalId, p.memberId, p.rentalType, " +
                " p.rentalStateType, p.rentalNote, p.insertDt, p.updateDt " +
                " FROM branch_rental p " +             
                //" JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " WHERE p.branchId = :branchId and p.useYn = :useYn ";*/

        String s = " SELECT p.id, p.branchId, p.rentalId, p.goodsId, p.memberId, p.rentalDt, p.rentalTm, " +
        			//" GROUP_CONCAT(p.rentalType SEPARATOR :separator) AS rentalTypeNames, " +
        			" (SELECT rentalType FROM goods g " +
        			" WHERE g.branchId = p.branchId AND g.goodsId = p.goodsId AND g.useYn = :useYn) AS rentalType, " +
        			" p.rentalStateType, p.rentalNote, p.insertDt, p.updateDt " +
                	 " FROM branch_rental p  " + 
                	// " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) "+
                	 " WHERE p.branchId = :branchId AND p.useYn = :useYn ";
     
        if(!StringUtils.isEmpty(memberId)) {
            s += " AND p.memberId = :memberId ";

        }
        if(!StringUtils.isEmpty(goodsId)) {
            s += " AND p.goodsId = :goodsId ";

        }
        if(!StringUtils.isEmpty(rentalStartDt)) {
            //payStartDt = DateUtil.getCurrentDateString();
            s += " AND p.rentalDt >= :startDate ";

        }

        if(!StringUtils.isEmpty(rentalEndDt)) {
            //payEndDt = DateUtil.getCurrentDateString();
            s += " AND p.rentalDt <= :endDate ";

        }
        s += " ORDER BY p.rentalDt ASC, p.rentalTm ASC";
//        if(!StringUtils.isEmpty(rentalType)) {
//            s += " AND p.rentalType = :rentalType ";
//
//        }

      //  s += " AND p.useYn = :useYn ";
     //   s += " ORDER BY p.insertDt ASC";
   //     s += " GROUP BY p.memberId";
        
        if(page == null) {}
        else {
	        int perPage = (page.getPage() -1) * page.getPerPageNum();
	    	s += " limit "+perPage+", "+page.getPerPageNum()+" ";
        }
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("goodsId", goodsId);
        args.put("useYn", Constants.USE);
        args.put("startDate", rentalStartDt);
        args.put("endDate", rentalEndDt);
        args.put("separator", Constants.SEPERATOR);
        
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Rental.class));

    }

    /**
     * 대여 리스트 조회 (결제일 기준)
     * @param branchId
     * @param rentalStartDt
     * @param rentalEndDt
     * @param memberId
     * @return
     *
     * MySQL Date Time Fuctions : http://dev.mysql.com/doc/refman/5.7/en/date-and-time-functions.html
     *
     */
    public List<Rental> selectTotalRental(String branchId, String rentalStartDt, String rentalEndDt,
                                          String memberId, String goodsId) {

        String s = " SELECT p.rentalStateType " +
                " FROM branch_rental p " +
              // " LEFT OUTER JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                //" JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " WHERE p.branchId = :branchId and p.useYn = :useYn ";

                //" AND p.rentalTm >= :startTime AND p.rentalTm <= :endTime " +

/*
        if(!StringUtils.isEmpty(rentalStartDt)) {
            //payStartDt = DateUtil.getCurrentDateString();
            s += " AND p.rentalDt >= :startDate ";

        }

        if(!StringUtils.isEmpty(rentalEndDt)) {
            //payEndDt = DateUtil.getCurrentDateString();
            s += " AND p.rentalDt <= :endDate ";

        }*/
        if(!StringUtils.isEmpty(memberId)) {
            s += " AND p.memberId = :memberId ";

        }
        
        if(!StringUtils.isEmpty(goodsId)) {
            s += " AND p.goodsId = :goodsId ";

        }
        s += " ORDER BY p.rentalDt ASC, p.rentalTm ASC ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);

        args.put("memberId", memberId);
        args.put("goodsId", goodsId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Rental.class));

    }
    
    /**
     * (지점의 특정 주문의) 대여 목록 조회
     * @param branchId
     * @param orderId
     * @return
     */
    public List<Rental> selectRentalList(String branchId, String orderId) {

        String s = " SELECT p.branchId, p.orderId, p.rentalDt, p.rentalTm, p.rentalId, p.memberId, " +
                " p.rentalStateType, p.rentalNote, p.insertDt, p.updateDt " +
                " FROM branch_rental p " +
                " JOIN branch_member m ON (m.branchId = p.branchId AND m.memberId = p.memberId AND m.useYn = :useYn) " +
                " WHERE p.branchId = :branchId AND p.orderId = :orderId " +
                " AND p.useYn = :useYn ";

        s += " ORDER BY p.rentalDt ASC p.rentalTm ASC ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("orderId", orderId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Rental.class));

    }

    /**
     * (지점의 특정 주문의) 대여 삭제
     * @param branchId
     * @param orderId
     * @return
     */
    public int deleteBranchOrderRental(String branchId, String orderId, String rentalId) {

        String s = " UPDATE branch_rental SET " +
                " rentalStateType = :rentalStateType, updateDt = NOW() " +
                " WHERE branchId = :branchId AND rentalId = :rentalId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("rentalId", rentalId);
        args.put("rentalStateType", Constants.CANCEL);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to delete rental");

        } else {
            History history = new History(branchId, Constants.HistoryType.RENTAL_DELETE, "");
            history.setRentalId(rentalId);
            historyService.insertHistory(history);

        }

        return result;

    }
    
 /*   *//**
     * (지점의 특정 주문의) 대여 수정
     * @param branchId
     * @param orderId
     * @return
     *//*
    public int updateBranchOrderRental(String branchId, String orderId, String rentalId, Rental rental) {
        String s = " UPDATE branch_rental SET " +
                " rentalType = :rentalType,memberId = :memberId, " +
                " rentalNote = :rentalNote, updateDt = NOW() " +
                " WHERE branchId = :branchId AND orderId = :orderId AND rentalId = :rentalId ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(rental);
        source.addValue("branchId", branchId);
        source.addValue("orderId", orderId);
        source.addValue("rentalId", rentalId);

        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to update rental");

        } else {
            History history = new History(branchId, Constants.HistoryType.PAY_UPDATE, rental.toString());
            history.setRentalId(rentalId);
            history.setMemberId(rental.getMemberId());
            historyService.insertHistory(history);

        }

        return result;

    }  */
    
    /*******************************************************************************/


    public List<Rental> selectRental(String branchId, String memberId, String rentalId) {
        String s = " SELECT p.branchId, p.rentalId, p.rentalDt, p.rentalTm, p.goodsId, p.memberId, " +
                " p.rentalStateType, p.rentalNote, p.insertDt, p.updateDt " +
                " FROM branch_rental p " +
                " WHERE p.branchId = :branchId AND useYn = :useYn";

        s += " ORDER BY p.insertDt ASC ";
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("rentalId", rentalId);
        args.put("useYn", Constants.USE);


        try {
        	 return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Rental.class));
        } catch (EmptyResultDataAccessException e) {
        	return null;
        }

    }

    public int[] insertRental(String branchId, String rentalId, Rental rentals, String[] b) {
    	
    	String memberId = rentals.getMemberId();
    	String rentalType = rentals.getRentalTypeNames();
           
        if (b.length > 0) {  
	        String s = " INSERT INTO branch_rental ( " +
	                " branchId, rentalId, goodsId, rentalDt, rentalTm, memberId, rentalStateType, rentalNote " +
	                " ) VALUES ( " +
	                " :branchId, :rentalId, :goodsId, :rentalDt, :rentalTm, :memberId, :rentalStateType,  :rentalNote " +
	                " ) ";
	        
	       
	        Map<String, Object>[] args = new HashMap[b.length];
	        for(int i = 0; i < b.length; i++) {
	            Map<String, Object> arg = new HashMap<>();
	            arg.put("rentalId", rentalId);
	            arg.put("goodsId", b[i]);
	            arg.put("memberId", memberId);
	            arg.put("branchId", branchId);
	            //arg.put("rentalType", b[i].replaceAll(" ", ""));
	            arg.put("rentalStateType", rentals.getRentalStateType());
	            arg.put("useYn", Constants.USE);
	            arg.put("rentalNote", rentals.getRentalNote());
	            arg.put("rentalDt", rentals.getRentalDt());
	            arg.put("rentalTm", DateUtil.getCurrentTimeString());
	            args[i] = arg;

	        }	
	        int[] results = jdbcTemplate.batchUpdate(s, args);

        for (int i = 0; i < b.length; i++) {
            String goodsId = b[i];

            if (results[i] == 1) {
                History history = new History(goodsId, Constants.HistoryType.RENTAL_CREATE, "");
                history.setUserId(branchId);
                historyService.insertHistory(history);

            } else if (results[i] == 2) {
                History history = new History(goodsId, Constants.HistoryType.RENTAL_CREATE, "");
                history.setUserId(branchId);
                historyService.insertHistory(history);

            }

        }

        return results;

    } else {
        return new int[0];
    }
    
    }

    public int deleteBranchRental(String branchId, Rental rental) {
    	String s = " DELETE FROM branch_rental WHERE branchId = :branchId AND rentalId = :rentalId ";
    	
    	CombinedSqlParameterSource source = new CombinedSqlParameterSource(rental);
    	source.addValue("branchId", branchId);
    	
    	int result = jdbcTemplate.update(s, source);
    	
    	if (result == 0) {
    		
    	} else {
    		
    	}
    	
    	return result;
    }
    
    public int updateRental(String branchId, String id, Rental rental) {

        String s = " UPDATE branch_rental SET " +
        		" rentalStateType = :rentalStateType, rentalNote = :rentalNote, " +
                " rentalDt = :rentalDt, updateDt = NOW() " +
                " WHERE branchId = :branchId AND id = :id ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(rental);
        source.addValue("branchId", branchId);
        source.addValue("id", id);

        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to update rental");

        } else {
            History history = new History(branchId, Constants.HistoryType.RENTAL_UPDATE, rental.toString());

            history.setMemberId(rental.getMemberId());
            historyService.insertHistory(history);

        }

        return result;
    }

    public int deleteRental(String branchId, String id) {
        String s = " UPDATE branch_rental SET " +
                " rentalStateType = '0', updateDt = NOW() " +
                " WHERE branchId = :branchId AND id = :id ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("id", id);
        //args.put("rentalStateType", RentalStateType.RETURN);
        
        
        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to delete rental");

        } else {
            History history = new History(branchId, Constants.HistoryType.RENTAL_DELETE, "");
           // history.setRentalId(rentalId);
            historyService.insertHistory(history);

        }

        return result;

    }


    /*******************************************************************************/

    /*
    public Rental selectCurrentRentalByMemberId(String branchId, String memberId) {

        String s = " SELECT p.branchId, p.orderId, p.memberId, p.rentalId, p.rentalDt, p.rentalType, p.rentalAmount, p.memberId, " +
                " p.rentalNote, p.insertDt, p.updateDt " +
                " FROM branch_rental p " +
                " JOIN branch_order o ON (o.branchId = p.branchId AND o.orderId = p.orderId AND o.useYn = :useYn) " +
                " WHERE p.branchId = :branchId AND p.memberId = :memberId AND p.useYn = :useYn " +
                " AND :currentDate >= p.deskStartDt AND :currentDate <= p.deskEndDt " +
                " AND :currentTime >= p.deskStartTm AND :currentTime <= p.deskEndTm " +
                " LIMIT 1 ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("useYn", Constants.USE);
        args.put("currentDate", DateUtil.getCurrentDateString());
        args.put("currentTime", DateUtil.getCurrentTimeString());

        return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(Rental.class));

    }
    */

}
