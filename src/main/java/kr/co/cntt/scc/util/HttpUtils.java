package kr.co.cntt.scc.util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import eu.bitwalker.useragentutils.Browser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtils {

	public static HttpServletRequest getCurrentRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}
	
	public static String getRootURL(HttpServletRequest request) {
	  String scheme = request.getScheme();
	  String serverName = request.getServerName();
	  int serverPort = request.getServerPort();
	  String contextPath = request.getContextPath();  
	  
	  StringBuilder strBuilder = new StringBuilder();
	  strBuilder.append(scheme + "://");
	  strBuilder.append(serverName);
	  if ( serverPort != 80 && serverPort != 443 )
	      strBuilder.append(":" + serverPort);
	  strBuilder.append(contextPath);
	  
	  log.debug("Result path: " + strBuilder.toString());
	  return strBuilder.toString();
	}
	/*
	public static HttpServletResponse getCurrentResponse() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
	}
	*/
	public static String getJsonContentType(HttpServletRequest request) {
		Browser browser = AgentUtils.getBrowser(request);

		if (browser != null && browser == Browser.IE) {
			return "text/plain; charset=UTF-8";
		}

		return "application/json; charset=UTF-8";
	}

	/*
	 * URL 호출하는 함수
	 */
	public static String send_URL(String u, String param, String method, String encoding) {
		String result = "";
		
		try {
			
			URL url = new URL(u);
			URLConnection connection = url.openConnection();
			HttpURLConnection hurlc = (HttpURLConnection) connection;
			hurlc.setRequestMethod(method);
			hurlc.setDoOutput(true);
			hurlc.setDoInput(true);
			hurlc.setUseCaches(false);
			hurlc.setDefaultUseCaches(false);

			PrintWriter out = new PrintWriter(new OutputStreamWriter(hurlc.getOutputStream(), encoding));
			
			out.println(param);
			out.flush();
			out.close();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(hurlc.getInputStream(), "utf-8"));
			result = in.readLine(); 
			in.close();
			
		} catch (Exception e) {
			log.error("send_URL", e);

			e.printStackTrace();

		}
		
		return result;
	}
	
	/**
	 * convert url Query ( Key=Value&...) to Map (Key, value)  
	 * @param query_param
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, String> parseUrlQuery(String query_param) {
		log.debug("[PARAM] : " + query_param);

		Map<String, String> params = new HashMap<>();

		try {
			for (String param : query_param.split("&")) {
				String[] pair = param.split("=");
				String key = URLDecoder.decode(pair[0], "UTF-8");
				String value = "";

				if (pair.length > 1) {
					value = URLDecoder.decode(pair[1], "UTF-8");
				}

				params.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return params;
	}
	
	/**
   * get ip adress
   * @return
   */
  public static String getHostAddress() {
    
    InetAddress ip = null;
    
    try {
      ip = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    
    return ip.getHostAddress();
  }
  
}