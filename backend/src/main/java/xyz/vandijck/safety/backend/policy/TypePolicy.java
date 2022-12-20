package xyz.vandijck.safety.backend.policy;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.dto.TypeDto;
import xyz.vandijck.safety.backend.entity.Role;
import xyz.vandijck.safety.backend.entity.Type;
import xyz.vandijck.safety.backend.request.TypeSearchRequest;
import xyz.vandijck.safety.backend.service.TypeService;

import java.util.Collections;
import java.util.List;

/**
 * Policy for operations concerning Types
 */
@Component
public class TypePolicy extends CRUDPolicy<TypeDto, TypeSearchRequest> {

    private static final List<String> ADMIN_EDITABLE_FIELDS;

    static {
        ADMIN_EDITABLE_FIELDS = List.of(
                "name",
                "notify"
        );
    }

    @Autowired
    private TypeService typeService;

    @Override
    public boolean allowGet(long id) {
        Type type = typeService.getByIdOrThrow(id);
        if (type.isArchived()) {
            return isAuthorized(id, Role.ADMIN);
        } else {
            return allowPublic();
        }
    }

    @Override
    public boolean allowPost(TypeDto entity) {
        return isAuthorized(entity.getId(), Role.ADMIN);
    }

    @Override
    public boolean allowPut(TypeDto entity) {
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
