package com.pepcus.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.pepcus.api.interceptors.APIProcessingTimeInterceptor;

/**
 * Application configuration class
 * 
 * @author Shiva Jain
 * @since 2017-11-14
 *
 */
@Configuration
@EnableWebMvc
public class AppConfig extends WebMvcConfigurerAdapter {
	
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new APIProcessingTimeInterceptor()).addPathPatterns("/v1/*");
    }
}