package kr.co.cntt.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Created by jslivane on 2016. 4. 12..
 */
@Data
@ToString(exclude = {"id", "useYn"})
public class Room {

    @JsonIgnore
    private Long id;

    private String roomId;

    private String name;
    
    private int roomType;

    // Design
    private int t;

    private int l;

    private int w;

    private int h;

    @JsonIgnore
    private int useYn;
    
    private String roomNote;

    private Date insertDt;

    private Date updateDt;

}
