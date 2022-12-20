package xyz.vandijck.safety.backend.policy;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.dto.CompanyDto;
import xyz.vandijck.safety.backend.entity.Role;
import xyz.vandijck.safety.backend.request.CompanySearchRequest;

import java.util.Collections;
import java.util.List;

/**
 * Policy for operations concerning Companies
 */
@Component
public class CompanyPolicy extends CRUDPolicy<CompanyDto, CompanySearchRequest> {

    @Override
    public boolean allowPost(CompanyDto entity) {
        return false;
    }

    @Override
    public boolean allowPut(CompanyDto entity) {
        return false;
    }

    @Override
    public boolean allowDelete(long id) {
        return isAuthorized(id, Role.ADMIN);
    }

    @Override
    public boolean allowPatch(long id, JsonPatch patch) {
        return false;
    }

    @Override
    public List<String> getEditableFields(long id) {
        return Collections.emptyList();
    }

}
