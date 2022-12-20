package xyz.vandijck.safety.backend.policy;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.dto.SiteDto;
import xyz.vandijck.safety.backend.entity.Role;
import xyz.vandijck.safety.backend.entity.Site;
import xyz.vandijck.safety.backend.request.SiteSearchRequest;
import xyz.vandijck.safety.backend.service.SiteService;

import java.util.Collections;
import java.util.List;

/**
 * Policy for operations concerning Sites
 */
@Component
public class SitePolicy extends CRUDPolicy<SiteDto, SiteSearchRequest> {

    private static final List<String> ADMIN_EDITABLE_FIELDS;

    static {
        ADMIN_EDITABLE_FIELDS = List.of(
                "name",
                "description",
                "coordinates"
        );
    }

    @Autowired
    private SiteService siteService;

    @Override
    public boolean allowGet(long id) {
        Site site = siteService.getByIdOrThrow(id);
        if (site.isArchived()) {
            return isAuthorized(id, Role.ADMIN);
        } else {
            return allowPublic();
        }
    }

    @Override
    public boolean allowPost(SiteDto entity) {
        return isAuthorized(entity.getId(), Role.ADMIN);
    }

    @Override
    public boolean allowPut(SiteDto entity) {
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
