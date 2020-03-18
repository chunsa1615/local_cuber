package suprema;

import com.sun.jna.Callback;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ByteByReference;

import static java.util.Arrays.asList;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * 
 *
 * 
 */


public class userInfo extends Structure {	
//public class BS2UserBlob extends Structure implements Structure.ByReference {
	
	public static class ByReference extends userInfo implements Structure.ByReference { }

    public BS2UserBlob.ByReference pUserInfo;
	
	public userInfo() {
		//this.name = new byte[48 * 4];
		//this.pin = new byte[32];
	}	
	
//	public BS2UserBlob.ByReference[] toArray(int size) {
//		return (BS2UserBlob.ByReference[]) pUserInfo.toArray(size);
//	}



	
	//public static class ByValue extends BS2UserBlob implements Structure.ByValue {}
    
    
    
	@Override
	protected List<String> getFieldOrder() {
		// TODO Auto-generated method stub
		//return null;
		return asList( "pUserInfo");
	}
	
	
	
}
	


