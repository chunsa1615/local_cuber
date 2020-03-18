package kr.co.cntt.scc.security;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.model.User;
import kr.co.cntt.scc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jslivane on 2016. 4. 20..
 *
 * http://www.namooz.com/2015/12/07/spring-boot-thymeleaf-10-spring-boot-security-final/
 * http://zgundam.tistory.com/52
 * https://github.com/mmeany/spring-boot-web-app-base/blob/master/src/main/java/com/mvmlabs/springboot/ConfigurationForSecurity.java
 *
 */
@Service("customUserDetailsService")
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userService.selectUserByName(username);

        if (user == null) throw new UsernameNotFoundException("해당 사용자가 존재하지 않습니다.");

        List<GrantedAuthority> authorities = buildUserAuthority(user.getRole(), user.getBranches());

        return buildUserForAuthentication(user, authorities);
    }

    private List<GrantedAuthority> buildUserAuthority(String role, String branches) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(0);
        authorities.add(new SimpleGrantedAuthority(role));

        if (!StringUtils.isEmpty(branches)) {
            for(String branch: branches.split(Constants.SEPERATOR)) {
                authorities.add(new SimpleGrantedAuthority(branch));

            }

        }

        return authorities;

    }

    private UserDetails buildUserForAuthentication(User user,
                                            List<GrantedAuthority> authorities) {

        boolean enabled = true; //(user.getUseYn() == Constants.USE);

        return new CustomUserDetails(user, true, enabled, true, true, authorities);

    }

}
