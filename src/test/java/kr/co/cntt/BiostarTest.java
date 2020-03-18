package kr.co.cntt;



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
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

import kr.co.cntt.scc.kakaoPay.KakaoPayResponse;
import kr.co.cntt.scc.util.JSONUtils;
import kr.co.cntt.scc.util.LoggingRequestInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class BiostarTest {

	
	public String login() {
	    
		RestTemplate restTemplate = new RestTemplate();
	    restTemplate.getMessageConverters().add(new FormHttpMessageConverter());	    
	    
	    HttpHeaders headers = new HttpHeaders();
	    //headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.add(HttpHeaders.CONTENT_TYPE,
	            MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
	    
	    MultiValueMap<String, String> paramReq = new LinkedMultiValueMap<>();
	    paramReq.add("name", "studycodi");
	    paramReq.add("password", "Dhehdxo1@");
	    paramReq.add("user_id", "admin");
	    
	    HttpEntity<MultiValueMap<String, String>> httpParam =
	        new HttpEntity<>(paramReq, headers);
	    //log.info("kakaopay ready request : {}", httpParam.toString());

	    // Debugging
	    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
	    interceptors.add(new LoggingRequestInterceptor());
	    restTemplate.setInterceptors(interceptors);
	    restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory())); // Response 로깅을 위해서


	    ResponseEntity<String> response = restTemplate
	        .exchange("https://api.biostar2.com:443/v2/login",
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


	    return response.getHeaders().getFirst("set-cookie");
		
	}
	
	@Test
	  public void getUsers() {
		//login();
		
			    

		    //String cookie = login();
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    headers.add("Cookie", null);
		    
//		    MultiValueMap<String, String> paramReq = new LinkedMultiValueMap<>();
//		    paramReq.add("Content-Type", "application/json");
//		    paramReq.add("Cookie", login());
		    
		    HttpEntity<String> httpParam = new HttpEntity<String>("",headers);

		    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		    interceptors.add(new LoggingRequestInterceptor());
		    
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
		    restTemplate.setInterceptors(interceptors);
		    restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory())); // Response 로깅을 위해서
		    
		    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.biostar2.com/v2/users")		    																 
		    								.queryParam("limit", 10)
		    								.queryParam("offset", 0);
		    
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
