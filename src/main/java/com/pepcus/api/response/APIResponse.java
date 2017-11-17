package com.pepcus.api.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pepcus.api.db.entities.Company;

import lombok.Data;

/**
 * Global response object to wrap response from all the APIs and return additional attributes.
 * 
 * TODO: Currently hard-coded company and companies attributes those needs to be replaced by generic 
 * name like object and list to be used by all entities's
 * 
 * @author Shiva Jain
 * @since 2017-11-13
 *
 */
@Data
@JsonInclude(Include.NON_EMPTY)
public class APIResponse {

	private String status;
	private String code;
	private String limit;
	private String offset;
	private String sort;
	private String total;
	private String message;
	/*
	 * TODO: Replace with generic attribute like list
	 */
	private List<Company> companies;
	private Company company; 
	private String nodeName;
}
