package xyz.vandijck.safety.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.vandijck.safety.entity.consequence.Consequence;

@Repository
public interface ConsequenceRepository extends JpaRepository<Consequence, Long> {
}
