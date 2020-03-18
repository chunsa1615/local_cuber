package kr.co.cntt.scc.security;

import kr.co.cntt.scc.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

/**
 * Created by jslivane on 2016. 4. 20..
 *
 * https://spring.io/guides/gs/securing-web/
 * http://docs.spring.io/spring-security/site/docs/current/reference/html/jc.html
 * http://www.namooz.com/2015/12/07/spring-boot-thymeleaf-10-spring-boot-security-final/
 * https://github.com/spring-projects/spring-boot/blob/master/spring-boot-samples/spring-boot-sample-secure/src/main/java/sample/secure/SampleSecureApplication.java
 * Remember Me : http://kielczewski.eu/2014/12/spring-boot-security-application/
 *              http://www.mkyong.com/spring-security/spring-security-remember-me-example/
 */

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    @Qualifier("customUserDetailsService")
    private UserDetailsService customUserDetailsService;


//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(16);
//
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //        auth.userDetailsService(customUserDetailsService);
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

    }

    // 2016-12-22 입출입시 속도문제로 임시로 허용
    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        @Qualifier("customBasicAuthenticationEntryPoint")
        private CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable();

            http		        
	            //.authorizeRequests()
	            //.antMatchers("/app/entry/api/**") //.hasRole("entry")
	            //.anyRequest().authenticated()
            	.antMatcher("/app/entry/api/**")
            	.authorizeRequests()            	
            	.anyRequest().authenticated()
                .and()
                .httpBasic().realmName(Constants.APP_REALM).authenticationEntryPoint(customBasicAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            			
                    

        }

    }

    static SecurityExpressionHandler<FilterInvocation> expressionHandler() {
    	DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
    	defaultWebSecurityExpressionHandler.setDefaultRolePrefix("");
    	return defaultWebSecurityExpressionHandler;
    }
    @Configuration
    @Order(2)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        @Qualifier("customAuthenticationSuccessHandler")
        private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

        @Autowired
        @Qualifier("customLogoutSuccessHandler")
        private CustomLogoutSuccessHandler customLogoutSuccessHandler;

        @Override
        public void configure(WebSecurity web) throws Exception {
            super.configure(web);

            web.ignoring().antMatchers("/robots.txt", "/*.xml", "/*.json", "/*.png", "/*.ico", "/*.svg", "/common/**", "/lib/**", "/images/**", "/files/**", "/e/**", "/app/images/**", "/app/admin/api/v1/**", "/app/client/api/v1/**");

        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
            .csrf().disable()
            .authorizeRequests()
                    .antMatchers("/", "/error").permitAll()
                    .anyRequest().authenticated()
                    .and()
            .formLogin()
                    .loginPage("/login").permitAll()
                    .loginProcessingUrl("/login/process")
                    .failureUrl("/login?error")
                    .usernameParameter("name")
                    .passwordParameter("password")
                    //                .defaultSuccessUrl("/branch", true)
                    .successHandler(customAuthenticationSuccessHandler)
                    .and()
            .logout()
                    .logoutUrl("/logout").permitAll()
                    .deleteCookies("remember-me")
                    //                .logoutSuccessUrl("/login?logout")
                    .logoutSuccessHandler(customLogoutSuccessHandler)
                    .and()
            .rememberMe();


            http.sessionManagement().invalidSessionUrl("/login");
            //http.sessionManagement().invalidSessionUrl("/asdf");

        }

    }


    
}


