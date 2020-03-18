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
public class Desk {

    @JsonIgnore
    private Long id;

    private String deskId;

    private String name;

    private String roomId;

    private String roomName;

    private int deskMax;
    
    private int deskType;

    // Design
    private int t;

    private int l;

    private int w;

    private int h;


    @JsonIgnore
    private int useYn;

    private String deskNote;
    
    private Date insertDt;

    private Date updateDt;

}
