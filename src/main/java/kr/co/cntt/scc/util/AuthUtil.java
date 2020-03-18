package kr.co.cntt.scc.util;

import kr.co.cntt.scc.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by jslivane on 2016. 7. 8..
 */
public class AuthUtil {

    private static CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return (CustomUserDetails) principal;

            }

        }

        return null;

    }

    public static String getCurrentUserId() {
        CustomUserDetails userDatails = getCurrentUserDetails();

        return (userDatails != null) ? userDatails.getUserId() : null;
    }


}
