package kr.co.cntt.scc.security;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.service.HistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by jslivane on 2016. 5. 3..
 *
 * http://www.baeldung.com/spring_redirect_after_login
 *
 */
@Service("customAuthenticationSuccessHandler")
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    HistoryService historyService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {


        /****************************************************************************************************/

        String userId = ((CustomUserDetails)authentication.getPrincipal()).getUserId();

        // 로그인 IP 수집 (2017-01-11)
        // TODO : 계정별 로그인 IP 제한
        String ip = httpServletRequest.getRemoteAddr();

        History history = new History(null, Constants.HistoryType.USER_LOGIN, ip);
        history.setUserId(userId);
        historyService.insertHistory(history);


        /****************************************************************************************************/


        handle(httpServletRequest, httpServletResponse, authentication);

        clearAuthenticationAttributes(httpServletRequest);

    }

    private void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(authentication);

        if (httpServletResponse.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, targetUrl);

    }

    private String determineTargetUrl(Authentication authentication) {
        boolean isUser = false;
        boolean isManager = false;
        boolean isAdmin = false;
        boolean isEntry = false;

        String firstBranchId = "";

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            String authority = grantedAuthority.getAuthority();
            if (authority.equals(Constants.UserRoleType.USER.getValue())) {
                isUser = true;

            } else if (authority.equals(Constants.UserRoleType.MANAGER.getValue())) {
                isManager = true;

            } else if (authority.equals(Constants.UserRoleType.ADMIN.getValue())) {
                isAdmin = true;

            } else if (authority.equals(Constants.UserRoleType.ENTRY.getValue())) {
                isEntry = true;

            } else {
                firstBranchId = authority;

            }

        }

        // 관리센터가 없는경우
        if(firstBranchId == ""){
        	return "/home";
        }
        else{
        	// 권한에 따라 다른 링크 
        	 if (isUser) {
                 return "/branch/" + firstBranchId;

             } else if (isManager) {
                 return "/branch/" + firstBranchId;

             } else if (isAdmin) {
                 return "/admin";
                 //return "/home";

             } else if (isEntry) {
                 return "/entry/" + firstBranchId;

             } else {
                 throw new IllegalStateException();

             }
        	
        }

    }

    private void clearAuthenticationAttributes(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;

    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;

    }
}
