package kr.co.cntt.scc.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class PreReservation {
  @JsonIgnore
  private Long id;
  private String branchId;
  private String preReservationId;
  private String period;
  private String gender;
  private String name;
  private String phone;
  private String email;
  private String birth;
  private String memo;
  private Date insertDt;
  private Date updateDt;
  private int useYn;
}
