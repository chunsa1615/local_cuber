package kr.co.cntt.scc.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

/**
 * (시스템의) 사용자
 * Created by jslivane on 2016. 4. 20..
 */
@Data
public class CommonCode {

    @JsonIgnore
    private Long id;

    private String branchId;

    private String branchNm;

    private String codeType;
    
    private String code;

    private String codeNm;

    private String description;


    @JsonIgnore
    private int useYn;

    private Date insertDt;

    private Date updateDt;

}
