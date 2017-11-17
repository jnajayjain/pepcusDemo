package com.pepcus.api.exception;

import lombok.Getter;

/**
 * To make an API's error uniquely identifiable by its error code.
 * 
 * @author Shiva Jain
 * @since 2017-11-02
 *
 */
public enum APIErrorCodes {
	
	// GENERAL ERROR CODES
	VALIDATION_FAILED(1000),
	REQUEST_PARAM_INVALID(1000),
	REQUIRED_PARAMETER(1001),
	UNAUTHORIZED_USER(1002),
	ARGUMENT_TYPE_MISMATCH(1003),
	MEDIA_NOT_SUPPORTED(1004),
	ENTITY_NOT_FOUND(1005),
	MALFORMED_JSON_REQUEST(1006),
	ERROR_WRITING_JSON_OUTPUT(1007),
	DATABASE_ERROR(1008),
	NO_RECORDS_FOUND(1009),
	
	// BULK IMPORT ERROR CODES
	NO_RECORDS_FOUND_FOR_IMPORT(1010),
	MISSING_REQUIRED_HEADERS(1011),
	MAX_RECORD_EXCEEDED(1012),
	INVALID_FILE_EXTENTION(1013),
	FILE_READ_ERROR(1014); 
		
	
	@Getter
    private final Integer code;

    APIErrorCodes(Integer intCode) {
        this.code = intCode;
    }

}
