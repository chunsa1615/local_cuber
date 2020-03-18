package suprema;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

import static java.util.Arrays.asList;
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
//@Data
public class BS2UserSetting extends Structure {
	
//	public static class ByReference extends BS2UserSetting implements Structure.ByReference { }
    
	//public int startTime;
	public int startTime;
    //public int endTime;
	public int endTime;
    //public int fingerAuthMode;
	public byte fingerAuthMode;
    public byte cardAuthMode;
    public byte idAuthMode;
    //public int securityLevel;
    public byte securityLevel;

    public BS2UserSetting() {
    	//startTime = 0;
    	//endTime = 0;
    	fingerAuthMode = 0;
    	cardAuthMode = 0;
    	idAuthMode = 0;
    	securityLevel = 0;
    }
    
    public BS2UserSetting(Pointer peer) { 
		
    	super();
		useMemory(peer);		
		read();
		
    }
    
//    public static class ByValue extends BS2UserSetting implements Structure.ByValue { }
    
    
	@Override
	protected List<String> getFieldOrder() {
		// TODO Auto-generated method stub
		//return null;
		return asList("startTime","endTime", "fingerAuthMode", "cardAuthMode", "idAuthMode", "securityLevel");
	}
	

}
