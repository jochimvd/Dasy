package xyz.vandijck.safety.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.vandijck.safety.backend.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long>
{

    /**
     * Finds the company with the given name
     * @param name The name of the company
     * @return The company if it exists already
     */
    Company findByName(String name);

}
