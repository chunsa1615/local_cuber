package suprema;

import com.sun.jna.Callback;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.WString;
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


public class BS2UserBlob extends Structure   {	
//public class BS2UserBlob extends Structure implements Structure.ByReference {
	
	public static class ByReference extends BS2UserBlob implements Structure.ByReference { }
	
	 //public PointerByReference name = new PointerByReference();
	
	public BS2User user;
	public BS2UserSetting setting; // = new BS2UserSetting();
	
	//public BS2User user = new BS2User();   
    
    public byte[] name = new byte[48 * 4];
    
    //public Pointer name = new Pointer(0);
    
//    public IntByReference name;
    //IntByReference nameTemp = new IntByReference();    
    //public Pointer name = nameTemp.getPointer();
    
    
    public BS2UserPhoto photo; // = new BS2UserPhoto();
    public byte[] pin = new byte[32];
    
    //public BS2Fingerprint fingerObjs;
    //public PointerByReference fingerObjs = new PointerByReference();
    //public PointerByReference fingerObjs;
    public BS2Fingerprint fingerObjs;
    
    public IntByReference cardObjs;    
    //public IntByReference fingerObjs;
    public IntByReference faceObjs;    
    
    //List<String> accessGroupId;
    public int[] accessGroupId = new int[16] ;

    
	public BS2UserBlob() {
		//name = new byte[48 * 4];
//		pin = new byte[32];
//		
//		user = new BS2User();
//		setting = new BS2UserSetting();
//		photo = new BS2UserPhoto();	
//		//fingerObjs = new BS2Fingerprint();
//		
//		accessGroupId = new int[16];
		
		//super();
	}	
	
	public BS2UserBlob(Pointer peer) {
		//this();
		//this.useMemory(ptr);
		//this.read();
		//super(peer);
		
		super();
		useMemory(peer);
		read();
		
		//super(peer);
		//read();
	}



	
	//public static class ByValue extends BS2UserBlob implements Structure.ByValue {}
    
    
    
	@Override
	protected List<String> getFieldOrder() {
		// TODO Auto-generated method stub
		//return null;
		return asList("user", "setting", "name", "photo", "pin", "cardObjs", "fingerObjs", "faceObjs", "accessGroupId");
	}
	
	
	
}
	


