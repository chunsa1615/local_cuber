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
public class User {
    
	public byte[] userId = new byte[32];
	
    public int formatVersion;
    public int flag;
    public int version;
    public int numCards;
    public int numFingers;
    public int numFaces;
    public int reserved;
    public int authGroupID;
    public int faceChecksum;

}
