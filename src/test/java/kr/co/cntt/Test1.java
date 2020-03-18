package kr.co.cntt;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static java.util.Arrays.asList;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.service.SmsService.SmsResultSaveSendNumber;
import okhttp3.Credentials;

public class Test1 extends Structure {
	public String[] test = new String[2];
	public static void main(String[] args) {
		System.out.println(new Test1());
		
		 RestTemplate restTemplate = new RestTemplate();

	        restTemplate.getMessageConverters().add(new FormHttpMessageConverter()); // FORM_URLENCODED

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        //headers.add("x-waple-authorization", Constants.SMS_SERVER_KEY);
	        //String credential = Credentials.basic("cntstudy092", "cn935t");
	        String credential = Credentials.basic("entry", "entry!@");
	        headers.add("Authorization", credential);
	         
	        try {
	            //String url = "https://studycodi.com/app/entry/api/v1/b/ef83445e-e6c9-4901-954b-35d145fcf73b/e";
	        	String url = "http://localhost:8888/app/entry/api/v1/b/7ebf50c6-3b2e-41b0-9272-55c5bb80a839/e";
	            // FORM_URLENCODED
	            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
	            param.add("entryType", "1");
	            param.add("no", "0592");

	            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(param, headers);

	            ResponseEntity<SmsResultSaveSendNumber> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, SmsResultSaveSendNumber.class);
	            
	            
	            if (responseEntity.getStatusCode() == HttpStatus.OK) {
	                SmsResultSaveSendNumber result = responseEntity.getBody();
	                System.out.println("=======result : " + result);
	                //return new AsyncResult<>(result);

	            } else {
	            	System.out.println("=======result : " + responseEntity.getBody().toString());
	                //log.error("error in saveSmsSendNumber", responseEntity.getBody().toString());

	            }

	        } catch (RestClientException e) {
	            //log.error("error in saveSmsSendNumber", e);

	        }
	}
	@Override
	protected List<String> getFieldOrder() {
		return asList("test");
	}
}
