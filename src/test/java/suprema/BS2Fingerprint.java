package suprema;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

import kr.co.cntt.scc.app.admin.model.main;

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
public class BS2Fingerprint extends Structure {
	

    public byte index;
    public byte flag;
    public byte[] reserved = new byte[2];
    public Pointer[] data = new Pointer[2];

    public BS2Fingerprint() {
//    	index = 0;
//    	flag = 0;
//    	reserved = new Byte[2];
//    	data = new byte[2][384];
    }
    
    public BS2Fingerprint(Pointer peer) { 
		
    	super();
		useMemory(peer);
		read();
		
    }
    public static class ByReference extends BS2Fingerprint implements Structure.ByReference { }
    public static class ByValue extends BS2Fingerprint implements Structure.ByValue { }
    
    @Override
    public String toString() {
    	return super.toString();
    }
	@Override
	protected List<String> getFieldOrder() {
		// TODO Auto-generated method stub
		//return null;
		return asList("index","flag", "reserved", "data");
	}
}
