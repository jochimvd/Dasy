package xyz.vandijck.safety.backend.policy;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.dto.ConsequenceDto;
import xyz.vandijck.safety.backend.entity.Consequence;
import xyz.vandijck.safety.backend.entity.Role;
import xyz.vandijck.safety.backend.request.ConsequenceSearchRequest;
import xyz.vandijck.safety.backend.service.ConsequenceService;

import java.util.Collections;
import java.util.List;

/**
 * Policy for operations concerning Consequences
 */
@Component
public class ConsequencePolicy extends CRUDPolicy<ConsequenceDto, ConsequenceSearchRequest> {

    private static final List<String> ADMIN_EDITABLE_FIELDS;

    static {
        ADMIN_EDITABLE_FIELDS = List.of(
                "name",
                "description",
                "probability"
        );
    }

    @Autowired
    private ConsequenceService consequenceService;

    @Override
    public boolean allowGet(long id) {
        Consequence consequence = consequenceService.getByIdOrThrow(id);
        if (consequence.isArchived()) {
            return isAuthorized(id, Role.ADMIN);
        } else {
            return allowPublic();
        }
    }

    @Override
    public boolean allowPost(ConsequenceDto entity) {
        return isAuthorized(entity.getId(), Role.ADMIN);
    }

    @Override
    public boolean allowPut(ConsequenceDto entity) {
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
