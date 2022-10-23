package xyz.vandijck.safety.backend.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.vandijck.safety.backend.controller.exceptions.BadRequestException;
import xyz.vandijck.safety.backend.controller.exceptions.CategoryNotFoundException;
import xyz.vandijck.safety.backend.entity.Category;
import xyz.vandijck.safety.backend.repository.CategoryRepository;
import xyz.vandijck.safety.backend.request.CategorySearchRequest;
import xyz.vandijck.safety.backend.request.DeleteRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Patcher patcher;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public SearchDTO<Category> findAll(CategorySearchRequest request) {
        return SearchHelper.findAll(request, entityManager, Category.class);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll().stream().filter(category -> !category.isArchived()).collect(Collectors.toList());
    }

    @Override
    public Category findById(long id) {
        return getByIdOrThrow(id);
    }


    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteById(long id) {
        categoryRepository.delete(getByIdOrThrow(id));
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(long id) {
        Category category = getByIdOrThrow(id);
        boolean deleted = true; // TODO check for observations with this category

        if (deleted) {
            categoryRepository.delete(category);
        } else {
            category.setArchived(true);
            categoryRepository.save(category);
        }
        return deleted;
    }

    @Override
    public Category patch(JsonPatch patch, long id) {
        Category category = getByIdOrThrow(id);
        Category patched = patcher.applyPatch(Category.class, patch, category);
        return save(patched);
    }

    @Override
    public Category getByIdOrThrow(long id) throws BadRequestException {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            throw new CategoryNotFoundException();
        }
        return category.get();
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(DeleteRequest request, long id) {
        userService.validateDeleteRequest(request);
        return archiveOrDeleteById(id);
    }

}
