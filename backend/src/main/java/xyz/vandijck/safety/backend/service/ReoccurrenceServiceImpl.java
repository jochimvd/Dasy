package xyz.vandijck.safety.backend.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.vandijck.safety.backend.controller.exceptions.BadRequestException;
import xyz.vandijck.safety.backend.controller.exceptions.ReoccurrenceNotFoundException;
import xyz.vandijck.safety.backend.entity.Reoccurrence;
import xyz.vandijck.safety.backend.repository.ReoccurrenceRepository;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.ReoccurrenceSearchRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class ReoccurrenceServiceImpl implements ReoccurrenceService {

    @Autowired
    private ReoccurrenceRepository reoccurrenceRepository;

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Patcher patcher;

    public ReoccurrenceServiceImpl(ReoccurrenceRepository reoccurrenceRepository) {
        this.reoccurrenceRepository = reoccurrenceRepository;
    }

    @Override
    public SearchDTO<Reoccurrence> findAll(ReoccurrenceSearchRequest request) {
        return SearchHelper.findAll(request, entityManager, Reoccurrence.class);
    }

    @Override
    public List<Reoccurrence> findAll() {
        return reoccurrenceRepository.findAll().stream().filter(reoccurrence -> !reoccurrence.isArchived()).collect(Collectors.toList());
    }

    @Override
    public Reoccurrence findById(long id) {
        return getByIdOrThrow(id);
    }


    @Override
    public Reoccurrence save(Reoccurrence reoccurrence) {
        return reoccurrenceRepository.save(reoccurrence);
    }

    @Override
    public void deleteById(long id) {
        reoccurrenceRepository.delete(getByIdOrThrow(id));
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(long id) {
        Reoccurrence reoccurrence = getByIdOrThrow(id);
        boolean deleted = true; // TODO check for categories with this reoccurrence

        if (deleted) {
            reoccurrenceRepository.delete(reoccurrence);
        } else {
            reoccurrence.setArchived(true);
            reoccurrenceRepository.save(reoccurrence);
        }
        return deleted;
    }

    @Override
    public Reoccurrence patch(JsonPatch patch, long id) {
        Reoccurrence reoccurrence = getByIdOrThrow(id);
        Reoccurrence patched = patcher.applyPatch(Reoccurrence.class, patch, reoccurrence);
        return save(patched);
    }

    @Override
    public Reoccurrence getByIdOrThrow(long id) throws BadRequestException {
        Optional<Reoccurrence> reoccurrence = reoccurrenceRepository.findById(id);
        if (reoccurrence.isEmpty()) {
            throw new ReoccurrenceNotFoundException();
        }
        return reoccurrence.get();
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(DeleteRequest request, long id) {
        userService.validateDeleteRequest(request);
        return archiveOrDeleteById(id);
    }

}
