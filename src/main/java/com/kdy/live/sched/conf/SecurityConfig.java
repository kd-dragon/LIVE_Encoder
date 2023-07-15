package com.kdy.live.sched.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.kdy.app.service.LoginAuthenticationEntryPoint;
import com.kdy.app.service.LoginFailureService;
import com.kdy.app.service.LoginSuccessService;
import com.kdy.app.service.PasswordEncoderService;
import com.kdy.app.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoderService passwordEncoderService;
	
	@Autowired
	private LoginSuccessService loginSuccessService;
	
	@Autowired
	private LoginFailureService loginFailService;
	
	@Autowired
	private LoginAuthenticationEntryPoint authenticationEntryPoint;
	
	@Bean
	public BCryptPasswordEncoder bcryptEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		// static 디렉터리의 하위 파일 목록은 인증 무시 ( = 항상통과 ) + API security 적용X
        web.ignoring().antMatchers("/app-assets/**", "/assets/**", "/player/**", "/api/**", "/h2-console/**", "/liveNoChat/**", "/p/*", "/v/*");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		//페이지 권한 설정
		http.authorizeRequests() 
			.antMatchers("/").permitAll()
			.antMatchers("/h2-console/**").permitAll()
			.antMatchers("/login*.do").permitAll()
			.antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
			.antMatchers("/**").hasRole("ADMIN");

		
		//로그인설정
		http.formLogin() 
			.loginPage("/loginForm.do")
			.loginProcessingUrl("/loginChk.do")
			.usernameParameter("encId")
			.passwordParameter("encPw")
			.successHandler(loginSuccessService)
			.failureHandler(loginFailService);

		
		//로그아웃
		http.logout() 
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout.do"))
			.invalidateHttpSession(true)
			.deleteCookies("JSESSIONID")
			.logoutSuccessUrl("/loginForm.do");
		
		http.sessionManagement()
			.maximumSessions(1)
			.expiredUrl("/loginForm.do")
			.maxSessionsPreventsLogin(false)
			.sessionRegistry(sessionRegistry());
		
		http.exceptionHandling()
			.accessDeniedPage("/loginAccessDenied.do") //권한인증 X
			.authenticationEntryPoint(authenticationEntryPoint); //인증 전 요청 처리

		http.headers().frameOptions().sameOrigin();
		
		http.headers(headers -> headers .cacheControl(cache -> cache.disable()));
	}

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoderService);
	}
}