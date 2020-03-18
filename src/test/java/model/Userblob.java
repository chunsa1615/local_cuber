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
public class Userblob {
    

    public byte[] name = new byte[48 * 4];
    
    public User user = new User();
    public UserSetting setting = new UserSetting();
    public UserPhoto photo = new UserPhoto();
    public Fingerprint fingerObjs = new Fingerprint();

    
    public byte[] pin = new byte[32];
    
    public int[] accessGroupId = new int[16];

}
