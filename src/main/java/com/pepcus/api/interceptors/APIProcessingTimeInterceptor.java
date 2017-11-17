package com.pepcus.api.interceptors;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor to log each API request's start time, end time
 * and total time taken to process request
 * 
 * @author Shiva Jain
 * @since 2017-11-14
 *
 */
public class APIProcessingTimeInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory
			.getLogger(APIProcessingTimeInterceptor.class);
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest,
	 *  javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		long startTime = System.currentTimeMillis();
		
		String jobId = UUID.randomUUID().toString();

		logger.info("###### Request JobID:: ##### " + jobId);
		logger.info(" Request URL::" + request.getRequestURL().toString());
		logger.info("API execution Start Time " + System.currentTimeMillis());
		
		request.setAttribute("startTime", startTime);
		request.setAttribute("jobId", jobId);
		
		//if returned false, we need to make sure 'response' is sent
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#postHandle(javax.servlet.http.HttpServletRequest,
	 *  javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
	 */
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		logger.info("Request URL::" + request.getRequestURL().toString());
		logger.info("Sent to controller to process :: Current Time=" + System.currentTimeMillis());
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#afterCompletion(javax.servlet.http.HttpServletRequest,
	 *  javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
	 */
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		long startTime = (Long) request.getAttribute("startTime");
		String jobId = (String) request.getAttribute("jobId");
		
		logger.info("###### Request JobID:: ##### " + jobId);
		logger.info(" Request URL::" + request.getRequestURL().toString());
		logger.info("API execution End Time " + System.currentTimeMillis());
		logger.info(" Total Time taken to complete request :: Time Taken=" + (System.currentTimeMillis() - startTime));
	}

}