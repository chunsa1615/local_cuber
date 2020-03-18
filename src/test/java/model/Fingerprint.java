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
public class Fingerprint {
    
    public byte index;
    public byte flag;
    public byte[] reserved = new byte[2];
    public byte[][] data = new byte[2][3008];

}
