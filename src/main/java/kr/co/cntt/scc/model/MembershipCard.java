package kr.co.cntt.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 멤버쉽 카드
 *
 * Created by jaisunglee on 2016-11-04.
 */
@Data
@ToString(exclude = {"id", "useYn"})
public class MembershipCard {

    @JsonIgnore
    private Long id;

    private String membershipId;

    private String no;


    private String memberId;


    @JsonIgnore
    private int registerYn;

    @JsonIgnore
    private String registerBranchId;

    @JsonIgnore
    private String registerUserId;

    @JsonIgnore
    private Date registerDt;


    @JsonIgnore
    private int useYn;

    private Date insertDt;

    private Date updateDt;

}
