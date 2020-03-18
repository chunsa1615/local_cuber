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
public class BS2Face extends Structure{
    
	public int faceIndex;
    public int numOfTemplate;
    public int flag;
    public int reserved;
    
    public int imageLen;
    public int reserved2;
    
    public int[] imageData = new int[16384];
    //public int[][] templateData = new int[30][3008];
    public Pointer[] templateData = new Pointer[30];
    
    public static class ByReference extends BS2Face implements Structure.ByReference { };
    public static class ByValue extends BS2Face implements Structure.ByValue { }
    
	@Override
	protected List<String> getFieldOrder() {
		// TODO Auto-generated method stub
		//return null;
		return asList("faceIndex","numOfTemplate", "flag", "reserved", "imageLen", "reserved2", "imageData", "templateData");
	}
	

}
