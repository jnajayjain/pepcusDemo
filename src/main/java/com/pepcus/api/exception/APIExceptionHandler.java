package com.pepcus.api.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static com.pepcus.api.response.APIMessageUtil.getMessageFromResourceBundle;

import org.hibernate.JDBCException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * This class is a single global exception handler component wrapping for all 
 * the exceptions from APIs, prepare a Response object with required exception 
 * details sending back to users.
 * 
 * @ControllerAdvice 
 * @RestController
 * @author Shiva Jain
 * @since   2017-11-02
 * 
 *
 */
@ControllerAdvice
@RestController
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	MessageResourceHandler resourceHandler;
	
    /**
     * Handle MissingServletRequestParameterException when a 'required' request parameter is missing.
     *
     * @param ex      MissingServletRequestParameterException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the APIError object
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, 
            HttpHeaders headers,
            HttpStatus status, 
            WebRequest request) {
    	APIError apiError = new APIError(BAD_REQUEST, ex);
    	apiError.setMessage(getMessageFromResourceBundle(resourceHandler, APIErrorCodes.REQUIRED_PARAMETER, ex.getParameterName()));
        return buildResponseEntity(apiError);
    }


    /**
     * Handle HttpMediaTypeNotSupportedException when request JSON is invalid.
     *
     * @param ex      HttpMediaTypeNotSupportedException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the APIError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
    	APIError apiError = new APIError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex);
    	StringBuilder builder = new StringBuilder();
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
    	apiError.setMessage(getMessageFromResourceBundle(resourceHandler, APIErrorCodes.REQUIRED_PARAMETER, 
    			ex.getContentType().toString(), builder.toString()));
        return buildResponseEntity(apiError);
    }

    /**
     * Handle MethodArgumentNotValidException an object fails @Valid validation.
     *
     * @param ex      the MethodArgumentNotValidException that is thrown when @Valid validation fails
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the APIError object
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
	    APIError apiError = new APIError(BAD_REQUEST, APIErrorCodes.VALIDATION_FAILED);
	    apiError.setMessage(resourceHandler.get(APIErrorCodes.VALIDATION_FAILED.name()));
        apiError.addErrorDetail(ex.getBindingResult().getFieldErrors());
        return buildResponseEntity(apiError);
    }
            
    /**
     * Handles javax.validation.ConstraintViolationException. Thrown when @Validated fails.
     *
     * @param ex the ConstraintViolationException
     * @return the APIError object
     */
    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(
            javax.validation.ConstraintViolationException ex) {
	    APIError apiError = new APIError(BAD_REQUEST, APIErrorCodes.VALIDATION_FAILED, ex);
	    apiError.setMessage(resourceHandler.get(APIErrorCodes.VALIDATION_FAILED.name()));
        return buildResponseEntity(apiError);
    }

    /**
     * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
     *
     * @param ex      HttpMessageNotReadableException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the APIError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, 
    		HttpHeaders headers, HttpStatus status, WebRequest request) {
	    APIError apiError = new APIError(BAD_REQUEST, APIErrorCodes.MALFORMED_JSON_REQUEST, ex);
	    apiError.setMessage(resourceHandler.get(APIErrorCodes.MALFORMED_JSON_REQUEST.name()));
        return buildResponseEntity(apiError);
    }

    /**
     * Handle HttpMessageNotWritableException.
     *
     * @param ex      HttpMessageNotWritableException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the APIError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
    		HttpHeaders headers, HttpStatus status, WebRequest request) {
	    APIError apiError = new APIError(HttpStatus.INTERNAL_SERVER_ERROR, APIErrorCodes.ERROR_WRITING_JSON_OUTPUT, ex);
	    apiError.setMessage(resourceHandler.get(APIErrorCodes.ERROR_WRITING_JSON_OUTPUT.name()));
        return buildResponseEntity(apiError);
    }
    
    /**
     * Handle Exception, handle generic ApplicationException.class
     *
     * @param ex the Exception
     * @return the APIError object
     */
    @ExceptionHandler(ApplicationException.class)
    protected ResponseEntity<Object> handleAPIBadRequest(ApplicationException ex) {
    	APIError apiError = new APIError(ex.getHttpStatus(), ex.getApiErrorCode());
    	apiError.setMessage(getMessageFromResourceBundle(resourceHandler, ex.getApiErrorCode(), ex.getErrorMessageParameters()));
        return buildResponseEntity(apiError);
    }

    /**
     * Handle DataIntegrityViolationException, inspects the cause for different DB causes.
     *
     * @param ex the DataIntegrityViolationException
     * @return the ApiError object
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                  WebRequest request) {
    	APIError apiError = null;
        if (ex.getCause() instanceof ConstraintViolationException) {
        	apiError = new APIError(HttpStatus.CONFLICT, APIErrorCodes.DATABASE_ERROR, ex.getCause());
    	    apiError.setMessage(resourceHandler.get(APIErrorCodes.DATABASE_ERROR.name()));
        } else {
        	apiError = new APIError(HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }
        return buildResponseEntity(apiError);
    }
    
    /**
     * Handle JDBC Exception, inspects the cause for different DB causes.
     *
     * @param ex the JDBCException
     * @return the ApiError object
     */
    @ExceptionHandler(JDBCException.class)
    protected ResponseEntity<Object> handleDatabaseException(JDBCException ex,
                                                                  WebRequest request) {
    	APIError apiError = new APIError(HttpStatus.INTERNAL_SERVER_ERROR, APIErrorCodes.DATABASE_ERROR, ex);
	    apiError.setMessage(resourceHandler.get(APIErrorCodes.DATABASE_ERROR.name()));
        return buildResponseEntity(apiError);
    }


    /**
     * Build Resposne entity for the given error message;
     * 
     * @param apiError
     * @return
     */
    private ResponseEntity<Object> buildResponseEntity(APIError apiError) {
    	logger.error("API throws an exception " + apiError);
        return new ResponseEntity<Object>(apiError, HttpStatus.valueOf(apiError.getCode()));
    }
    
    
    
}
