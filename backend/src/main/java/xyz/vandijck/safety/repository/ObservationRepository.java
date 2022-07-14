package xyz.vandijck.safety.repository;

import java.time.ZonedDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.vandijck.safety.entity.observation.Observation;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, Long> {

    long countByObservedAtBeforeAndCategoryId(ZonedDateTime before, Long categoryId);

    long countByObservedAtBetweenAndCategoryId(ZonedDateTime start, ZonedDateTime end, Long categoryId);

}
