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
public class Branch {

    public Branch() {
    }

    public Branch(String branchId, String name) {
        this.branchId = branchId;
        this.name = name;
    }

    @JsonIgnore
    private Long id;

    private String branchId;
    
    private int branchType;

    private String name;

    private String tel;

    private String telEtc;

    private String postcode;

    private String address1;

    private String address2;

    private String hpUrl;

    private String locNote;

    private String locUrl;

    private String opNote;    
    
    private Integer areaType;
    
    private String ip;
    
    //통계를 위한 변수 선언
    private int curDesk;
    private int totalDesk;
    private int monthReservation;
    private int dayReservation;
    
    private int twodaysAgoMonthReservation;
	private int	twodaysAgoDayReservation;
	private double twodaysAgoMonthReservationRatio;
	private double twodaysAgoDayReservationRatio;
	
	private int yesterdayMonthReservation;
	private int	yesterdayDayReservation;
	private double yesterdayMonthReservationRatio;
	private double yesterdayDayReservationRatio;
	
	private int todayMonthReservation;
	private int	todayDayReservation;
	private double todayMonthReservationRatio;
	private double todayDayReservationRatio;
	
	//가공데이터
	private int yesterdayMonthReservationExtension;
	private int yesterdayMonthReservationEnd;
	//private int yesterdayDayReservationExtension;
	private int yesterdayMonthReservationExtensionNew;
	private int yesterdayDayReservationExtensionNew;
	
	
    private double enterRatio;
    private double dayRatio;
    private String pay;
    private String yesterdayPay;
    private Integer payAmount;

    private int multi_sum;
    private int single_sum;
    private int private_sum;
    
    // 남/여, 성인/학생 비율
//    private int mid1_cnt;
//    private int mid2_cnt;
//    private int mid3_cnt;
//    private int high1_cnt;
//    private int high2_cnt;
//    private int high3_cnt;
//    private int orgAdult_cnt;
//    private int etc_cnt;
    
    private String m;
    private String ym;
    
    private int man_cnt;
    private int woman_cnt;
    private int stu_cnt;
    private int adult_cnt;
    
    private int man_cnt_01;
    private int woman_cnt_01;
    private int stu_cnt_01;
    private int adult_cnt_01;

    private double manRatio_01;
    private double womanRatio_01;
    private double stuRatio_01;
    private double adultRatio_01;
    
    private int man_cnt_02;
    private int woman_cnt_02;
    private int stu_cnt_02;
    private int adult_cnt_02;

    private double manRatio_02;
    private double womanRatio_02;
    private double stuRatio_02;
    private double adultRatio_02;
    
    private int man_cnt_03;
    private int woman_cnt_03;
    private int stu_cnt_03;
    private int adult_cnt_03;

    private double manRatio_03;
    private double womanRatio_03;
    private double stuRatio_03;
    private double adultRatio_03;
    
    private int man_cnt_04;
    private int woman_cnt_04;
    private int stu_cnt_04;
    private int adult_cnt_04;

    private double manRatio_04;
    private double womanRatio_04;
    private double stuRatio_04;
    private double adultRatio_04;
    
    
    private int man_cnt_05;
    private int woman_cnt_05;
    private int stu_cnt_05;
    private int adult_cnt_05;

    private double manRatio_05;
    private double womanRatio_05;
    private double stuRatio_05;
    private double adultRatio_05;
    
    private int man_cnt_06;
    private int woman_cnt_06;
    private int stu_cnt_06;
    private int adult_cnt_06;

    private double manRatio_06;
    private double womanRatio_06;
    private double stuRatio_06;
    private double adultRatio_06;
    
    private int man_cnt_07;
    private int woman_cnt_07;
    private int stu_cnt_07;
    private int adult_cnt_07;

    private double manRatio_07;
    private double womanRatio_07;
    private double stuRatio_07;
    private double adultRatio_07;
    
    private int man_cnt_08;
    private int woman_cnt_08;
    private int stu_cnt_08;
    private int adult_cnt_08;

    private double manRatio_08;
    private double womanRatio_08;
    private double stuRatio_08;
    private double adultRatio_08;
    
    private int man_cnt_09;
    private int woman_cnt_09;
    private int stu_cnt_09;
    private int adult_cnt_09;

    private double manRatio_09;
    private double womanRatio_09;
    private double stuRatio_09;
    private double adultRatio_09;
    
    private int man_cnt_10;
    private int woman_cnt_10;
    private int stu_cnt_10;
    private int adult_cnt_10;

    private double manRatio_10;
    private double womanRatio_10;
    private double stuRatio_10;
    private double adultRatio_10;


    private int man_cnt_11;
    private int woman_cnt_11;
    private int stu_cnt_11;
    private int adult_cnt_11;

    private double manRatio_11;
    private double womanRatio_11;
    private double stuRatio_11;
    private double adultRatio_11;

    

    private int man_cnt_12;
    private int woman_cnt_12;
    private int stu_cnt_12;
    private int adult_cnt_12;

    private double manRatio_12;
    private double womanRatio_12;
    private double stuRatio_12;
    private double adultRatio_12;

    
    @JsonIgnore
    private int useYn;

    private int visibleYn;

    private int fingerprintYn;
    
    private int standYn;
    
    private Date insertDt;

    private Date updateDt;

    private Date openDt;
    
    private String openDt_S;
    
    private int checkoutYn;
    
    @Data
    public static class Display {

        private String branchId;

        private int branchType;

        private String name;


    }

    @Data
    public static class AdOrNotice {

        @JsonIgnore
        private Long id;

        private String branchId;

        private String adOrNoticeType;

        private String title;
        private String subtitle;

        private String imgUrl;

        @JsonIgnore
        private int useYn;

        private int visibleYn;

        private Date startDt;

        private Time startTm;

        private Date endDt;

        private Time endTm;

        private Date insertDt;

        private Date updateDt;

    }

}


