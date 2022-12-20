package xyz.vandijck.safety.backend.service;

import xyz.vandijck.safety.backend.entity.Company;
import xyz.vandijck.safety.backend.request.CompanySearchRequest;

public interface CompanyService extends EntityService<Company, CompanySearchRequest> {

    /**
     * Retrieves the company for the given name, unless it does not exist. Then it is created
     * @param name The name of the company
     * @return The (possible new) Company with the given name
     */
    Company findElseCreate(String name);
    
}
