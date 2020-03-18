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
public class BS2Job extends Structure{

    public int code;
    public byte[] label = new byte[1024];

    
    public static class ByReference extends BS2Job implements Structure.ByReference { };
    public static class ByValue extends BS2Job implements Structure.ByValue { }
    
    
	@Override
	protected List<String> getFieldOrder() {
		// TODO Auto-generated method stub
		//return null;
		return asList("code","label");
	}
	

}
