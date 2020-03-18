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
public class BS2JobData extends Structure{

    public byte numJobs;
    public byte[] reserved = new byte[3];

    public BS2JobData[] jobs = new  BS2JobData[16];
    	
    
    public static class ByReference extends BS2JobData implements Structure.ByReference { };
    public static class ByValue extends BS2JobData implements Structure.ByValue { }
    
    
	@Override
	protected List<String> getFieldOrder() {
		// TODO Auto-generated method stub
		//return null;
		return asList("numJobs", "reserved", "jobs");
	}
	

}
