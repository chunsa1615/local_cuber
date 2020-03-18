package suprema;

import static java.util.Arrays.asList;

import java.util.List;

import com.sun.jna.Structure;


public class BS2Event extends Structure {
	public int id = 0;
	public int dateTime = 0;
	public int deviceID = 0;
	
	public byte[] userID = new byte[32];
	public int uid = 0;
	public int doorID = 0;
	public int liftID = 0;
	public int zoneID = 0;
	

//	public int ioDeviceID = 0;
//	public short port = 0;
//	public byte value = 0;
//	public byte[] reserved = new byte[25];
//
//	public int zoneID_ = 0;
//	public int doorID_ = 0;
//	public int ioDeviceID_ = 0;
//	public short port_ = 0;
//	public byte[] reserved_ = new byte[18];
//
//
//	public int zoneID__ = 0;
//	public int[] doorID__ = new int[4];
//	public byte[] reserved__ = new byte[12];
//
//		
//	
//	public int code = 0;
//
//	public byte subCode = 0;
//	public byte mainCode = 0;
//
//	public byte param = 0;

	
	protected List<String> getFieldOrder() {
//		return asList("id", "dateTime", "deviceID", "userID", "uid", "doorID", "liftID", "zoneID", "ioDeviceID", "port", "value", "reserved", "zoneID_", "ioDeviceID_","port_", "doorID_", "reserved_","zoneID__", "doorID__", "reserved__",
//				"code", "subCode", "mainCode", "param");
		return asList("id", "dateTime", "deviceID", "userID", "uid", "doorID", "liftID", "zoneID");

	}
}
