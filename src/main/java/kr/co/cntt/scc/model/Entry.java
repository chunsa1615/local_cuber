package kr.co.cntt.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * (지점의) 회원 출입(입퇴실)
 * Created by jslivane on 2016. 4. 5..
 */
@Data
@ToString(exclude = {"id", "useYn"})
public class Entry {
    @JsonIgnore
    private Long id;

    private String branchId;
    private String memberId;
    private Date entryDt;
    private Date BusinessDt;
    private int entryType;

    private String reservationId;
    private String deskId;

    @JsonIgnore
    private int useYn;

    private Date insertDt;

    private Date updateDt;

    @Data
    public static class Create {
        @NotBlank
        private String entryType;

        @NotBlank
        @Size(min = 4)
        private String no;

//        @NotBlank
//        @Size(min = 4)
//        private String password;

    }

    @Data
    public static class Response extends ApiResponse {

        private String memberId;

        private String name;

        private List<Entry> entryList;

        private Reservation reservation;

        private List<Desk> deskList;
        
        //추가
        private String time; //시간
        private int rank;	//랭킹
        private String schoolGrade;	//성인or학생
        private int deskDt; //남은기간
        private String endDt;
        private String studyDt;
        private String todayStudyTime; //당일학습시간
        
    }

}
