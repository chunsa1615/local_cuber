package kr.co.cntt.scc.service;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.model.Expense;
import kr.co.cntt.scc.model.Pay;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.model.Page;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import kr.co.cntt.scc.util.DateUtil;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pay 결제
 *
 * Created by jslivane on 2016. 6. 14..
 */
@Service
@Transactional
@Slf4j
public class BranchExpenseService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;

    /*******************************************************************************/   
    
    /**
     * 지출 리스트 조회 (결제일 기준)
     * @param branchId
     * @param payStartDt
     * @param payEndDt
     * @param memberId
     * @return
     *
     * MySQL Date Time Fuctions : http://dev.mysql.com/doc/refman/5.7/en/date-and-time-functions.html
     *
     */
    public List<Expense> selectExpenseList(String branchId, String expenseStartDt, String expenseEndDt,
                                          String payType, Page page) {

        String s = " SELECT e.branchId, e.expenseId, e.expenseDt, e.expenseTm, e.expenseGroup, e.expenseOption, e.payType, e.payInOutType, " +
                " e.expenseAmount, e.expenseNote, e.insertDt, e.updateDt, e.insertId " +
                " FROM branch_expense e " +
                " WHERE e.branchId = :branchId and e.useYn = :useYn ";


        if(!StringUtils.isEmpty(expenseStartDt)) {
            s += " AND e.expenseDt >= :startDate ";
        }

        if(!StringUtils.isEmpty(expenseEndDt)) {
            s += " AND e.expenseDt <= :endDate ";
        }
        
        if(!StringUtils.isEmpty(payType)) {
            s += " AND e.payType = :payType ";
        }

        s += " ORDER BY e.expenseDt ASC, e.expenseTm ASC ";

        if(page == null) {}
        else {
	        int perPage = (page.getPage() -1) * page.getPerPageNum();
	    	s += " limit "+perPage+", "+page.getPerPageNum()+" ";
        }
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("startDate", expenseStartDt);
        args.put("endDate", expenseEndDt);
        args.put("payType", payType);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Expense.class));

    }
 

    public Expense selectExpense(String branchId, String expenseId) {
        String s = " SELECT e.branchId, e.expenseId, e.expenseDt, e.expenseTm, e.expenseGroup, e.expenseOption, e.payType, e.payInOutType, " +
                " e.expenseAmount, e.expenseNote, e.insertDt, e.updateDt, e.insertId " +
                " FROM branch_expense e " +
                " WHERE e.branchId = :branchId AND e.expenseId = :expenseId AND e.useYn = :useYn ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("expenseId", expenseId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(Expense.class));

    }

    public int insertExpense(String branchId, String expenseId, Expense expense) {
        String s = " INSERT INTO branch_expense ( " +
                " branchId, expenseId, expenseDt, expenseTm, expenseGroup, expenseOption, payType, payInOutType, " +        		    
                " expenseAmount, expenseNote " +
                " ) VALUES ( " +
                " :branchId, :expenseId, :expenseDt, :expenseTm, :expenseGroup, :expenseOption, :payType, :payInOutType, " +
                " :expenseAmount, :expenseNote " +
                " ) ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(expense);
        source.addValue("branchId", branchId);
        source.addValue("expenseId", expenseId);
        //source.addValue("expenseDt", DateUtil.getCurrentDateString());
        source.addValue("expenseTm", DateUtil.getCurrentTimeString());

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to create pay");

        } else {
            //History history = new History(branchId, Constants.HistoryType.PAY_CREATE, pay.toString());
            //history.setOrderId(pay.getOrderId());
            //history.setPayId(payId);
            //history.setMemberId(pay.getMemberId());
            //historyService.insertHistory(history);

        }

        return result;


    }

    public int updateExpense(String branchId, String expenseId, Expense expense) {
        String s = " UPDATE branch_expense SET " +
                " expenseGroup = :expenseGroup, expenseOption = :expenseOption, payType = :payType, payInOutType = :payInOutType, " +        		    
                " expenseAmount = :expenseAmount , expenseNote = :expenseNote, expenseDt = :expenseDt, expenseTm = :expenseTm, updateDt = NOW() " +
                " WHERE branchId = :branchId AND expenseId = :expenseId ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(expense);
        source.addValue("branchId", branchId);
        source.addValue("expenseId", expenseId);

        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to update pay");

        } else {
            History history = new History(branchId, Constants.HistoryType.PAY_UPDATE, expense.toString());
            //history.setOrderId(pay.getOrderId());
            //history.setPayId(payId);
            //history.setMemberId(pay.getMemberId());
            //historyService.insertHistory(history);

        }

        return result;

    }

    public int deleteExpense(String branchId, String expenseId) {
        String s = " UPDATE branch_expense SET " +
                " useYn = :useYn, updateDt = NOW() " +
                " WHERE branchId = :branchId AND expenseId = :expenseId ";        
        
        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("expenseId", expenseId);
        args.put("useYn", Constants.NOT_USE);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to delete expense");

        } else {
            //History history = new History(branchId, Constants.HistoryType.PAY_DELETE, "");
            //history.setPayId(payId);
            //historyService.insertHistory(history);

        }

        return result;

    }



}
