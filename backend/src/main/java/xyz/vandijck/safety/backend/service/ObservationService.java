package xyz.vandijck.safety.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import xyz.vandijck.safety.backend.entity.Observation;
import xyz.vandijck.safety.backend.request.ObservationSearchRequest;

public interface ObservationService extends EntityService<Observation, ObservationSearchRequest> {
    Page<Observation> findAll(ObservationSearchRequest request, Pageable pageable);
}
