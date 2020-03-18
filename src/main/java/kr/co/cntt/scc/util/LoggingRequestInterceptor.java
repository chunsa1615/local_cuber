package kr.co.cntt.scc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Debugging/Logging of Requests/Responses for Spring RestTemplate
 * 
 * http://stackoverflow.com/a/33009822
 *
 */
@Slf4j
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

    	traceRequest(request, body);
    	
        ClientHttpResponse response = execution.execute(request, body);
        
        traceResponse(response);

        return response;
        
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        log.debug("REQUEST {} {}", request.getMethod(), request.getURI());
        log.debug("Headers : {}", request.getHeaders() );
        log.debug("Body    : {}", new String(body, "UTF-8"));

//        log.debug("===========================request begin================================================");
//        log.debug("URI         : {}", request.getURI());
//        log.debug("Method      : {}", request.getMethod());
//        log.debug("Headers     : {}", request.getHeaders() );
//        log.debug("Request body: {}", new String(body, "UTF-8"));
//        log.debug("==========================request end================================================");

    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }

        log.debug("RESPONSE {} {}", response.getStatusCode(), response.getStatusText());
        log.debug("Headers : {}", response.getHeaders());
        log.debug("Body    : {}", inputStringBuilder.toString());

//        log.debug("============================response begin==========================================");
//        log.debug("Status code  : {}", response.getStatusCode());
//        log.debug("Status text  : {}", response.getStatusText());
//        log.debug("Headers      : {}", response.getHeaders());
//        log.debug("Response body: {}", inputStringBuilder.toString());
//        log.debug("=======================response end=================================================");

    }

}