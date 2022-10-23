package xyz.vandijck.safety.backend.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.vandijck.safety.backend.controller.exceptions.BadRequestException;
import xyz.vandijck.safety.backend.controller.exceptions.ConsequenceNotFoundException;
import xyz.vandijck.safety.backend.entity.Consequence;
import xyz.vandijck.safety.backend.repository.ConsequenceRepository;
import xyz.vandijck.safety.backend.request.ConsequenceSearchRequest;
import xyz.vandijck.safety.backend.request.DeleteRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class ConsequenceServiceImpl implements ConsequenceService {

    @Autowired
    private ConsequenceRepository consequenceRepository;

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Patcher patcher;

    public ConsequenceServiceImpl(ConsequenceRepository consequenceRepository) {
        this.consequenceRepository = consequenceRepository;
    }

    @Override
    public SearchDTO<Consequence> findAll(ConsequenceSearchRequest request) {
        return SearchHelper.findAll(request, entityManager, Consequence.class);
    }

    @Override
    public List<Consequence> findAll() {
        return consequenceRepository.findAll().stream().filter(consequence -> !consequence.isArchived()).collect(Collectors.toList());
    }

    @Override
    public Consequence findById(long id) {
        return getByIdOrThrow(id);
    }


    @Override
    public Consequence save(Consequence consequence) {
        return consequenceRepository.save(consequence);
    }

    @Override
    public void deleteById(long id) {
        consequenceRepository.delete(getByIdOrThrow(id));
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(long id) {
        Consequence consequence = getByIdOrThrow(id);
        boolean deleted = true; // TODO check for categories with this consequence

        if (deleted) {
            consequenceRepository.delete(consequence);
        } else {
            consequence.setArchived(true);
            consequenceRepository.save(consequence);
        }
        return deleted;
    }

    @Override
    public Consequence patch(JsonPatch patch, long id) {
        Consequence consequence = getByIdOrThrow(id);
        Consequence patched = patcher.applyPatch(Consequence.class, patch, consequence);
        return save(patched);
    }

    @Override
    public Consequence getByIdOrThrow(long id) throws BadRequestException {
        Optional<Consequence> consequence = consequenceRepository.findById(id);
        if (consequence.isEmpty()) {
            throw new ConsequenceNotFoundException();
        }
        return consequence.get();
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(DeleteRequest request, long id) {
        userService.validateDeleteRequest(request);
        return archiveOrDeleteById(id);
    }

}
