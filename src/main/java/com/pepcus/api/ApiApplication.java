package com.pepcus.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Main class for Spring Boot based API application.
 * 
 * @author Shiva Jain
 * @since 2017-11-01
 *
 */

@SpringBootApplication
public class ApiApplication {
	
	/**
	 * Main method for spring application
	 * 
	 * @param args command line arguments passed to app
	 * 
	 */
	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}
	
	/**
	 * Facilitates messageSoruce
	 * 
	 * @return MessageSource
	 */
	@Bean
	public MessageSource messageSource () {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		return messageSource;
	}
	
}
