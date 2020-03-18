package suprema;

import com.sun.jna.Callback;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.StringArray;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import static java.util.Arrays.asList;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * 
 *
 * 
 */
public class BS2User extends Structure {
    
	//public static class ByReference extends BS2User implements Structure.ByReference { }
		
	/**
	 * 
	 */

	public byte[] userId = new byte[32];
	    
	//public Pointer userId = new Memory(32);
	//public Pointer userId;
    //public char[] userId = new char[32];
		
    public int formatVersion;
    public int flag;
    public int version;
    public int numCards;
    public int numFingers; //startTime
    public int numFaces; //endTime
    public int reserved;
    public int authGroupID;
    public int faceChecksum;
    
    
    
	public BS2User() {
    	//super(Structure.ALIGN_NONE);
		
		formatVersion = 0;
		flag = 0;
		version = 0;
		numCards = 0;
		numFingers = 0;
		numFaces = 0;
		reserved = 0;
		authGroupID = 0;
		faceChecksum = 0;
    }
	public BS2User(Pointer peer) {
		
		super(peer);
		useMemory(peer);
		read();
		
	}
    
    //public static class BS2UserByValue extends BS2User implements Structure.ByValue { }
    
	@Override
	protected List<String> getFieldOrder() {
		// TODO Auto-generated method stub
		//return null;
		return asList("userId", "formatVersion", "flag", "version", "numCards", "numFingers", "numFaces", "reserved", "authGroupID", "faceChecksum");
	}



	
	
	
}
