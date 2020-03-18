package suprema;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ByteByReference;

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
public class BS2UserBlobEx extends Structure{	
	public static class ByReference extends BS2UserBlobEx implements Structure.ByReference { };
	
	public BS2User user;
    public BS2UserSetting setting;
    public byte[] name = new byte[48 * 4];
    
//    public IntByReference name;
    //IntByReference nameTemp = new IntByReference();    
    //public Pointer name = nameTemp.getPointer();
    
    
    public BS2UserPhoto photo;
    public byte[] pin = new byte[32];
    public IntByReference cardObjs;
    public IntByReference fingerObjs;
    public IntByReference faceObjs;    
    //List<String> accessGroupId;
    public BS2Job job;
    public IntByReference phrase;
    
    public int[] accessGroupId = new int[16] ;
    
    public BS2UserBlobEx() {
		user = new BS2User();
		setting = new BS2UserSetting();
		photo = new BS2UserPhoto();
		job = new BS2Job();
		accessGroupId = new int[16] ;
    }
    
    
    
	@Override
	protected List<String> getFieldOrder() {
		// TODO Auto-generated method stub
		//return null;
		return asList( "user", "setting", "name", "photo", "pin", "cardObjs", "fingerObjs", "faceObjs", "job", "phrase", "accessGroupId");
	}

	

	
}


