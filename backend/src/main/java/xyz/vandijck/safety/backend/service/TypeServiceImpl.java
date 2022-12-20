package xyz.vandijck.safety.backend.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.vandijck.safety.backend.controller.exceptions.BadRequestException;
import xyz.vandijck.safety.backend.controller.exceptions.TypeNotFoundException;
import xyz.vandijck.safety.backend.entity.Type;
import xyz.vandijck.safety.backend.repository.TypeRepository;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.TypeSearchRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class TypeServiceImpl implements TypeService {

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Patcher patcher;

    public TypeServiceImpl(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    @Override
    public SearchDTO<Type> findAll(TypeSearchRequest request) {
        return SearchHelper.findAll(request, entityManager, Type.class);
    }

    @Override
    public List<Type> findAll() {
        return typeRepository.findAll().stream().filter(type -> !type.isArchived()).collect(Collectors.toList());
    }

    @Override
    public Type findById(long id) {
        return getByIdOrThrow(id);
    }


    @Override
    public Type save(Type type) {
        return typeRepository.save(type);
    }

    @Override
    public void deleteById(long id) {
        typeRepository.delete(getByIdOrThrow(id));
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(long id) {
        Type type = getByIdOrThrow(id);
        boolean deleted = true; // TODO check for categories with this type

        if (deleted) {
            typeRepository.delete(type);
        } else {
            type.setArchived(true);
            typeRepository.save(type);
        }
        return deleted;
    }

    @Override
    public Type patch(JsonPatch patch, long id) {
        Type type = getByIdOrThrow(id);
        Type patched = patcher.applyPatch(Type.class, patch, type);
        return save(patched);
    }

    @Override
    public Type getByIdOrThrow(long id) throws BadRequestException {
        Optional<Type> type = typeRepository.findById(id);
        if (type.isEmpty()) {
            throw new TypeNotFoundException();
        }
        return type.get();
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(DeleteRequest request, long id) {
        userService.validateDeleteRequest(request);
        return archiveOrDeleteById(id);
    }

}
