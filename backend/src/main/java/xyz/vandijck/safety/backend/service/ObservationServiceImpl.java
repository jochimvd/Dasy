package xyz.vandijck.safety.backend.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.vandijck.safety.backend.controller.exceptions.BadRequestException;
import xyz.vandijck.safety.backend.controller.exceptions.ObservationNotFoundException;
import xyz.vandijck.safety.backend.entity.Observation;
import xyz.vandijck.safety.backend.entity.common.Status;
import xyz.vandijck.safety.backend.repository.ObservationRepository;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.ObservationSearchRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class ObservationServiceImpl implements ObservationService {

    @Autowired
    private ObservationRepository observationRepository;

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Patcher patcher;

    public ObservationServiceImpl(ObservationRepository observationRepository) {
        this.observationRepository = observationRepository;
    }

    @Override
    public SearchDTO<Observation> findAll(ObservationSearchRequest request) {
        return SearchHelper.findAll(request, entityManager, Observation.class);
    }

    public Page<Observation> findAll(ObservationSearchRequest request, Pageable pageable) {
        return SearchHelper.findAll(request, pageable, entityManager, Observation.class);
    }

    @Override
    public List<Observation> findAll() {
        return observationRepository.findAll().stream().filter(observation -> !observation.isArchived()).collect(Collectors.toList());
    }

    @Override
    public Observation findById(long id) {
        return getByIdOrThrow(id);
    }


    @Override
    public Observation save(Observation observation) {
        if (observation.getStatus() == null) {
            observation.setStatus(Status.NEW);
        }
        return observationRepository.save(observation);
    }

    @Override
    public void deleteById(long id) {
        observationRepository.delete(getByIdOrThrow(id));
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(long id) {
        Observation observation = getByIdOrThrow(id);
        boolean deleted = false;

        if (deleted) {
            observationRepository.delete(observation);
        } else {
            observation.setArchived(true);
            observationRepository.save(observation);
        }
        return deleted;
    }

    @Override
    public Observation patch(JsonPatch patch, long id) {
        Observation observation = getByIdOrThrow(id);
        Observation patched = patcher.applyPatch(Observation.class, patch, observation);
        patched.setObserver(observation.getObserver());
        patched.setCategory(observation.getCategory());
        patched.setLocation(observation.getLocation());
        return save(patched);
    }

    @Override
    public Observation getByIdOrThrow(long id) throws BadRequestException {
        Optional<Observation> observation = observationRepository.findById(id);
        if (observation.isEmpty()) {
            throw new ObservationNotFoundException();
        }
        return observation.get();
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(DeleteRequest request, long id) {
        userService.validateDeleteRequest(request);
        return archiveOrDeleteById(id);
    }

}
