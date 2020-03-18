package suprema;

import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import static java.util.Arrays.asList;

public class BS2UserTest extends Structure {

	public byte[] userId = new byte[32];
	
//	public byte[] name = new byte[48 * 4];
	
	//public BS2Fingerprint fingerObjs;
//	public BS2Fingerprint[] fingerObjs = new BS2Fingerprint[1];
//	
//	public BS2CSNCard[] cardObjs = new BS2CSNCard[1];
//	
//	public byte[] pin = new byte[32];
//	
//	public int[] accessGroupId = new int[16] ;
	public int finger;
    public int card;
    public int pin;
    public int accessGroup;
    public int startTime; //startTime
    public int endTime; //endTime
    public byte[] name = new byte[48*4];
    

    
//	public int startTime;
//	
//	public int endTime;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	@Override
	protected List<String> getFieldOrder() {
//		return asList("userId", "name", "fingerObjs", "cardObjs", "pin", "accessGroupId", "startTime", "endTime");
		return asList("userId","finger","card","pin","accessGroup","startTime","endTime","name");
	}
}
