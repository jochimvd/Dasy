package xyz.vandijck.safety.backend.policy;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.dto.SeverityDto;
import xyz.vandijck.safety.backend.entity.Role;
import xyz.vandijck.safety.backend.entity.Severity;
import xyz.vandijck.safety.backend.request.SeveritySearchRequest;
import xyz.vandijck.safety.backend.service.SeverityService;

import java.util.Collections;
import java.util.List;

/**
 * Policy for operations concerning Severities
 */
@Component
public class SeverityPolicy extends CRUDPolicy<SeverityDto, SeveritySearchRequest> {

    private static final List<String> ADMIN_EDITABLE_FIELDS;

    static {
        ADMIN_EDITABLE_FIELDS = List.of(
                "name",
                "description",
                "level"
        );
    }

    @Autowired
    private SeverityService severityService;

    @Override
    public boolean allowGet(long id) {
        Severity severity = severityService.getByIdOrThrow(id);
        if (severity.isArchived()) {
            return isAuthorized(id, Role.ADMIN);
        } else {
            return allowPublic();
        }
    }

    @Override
    public boolean allowPost(SeverityDto entity) {
        return isAuthorized(entity.getId(), Role.ADMIN);
    }

    @Override
    public boolean allowPut(SeverityDto entity) {
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
