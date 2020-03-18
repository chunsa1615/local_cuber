package suprema;

import com.sun.jna.Callback;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
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


public class BS2UserBlob2 extends Structure   {	

		public byte[] userId = new byte[32];	
		
		 
	    public int formatVersion;
	    public int flag;
	    public int version;
	    public int numCards;
	    public int numFingers;
	    public int numFaces;
	    public int reserved2;
	    public int authGroupID;
	    public int faceChecksum;
	
	   
	
	    //public BS2UserSetting setting;    
	public int startTime;
	public int endTime;
	public int fingerAuthMode;
	public int cardAuthMode;
	public int idAuthMode;
	public int securityLevel;
	public byte[] name = new byte[48 * 4];
	
	
	//public BS2UserPhoto photo;
	public int size;
	public byte[] data = new byte[16 * 1024];
	
	public byte[] pin = new byte[32];
	
	public BS2CSNCard cardObjs;
	
	
	public BS2Fingerprint fingerObjs;
	
	
	public BS2Face faceObjs;
	
	
	public int[] accessGroupId = new int[16] ;

    
    
	@Override
	protected List<String> getFieldOrder() {
		// TODO Auto-generated method stub
		//return null;
		
		return asList(  "userId", "formatVersion", "flag", "version", "numCards", "numFingers", "numFaces", "reserved2", "authGroupID", "faceChecksum", "startTime", "endTime", "fingerAuthMode", "cardAuthMode", "idAuthMode", "securityLevel", "name", "size", "data", "pin", "cardObjs", "fingerObjs", "faceObjs", "accessGroupId");
//		return asList( "name", "userId", "formatVersion", "flag", "version", "numCards", "numFingers", "numFaces", "reserved", "authGroupID", "faceChecksum", "setting",  "photo", "pin", "type", "size", "data", "index", "flag2", "reserved2", "data2", "faceObjs", "accessGroupId");
//		return asList( "name", "userId", "user", "setting", "photo", "pin", "cardObjs", "fingerObjs", "faceObjs", "accessGroupId");
	}
	
	
}
	


