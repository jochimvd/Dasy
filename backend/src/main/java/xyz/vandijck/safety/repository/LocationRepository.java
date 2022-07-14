package xyz.vandijck.safety.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.vandijck.safety.entity.location.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}
