package kr.co.cntt.scc.security;

import kr.co.cntt.scc.Constants;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * CustomBasicAuthenticationEntryPoint
 *
 * Created by jslivane on 2016. 10. 18..
 *
 * http://websystique.com/spring-security/secure-spring-rest-api-using-basic-authentication/
 *
 */

@Service("customBasicAuthenticationEntryPoint")
public class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //super.commence(request, response, authException);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");

        PrintWriter writer = response.getWriter();
        writer.println("HTTP Status 401 : " + authException.getMessage());

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName(Constants.APP_REALM);

        super.afterPropertiesSet();

    }
}
