package kr.co.cntt.scc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import kr.co.cntt.scc.alimTalk.AlimTalk;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.util.AuthUtil;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;
import lombok.extern.slf4j.Slf4j;

/**
 * 시스템 사용이력 서비스
 * Created by jslivane on 2016. 7. 7..
 */
@Service
@Slf4j
public class HistoryService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    /*******************************************************************************/

    public int insertHistory(History history) {
        String s = " INSERT INTO history ( " +
                " branchId, insertId, type, memberId, orderId, reservationId, payId, roomId, deskId, userId, d " +
                " ) VALUES ( " +
                " :branchId, :insertId, :type, :memberId, :orderId, :reservationId, :payId, :roomId, :deskId, :userId, :d " +
                " ) ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(history);
        source.addValue("insertId", AuthUtil.getCurrentUserId());
        source.addValue("type", history.getType().getValue());

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        return jdbcTemplate.update(s, source);

    }
    
    //알림톡 history
    public int insertAlimtalkHistory(AlimTalk.Request alimTalkReq, AlimTalk.Response alimTalkRes, String link_url, String branchId) {
        String s = " INSERT INTO history_alimtalk ( " +
                " serial_number, sender_key, phone_number, template_code, message, " +
                " response_method, link_url, result_code, result_message, branchId  " +
                " ) VALUES ( " +
                " :serial_number, :sender_key, :phone_number, :template_code, :message, " +
                " :response_method, :link_url, :result_code, :result_message, :branchId " +
                " ) ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(alimTalkReq);
        source.addValue("link_url", link_url);
        source.addValue("result_code", alimTalkRes.getCode());
        source.addValue("result_message", alimTalkRes.getMessage());
        source.addValue("branchId", branchId);
        
        
        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        return jdbcTemplate.update(s, source);

    }


}
