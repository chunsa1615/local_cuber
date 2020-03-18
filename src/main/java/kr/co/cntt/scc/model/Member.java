package kr.co.cntt.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 회원
 *
 * Created by jaisunglee on 2016-11-04.
 *
 * @see BranchMember (지점의) 회원
 *
 */
@Data
@ToString(exclude = {"id", "useYn"})
public class Member {
    @JsonIgnore
    private Long id;

    private String memberId;

    private String no;
    private String membershipNo;
    
    private String name;

    private String tel;
    private String telParent;
    private String telEtc;
    private String email;

    private String school;
    private int schoolGrade;

    private int gender;
    private int examType;

    private String birthDt;

    private String postcode;
    private String address1;
    private String address2;

    @JsonIgnore
    private int useYn;

    private Date insertDt;

    private Date updateDt;

}
