package suprema;

import static java.util.Arrays.asList;

import java.util.List;

import com.sun.jna.Structure;

public class ABCD extends Structure {
	public short A = 0;
	public int B = 0;
	public int C = 0;
	
	public byte[] D = new byte[32];
	public int E = 0;
	public int F = 0;
	public int G = 0;
	public int H = 0;
	
	public static class Struct extends Structure {
		public int ioDeviceID = 0;
		public short port = 0;
		public byte value = 0;
//		public byte[] reserved = new byte[25];
		@Override
		protected List<String> getFieldOrder() {
			return asList("ioDeviceID", "port", "value");
		}
	}
	
	public Struct struct;
	
	public static class Alarm extends Structure {
		public int zoneID = 0;
		public int doorID = 0;
		public int ioDeviceID = 0;
		public short port = 0;
//		public byte[] reserved = new byte[18];
		@Override
		protected List<String> getFieldOrder() {
			return asList("zoneID","doorID", "ioDeviceID", "port");
		}
	}
	
	public Alarm alarm;
	
	public static class Interlock extends Structure {
		public int zoneID = 0;
		public int[] doorID = new int[4];
//		public byte[] reserved = new byte[12];
		@Override
		protected List<String> getFieldOrder() {
			return asList("zoneID", "doorID");
		}
	}
	public Interlock interlock;
	
	public int code = 0;
	public static class Struct2 extends Structure {
		public byte subCode = 0;
		public byte mainCode = 0;
		@Override
		protected List<String> getFieldOrder() {
			return asList("subCode", "mainCode");
		}
	}
	public Struct2 struct2;
	public byte param = 0;
	public int t1 = 0;
	public int t2 = 0;
	public int t3 = 0;
	public int t4 = 0;
	public int t5 = 0;
	public int t6 = 0;
	public int t7 = 0;
	public int t8 = 0;
	public int t9 = 0;
	public int t10 = 0;
	
	@Override
	protected List<String> getFieldOrder() {
//		return asList("id", "dateTime", "deviceID", "userID", "uid", "doorID", "liftID", "zoneID", "ioDeviceID", "port", "value", "reserved", "zoneID_", "ioDeviceID_","port_", "doorID_", "reserved_","zoneID__", "doorID__", "reserved__",
//				"code", "subCode", "mainCode", "param");
		return asList("A", "B", "C", "D", "E", "F", "G", "H", "struct", "alarm", "interlock", "code","struct2", "param", "t1","t2","t3","t4","t5","t6","t7","t8","t9","t10");

	}
}
