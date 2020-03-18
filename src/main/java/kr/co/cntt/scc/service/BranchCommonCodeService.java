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
import kr.co.cntt.scc.model.CommonCode;
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
public class BranchCommonCodeService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HistoryService historyService;



    public BranchCommonCodeService() {
    }


    /*******************************************************************************/


    /**
     * (캐시 제거) 공통 코드
     */
    @CacheEvict(cacheNames = "commonCodes", allEntries = true)
    public void resetCommonCodeList() { }

    /**
     * (캐시) 공통 코드 리스트 조회
     * @return
     * Cache : http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html
     */    
    public List<CommonCode> selectCommonCodeList(String branchId, String codeType) {

        String s = " SELECT c.branchId, c.branchNm, c.codeType, c.code, c.codeNm, c.description, c.insertDt, c.updateDt " +                
                " FROM common_code c " +
                " WHERE c.useYn = :useYn " +
                " AND c.branchId = :branchId " +
                " AND c.codeType = :codeType " +
                " ORDER BY c.codeNm ";



        Map<String, Object> args = new HashMap<>();
        args.put("useYn", Constants.USE);
        args.put("branchId", branchId);
        args.put("codeType", codeType);
        
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(CommonCode.class));

    }

    
    
    public List<CommonCode> selectCommonCodeListNotUse(String branchId, String codeType) {

        String s = " SELECT c.branchId, c.branchNm, c.codeType, c.code, c.codeNm, c.description, c.insertDt, c.updateDt " +                
                " FROM common_code c " +
                " WHERE c.useYn = :useYn " +
                " AND c.branchId = :branchId " +
                " AND c.codeType = :codeType " +
        		" ORDER BY c.insertDt asc ";


        Map<String, Object> args = new HashMap<>();
        args.put("useYn", Constants.NOT_USE);
        args.put("branchId", branchId);
        args.put("codeType", codeType);
        
        return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(CommonCode.class));

    }
    
    
    
    
    //가장 마지막 순서의 코드
    //@Cacheable(cacheNames = "commonCodes")
    public Integer selectCommonCode(String branchId, String codeType) {

        String s = " SELECT c.code " +                
                " FROM common_code c " +
                " WHERE c.useYn = :useYn " +
                " AND c.branchId = :branchId " +
                " AND c.codeType = :codeType " +
        		" ORDER BY c.id desc " +        		
                " limit 1 ";


        Map<String, Object> args = new HashMap<>();
        args.put("useYn", Constants.USE);
        args.put("branchId", branchId);
        args.put("codeType", codeType);
        
        //return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(CommonCode.class));
        return jdbcTemplate.queryForObject(s, args, Integer.class);

    }    
    


    public List<CommonCode> insertCommonCode(CommonCode commonCode) {
        String s = " INSERT INTO common_code ( " +
                " branchId, branchNm, codeType, code, codeNm, description" +                
                " ) VALUES ( " +
                " :branchId, :branchNm, :codeType, :code, :codeNm, :description" +
                " ) ";

        CombinedSqlParameterSource source = new CombinedSqlParameterSource(commonCode);        

        //KeyHolder keyHolder = new GeneratedKeyHolder();
        //return jdbcTemplate.update(s.toString(), source, keyHolder, new String[] {"id"});
        int result = jdbcTemplate.update(s, source);
        List<CommonCode> commonCodeList = null;
        
        if (result == 0) {
            throw new InternalServerError("Failed to create commonCode");

        } else {
        	commonCodeList = selectCommonCodeList(commonCode.getBranchId(), commonCode.getCodeType());
        	History history = new History(commonCode.getBranchId(), Constants.HistoryType.COMMONCODE_CREATE, commonCode.toString());            
            historyService.insertHistory(history);

        }

        return commonCodeList;

    }
}
