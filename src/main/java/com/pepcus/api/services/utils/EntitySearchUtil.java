package com.pepcus.api.services.utils;

import static com.pepcus.api.ApplicationConstants.ASCENDING;
import static com.pepcus.api.ApplicationConstants.DEFAULT_LIMIT;
import static com.pepcus.api.ApplicationConstants.DEFAULT_OFFSET;
import static com.pepcus.api.ApplicationConstants.DESENDING;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import com.pepcus.api.db.entities.Company;
import com.pepcus.api.db.entities.SearchableEntity;
import com.pepcus.api.exception.APIErrorCodes;
import com.pepcus.api.exception.ApplicationException;
import com.pepcus.api.services.EntitySearchSpecification;
import com.pepcus.api.services.OffsetPageRequest;

/**
 * Class is specific to keep utility methods for Entity Search feature. 
 * @author Shiva Jain
 * @since 2017-11-09
 *
 */
public class EntitySearchUtil {

	private static Logger logger = LoggerFactory.getLogger(EntitySearchUtil.class);

    /**
     * Get Pageable instance
     * @param offset
     * @param limit
     * @param sortedBy
     * @return
     */
    public static Pageable getPageable(Integer offset, Integer limit, String sortedBy, String defaultSortedBy) {
    	
    	OffsetPageRequest pageable = null;
    	
    	offset = offset == null ? DEFAULT_OFFSET : offset;
    	limit = limit == null ? DEFAULT_LIMIT : limit;
    	
    	sortedBy = StringUtils.isBlank(sortedBy) ? defaultSortedBy : sortedBy.trim();

    	Sort.Direction sortDirection = getSortDirection(sortedBy);
    	
		sortedBy = extractSortDirection(sortedBy, sortDirection); // Extracted out + or - character from sortBy string

		Sort sort = new Sort(sortDirection, sortedBy);
		
    	pageable = new OffsetPageRequest(offset/limit, limit, sort);
    	
    	pageable.setOffset(offset);
    	
    	return pageable;
    }
    
    /**
     * It will extract + or - character those stands for sort direction from column field name
     * @param sortedBy
     * @param sortDirection
     * @return
     */
    public static String extractSortDirection(String sortedBy, Sort.Direction sortDirection) {
    	String directionIndicator = sortedBy.substring(0,1);
		if(directionIndicator.equals(ASCENDING) || directionIndicator.equals(DESENDING) ) {
			sortedBy = sortedBy.substring(1);
		} 
		
		return sortedBy;
	}
    
    /**
     * Return formatted string to display like +-fieldName will be formatted as 
     * 
     * fieldName ASC or fieldName DESC
     */
    public static String getFormattedString(String sortBy) {
    	if (StringUtils.isBlank(sortBy)) {
    		return sortBy;
    	}
    	Sort.Direction sortDirection = getSortDirection(sortBy);
    	String sortedBy = extractSortDirection(sortBy, sortDirection);
    	
    	return sortedBy + " " + sortDirection.name();
    }

	/**
     * Get the first character out from sortedBy value. 
     * like +companyName
     * @param sortedBy
     * @return
     */
    public static Direction getSortDirection(String sortedBy) {
    	String sortDirection =  sortedBy.substring(0, 1);
    	return DESENDING.equalsIgnoreCase(sortDirection) ? Direction.DESC : Direction.ASC;
	}

    /**
     * @return
     */
    public static Set<String> getSortAndLimitRequestParams() {
    	//TODO: To externalize them
    	String requestParams[] = {"offset", "limit", "sort", "searchSpec"};
    	return new HashSet<String>(Arrays.asList(requestParams));
    }
    
	/**
	 * To validate given Class has field with fieldName or not
	 * 
	 * @param kclass
	 * @param fieldName
	 * @return
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public static boolean classHasField(Class kclass, String fieldName) {
		try {
			Field field = kclass.getDeclaredField(fieldName);
			if (field == null) {
				return false;
			}
		} catch(NoSuchFieldException | SecurityException ex) {
			return false;
		}

		return true;
	}
	
	/**
	 * To filter request parameters on field Name
	 * @param <T>
	 * 
	 * @param allRequestParams
	 * @param kclass
	 * @return 
	 */
	public static <T> Map<String, String> extractParametersForFilterRecords(Map<String, String> allRequestParams,
			Class<T> kclass) throws ApplicationException {
		
		    Set<String> excludedParams = getSortAndLimitRequestParams();
		    
		    Map<String, String> filteredParameters = new HashMap<String, String>();
		    
		    for(Entry<String, String> entry : allRequestParams.entrySet()) { 
		    	if (excludedParams.contains(entry.getKey())) {
		    		continue;
		    	} else if (!classHasField(kclass, entry.getKey())) {
		    		throw ApplicationException.createBadRequest(APIErrorCodes.REQUEST_PARAM_INVALID, entry.getKey(), kclass.getName());
		    	} 
		    	filteredParameters.put(entry.getKey(), entry.getValue());
		    }
		    
		    return filteredParameters;
	}
	
    /**
     * Create Entity Search Specification
     * It will give priority over requestParameters on searchSpec
     * 
     * @param searchSpec
     * @param requestParameters
     * @return
     * @throws ApplicationException 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Specification<T> getEntitySearchSpecification(String searchSpec,
    		Map<String, String> requestParameters, Class<T> kclass, SearchableEntity entity) throws ApplicationException {
    	
    	//To get any other requestParameter like Company's fieldName to filter record on filterName with this case we will ignore searchSpec.
    	if (requestParameters != null) {
    		Map<String, String> requestParametersForFilterRecords = EntitySearchUtil.extractParametersForFilterRecords(requestParameters, kclass);
    		if (requestParametersForFilterRecords != null && !requestParametersForFilterRecords.isEmpty()) {
    			return new EntitySearchSpecification(requestParametersForFilterRecords, entity);
    		} 
    	}

    	if (StringUtils.isNotBlank(searchSpec)) {
    		if(logger.isDebugEnabled()) {
				logger.debug("Applying searchSpec filter on records " + searchSpec);
			}
    		return new EntitySearchSpecification (searchSpec, entity);
    	}
    	
    	return null;
    }


}
