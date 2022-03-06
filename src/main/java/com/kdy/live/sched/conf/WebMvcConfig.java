package com.kdy.live.sched.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Value("${server.resources.location}")
	private String resourcesLocation; //upload path
	
	@Value("${server.resources.uri-path}")
	private String resourcesUriPath; //upload uri
	
	@Value("${live.osType}")
	private String osType;
	
	/**
	 * File 경로
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		if(osType.equalsIgnoreCase("window")) {
			registry.addResourceHandler(resourcesUriPath + "/**")
			.addResourceLocations("file:///" + resourcesLocation);
			
		} else {
			registry.addResourceHandler(resourcesUriPath + "/**")
			.addResourceLocations("file:" + resourcesLocation);
		}
		
	}
	
}
