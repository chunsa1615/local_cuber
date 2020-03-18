package kr.co.cntt.scc.push;

public class DefaultConfig {
	/** 
	 * Android GCM Setting 
	 */
	public String 	MESSAGE_ID 		= String.valueOf(Math.random() % 100 + 1);
	public boolean SHOW_ON_IDLE 	= false;
	public Integer	LIVE_TIME 		= 1;
	public Integer	RETRY 			= 2;
	public String 	simpleApiKey 	= "AIzaSyD0gGPvhtXgzYX_nQZ2ruHGZ9mZDc6M_x4";
	public String 	gcmURL 			= "https://android.googleapis.com/gcm/send";

	/**
	 * IPhone  Setting 
	 */

	//리얼
	//public String   CERTIFICATE_PATH = "cntstudy_real.p12"; //Real
	public String   CERTIFICATE_PATH = "cntstudy_dev.p12"; //Test                           // 푸시 인증서 비밀번호
	
	public String   CERTIFICATE_PWD  = "qwer1234";                               // Smith: 푸시 인증서 비밀번호
	
	public String   APNS_HOST    	= "gateway.sandbox.push.apple.com";        // 개발용 푸시 전송서버 (TEST)
	//public String   APNS_HOST        = "gateway.push.apple.com";                // 운영 푸시 전송서버
	
	public String   FEEDBACK_HOST  	     = "feedback.sandbox.push.apple.com";   // 개발용 푸시 전송서버(TEST) 
	//public String   FEEDBACK_HOST        = "feedback.push.apple.com";           // 운영 푸시 전송서버
	
	public int      APNS_PORT        = 2195;                                    //
	public int      FEEDBACK_PORT    = 2196;                                    //
	public int      BADGE            = 1;                                       // App 아이콘 우측상단에 표시할 숫자
	public String   SOUND            = "default";                               // 푸시 알림음
	
	/**
	 * Image Setting 
	 */
//	public static String default_img_path = "C:/Web/MobilePush/";/
//	public static String default_img_path = "D:/02. Source/3. MobilePush/";
//	public static String default_img_path = "D:/02. Source/3. MobilePush/";
	public static String save_path = "pushImage/";
//	public static String defalut_url_path = "http://127.0.0.1:8080/MobilePush/view.do?imageFile=";
//	public static String defalut_url_path = "http://211.219.135.109/mstl/view.do?imageFile=";
//	public static String defalut_url_path = "http://211.219.135.21/MobilePush/view.do?imageFile=";
}
