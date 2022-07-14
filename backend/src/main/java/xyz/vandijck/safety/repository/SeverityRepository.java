package xyz.vandijck.safety.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.vandijck.safety.entity.severity.Severity;

@Repository
public interface SeverityRepository extends JpaRepository<Severity, Long> {
}
