package xyz.vandijck.safety.service;

import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.vandijck.safety.entity.observation.Observation;
import xyz.vandijck.safety.entity.observation.ObservationInput;
import xyz.vandijck.safety.entity.observation.ObservationPatchInput;
import xyz.vandijck.safety.exception.ResourceDoesNotExistException;
import xyz.vandijck.safety.repository.ObservationRepository;

@Service
public class ObservationService {

    @Autowired
    ObservationRepository observationRepository;

    @Autowired
    LocationService locationService;

    @Autowired
    CategoryService categoryService;

    public List<Observation> findAll() {
        return observationRepository.findAll();
    }

    public Observation findById(long id) {
        return observationRepository.findById(id)
                .orElseThrow(ResourceDoesNotExistException::new);
    }

    public Observation create(ObservationInput observationInput) {
        return update(new Observation(), observationInput);
    }

    public void update(long id, ObservationInput observationInput) {
        update(findById(id), observationInput);
    }

    public Observation update(Observation observation, ObservationInput observationInput) {
        observation.setKey(observationInput.getKey());
        observation.setObserver(observationInput.getObserver());
        observation.setObserverCompany(observationInput.getObserverCompany());
        observation.setObservedAt(observationInput.getObservedAt());
        observation.setLocation(locationService.findByURI(observationInput.getLocationURI()));
        observation.setObservedCompany(observationInput.getObservedCompany());
        observation.setImmediateDanger(observationInput.isImmediateDanger());
        observation.setType(observationInput.getType());
        observation.setCategory(categoryService.findByURI(observationInput.getCategoryURI()));
        observation.setDescription(observationInput.getDescription());
        observation.setActionsTaken(observationInput.getActionsTaken());
        observation.setFurtherActions(observationInput.getFurtherActions());

        return observationRepository.save(observation);
    }

    public Observation patch(long id, ObservationPatchInput observationPatchInput) {
        Observation observation = findById(id);
        observation.setStatus(observationPatchInput.getStatus());

        return observationRepository.save(observation);
    }

    public void deleteById(long id) {
        observationRepository.delete(findById(id));
    }

    public long countByObservedAtBeforeAndCategoryId(ZonedDateTime before, long categoryId) {
        return observationRepository.countByObservedAtBeforeAndCategoryId(before, categoryId);
    }

    public long countByObservedAtBetweenAndCategoryId(ZonedDateTime start, ZonedDateTime end, long categoryId) {
        return observationRepository.countByObservedAtBetweenAndCategoryId(start, end, categoryId);
    }
}
