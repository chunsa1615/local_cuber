package kr.co.cntt.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * 지점 (센터)
 * Created by jslivane on 2016. 4. 5..
 */
@Data
@ToString(exclude = {"id", "useYn"})
public class AdminStatistics {

    public AdminStatistics() {
    }


    @JsonIgnore
    private Long id;

    private String m;
    private String branchNm;
    private String totalDesk;
    
    
    // 남/여, 성인/학생 비율
    private int man_cnt;
    private int woman_cnt;
    private int stu_cnt;
    private int adult_cnt;
    private int mid1_cnt;
    private int mid2_cnt;
    private int mid3_cnt;
    private int high1_cnt;
    private int high2_cnt;
    private int high3_cnt;
    private int orgAdult_cnt;
    private int etc_cnt;
    
    private double manRatio;
    private double womanRatio;
    private double stuRatio;
    private double adultRatio;
    
    
    @JsonIgnore
    private int useYn;


}


