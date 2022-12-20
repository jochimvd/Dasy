package xyz.vandijck.safety.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import xyz.vandijck.safety.backend.entity.Observation;
import xyz.vandijck.safety.backend.request.ObservationSearchRequest;

import java.time.ZonedDateTime;

public interface ObservationService extends EntityService<Observation, ObservationSearchRequest> {
    Page<Observation> findAll(ObservationSearchRequest request, Pageable pageable);

    long countByObservedAtBeforeAndCategoryId(ZonedDateTime before, long categoryId);

    long countByObservedAtBetweenAndCategoryId(ZonedDateTime start, ZonedDateTime end, long categoryId);

    long countByObservedAtBetweenAndSiteId(ZonedDateTime start, ZonedDateTime end, long siteId);

    long countByObservedAtBetweenAndSiteIdAndTypeId(ZonedDateTime start, ZonedDateTime end, long siteId, long typeId);

}
