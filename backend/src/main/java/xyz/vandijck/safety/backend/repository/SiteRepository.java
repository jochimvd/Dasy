package xyz.vandijck.safety.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.vandijck.safety.backend.entity.Site;

public interface SiteRepository extends JpaRepository<Site, Long>
{

    /**
     * Finds the site with the given name
     * @param name The name of the site
     * @return The site if it exists already
     */
    Site findByName(String name);

}
