package kr.co.cntt.scc.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Created by jslivane on 2016. 6. 5..
 */
public class CustomUserDetails extends User {

    private String userId;

    public CustomUserDetails(kr.co.cntt.scc.model.User user, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(user.getName(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.userId = user.getUserId();

    }

    public String getUserId() {
        return userId;
    }

}
