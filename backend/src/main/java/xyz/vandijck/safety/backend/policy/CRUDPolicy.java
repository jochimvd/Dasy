package xyz.vandijck.safety.backend.policy;

import com.github.fge.jsonpatch.JsonPatch;
import xyz.vandijck.safety.backend.entity.UniqueEntity;
import xyz.vandijck.safety.backend.request.SearchRequest;

import java.util.List;

/**
 * Base policy-class for policies concerned with CRUD operations
 */
public abstract class CRUDPolicy<Entity extends UniqueEntity, Sr extends SearchRequest> extends BasePolicy {

    /**
     * @param id The id of the entity for which the user wants information
     * @return Whether the current user is allowed to see information about the request entity
     * By default public access
     */
    public boolean allowGet(long id) {
        return allowPublic();
    }

    public void authorizeGet(long id) {
        authorize(allowGet(id));
    }

    /**
     * Checks whether the filters in the request are allowed.
     * By default, public.
     *
     * @param searchRequest The {@link SearchRequest} to be checked.
     * @return Whether the current user is allowed to see information about all entities
     */
    public boolean allowGetAll(Sr searchRequest) {
        return allowPublic();
    }

    public void authorizeGetAll(Sr searchRequest) {
        authorize(allowGetAll(searchRequest));
    }

    /**
     * @param entity The entity to be created
     * @return Whether the current user is allowed to create the entity
     */
    public abstract boolean allowPost(Entity entity);

    public void authorizePost(Entity entity) {
        authorize(allowPost(entity));
    }

    /**
     * @param entity The entity to be updated
     * @return Whether the current user is allowed to fully update the given entity
     */
    public abstract boolean allowPut(Entity entity);

    public void authorizePut(Entity entity) {
        authorize(allowPut(entity));
    }

    /**
     * @param id The id of the entity to be deleted
     * @return Whether the current user is allowed to delete the referred entity
     */
    public abstract boolean allowDelete(long id);

    public void authorizeDelete(long id) {
        authorize(allowDelete(id));
    }

    /**
     * @param id The id of the entity that will be edited
     * @return A List of Strings that map to fields for that entity
     */
    protected abstract List<String> getEditableFields(long id);

    public boolean canEdit(long id){
        return !getEditableFields(id).isEmpty();
    }

    /**
     * @param id    The id of the entity to be partially updated
     * @param patch The updates to be performed on the referred entity
     * @return Whether the current user is allowed to perform the update described by the patch
     */
    public boolean allowPatch(long id, JsonPatch patch) {
        return patcher.validatePatch(patch, getEditableFields(id));
    }

    public void authorizePatch(long id, JsonPatch patch) {
        authorize(allowPatch(id, patch));
    }
}
