package xyz.vandijck.safety.service;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriTemplate;
import xyz.vandijck.safety.entity.category.Category;
import xyz.vandijck.safety.entity.category.CategoryInput;
import xyz.vandijck.safety.repository.CategoryRepository;

@Service
public class CategoryService {

    private static final UriTemplate CATEGORY_URI_TEMPLATE = new UriTemplate("/categories/{id}");

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ConsequenceService consequenceService;

    @Autowired
    SeverityService severityService;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id) );
    }

    public Category findByURI(URI categoryURI) {
        return findById(Long.parseLong(CATEGORY_URI_TEMPLATE.match(categoryURI.toASCIIString()).get("id")));
    }

    public Category create(CategoryInput categoryInput) {
        return update(new Category(), categoryInput);
    }

    public void update(long id, CategoryInput categoryInput) {
        update(findById(id), categoryInput);
    }

    public Category update(Category category, CategoryInput categoryInput) {
        category.setName(categoryInput.getName());
        category.setConsequence(consequenceService.findByURI(categoryInput.getConsequenceURI()));
        category.setSeverity(severityService.findByURI(categoryInput.getSeverityURI()));

        return categoryRepository.save(category);
    }

    public void deleteById(long id) {
        categoryRepository.delete(findById(id));
    }

}
