package kr.co.cntt.scc.service;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.model.BranchMember;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.model.Page;
import kr.co.cntt.scc.model.PageMaker;
import kr.co.cntt.scc.model.PreReservation;
import kr.co.cntt.scc.model.Reservation;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.util.DateUtil;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jslivane on 2016. 4. 5..
 */
@Service
@Transactional
@Slf4j
public class BranchPreReservationService {

  @Autowired
  NamedParameterJdbcTemplate jdbcTemplate;

  public BranchPreReservationService() {}

  public List<Branch> selectBranchOpenDt() {
    String s = " SELECT pr.branchId, pr.openDt " 
        + " FROM branch_pre_reservation_setup pr " 
        + " WHERE pr.useYn = :useYn ";

    Map<String, Object> args = new HashMap<>();
    args.put("useYn", Constants.USE);

    return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Branch.class));
  }

  public Branch selectBranchWithOpenDt(String branchId) {
    return selectBranchOpenDt().stream().filter(b -> b.getBranchId().equals(branchId)).findFirst().orElse(null); // FIXME
  }

  public List<PreReservation> selectPreReservation(String branchId, String startDt, String endDt, String keyword, Page page) {
    Map<String, Object> args = new HashMap<>();

    String s = " SELECT id, branchId, preReservationId, period, gender, phone, name, email, birth, memo, insertDt, updateDt " 
        + " FROM branch_pre_reservation " 
        + " WHERE branchId = :branchId "
        + " AND   useYn = :useYn ";
    
    if ( !StringUtils.isEmpty(startDt)) {
      s += " AND insertDt >= :startDt ";
    }
    
    if ( !StringUtils.isEmpty(endDt)) {
      s += " AND insertDt <= :endDt ";
    }
    
    if ( !StringUtils.isEmpty(keyword)) {
      s += " AND concat(name, email) like :keyword ";
    }

    if(page == null) {
      
    } else {
      int perPage = (page.getPage() - 1 ) * page.getPerPageNum();
      s += " limit " + perPage + ", " + page.getPerPageNum() + " ";
    }
    
    args.put("branchId", branchId);
    args.put("useYn", Constants.USE);
    args.put("startDt", startDt);
    args.put("endDt", endDt);
    args.put("keyword", "%" + keyword + "%");
    
    return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(PreReservation.class));
  }
  
  public int selectPreReservationCount(String branchId, String startDt, String endDt, String keyword) {
    Map<String, Object> args = new HashMap<>();
    
    String s = " SELECT count(id) " 
        + " FROM branch_pre_reservation " 
        + " WHERE branchId = :branchId "
        + " AND   useYn = :useYn ";
    
    if ( !StringUtils.isEmpty(startDt)) {
      s += " AND insertDt >= :startDt ";
    }
    
    if ( !StringUtils.isEmpty(endDt)) {
      s += " AND insertDt <= :endDt ";
    }
    
    if ( !StringUtils.isEmpty(keyword)) {
      s += " AND concat(name, email) like :keyword ";
    }
    
    args.put("branchId", branchId);
    args.put("useYn", Constants.USE);
    args.put("startDt", startDt);
    args.put("endDt", endDt);
    args.put("keyword", "%" + keyword + "%");
    
    return jdbcTemplate.queryForObject(s, args, int.class);
  }
  
  public PreReservation selectOnePreReservation(String branchId, String preReservationId) {
    String s = " SELECT id, branchId, preReservationId, period, gender, phone, name, email, birth, memo, insertDt, updateDt " 
        + " FROM branch_pre_reservation " 
        + " WHERE branchId = :branchId "
        + " AND   preReservationId = :preReservationId"
        + " AND   useYn = :useYn";


    Map<String, Object> args = new HashMap<>();
    args.put("branchId", branchId);
    args.put("preReservationId", preReservationId);
    args.put("useYn", Constants.USE);

    return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(PreReservation.class));
  }
  
  public int insertPreReservation(PreReservation preReservation) {
    String s = "INSERT INTO branch_pre_reservation ("
          + " branchId, preReservationId, period, gender, phone, name, email, birth, memo "
          + ") values ("
          + " :branchId, :preReservationId, :period, :gender, :phone, :name, :email, :birth, :memo "
          + ")";
    
    CombinedSqlParameterSource source = new CombinedSqlParameterSource(preReservation);

    // KeyHolder keyHolder = new GeneratedKeyHolder();
    // return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
    int result = jdbcTemplate.update(s, source);

    if (result == 0) {
      throw new InternalServerError("Failed to create Pre Reservation");

    }
    return result;
  }

  public int updatePreReservation(PreReservation preReservation) {
    String s = "UPDATE branch_pre_reservation SET "
                  + "  branchId = :branchId"
                  + ", period = :period"
                  + ", gender = :gender"
                  + ", phone = :phone"
                  + ", name =  :name"
                  + ", email = :email"
                  + ", birth = :birth"
                  + ", memo = :memo "
             + "WHERE preReservationId = :preReservationId";

    CombinedSqlParameterSource source = new CombinedSqlParameterSource(preReservation);

    // KeyHolder keyHolder = new GeneratedKeyHolder();
    // return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
    int result = jdbcTemplate.update(s, source);

    if (result == 0) {
      throw new InternalServerError("Failed to update Pre Reservation");

    }
    return result;
  }
  

  public int deletePreReservation(String branchId, PreReservation preReservation) {
      String s = " UPDATE branch_pre_reservation SET " +
              " useYn = :useYn, updateDt = NOW() " +
              " WHERE branchId = :branchId AND preReservationId = :preReservationId ";

      Map<String, Object> args = new HashMap<>();
      args.put("branchId", branchId);
      args.put("preReservationId", preReservation.getPreReservationId());
      args.put("useYn", Constants.NOT_USE);

      int result = jdbcTemplate.update(s, args);

      if (result == 0) {
          throw new InternalServerError("Failed to delete preReservation");

      } else {
          //History history = new History(branchId, Constants.HistoryType.MEMBER_DELETE, "");
          //history.
          //historyService.insertHistory(history);

      }

      return result;

  }
  

}
