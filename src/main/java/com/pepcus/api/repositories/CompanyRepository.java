package com.pepcus.api.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.pepcus.api.db.entities.Company;


/**
 * Company repository for company entity.
 *  
 * @author Shiva Jain
 * @since   2017-11-01 
 *
 */

public interface CompanyRepository extends PagingAndSortingRepository<Company, Integer> ,JpaSpecificationExecutor<Company> {
	
	
}

