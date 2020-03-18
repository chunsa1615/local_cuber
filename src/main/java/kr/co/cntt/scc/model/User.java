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
@ToString(exclude = {"id", "password", "useYn"})
public class User {

    @JsonIgnore
    private Long id;

    private String userId;

    private String name;

    @JsonIgnore
    private String password;
    
    private String Encoded_password;

    private String role;

    private String branches;

    private String branchesNames;

    @JsonIgnore
    private int useYn;

    private Date insertDt;

    private Date updateDt;

}
