package kr.co.cntt.scc.security;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.model.History;
import kr.co.cntt.scc.service.HistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by jslivane on 2016. 5. 3..
 *
 *
 *
 */
@Service("customLogoutSuccessHandler")
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    HistoryService historyService;

    public CustomLogoutSuccessHandler() {
        super();

    }

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {

        /****************************************************************************************************/

        Object principal = authentication.getPrincipal();
        if (principal == null) {
            String targetUrl = "/login";

            redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, targetUrl);

        } else {
            String userId = ((CustomUserDetails) principal).getUserId();

            History history = new History(null, Constants.HistoryType.USER_LOGOUT, "");
            history.setUserId(userId);
            historyService.insertHistory(history);


            String targetUrl = "/login?logout";

            if (httpServletResponse.isCommitted()) {
                log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
                return;
            }

            redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, targetUrl);

        }

        /****************************************************************************************************/

    }
}
