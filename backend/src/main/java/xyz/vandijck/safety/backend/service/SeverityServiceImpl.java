package xyz.vandijck.safety.backend.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.vandijck.safety.backend.controller.exceptions.BadRequestException;
import xyz.vandijck.safety.backend.controller.exceptions.SeverityNotFoundException;
import xyz.vandijck.safety.backend.entity.Severity;
import xyz.vandijck.safety.backend.repository.SeverityRepository;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.SeveritySearchRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class SeverityServiceImpl implements SeverityService {

    @Autowired
    private SeverityRepository severityRepository;

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Patcher patcher;

    public SeverityServiceImpl(SeverityRepository severityRepository) {
        this.severityRepository = severityRepository;
    }

    @Override
    public SearchDTO<Severity> findAll(SeveritySearchRequest request) {
        return SearchHelper.findAll(request, entityManager, Severity.class);
    }

    @Override
    public List<Severity> findAll() {
        return severityRepository.findAll().stream().filter(severity -> !severity.isArchived()).collect(Collectors.toList());
    }

    @Override
    public Severity findById(long id) {
        return getByIdOrThrow(id);
    }


    @Override
    public Severity save(Severity severity) {
        return severityRepository.save(severity);
    }

    @Override
    public void deleteById(long id) {
        severityRepository.delete(getByIdOrThrow(id));
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(long id) {
        Severity severity = getByIdOrThrow(id);
        boolean deleted = true; // TODO check for categories with this severity

        if (deleted) {
            severityRepository.delete(severity);
        } else {
            severity.setArchived(true);
            severityRepository.save(severity);
        }
        return deleted;
    }

    @Override
    public Severity patch(JsonPatch patch, long id) {
        Severity severity = getByIdOrThrow(id);
        Severity patched = patcher.applyPatch(Severity.class, patch, severity);
        return save(patched);
    }

    @Override
    public Severity getByIdOrThrow(long id) throws BadRequestException {
        Optional<Severity> severity = severityRepository.findById(id);
        if (severity.isEmpty()) {
            throw new SeverityNotFoundException();
        }
        return severity.get();
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(DeleteRequest request, long id) {
        userService.validateDeleteRequest(request);
        return archiveOrDeleteById(id);
    }

}
