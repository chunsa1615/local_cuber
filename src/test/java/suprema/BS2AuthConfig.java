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


public class BS2AuthConfig extends Structure {	
//public class BS2UserBlob extends Structure implements Structure.ByReference {
	
	public static class ByReference extends BS2AuthConfig implements Structure.ByReference { }
	
	public int useServerMatching = 1;

	public BS2AuthConfig() {
		useServerMatching = 1;
		//accessGroupId = new int[16] ;
	}	
	
	public BS2AuthConfig(Pointer peer) {
		//this();
		//this.useMemory(ptr);
		//this.read();
		super(peer);
		//read();
	}



	
	//public static class ByValue extends BS2UserBlob implements Structure.ByValue {}
    
    
    
	@Override
	protected List<String> getFieldOrder() {
		// TODO Auto-generated method stub
		//return null;
		return asList( "useServerMatching");
	}
	
	
	
}
	


