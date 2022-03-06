package com.kdy.live.sched.conf;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kdy.app.bean.api.ApiUrlFilterBean;

@Configuration
public class FilterConfig {
	
	@Bean
	public FilterRegistrationBean getFilterRegistrationBean(){
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(new ApiUrlFilterBean());
		 registrationBean.addUrlPatterns("/p/*","/v/*");
		return registrationBean;
	}
}
