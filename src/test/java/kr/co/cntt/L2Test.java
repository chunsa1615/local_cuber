package kr.co.cntt;

import com.sun.jna.Structure;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ByteByReference;

import com.sun.jna.ptr.PointerByReference;

import kr.co.cntt.scc.util.LoggingRequestInterceptor;
import model.Fingerprint;
import model.Userblob;
import suprema.BS2AuthConfig;
import suprema.BS2Fingerprint;
import suprema.BS2SimpleDeviceInfo;
import suprema.BS2User;
import suprema.BS2UserBlob;
import suprema.BS2UserBlobEx;
import suprema.OnReadyToScan;
import suprema.userInfo;



public class L2Test {
	
	//static String device_id = Integer.toUnsignedString(540123948);
	static int device_id = 540123948;
	static int device_port = 51211;
	static String device_address = "169.254.219.145";
	static {
		System.loadLibrary("BS_SDK_V2");	
	}
	
	
	private static final IntByReference ZERO = new IntByReference(0);
	
	private OnIdentifyUser onIdentifyUser;
	private OnVerifyUser onVerifyUser;
	

	public void setOnIdentifyUser(OnIdentifyUser onIdentifyUser) {
		this.onIdentifyUser = onIdentifyUser;
	}

	public void setOnVerifyUser(OnVerifyUser onVerifyUser) {
		this.onVerifyUser = onVerifyUser;
	}


	public interface dlltest extends Library {
			
		
//		dlltest INSTANCE = (dlltest) Native.loadLibrary(
//		            (Platform.isWindows() ? "BS_SDK_V2" : "simpleDLLLinuxPort"), dlltest.class);
//		
		dlltest INSTANCE = (dlltest) Native.loadLibrary( ("BS_SDK_V2"), dlltest.class);
		        // it's possible to check the platform on which program runs, for example purposes we assume that there's a linux port of the library (it's not attached to the downloadable project)
		        
				int BS2_AllocateContext();	//연결성공
				int BS2_Version();
				
		        int BS2_Initialize(int context); 	//연결성공
		        
		        int BS2_ConnectDevice(int context, int device_id);	//연결성공
		        int BS2_DisconnectDevice(int context, int deviceId);
		        
		        int BS2_SearchDevices(int context);	//연결성공
		        
		        int BS2_GetDevices(int context, Pointer deviceListObj, IntByReference numDevice);
		        
		        
		        
		        
		        int BS2_ConnectDeviceViaIP(int context, String deviceAddress, int devicePort, IntByReference deviceId); //연결성공

		        
		        
		        //int BS2_GetDeviceInfo(int context, int deviceId, String id, String type, byte connectionMode, String ipv4Address, String port); //BS2SimpleDeviceInfo deviceInfo
		        //int BS2_GetDeviceInfo(int context, int deviceId, BS2SimpleDeviceInfo deviceInfo);
		        //int BS2_GetDeviceInfo(int context, int deviceId, BS2SimpleDeviceInfo deviceInfo); //BS2SimpleDeviceInfo deviceInfo
		        int BS2_GetDeviceInfo(int context, int deviceId, BS2SimpleDeviceInfo deviceInfo); //BS2SimpleDeviceInfo deviceInfo
		        		        		        
		        void OnDeviceFound(int deviceId);
		        
		        void OnDeviceAccepted(int deviceId);
		        
		        void OnDeviceConnected(int deviceId);
		        
		        void OnDeviceDisconnected(int deviceId);
		        
		        int BS2_SetDeviceEventListener(IntByReference context, String cbOnDeviceFound, String cbOnDeviceAccepted, String cbOnDeviceConnected, String cbOnDeviceDisconnected );
		        
		        int IsAcceptableUserID(String uid);
		        

		        int BS2_GetUserList(int context, int deviceId, PointerByReference outUid, IntByReference outNumUids, Integer cbIsAcceptableUserID); //, IsAcceptableUserID cbIsAcceptableUserID
		        
		        int BS2_GetUserInfos(int context, int deviceId, Pointer p, int uidCount, BS2UserBlob[] userBlob);
		        int BS2_GetUserInfos(int context, int deviceId, String p, int uidCount, BS2UserBlob[] userBlob);
		        int BS2_GetUserInfosEx(int context, int deviceId, Pointer uids, int uidCount, BS2UserBlobEx[] userBlobEx);
		        
//		        int BS2_GetUserDatas(int context, int deviceID, IntByReference uids, int available, BS2UserBlob userBlob, int DATA);
//		        int BS2_GetUserDatas(int context, int deviceID, IntByReference uids, int available, PointerByReference userBlob, int DATA);
		        int BS2_GetUserDatas(int context, int deviceID, Pointer p, int available, BS2UserBlob[] userBlob, int DATA);
		        
		        
		        int BS2_RemoveAllUser(int context, int deviceId);

		        int BS2_EnrolUser(int context, int deviceId, BS2UserBlob[] userBlob, int userCount, int overwrite);
		        int BS2_RemoveUser(int context, int deviceId, Pointer uids, int uidCount);
		        
		        int BS2_SetServerMatchingHandler(int context, OnVerifyUser ptrVerifyUser, OnIdentifyUser ptrIdentifyUser);
//		        int BS2_SetServerMatchingHandler(int context, String ptrVerifyUser, String ptrIdentifyUser);

		        //void OnIdentifyUser(int deviceId, int seq, byte[] format, IntByReference templateData, int templateSize);
		        		        
		        
		        int BS2_ResetConfig(int context, int deviceId, int includingDB);
		        
		        int BS2_GetAuthConfig(int context, int deviceId, BS2AuthConfig config);
		        int BS2_SetAuthConfig(int context, int deviceId, BS2AuthConfig config);
		        
		        
		        
		        int BS2_RebootDevice(int temp, int device_id);
		        
		        
		        int BS2_IdentifyUser(int context, int deviceId, int seq, int handleResult, BS2UserBlob[] userBlob);
		        
		        int BS2_ScanFingerprintEx(int context, int deviceId, BS2Fingerprint finger, int templateIndex, int quality, int templateFormat, PointerByReference outquality, OnReadyToScan cbReadyToScan);
		        
		        int BS2_VerifyFingerprint(int context, int deviceId, BS2Fingerprint fingerT);
	}
	
	  public static long unsigned32(int n) {
		    return n & 0xFFFFFFFFL;
		  }
	
		
	public static void main(String[] args) {
		System.setProperty("jna.encoding", "utf-8");
		//System.out.println("============device_id==========" + device_id);
		//System.out.println("============device_port==========" + device_port);
		//System.out.println("============device_address==========" + device_address);
		
		
		dlltest sdll = dlltest.INSTANCE;	
		//System.out.println("============sdll==========" + sdll);
		
		int temp =  sdll.BS2_AllocateContext();
		

		
		//System.out.println("============BS2_AllocateContext==========" + temp);
		System.out.println("============BS2_Initialize==========" + sdll.BS2_Initialize(temp));
		//System.out.println("============sdll.BS2_Version==========" + sdll.BS2_Version());	
		//System.out.println("============deviceListObj==========" + deviceListObj.getValue());
		//int deviceListObj = 0;
			
		//sdll.OnDeviceFound(device_id);
		//sdll.OnDeviceAccepted(device_id);	       
	    //sdll.OnDeviceConnected(device_id);
	    //sdll.OnDeviceDisconnected(device_id);
		
	    //System.out.println("============BS2_SetDeviceEventListener==========" + sdll.BS2_SetDeviceEventListener(temp, null, null, null, null));
		
	    System.out.println("============BS2_SearchDevices==========" + sdll.BS2_SearchDevices(temp));
			    
		
		IntByReference numDevice = new IntByReference(0);
		IntByReference deviceListObj = new IntByReference(0);
		Pointer pointer = deviceListObj.getPointer();
		
		
		System.out.println("====before========= pointer ============" + pointer.getInt(0));
		
		System.out.println("============BS2_GetDevices==========" + sdll.BS2_GetDevices(temp, pointer , numDevice));
		
		System.out.println("====after========= pointer ============" + pointer.getInt(0));
		System.out.println("====after========= deviceListObj ============" + pointer.getInt(0));
		//System.out.println("============= deviceListObj ============" + deviceListObj[1]);

		//String ipp = "169.254.7.11";
		//byte[] ipp1 = ipp.getBytes();
		//System.out.println("============= ipp1 ============" + ipp1);
		
		//System.out.println("============deviceListObj==========" + deviceListObj);
		//System.out.println("============numDevice==========" + numDevice);
		
		//int outUidObjs = 0;
		
		int IsAcceptableUserID = 0;
		String uid = null;
		//IntByReference outUidObjs = new IntByReference(0);		
		//System.out.println("============BS2_GetUserList==========" + sdll.BS2_GetUserList(temp, device_id, outUidObjs, outNumUids, ""));
		
		//System.out.println("============BS2_ConnectDevice==========" + sdll.BS2_ConnectDevice(temp, device_id));
		
		IntByReference device = new IntByReference(0);
//		String add = "169.254.7.11";
//		int port2 = 51211;
		System.out.println("============BS2_ConnectDeviceViaIP==========" + sdll.BS2_ConnectDeviceViaIP(temp, device_address, device_port, device ));
		
//		String type = null;
		
		String id = "";
		String type = "";
	    byte connectionMode = 0;
	    String ipv4Address = "";
	    String port = "";
	    InetAddress IP = null;
	    
	    //BS2SimpleDeviceInfo deviceInfo = new BS2SimpleDeviceInfo();
	    //deviceInfo.ipv4Address = "asdf";
	    //deviceInfo.id = "0";
	    BS2SimpleDeviceInfo deviceInfo = new BS2SimpleDeviceInfo();
	    //System.out.println("============device.ipv4Address==========" + deviceInfo.ipv4Address);
	    
		System.out.println("============BS2_GetDeviceInfo==========" + sdll.BS2_GetDeviceInfo(temp, device_id, deviceInfo));		
		
		

		//System.out.println("============device==========" + deviceInfo);
		
		//System.out.println("============device.type==========" + deviceInfo.type);
		//System.out.println("============device.port==========" + deviceInfo.port);
		//System.out.println("============device.ipv4Address==========" + deviceInfo.ipv4Address);
		
		//IntByReference outUid = new IntByReference(0);
		//ByteByReference[] outUid = new ByteByReference[1024];
		//Pointer outUidObjs = outUid.getPointer(); 
		
		PointerByReference outUid = new PointerByReference();
		//IntByReference[] outUid = new IntByReference[5];
//		String[] outUid = new String[5];00000000000
		//PointerByReference outUid = new PointerByReference();
		IntByReference outNumUids = new IntByReference(0);
		
		
		
		//System.out.println("====before=====outUid.getValue( : " + outUid[0].getValue() );
		//System.out.println("====bofore=====outUidObjs : " + outUidObjs.getChar(0) );
		Integer integer = null;
		System.out.println("============BS2_GetUserList==========" + sdll.BS2_GetUserList(temp, device_id, outUid, outNumUids, integer));
		Pointer p = outUid.getValue(); 
		byte buffer[] = p.getByteArray(0, 32); //these offsets are indicative, I pass actual value by calculating them in C 
		System.out.println("============id_list[0]==================" + (new String(buffer)));
				
		byte buffer2[] = p.getByteArray(32, 32); //these offsets are indicative, I pass actual value by calculating them in C 
		System.out.println("============id_list[1]==================" + (new String(buffer2))); 
		
		byte buffer3[] = p.getByteArray(64, 32); //these offsets are indicative, I pass actual value by calculating them in C 
		System.out.println("============id_list[2]==================" + (new String(buffer3)));

		byte buffer4[] = p.getByteArray(96, 32); //these offsets are indicative, I pass actual value by calculating them in C 
		System.out.println("============id_list[3]==================" + (new String(buffer4)));		
		
		byte buffer5[] = p.getByteArray(128, 32); //these offsets are indicative, I pass actual value by calculating them in C 
		System.out.println("============id_list[4]==================" + (new String(buffer5)));
		BS2Fingerprint print = new BS2Fingerprint();
		PointerByReference ptr11 = new PointerByReference();
		sdll.BS2_ScanFingerprintEx(temp, device_id, print, 1, 40, 0, ptr11, new OnReadyToScan() {

			@Override
			public void invoke(int deviceId, int seq) {
				System.out.println(deviceId);
				System.out.println(seq);
			}
			
		});

		
		//byte[] bytes = ByteBuffer.allocate(4).putInt(185073321).array();
		//byte[] bytes = ByteBuffer.allocate(4).putInt(Integer.parseInt(deviceInfo.ipv4Address.toString()).array();
//		List<String> result = new ArrayList<String>();
//	     Pointer[] pList = outUid.getPointer().getPointerArray(0, 1);
//	     for( Pointer p : pList) {
//	         String value = p.getString(0);
//	         result.add(value);
//	         System.out.println(value);
//	     }
		
//		byte buffer[] = p.getByteArray(0, outNumUids.getValue());
//		System.out.println("====after=====buffer : " + buffer);
		//System.out.println("====after=====outUidObjs : " + outUid.getValue().getStringArray(0));
//		System.out.println("====after=====outUidObjs : " + outUid[0].getValue().getStringArray(0));
//		System.out.println("====after=====outUidObjs : " + outUid[1].getValue().getStringArray(0));
//		System.out.println("====after=====outUidObjs : " + outUid[2].getValue().getStringArray(0));
		//System.out.println("====after=====new String(outUid[0]) : " + new String(outUid[0]));
		//System.out.println("====after=====outUidObjs : " + outUid[1]);
		
		System.out.println("============outNumUids==========" + outNumUids.getValue());
//		
		
		
		
//		IntByReference uids = outUid;
//		System.out.println("123 : "+outUid.getValue());
//		
//		System.out.println("456 : "+outUid);
		//PointerByReference uids = new PointerByReference(outUid.getPointer());
		
		
		//BS2UserBlob user = new BS2UserBlob(); 
		
		
		
		//IntByReference uids = new IntByReference(0);
		
		PointerByReference uids = new PointerByReference();
		PointerByReference pref = new PointerByReference();
		//uids = outUid;
		//System.out.println("============outUid.getValue()==========" + outUid.getValue() );
		
		//Pointer ptr = outUid.getValue();
		
		
		//BS2UserBlob[] userBlob = new BS2UserBlob[outNumUids.getValue()];
		BS2UserBlob userBlobtt = new BS2UserBlob();
		BS2UserBlob[] userBlobAry = (BS2UserBlob[])userBlobtt.toArray(outNumUids.getValue());
		//Pointer source = outUid.getPointer();
//		System.out.println("===========source========"+source);
		//BS2UserBlob userBlob = new BS2UserBlob();
		//PointerByReference userBlob = new PointerByReference();
		//System.out.println("메모리에 할당됨 : "+userBlob);
		//BS2UserBlob[] array = (BS2UserBlob[])userBlob.toArray(outNumUids.getValue());
		
		//System.out.println("================BS2_GetUserDatas===============" + sdll.BS2_GetUserDatas(temp, device_id, p, outNumUids.getValue(), userBlobAry, 0x0004));
//		System.out.println("====before======userBlob=========="+userBlob[0]);
		
//		BS2UserBlobEx[] userBlobEx = new BS2UserBlobEx[outNumUids.getValue()];
		Pointer pUserBlob = new Memory(outNumUids.getValue());
		//BS2UserBlob[] userBlobttt = new BS2UserBlob[outNumUids.getValue()];
		//BS2UserBlob[] userBlobttt = (BS2UserBlob[])userBlobtt.toArray(outNumUids.getValue());
		BS2UserBlob userBlobttt = new BS2UserBlob();
		
//		userBlobttt[0].user.userId = outUid.getPointer().getByteArray(0, 32);
//		userBlobttt[1].user.userId = outUid.getPointer().getByteArray(1, 32);
//		userBlobttt[2].user.userId = outUid.getPointer().getByteArray(2, 32);
//		userBlobttt[3].user.userId = outUid.getPointer().getByteArray(3, 32);
//		userBlobttt[4].user.userId = outUid.getPointer().getByteArray(4, 32);
		
		
		
		System.out.println("============sdll.BS2_GetUserInfos==========" + sdll.BS2_GetUserInfos(temp, device_id, p, outNumUids.getValue(), userBlobAry) );
		//System.out.println(sdll.BS2_GetUserInfos(temp, device_id, "592", outNumUids.getValue(), userBlobAry));
		
		
		//BS2Fingerprint FingP = new BS2Fingerprint(userBlobAry[0].fingerObjs.getPointer());
		//.getPointer(0)
		//System.out.println("FingP : : : : : " + FingP);
		//System.out.println("asd : : : : : : "+ userBlobAry[0]);
//		System.out.println(new String(userBlobAry[0].user.userId));
		
//		System.out.println("========userBlobAry[2]==========" + userBlobAry[2]);
//		System.out.println("========userBlobAry[2]==========" + new String(userBlobAry[2].fingerObjs.data[0]));
		
		//System.out.println("========userBlobttt==========" + userBlobttt);
		
		
		//System.out.println("============sdll.BS2_GetUserInfosEx==========" + sdll.BS2_GetUserInfosEx(temp, device_id, ptr, outNumUids.getValue(), userBlobEx) );


		
		
		//System.out.println(userBlob[0]);
//		System.out.println(userBlob[0].pin[0]);
//		System.out.println(userBlob[0].pin[1]);
		//System.out.println(userBlob[1]);

		
//		Pointer pp = pRef.getValue();
		
		//BS2UserBlob.ByReference[] userInfo = pPackageInfo.toArray(4); 
		

		
//		System.out.println("============userBlob[0].user.userId[0]==========" + userBlob[0].user.userId[0]);
//		System.out.println("============userBlob[0].user.userId[1]==========" + userBlob[0].user.userId[1]);
//		System.out.println("============userBlob[0].name[0]==========" + userBlob[0].name[0]);
//		System.out.println("============userBlob[0].name[1]==========" + userBlob[0].name[1]);
//		
//		System.out.println("============userBlob[1].user.userId[0]==========" + userBlob[1].user.userId[0]);
//		System.out.println("============userBlob[1].user.userId[1]==========" + userBlob[1].user.userId[1]);
//		System.out.println("============userBlob[1].name[0]==========" + userBlob[1].name[0]);
//		System.out.println("============userBlob[1].name[1]==========" + userBlob[1].name[1]);
		
		

//		System.out.println("================BS2_GetUserDatas===============" + sdll.BS2_GetUserDatas(temp, device_id, uids, 4, userBlob, 1));
//		System.out.println("================BS2_GetUserDatas===============" + sdll.BS2_GetUserDatas(temp, device_id, uids, 3, userBlob, 1));
//		for(int i=1; i<10000; i++) {
//			System.out.println("===userInfo==" + sdll.BS2_GetUserInfos(temp, device_id, uids, i, userBlob));
//			if(userBlob.getValue() != null) {
//				System.out.println("GetUser Data : "+userBlob.getValue());
//				System.out.println(new BS2UserBlob(userBlob.getValue()));
//			}
//		}
		
		BS2UserBlob EnroluserBlob = new BS2UserBlob();
		BS2UserBlob[] EnrolArray = (BS2UserBlob[])EnroluserBlob.toArray(1);
		BS2UserBlob Enroluser = new BS2UserBlob();
		String userId = "1234";
		String name = "1234";
		//Enroluser.user.userId = userId.getBytes();
		
//		EnrolArray[0].user.version = 0;
//		EnrolArray[0].user.formatVersion = 0;
//		EnrolArray[0].user.faceChecksum = 0;
//		EnrolArray[0].user.numCards = 0;
//		EnrolArray[0].user.numFingers = 0;
//		EnrolArray[0].user.numFaces = 0;
//		EnrolArray[0].user.flag = 0;
		
		EnrolArray[0].cardObjs = new IntByReference(0);
		//EnrolArray[0].fingerObjs = new IntByReference(0);
		EnrolArray[0].faceObjs = new IntByReference(0);
		int i = (int) new Date().getTime();
		Timestamp timestamp = new Timestamp(i); 
		
		//EnrolArray[0].user.userId = userId.getBytes();
		//EnrolArray[0].name = name.getBytes();
//		EnrolArray[0].setting.startTime = i;
//		EnrolArray[0].setting.endTime = i;
//		EnrolArray[0].setting.securityLevel = (byte)0;
//		EnrolArray[0].setting.fingerAuthMode = (byte)0;
//		EnrolArray[0].setting.idAuthMode = (byte)255;
		
		//EnroluserBlob[0].user.userId = userId.getBytes();
//		EnroluserBlob[0].name = name.getBytes();
		//System.out.println("================BS2_EnrolUser==============="+sdll.BS2_EnrolUser(temp, device_id, EnrolArray, 1, 1));
		
		
		
//		int seq = 1;
//		int handleResult = 0;
//		byte[] format = new byte[32];
//		IntByReference templateData = new IntByReference();
//		//sdll.OnIdentifyUser(device_id, seq, format, templateData, 32);
//
//		BS2UserBlob[] userBlobary = (BS2UserBlob[])EnroluserBlob.toArray(1);
//		System.out.println("==============BS2_IdentifyUser================="+sdll.BS2_IdentifyUser(temp, device_id, seq, handleResult, userBlobary));
//		
//		System.out.println("==============seq================="+seq);
//		System.out.println("==============handleResult================="+handleResult);
		
		
//		BS2AuthConfig auth = new BS2AuthConfig();
//		auth.useServerMatching = 1;
//		System.out.println("===========BS2_SetAuthConfig==============" + sdll.BS2_SetAuthConfig(temp, device_id, auth));
		
		
//		new OnIdentifyUser() {
//			@Override
//			public void invoke(int deviceId, int seq, byte format, IntByReference templateData, int templateSize, int handleResult) {
//								
//				System.out.println(deviceId);
//				System.out.println(seq);
//				System.out.println(format);
//				System.out.println(templateData.getValue());
//				System.out.println(templateSize);
//
//				
//				System.out.println("============handleResult=========="+handleResult);
//
//				int result = sdll.BS2_IdentifyUser(temp, deviceId, seq, 1, tempFingerUser);
//				//sdll.BS2_IdentifyUser(temp, deviceId, seq, 1, userBlob2);
//				System.out.println(result);
//				
//			}
//		}
		
		//OnVerifyUser onVerifyUser = null;
		//OnIdentifyUser onIdentifyUser = null;
		
		int identiSeq = 0;
		OnVerifyUser onVerifyUser = null; 
//		onVerifyUser = new OnVerifyUser() {
//			@Override
//			public void invoke(int deviceId, int seq, byte isCard, byte cardType, IntByReference data, int dataLen) {
//				System.out.println("true"); }
//		};
			

		
		BS2UserBlob identiUser = new BS2UserBlob(); 
		OnIdentifyUser onIdentifyUser = null;
		int fingerSeq = 0;
		
//		onIdentifyUser = new OnIdentifyUser() {			
//			@Override
//			public void invoke(int deviceId, int seq, byte format, IntByReference templateData, int templateSize, int handleResult, PointerByReference uidd) {
//				//System.out.println(new String(user[0].user.userId));
//				
//				Pointer Puidd = uidd.getValue();
//				byte buffer[] = Puidd.getByteArray(0, 32); //the
//				System.out.println("Puidd : " + new String(buffer));
//				
//				System.out.println("handleResult : " + handleResult);
////				System.out.println(new String(usertem.user.userId));
////				System.out.println(Native.toString(usertem.user.userId));
//				sdll.BS2_IdentifyUserEx(temp, deviceId,  seq, 1, userBlobtt);
//			}
//		};

		//
//		sdll.BS2_SetServerMatchingHandler(temp, onVerifyUser, new OnIdentifyUser() {			
//			@Override
//			public void invoke(int deviceId, int seq, byte format, PointerByReference templateData, int templateSize, int handleResult, PointerByReference ouid) {
//				System.out.println(ouid);
//				System.out.println(ouid.getPointer());
//				System.out.println("templateSize : " + templateSize);
//				//System.out.println(new String(new BS2UserBlob(ouid.getPointer()).user.userId));
//				//System.out.println(Native.toString(new BS2UserBlob(ouid.getPointer()).user.userId));
//				Pointer Pouid = templateData.getValue();
//				BS2UserBlob[] users = new BS2UserBlob[1];
//				//int in = templateData.getValue() & 0xFF;
//				
//				
//				String bob_template1 = "asdfasfwefasdfdsf";
//				
//				String template1 = Base64.getEncoder().encodeToString(templateData.getPointer().getByteArray(0, templateSize));
//				//Pointer p = templateData.getValue(); 
//				//ByteBuffer buffer = templateData.getPointer(0).getByteBuffer(0, templateSize);         //p.getByteBuffer(0, templateSize);
//				System.out.println("templateData : " + template1);
////				System.out.println("templateData : " + new String(buffer));
//				BS2Fingerprint FingerT = new BS2Fingerprint();
////				BS2Fingerprint[] FingerP = (BS2Fingerprint[]) FingerT.toArray(2);
//				FingerT.index = 0;
//				FingerT.flag = 0;
//				//byte[] array = templateData.getPointer().getByteArray(0, templateSize);
//				byte[] array = "RSYPFJkAVUYSQzGvBRsEcVqDCcUhBgYTRYEBAxqJMbMFK4HgUQcewnBTAySDMK6BIwNwUwIdxEAGhjbG4EyFKUcQTIQgh+CxBxkKkAWIB0uwGoMSC/AWjBNNMB6NFs1wIxoFTkAkhxOPoDQNGI/RPZQlS8GpDDvMMUGJNszhOBQiTSGtCiuNQKgQMo1wnKAyTfCjti/OkAGYNI6gh0AdzrBMrC8PAGSLNdAxbhgtkJBUhS6RkFMEM9HQroUpEiBPCTrTYASI//AAERH/////AAEREf///+AAEREf///+AAERES///uAAERESL//e4AAREiIv/d4AARIiIj/N4AERIiIj/N7gARIjMz/M3gARIjMz+83uARIzMz+7zeARIzRE+rvNABI0RE+qq84BIkVl+ama4RIjZ3iIh2QzIQ7LiIdlQyIQDtd3ZUQzIhAO93VUMzIhEA/3VDMzIhEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".getBytes();
//				byte[] array2 = "RRONFI0AVUYLRtFliBMJIAmMIcRgsQgdBbCxAhlIEASHHYlwUwcDjoB4jwNQAH6MC5GQd4ozS5CkhyLMQLKKFo2QEw4ezuBUlhXPURcEKA9wq40hkFEGHxrQ4R0QFNMAIQwl0wE7Dv//////AQP/////////////dwEEBv////////9xdXcCBQcL//////9rcXR3AgUHCv//////am9zdgIEBwkP/////2htcXUBAwQGDQ////9obXJ1AQIEBg0PE/9cZmxxdXcBAwUNDxEXW2RpbXF0dwMGDA8SF1lhZWltcnYEBw0RFBhYX2FlaXB2BQgPEhUY/1xeYmhvdQMIDxIVGFNZXGBlbHMBBg8SFBf/V1tdYGZtdQQPEhQX/1VYW11gZ3IDERUWF/9PU1VWWF1ndhMYGhr/TVFSUlNVXG4WGhwf/01PTk9NS0xHGx0gIv9XUkxLREA9Nh8dIyn///9MSkE6NjMiHSo0AAAAAAAAAAAAAAAAAAAAAAAAAAAA".getBytes();
//				
//				System.out.println("array.length : " + array.length);
//				//System.out.println("array2.length : " + array2.length);
//				
//				FingerT.data[0] = new Memory(array.length * Native.getNativeSize(Byte.TYPE));
//				FingerT.data[1] = new Memory(array.length * Native.getNativeSize(Byte.TYPE));
//												
//				FingerT.data[0].write(0, array ,0, array.length);
//				FingerT.data[1].write(0, array ,0, array.length);
//				
//				System.out.println(new String(FingerT.data[0].getByteArray(0, array.length)));
////				System.out.println("FingerT.data[0] : " + FingerP[0].data[0].getByteArray(0, array.length));
//				//System.out.println("FingerT.data[1] : " + FingerP[0].data[1].getByteArray(0, array2.length));
//				
//				BS2UserBlob serverUser = new BS2UserBlob();
//				BS2UserBlob[] userAry = (BS2UserBlob[])serverUser.toArray(1);
//				System.out.println("============sdll.BS2_GetUserInfos==========" + sdll.BS2_GetUserInfos(temp, device_id, Pouid, 1, userBlobAry) );
//				
//				FingerP[1].index = 0;
//				FingerP[1].flag = 0;
				//byte[] array = templateData.getPointer().getByteArray(0, templateSize);
				
//				System.out.println("array.length : " + array.length);
				//System.out.println("array2.length : " + array2.length);
				
//				FingerP[1].data[0] = new Memory(array.length * Native.getNativeSize(Byte.TYPE));
				//FingerP[1].data[1] = new Memory(array2.length * Native.getNativeSize(Byte.TYPE));
												
//				FingerP[1].data[0].write(0, array ,0, array.length);
				//FingerP[1].data[1].write(0, array2 ,0, array2.length);
				
//				System.out.println("FingerT.data[0] : " + FingerP[1].data[0].getByteArray(0, array.length));
				//System.out.println("FingerT.data[1] : " + FingerP[1].data[1].getByteArray(0, array2.length));
				
//				System.out.println("RDAPFJkAVUYUHQ+JFCGHCCgi+YkbNRMJFDuQBTY9AAscSBMKOQfzB0oW7wY7F/UEQC73Bj40cAltRWQESk9kCyZRFw4hZjYTMWg+WDNpTEYNbi+NE287DAd+vQUuf8oRDoY7iSSGTws4irqeNYvLIyCMWg0njtWGOZKrEjST0BkwlNMVLZZXkhqXWQspmNkPcmDcFFJjU45nZFkGcGXaD210TAlrdM0HWnVGCXJ9TAk+jzIKdo+6hkmPKoRTlyUJRJysDGmcpg7//xESIv////8BEiIj////8BESIi////0AESIiP///0AERIiI//93gARIiM//83uARIiM0/83uARIiMz/M3eABIiM0S8zd4BIjM0W8zN4CMzREWru84SNEREaau7wURVRVaaqallZmVWaJmId3d3Zmd3d2aIiId3f3dlWJmIh3j3dUOamZmIj3ZULamZmZn/ZD//mZmv8".length());
//				System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".length());
				
				
				
				
//				FingerP[0].data[1] = "RDAQFJoAVUY2EPcDJBkGCBEdjBA+JfsEPStzBhwwEIg4NAQLFTWVBx5AFQspSBcORQ7zB01EZwwnXjISBV+uDTdhPkk5YUxAGGY+DRFnLowIbbMGCG9ABAV0uQITfj+HJoRaDRCKuwUVizaLHY5KGReRNgopk80bJpUyoVlYVAxqWVcIcGdeEV9sRAdubk0ReHNeCHJ5UaJ/et4HSoc7B1CJOwZSibMGYIlEB1mKuwVWjz2GT5LFlVyVVpRUl7kbSZjOlliawxj//wESIv/////+AREiL/////4BERIv////3gARESL///zeABERIv//zN3gAREi///M3e4BEiL//8zM7gESI///zMzeABIzX/+7vM3hI0Rf/6u7vOE0RV//qqqr00VVVf+ZmZmGZmZlP5iIh3d3d2U0d4d3d4eHdlT4d1VPmZiPVPiHVP/5iP/0/4hf//iI/0T/uY//9mZVVf+6///1VlRFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
//				FingerP[0].data[0] = templateData.getPointer().getByteArray(0, templateSize);
//				FingerP[0].data[1] = "RDAQFJoAVUY2EPcDJBkGCBEdjBA+JfsEPStzBhwwEIg4NAQLFTWVBx5AFQspSBcORQ7zB01EZwwnXjISBV+uDTdhPkk5YUxAGGY+DRFnLowIbbMGCG9ABAV0uQITfj+HJoRaDRCKuwUVizaLHY5KGReRNgopk80bJpUyoVlYVAxqWVcIcGdeEV9sRAdubk0ReHNeCHJ5UaJ/et4HSoc7B1CJOwZSibMGYIlEB1mKuwVWjz2GT5LFlVyVVpRUl7kbSZjOlliawxj//wESIv/////+AREiL/////4BERIv////3gARESL///zeABERIv//zN3gAREi///M3e4BEiL//8zM7gESI///zMzeABIzX/+7vM3hI0Rf/6u7vOE0RV//qqqr00VVVf+ZmZmGZmZlP5iIh3d3d2U0d4d3d4eHdlT4d1VPmZiPVPiHVP/5iP/0/4hf//iI/0T/uY//9mZVVf+6///1VlRFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".getBytes();
//				System.out.println(FingerP[0].data[0].length);
//				System.out.println(FingerP[0].data[0][0]);
//				System.out.println(FingerP[0].data[0][1]);
//				System.out.println("BS2_VerifyFingerprint : " + sdll.BS2_VerifyFingerprint(temp, device_id, -++-------------------+));
				
				
				//592 지문
				//RC4QFJsAVUYvHgYIJTQVCB47lAMoRRYJM00XDkIG9wZTFPMGQhb1A0sr+QZIM3WLfzfYl0M5Agp9RtaWV0tlCg1cJggvYjYTBGSpDz9lPlcYbC+MIG05Cw55MAg8e8YQGoQ3ii6ESo0jkVQRLZFrkhWSNpAykuacH5hdFSSZ0gYtml2OYWBRjnRiUwh7Y9cYQmZMQX1t0gpndUIHennHhHuGRgpTjiiGSpIylkOTCDdNlaQSQpcKRFyXIQdVnKUO//8BESIv/////wERIiL/////ABESIv////4AEREiL////eABESIj///d7gARIjM1/N3u4BEiM0X8zd7gESIzRbzM3eABIjNFvMzN3hEjRFW7zMzeEjREVbu7u84TRVVVqqqqq0VWZWaZqZmYdmdmZ5mZiHd3d3dniJh3Z5mYh3f4iHZYupmId/iIZU6qmZh2/4dUPqqqmYX/91T/+Zqv/wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
				//"template0": "RScNFJoAVUYTBBAQiBDEwGiFCIkQHYcWwVAHByWCwLKFJQNwUYciRKABihVGABCKLcawRosbBvARjjKJQTiOGglwJ5QjifA2yBKKsCuNDorhIQ4JjIAkig+NsSgJAs4QewsHjmEfCw3PwSwmCBDQeJILEPFsLRNRMKuSAtHwdYoG0nFmFwBS0G0ZIkoALsAgjJCLoCcNgBMYGM3wPJAZj1FLkxwPYaKbKA9gEJAVD3A4ES5PwBkFMVAgGQgWkHGYEyjSkGmRJ5LgBxL//uAAES///e7gARIv/N3uAAEiP83e7gASI/zN3uABIj/MzN3gEjO8zMzdAjM7u7vM3iNEq7u7vONFWqqqqqpVZqqpmYiGd2qpmId3d3eZmId3eIiJmYd2Z5iImYh2VAqZmql1QyC6qquzMyELq6vAESEQy7vOAREQDLu9/xEQAMu/",
			    // "template1": "RSsOFJwAVUYVAZAHBxDEUBCIDoUgagQSxkAQiRhHIBGNBYmgHIUWybAmEx+AcK+CI8LwsQQjw7BRhyAEwAGKK0bQR4swCWE5Dg9LECiLCwsxIY0GDPAkiRaN0TKQC84hKAkEDtEfhxgPQJgZEQ+wORIVj8BKEwoQMTMwEtCxmBMNkMGZogfRUW0xD9GQrpAFkgFoHARSwGGhH0oAK88giiA2wDNMAC6IHUzAi7cejNCBxxzNcVxfI82QFBUkz4AQjCsP8BgELhBAGAk1EgFzgTJSYBQKJVLAaYwkkxAHDf/+4AERL///3uABESL//97uABEiP//d7gARIjT83d7gESI0/Mzd4AEjNPvMzd4BMzS7zMzeEkREu7u7zRNFVKu6qrs0VVWqqZmYdnZmmpiId3d3dpmId3eIiHepiHZoqYiHmYdlTsqZiPl1QxDbqpn6QzIQ27qq+xIhANy7uv0REQ7sy7r/ERAA7cv/"

				
				
				
				
//				String result = verify_fingerprint(device_id, template1, template1);
//				if (result.equals("200")) {
					
//				}
				
				//System.out.println("============sdll.BS2_GetUserInfos==========" + sdll.BS2_GetUserInfos(temp, device_id, Pouid, new IntByReference(1).getValue(), users));
				
				//System.out.println("========userBlobAry==========" + users[0]);
				
				// templateData -> Base64
				// /devices/{id}/verify_fingerprint          body -> template{ templateData -> Base64 }
//				
//				sdll.BS2_IdentifyUser(temp, deviceId,  seq, 1, userBlobAry);
//			}
//		});
		
		
		
		//sdll.BS2_IdentifyUser(temp, deviceId, seq, handleResult, onIdentifyUser);
		
		//System.out.println("onIdentifyUser : " + onIdentifyUser);
		//sdll.BS2_SetServerMatchingHandler(temp, onVerifyUser, onIdentifyUser);
		
		
		//System.out.println(sdll.BS2_ResetConfig(temp, device_id, 0));

		try {
			Thread.sleep(1000 * 60);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		
		
//		for(BS2UserBlob u : array) {
//			
//			try {
//				
//				System.out.println("==========u.name=====" + u.name);
//				System.out.println("==========u.name[1]=====" + u.name[0]);
//				System.out.println("==========u.name[0]=====" + u.name[1]);
//				
//				System.out.println("==========u.user.userId=====" + u.user.userId);
//				System.out.println("==========u.user.userId[0]=====" + u.user.userId[0]);
//				System.out.println("==========u.user.userId[1]=====" + u.user.userId[1]);
//				
//				idid = new String(u.user.userId, "UTF-8");
//				name = new String(u.name, "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			name = name.substring(0,  name.indexOf('\0'));
//			System.out.println("==========name=====" + name);
//			System.out.println("==========idid=====" + idid);
//			
//		}
		
		
		//System.out.println("==============after=====result====="+result);
				
//			sdll.BS2_GetUserDatas(temp, device_id, uids, 3, userBlobs, (UInt32)BS2UserMaskEnum.DATA);
		//StatByValue statByValue = new StatByValue(userBlob);
		//System.out.println("=======user======" + userBlob[0].user);
		//System.out.println("=======userId======" + userBlob[0].user.userId);
		//System.out.println("=======userId[0]======" + userBlob[0].user.userId[0]);
		//System.out.println("=======userId[1]======" + userBlob[0].user.userId[1]);
		
		//assertEquals()
			

		//statByValue
//		System.out.println("============BS2_GetUserDatas==========" + sdll.BS2_GetUserDatas(temp, device_id, uids, 2, userBlob, 0x0001));
//		System.out.println("=====after=======userBlob==========" + userBlob[0].user);
//		String str = new String(userBlob[0].name);
//		System.out.println("============str==========" + str);
//		

		//System.out.println("====before========userId==========" + userBlob.user.userId);
		//System.out.println("====before========name==========" + userBlob);
		
//		System.out.println("============sdll.BS2_GetUserInfos==========" + sdll.BS2_GetUserInfos(temp, device_id, uids, 3, userBlob) );
//		
//		
		//System.out.println("=============userBlob[0]==========" + userBlob[0]);
//		
		//System.out.println("::::::::::name : " + userBlob[0].name);
		//System.out.println("::::::::::name[0] : " + userBlob[0].name[0]);
//		System.out.println("::::::::::name : " + userBlob[1].name);
//		System.out.println("::::::::::name[0] : " + userBlob[1].name[0]);
		
//		for(int i=0; i<userBlob.length; i++) {
//			for(int e=0; e<userBlob[i].name.length; e++) {
//				System.out.println("::::::::::name : "+userBlob[i].name[e]);
//			}			
//		}
		
	
		
//		byte[] buff = new byte[4];
//		buff[0] = 0x02;
//        buff[1] = (byte) 0x02;
//        buff[2] = 0x36;
//        buff[3] = 0x03;
//        System.out.println("=================buff=================" + buff);
//        System.out.println("=================buff[0]=================" + buff[0]);
//        System.out.println("=================buff[1]=================" + buff[1]);
//        System.out.println("=================buff[0]=================" + buff[2]);
//        System.out.println("=================buff[1]=================" + buff[3]);
//        System.out.println("=================new String(buff)=================" + new String(buff));
        
        
//		for(int e=0; e<userBlob[0].name.length; e++) {
//			System.out.println("::::::::::name : "+userBlob[0].name[e]);
//		}
		
//		System.out.println("::::::::::name[0] : "+userBlob[0].name[0]);
//		System.out.println("::::::::::name[0] : "+userBlob[0].name[1]);
//		System.out.println("::::::::::name[0] : "+userBlob[0].name[2]);
//		
//		
//		System.out.println("::::::::::name[1] : "+userBlob[1].name[0]);
//		System.out.println("::::::::::name[1] : "+userBlob[1].name[1]);
//		System.out.println("::::::::::name[1] : "+userBlob[1].name[2]);

		
//		for(int i=0; i<userBlob.length; i++) {
//			for(int e=0; e<userBlob[i].name.length; e++) {
//				System.out.println("::::::::::name : "+userBlob[i].name[e]);
//			}
//			for(int e=0; e<userBlob[i].user.userId.length; e++) {
//				System.out.println("::::::::::::userId : "+userBlob[i].user.userId[e]);
//			}
//		}
		
//		System.out.println("============userBlob==========" + userBlob[3].name);
//		System.out.println("============userBlob==========" + userBlob[4].name);
//		
//		System.out.println("====after========userId[0]==========" + userBlob[0].user.userId);
//		System.out.println("====after========userId[1]==========" + userBlob[1].user.userId);
//		System.out.println("====after========userId[2]==========" + userBlob[2].user.userId);
//		System.out.println("====after========name[0]==========" + userBlob[0].name);
//		System.out.println("====after========name[1]==========" + userBlob[1].name);
//		System.out.println("====after========name[2]==========" + userBlob[2].name);
		
		
//		userBlob[2].name = "[B@dcf3e99".getBytes();
//		System.out.println("====after========name[2]==========" + userBlob[2].name);
//		
////		Pointer p0 = userBlob[0].user.userId;
//		byte[] bytearr0 = null;
//		//bytearr0 = 
//		//System.out.println("============bytearr0==========" + bytearr0);
//		
//		String str0 = null;
//		try {
//			System.out.println("=============================================================");
//			str0 = new String(userBlob[0].user.userId, "ISO-8859-1");
//				
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		 
//		System.out.println("====after========str0==========" + str0);
//
//		
//		String test = "Bobtest";
//		byte[] byteT = test.getBytes();
//		//userBlob[0].user.userId
//		System.out.println("============userBlob[0].user.userId==========" + userBlob[0].user.userId);
//		System.out.println("============byteT==========" + byteT);
//		
//		
//		String testT = null;
//		String testT2 = null;
//		try {
//			testT = new String(byteT, "UTF-8");
//			testT2 = new String(userBlob[2].name, "UTF-8");
//			
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("============testT==========" + testT);		
//		System.out.println("============testT2==========" + testT2);
		
		
//		BS2User.ByReference ref1 = new BS2User.ByReference(); 
//		BS2User.ByReference[] ref1arr = (BS2User.ByReference[])ref1.toArray(32);
//		ref1arr[0].userId = new Memory(100);
//		
//		
//		System.out.println("============ref1arr[0].userId==========" + ref1arr[0].userId);
		
		
		//System.out.println("============userBlob==========" + userBlob);

		
		//try {
			//System.out.println("============InetAddress.getByName==========" + InetAddress.getByAddress(bytes));
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		
//		System.out.println("============type==========" + type + " / "+ connectionMode + " / "+ ipv4Address + " / "+ port); 
		
		
		//System.out.println("============deviceInfo2==========" + deviceInfo2);
		
		//		System.out.println("============BS2_GetDeviceInfo==========" + type);
	    
		
		//System.out.println("============sdll==========" + device.getValue());
		
		
		
		//System.out.println("============BS2_DisconnectDeviceSystem==========" + sdll.BS2_DisconnectDevice(temp, device_id));
	}


public static String verify_fingerprint(int device_id, String template0, String template1) {
	    
		RestTemplate restTemplate = new RestTemplate();
	    restTemplate.getMessageConverters().add(new FormHttpMessageConverter());	    
	    
	    HttpHeaders headers = new HttpHeaders();
	    //headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.add(HttpHeaders.CONTENT_TYPE,
	            MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
	    
	    MultiValueMap<String, String> paramReq = new LinkedMultiValueMap<>();
	    paramReq.add("security_level", "DEFAULT");
	    paramReq.add("template0", template0 );
	    paramReq.add("template1", template1);
	    
	    HttpEntity<MultiValueMap<String, String>> httpParam =
	        new HttpEntity<>(paramReq, headers);
	    //log.info("kakaopay ready request : {}", httpParam.toString());

	    // Debugging
	    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
	    interceptors.add(new LoggingRequestInterceptor());
	    restTemplate.setInterceptors(interceptors);
	    restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory())); // Response 로깅을 위해서


	    ResponseEntity<String> response = restTemplate
	        .exchange("https://api.biostar2.com:443/v2/devices/" + device_id + "/verify_fingerprint",
	                HttpMethod.POST,
	                httpParam,
	                String.class);
	    
	    
	    if (response.getStatusCode() == HttpStatus.OK) {
	      //log.info("kakaopay ready response : {}", response);
	    	System.out.println("============================="+response.toString());
	    	HttpHeaders httpHeaders = response.getHeaders();
	    	String set_cookie = headers.getFirst(HttpHeaders.SET_COOKIE);
	    	System.out.println("============header=================" + response.getHeaders().getFirst("set-cookie"));
	    	
	      //response.getBody().setCode("200");
	    } else {
	    	System.out.println("============================="+response.toString());
	      //log.error("kakaopay ready response : {}", response);
	    }


	    return response.getStatusCode().toString();
		
	}
	
public void getUsersFingerList(String user_id) {
	//login();
	
		    

	    //String cookie = login();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.add("Cookie", null);
	    
//	    MultiValueMap<String, String> paramReq = new LinkedMultiValueMap<>();
//	    paramReq.add("Content-Type", "application/json");
//	    paramReq.add("Cookie", login());
	    
	    HttpEntity<String> httpParam = new HttpEntity<String>("",headers);

	    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
	    interceptors.add(new LoggingRequestInterceptor());
	    
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
	    restTemplate.setInterceptors(interceptors);
	    restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory())); // Response 로깅을 위해서
	    
	    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.biostar2.com/v2/users/" + user_id + "/fingerprint_templates")		    																 
	    								.queryParam("user_id", user_id);
	    
	    ResponseEntity<String> response = null;
	    try {
		    response = restTemplate.exchange(
		    		builder.toUriString(),
		    		//"https://api.biostar2.com/v2/users?limit=10&offset=0",
		    				HttpMethod.GET,
		    				httpParam,
			                String.class);

	    } catch (HttpStatusCodeException exception) {
	        int statusCode = exception.getStatusCode().value();
	        
	        System.out.println("============statusCode================="+statusCode);
	    }
	    

	    
	    if (response.getStatusCode() == HttpStatus.OK) {
	      
	    	System.out.println("============================="+response.toString());
	      //response.getBody().setCode("200");
	    } else {
	    	System.out.println("============================="+response.toString());
	      
	    }


	    return ;
  }
	
}

