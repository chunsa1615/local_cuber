package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Reservation 예약
 *
 * Created by jslivane on 2016. 8. 12..
 *
 */
@Data
public class UserSetting {
    
	public int startTime;
    //public int endTime;
	public int endTime;
    //public int fingerAuthMode;
	public byte fingerAuthMode;
    public byte cardAuthMode;
    public byte idAuthMode;
    //public int securityLevel;
    public byte securityLevel;


}
