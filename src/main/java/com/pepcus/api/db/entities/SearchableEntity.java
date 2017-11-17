package com.pepcus.api.db.entities;

import java.util.List;

/**
 * Interface for Search utility
 * @author Shiva Jain
 * @since 2017-11-09
 *
 */
public interface SearchableEntity {
    public List<String> getSearchFields();
    
}
