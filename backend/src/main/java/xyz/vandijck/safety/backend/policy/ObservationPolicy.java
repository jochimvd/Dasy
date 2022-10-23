package xyz.vandijck.safety.backend.policy;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.dto.ObservationDto;
import xyz.vandijck.safety.backend.entity.Observation;
import xyz.vandijck.safety.backend.entity.Role;
import xyz.vandijck.safety.backend.request.ObservationSearchRequest;
import xyz.vandijck.safety.backend.service.ObservationService;

import java.util.Collections;
import java.util.List;

/**
 * Policy for operations concerning Categories
 */
@Component
public class ObservationPolicy extends CRUDPolicy<ObservationDto, ObservationSearchRequest> {

    private static final List<String> ADMIN_EDITABLE_FIELDS;

    static {
        ADMIN_EDITABLE_FIELDS = List.of(
                "observedAt",
                "observedCompany",
                "immediateDanger",
                "type",
                "description",
                "actionsTaken",
                "furtherActions",
                "status",
                "archived"
        );
    }

    @Autowired
    private ObservationService observationService;

    @Override
    public boolean allowGet(long id) {
        Observation observation = observationService.getByIdOrThrow(id);
        if (observation.isArchived()) {
            return isAuthorized(id, Role.ADMIN);
        } else {
            return allowPublic();
        }
    }

    @Override
    public boolean allowPost(ObservationDto entity) {
        return isAuthorized(entity.getId(), Role.ADMIN);
    }

    @Override
    public boolean allowPut(ObservationDto entity) {
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
