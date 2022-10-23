package xyz.vandijck.safety.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.vandijck.safety.backend.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long>
{
}
