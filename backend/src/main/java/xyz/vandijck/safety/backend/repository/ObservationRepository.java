package xyz.vandijck.safety.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.vandijck.safety.backend.entity.Observation;

import java.time.ZonedDateTime;

public interface ObservationRepository extends JpaRepository<Observation, Long>
{

    long countByObservedAtBeforeAndCategoryId(ZonedDateTime before, Long categoryId);

    long countByObservedAtBeforeAndCategoryIdAndTypeIdNot(ZonedDateTime before, Long categoryId, Long typeId);

    long countByObservedAtBetweenAndCategoryIdAndTypeIdNot(ZonedDateTime start, ZonedDateTime end, Long categoryId, Long typeId);

    long countByObservedAtBetweenAndSiteId(ZonedDateTime start, ZonedDateTime end, Long siteId);

    long countByObservedAtBetweenAndSiteIdAndImmediateDanger(ZonedDateTime start, ZonedDateTime end, Long siteId, boolean immediateDanger);

    long countByObservedAtBetweenAndSiteIdAndTypeId(ZonedDateTime start, ZonedDateTime end, Long siteId, Long typeId);

}
