package xyz.vandijck.safety.backend.policy;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.dto.CategoryDto;
import xyz.vandijck.safety.backend.entity.Category;
import xyz.vandijck.safety.backend.entity.Role;
import xyz.vandijck.safety.backend.request.CategorySearchRequest;
import xyz.vandijck.safety.backend.service.CategoryService;

import java.util.Collections;
import java.util.List;

/**
 * Policy for operations concerning Categories
 */
@Component
public class CategoryPolicy extends CRUDPolicy<CategoryDto, CategorySearchRequest> {

    private static final List<String> ADMIN_EDITABLE_FIELDS;

    static {
        ADMIN_EDITABLE_FIELDS = List.of(
                "name",
                "description"
//                "severity", // TODO implement nested JSON patch
//                "consequence"
        );
    }

    @Autowired
    private CategoryService categoryService;

    @Override
    public boolean allowGet(long id) {
        Category category = categoryService.getByIdOrThrow(id);
        if (category.isArchived()) {
            return isAuthorized(id, Role.ADMIN);
        } else {
            return allowPublic();
        }
    }

    @Override
    public boolean allowPost(CategoryDto entity) {
        return isAuthorized(entity.getId(), Role.ADMIN);
    }

    @Override
    public boolean allowPut(CategoryDto entity) {
        return isAuthorized(entity.getId(), Role.ADMIN);
    }

    @Override
    public boolean allowDelete(long id) {
        return isAuthorized(id, Role.ADMIN);
    }

    @Override
    public boolean allowPatch(long id, JsonPatch patch) {
        return patcher.validatePatch(patch, getEditableFields(id));
    }

    @Override
    public List<String> getEditableFields(long id) {
        Role role = roleFor(id);
        if (role == Role.ADMIN) {
            return ADMIN_EDITABLE_FIELDS;
        }
        return Collections.emptyList();
    }

}
