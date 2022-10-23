package xyz.vandijck.safety.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.vandijck.safety.backend.entity.Consequence;

public interface ConsequenceRepository extends JpaRepository<Consequence, Long>
{
}
