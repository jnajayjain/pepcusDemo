package com.pepcus.api.services;

import static com.pepcus.api.ApplicationConstants.DEFAULT_SORT_BY_COMPANY_NAME;
import static com.pepcus.api.services.utils.EntitySearchUtil.getEntitySearchSpecification;
import static com.pepcus.api.services.utils.EntitySearchUtil.getPageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.pepcus.api.db.entities.Company;
import com.pepcus.api.exception.APIErrorCodes;
import com.pepcus.api.exception.ApplicationException;
import com.pepcus.api.repositories.CompanyRepository;


/**
*
* Provides a collection of all services related with Company
* database object

* @author Shiva Jain
* @Since 2017-11-04
*
* 
*/

@Service
public class CompanyService  extends CommonService {
	
	private Logger logger = LoggerFactory.getLogger(CompanyService.class);
	
    @Autowired
    private CompanyRepository companyRepository;
    
	 @Autowired
	 JdbcTemplate jdbcTemplate;


    /**
     * To fetch companies records. Based on given parameters companies records will be filtered out.
     * 
     * @param Integer offset First record index from database after sorting. Default value is 0
     * @param Integer limit Number of records to be fetched. Default value is 50
     * @param String sortField Field on which records needs to be sorted
     * @param String searchSpec Search string for filtering results
     * @param Map<String, String>
     * @return List<Company> object 
     * @throws ApplicationException 
     */
    public List<Company> getAllCompany(Integer offset, 
    		Integer limit,
    		String sortField, 
    		String searchSpec, 
    		Map<String, String> requestParameters) throws ApplicationException {
    	
    	List<Company> companies = new ArrayList<Company>();

    	Pageable pageable = getPageable(offset, limit, sortField, getDefaultSortField());
    	
		if(logger.isDebugEnabled()) {
			logger.debug("Request parameters to filter, size and paginate records ");
			requestParameters.entrySet().stream().forEach(entry -> { logger.debug(entry.getKey() + ":: " + entry.getValue()); });
		}

    	
    	Specification<Company> spec = getEntitySearchSpecification(searchSpec, requestParameters, Company.class, new Company());

    	Page<Company> companyList  = (Page<Company>) companyRepository.findAll(spec, pageable);

    	companyList.getContent().forEach(c -> companies.add(c));
    	
    	return companies;
    }

    
	/**
     * Fetch specific company from database
     * 
     * @param companyId
     * @return Company object 
     */
    public Company getCompany(Integer companyId) {
    	return companyRepository.findOne(companyId);
    }
    
    /**
     * Add a company in database
     * 
     * @param company object
     */
    public Company addCompany(Company company)  {
    	return companyRepository.save(company);
    }
    
    /**
     * Update a company in database
     * 
     * @param company object
     * @throws ApplicationException 
     */
    public Company updateCompany(Company company) throws ApplicationException  {
    	Integer companyId = company.getCompanyId();
    	
		if (null == companyRepository.findOne(companyId)) {
    		throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "company", "companyId="+companyId);
    	}
		
    	return companyRepository.save(company);

    }
    
    /**
     * Delete specific company from database
     * 
     * @param companyId
     */
    public int deleteCompany(int companyId) throws ApplicationException {
    	try {
    		companyRepository.delete(companyId);
    	} catch (EmptyResultDataAccessException ex ) {
    		throw ApplicationException.createEntityNotFoundError(APIErrorCodes.ENTITY_NOT_FOUND, "company", "companyId="+companyId);
    	}
    	return companyId;
    }    
    
 	/**
	 * 
	 * SOME UTILITY METHODS
	 * 
	 */
	
    /**
     * Return default sort field for company service
     * 
     * @return String 
     */
    @Override
    public String getDefaultSortField()  {
    	return DEFAULT_SORT_BY_COMPANY_NAME;
    }
	
}