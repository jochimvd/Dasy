package xyz.vandijck.safety.backend.controller;

import xyz.vandijck.safety.backend.request.SearchRequest;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UpdatableEntityController<E, T extends SearchRequest> extends BaseEntityController<E, T> {
    /**
     * Create an entity
     *
     * @param dto The information for the new entity
     * @return An {@link EntityModel} representation of the newly created entity
     */
    EntityModel<E> create(E dto);

    /**
     * Handles the patch request for an entity
     *
     * @param patch The new information for the entity, fields are allowed to be left unspecified, these will then not be
     *              updated
     * @param id    The id of the entity you want to update
     * @return An {@link EntityModel} representation of the entity with the new information
     */
    EntityModel<E> patch(JsonPatch patch, long id);

    /**
     * Handles the put HTTP request for an entity
     *
     * @param dto The new information for the entity
     * @param id  The id of the entity you want to update
     * @return An {@link EntityModel} representation of the entity with the updated information
     */
    EntityModel<E> update(E dto, long id);

    ResponseEntity<Map<String, List<String>>> getEditableFields(long id);
}
