package xyz.vandijck.safety.backend.policy;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.dto.LocationDto;
import xyz.vandijck.safety.backend.entity.Location;
import xyz.vandijck.safety.backend.entity.Role;
import xyz.vandijck.safety.backend.request.LocationSearchRequest;
import xyz.vandijck.safety.backend.service.LocationService;

import java.util.Collections;
import java.util.List;

/**
 * Policy for operations concerning Locations
 */
@Component
public class LocationPolicy extends CRUDPolicy<LocationDto, LocationSearchRequest> {

    private static final List<String> ADMIN_EDITABLE_FIELDS;

    static {
        ADMIN_EDITABLE_FIELDS = List.of(
                "name",
                "description",
                "coordinates"
        );
    }

    @Autowired
    private LocationService locationService;

    @Override
    public boolean allowGet(long id) {
        Location location = locationService.getByIdOrThrow(id);
        if (location.isArchived()) {
            return isAuthorized(id, Role.ADMIN);
        } else {
            return allowPublic();
        }
    }

    @Override
    public boolean allowPost(LocationDto entity) {
        return isAuthorized(entity.getId(), Role.ADMIN);
    }

    @Override
    public boolean allowPut(LocationDto entity) {
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
