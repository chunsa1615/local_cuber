package kr.co.cntt.scc.security;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * AppAuthenticationFilter
 *
 * Created by jslivane on 2016. 10. 3..
 */
public class AppAuthenticationFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {



        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }

}
