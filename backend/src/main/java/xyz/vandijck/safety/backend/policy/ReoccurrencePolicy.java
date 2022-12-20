package xyz.vandijck.safety.backend.policy;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.dto.ReoccurrenceDto;
import xyz.vandijck.safety.backend.entity.Reoccurrence;
import xyz.vandijck.safety.backend.entity.Role;
import xyz.vandijck.safety.backend.request.ReoccurrenceSearchRequest;
import xyz.vandijck.safety.backend.service.ReoccurrenceService;

import java.util.Collections;
import java.util.List;

/**
 * Policy for operations concerning Reoccurrences
 */
@Component
public class ReoccurrencePolicy extends CRUDPolicy<ReoccurrenceDto, ReoccurrenceSearchRequest> {

    private static final List<String> ADMIN_EDITABLE_FIELDS;

    static {
        ADMIN_EDITABLE_FIELDS = List.of(
                "name",
                "description",
                "rate"
        );
    }

    @Autowired
    private ReoccurrenceService reoccurrenceService;

    @Override
    public boolean allowGet(long id) {
        Reoccurrence reoccurrence = reoccurrenceService.getByIdOrThrow(id);
        if (reoccurrence.isArchived()) {
            return isAuthorized(id, Role.ADMIN);
        } else {
            return allowPublic();
        }
    }

    @Override
    public boolean allowPost(ReoccurrenceDto entity) {
        return isAuthorized(entity.getId(), Role.ADMIN);
    }

    @Override
    public boolean allowPut(ReoccurrenceDto entity) {
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
