package com.kdy.live.sched.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
    @Bean
    public Docket apiV1(){
        return new Docket(DocumentationType.SWAGGER_2)
        		.apiInfo(getApiInfo()).groupName("v2.0").select()
                .apis(RequestHandlerSelectors.basePackage("com.kdy.app.controller.api"))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false);
    }
    
    /*
    @Bean
    public Docket apiV2(){
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName("groupName2")
                .select()
                .apis(RequestHandlerSelectors.
                        basePackage("javable.controller"))
                .paths(PathSelectors.ant("/posts/**")).build();
    }
    */
    
    private ApiInfo getApiInfo() {
    	
    	return new ApiInfoBuilder()
    			.title("KD-dragon Live Encoder")
    			.description("단방향 라이브 웹 서비스")
    			.version("2.0")
    			.contact(new Contact("Contact Me", "https://kd-dragon.co.kr/", "ddragon@gmail.com"))
    			.build();
    }
}
