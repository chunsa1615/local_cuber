package kr.co.cntt.scc.service;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.model.Order;
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
 * Order 
 * Created by jslivane on 2016. 6. 14..
 */
@Service
@Transactional
@Slf4j
public class BranchOrderService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;

    /*******************************************************************************/


    /**
     * 주문 리스트 조회
     * @param branchId
     * @param orderStartDt
     * @param orderEndDt
     * @param memberId
     * @return
     * MySQL Date Time Fuctions : http://dev.mysql.com/doc/refman/5.7/en/date-and-time-functions.html
     *
     */
    public List<Order> selectOrderList(String branchId, String orderStartDt, String orderEndDt, String memberId) {

        String s = " SELECT o.branchId, o.orderId, o.memberId, " +
                " o.orderDt, o.orderTm, o.orderNote, o.orderStatus, o.insertDt, o.updateDt " +
                " FROM branch_order o " +
                " WHERE o.branchId = :branchId ";

        if(!StringUtils.isEmpty(memberId)) {
            s += " AND o.memberId = :memberId ";

        }

        if(!StringUtils.isEmpty(orderStartDt)) {
            //orderStartDt = DateUtil.getCurrentDateString();
            s += " AND o.orderDt >= :orderStartDt";

        }

        if(!StringUtils.isEmpty(orderEndDt)) {
            //orderEndDt = DateUtil.getCurrentDateString();
            s += " AND o.orderDt <= :orderEndDt ";

        }

        s += " AND o.useYn = :useYn ";

        s += " ORDER BY o.id DESC ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("orderStartDt", orderStartDt);
        args.put("orderEndDt", orderEndDt);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(Order.class));

    }


    /*******************************************************************************/


    public Order selectOrder(String branchId, String orderId) {
        String s = " SELECT o.branchId, o.orderId, o.memberId, " +
                " o.orderDt, o.orderTm, o.orderNote, o.orderStatus, o.insertDt, o.updateDt " +
                " FROM branch_order o " +
                " WHERE o.branchId = :branchId AND o.orderId = :orderId AND o.useYn = :useYn ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("orderId", orderId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(Order.class));

    }

    public int insertOrder(String branchId, String orderId, Order order) {
        String s = " INSERT INTO branch_order ( " +
                " branchId, orderId, memberId, orderDt, orderTm, orderNote, orderStatus " +
                " ) VALUES ( " +
                " :branchId, :orderId, :memberId, :orderDt, :orderTm, :orderNote, :orderStatus " +
                ") ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(order);
        source.addValue("branchId", branchId);
        source.addValue("orderId", orderId);
        source.addValue("orderDt", DateUtil.getCurrentDateString());
        source.addValue("orderTm", DateUtil.getCurrentTimeString());

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to create order");

        } else {
            History history = new History(branchId, Constants.HistoryType.ORDER_CREATE, order.toString());
            history.setOrderId(orderId);
            history.setMemberId(order.getMemberId());
            //history.setDeskId(order.getDeskId());
            historyService.insertHistory(history);

        }

        return result;


    }

    public int updateOrder(String branchId, String orderId, Order order) {
        String s = " UPDATE branch_order SET " +
                " memberId = :memberId, orderNote = :orderNote, orderStatus = :orderStatus, updateDt = NOW() " +
                " WHERE branchId = :branchId AND orderId = :orderId ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(order);
        source.addValue("branchId", branchId);
        source.addValue("orderId", orderId);

        int result = jdbcTemplate.update(s, source);

        if (result == 0) {
            throw new InternalServerError("Failed to update order");

        } else {
            History history = new History(branchId, Constants.HistoryType.ORDER_UPDATE, order.toString());
            history.setOrderId(orderId);
            history.setMemberId(order.getMemberId());
            //history.setDeskId(order.getDeskId());
            historyService.insertHistory(history);

        }

        return result;

    }

    public int deleteOrder(String branchId, String orderId) {
        String s = " UPDATE branch_order SET " +
                " orderStatus = :orderStatus, " +
                " useYn = :useYn, updateDt = NOW() " +
                " WHERE branchId = :branchId AND orderId = :orderId ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("orderId", orderId);
        args.put("orderStatus", Constants.OrderStatusType.CANCELED.getValue());
        args.put("useYn", Constants.NOT_USE);

        int result = jdbcTemplate.update(s, args);

        if (result == 0) {
            throw new InternalServerError("Failed to delete order");

        } else {
            History history = new History(branchId, Constants.HistoryType.ORDER_DELETE, "");
            history.setOrderId(orderId);
            historyService.insertHistory(history);

        }

        return result;

    }


    /*******************************************************************************/


    public Order selectCurrentOrderByMemberId(String branchId, String memberId) {

        String s = " SELECT o.branchId, o.orderId, o.memberId, o.orderNote, o.orderStatus, o.insertDt, o.updateDt " +
                " FROM branch_order o " +
                " WHERE o.branchId = :branchId AND o.memberId = :memberId AND o.useYn = :useYn " +
                " ORDER BY o.id DESC LIMIT 1 ";

        Map<String, Object> args = new HashMap<>();
        args.put("branchId", branchId);
        args.put("memberId", memberId);
        args.put("useYn", Constants.USE);

        return jdbcTemplate.queryForObject(s, args, new BeanPropertyRowMapper<>(Order.class));

    }


    /*******************************************************************************/


}
