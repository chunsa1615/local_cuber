package suprema;

import com.sun.jna.Structure;

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
public class BS2SimpleDeviceInfo extends Structure{
    
    public int id ;
    public int type;
    public int connectionMode;
    public int ipv4Address;
	public int port;
    public int maxNumOfUser;
    public int userNameSupported;
    public int userPhotoSupported;
    public int pinSupported;
    public int cardSupported;
    public int fingerSupported;
    public int faceSupported;
    public int wlanSupported;
    public int tnaSupported;
    public int triggerActionSupported;
    public int wiegandSupported;
    public int imageLogSupported;
    public int dnsSupported;
    public int jobCodeSupported;        
    public int wiegandMultiSupported;
    public int rs485Mode;
    public int sslSupported;
    public int rootCertExist;
    public int dualIDSupported;
    public int useAlphanumericID;
    public int connectedIP;
    public int phraseCodeSupported;
    public int card1xSupported;
    public int systemExtSupported;
    public int voipSupported;

    
    public static class ByReference extends BS2SimpleDeviceInfo implements Structure.ByReference { };
    public static class ByValue extends BS2SimpleDeviceInfo implements Structure.ByValue { }
    
    
	@Override
	protected List<String> getFieldOrder() {
		// TODO Auto-generated method stub
		//return null;
		return asList("id","type", "connectionMode", "ipv4Address", "port", "maxNumOfUser", "userNameSupported", "userPhotoSupported", "pinSupported", "cardSupported",
				"fingerSupported", "faceSupported", "wlanSupported", "tnaSupported", "triggerActionSupported", "wiegandSupported", "imageLogSupported", "dnsSupported",
				"jobCodeSupported", "wiegandMultiSupported", "rs485Mode", "sslSupported", "rootCertExist", "dualIDSupported", "useAlphanumericID", "connectedIP", 
				"phraseCodeSupported", "card1xSupported", "systemExtSupported", "voipSupported");
	}
	

}
