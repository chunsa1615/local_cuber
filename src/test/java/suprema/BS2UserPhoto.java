package suprema;

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
public class BS2UserPhoto extends Structure{
    
    public int size;
    public byte[] data = new byte[16 * 1024];
    
    public static class ByReference extends BS2UserPhoto implements Structure.ByReference { };
    public static class ByValue extends BS2UserPhoto implements Structure.ByValue { }
    
	@Override
	protected List<String> getFieldOrder() {
		// TODO Auto-generated method stub
		//return null;
		return asList("size","data");
	}
	

}
