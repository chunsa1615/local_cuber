package kr.co.cntt.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * (지점의) 회원
 *
 *
 * Created by jaisunglee on 2016. 4. 5..
 *
 * @see Member 회원
 * @see Branch 지점
 */
@Data
@ToString(exclude = {"id", "useYn"})
public class BranchMember {
    @JsonIgnore
    private Long id;

    private String branchId;
    private String memberId;
    
    private String branchNm;

    /**
     * 회원번호
     */
    private String regDt;
    private String no;

    private String name;

    private String tel;
    private String telParent;
    private String telEtc;
    private String email;

    private String school;
    private int schoolGrade;

    private int gender;
    private int examType;    
    
    private int jobType;
    private int interestType;
    
    private String birthDt;

    private String postcode;
    private String address1;
    private String address2;
    private String addressDetail;
    private String memberNote;
    private String cabinet;
    private String remainTime;
    private int timeYn;
    
    /**
     * 멤버십 번호
     */
    private String membershipNo;

    @JsonIgnore
    private int useYn;

    private Date insertDt;

    private Date updateDt;
    
    private int enterexitYes;
    
    private int smsYes;
    
    private int promotionYes;

    private int personalYn;
    
    private int utilYn;
    
    private int expireYn;
        
}

