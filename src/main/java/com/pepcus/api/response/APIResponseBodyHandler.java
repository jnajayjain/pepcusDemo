package com.pepcus.api.response;

import static com.pepcus.api.ApplicationConstants.DEFAULT_LIMIT;
import static com.pepcus.api.ApplicationConstants.DEFAULT_OFFSET;
import static com.pepcus.api.ApplicationConstants.DEFAULT_SORT_BY_COMPANY_NAME;
import static com.pepcus.api.ApplicationConstants.SUCCESS_DELETED;
import static com.pepcus.api.response.APIMessageUtil.getMessageFromResourceBundle;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.pepcus.api.db.entities.Company;
import com.pepcus.api.exception.APIError;
import com.pepcus.api.exception.APIErrorCodes;
import com.pepcus.api.exception.MessageResourceHandler;
import com.pepcus.api.services.utils.EntitySearchUtil;


/**
 * This class is a single global response handler component wrapping for all 
 * the responses from APIs, prepare a wrapper over response object and put additional informations those are 
 * useful for API consumer like status and code etc.
 * 
 * @author Shiva Jain
 * @since 2017-11-13
 *
 */
@ControllerAdvice
public class APIResponseBodyHandler implements ResponseBodyAdvice<Object> {
    
	private static Logger logger = LoggerFactory.getLogger(APIResponseBodyHandler.class);
	
	@Autowired
	MessageResourceHandler resourceHandler;
	
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	/**
	 * This method to identify response body and inject additional information to response. 
	 * 
	 *  @param body
	 *  @param returnType
	 *  @param selectedContenctType
	 *  @param selectedConverterType
	 *  @param request
	 *  @param response
	 */
	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
					ServerHttpResponse response) {
		
		if (body instanceof APIError || body instanceof Exception ) {
			return body;
		}
		ServletServerHttpRequest httpRequest = (ServletServerHttpRequest) request;
		ServletServerHttpResponse httpResponse = (ServletServerHttpResponse) response;
		
		APIResponse apiResponse = new APIResponse();
		int statusCode = httpResponse.getServletResponse().getStatus();
		apiResponse.setCode(String.valueOf(statusCode));
		apiResponse.setStatus(HttpStatus.valueOf(statusCode).name());
		/*
		 * TODO: Currently there are references those are Company specific, we need to make them generic so 
		 * the same code reference will be used by all APIs for different entities as well.
		 */
		if (body instanceof List) {
			String limit = httpRequest.getServletRequest().getParameter("limit");
			limit = limit == null ? String.valueOf(DEFAULT_LIMIT) : limit;
			apiResponse.setLimit(limit);
			String offset = httpRequest.getServletRequest().getParameter("offset");
			offset = offset == null ? String.valueOf(DEFAULT_OFFSET) : offset;
			apiResponse.setOffset(offset);
			String sort = httpRequest.getServletRequest().getParameter("sort");
			sort = sort == null ? String.valueOf(DEFAULT_SORT_BY_COMPANY_NAME) : sort;
			apiResponse.setSort(EntitySearchUtil.getFormattedString(sort));
			/*
			 * TODO: FIXME for generic list
			 */
			apiResponse.setCompanies((List)body);
			if (body == null || ((List)body).isEmpty()) {
				apiResponse.setMessage(getMessageFromResourceBundle(resourceHandler, APIErrorCodes.NO_RECORDS_FOUND, "company"));
			}
		} else {
			/*
			 * TODO: FIXME for generic object
			 */
			if (body != null && body instanceof Company) {
				apiResponse.setCompany((Company)body);
			} 
			
			if (body instanceof Integer && statusCode == HttpStatus.ACCEPTED.value()) {
				apiResponse.setMessage(getMessageFromResourceBundle(resourceHandler, SUCCESS_DELETED, "Company", body.toString()));
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Request processed and response is " + apiResponse);
		}
		return apiResponse;
	}

}
