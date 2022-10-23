package xyz.vandijck.safety.backend.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.SearchRequest;

/**
 * Generic controller interface for a specific endpoint.
 *
 * @param <E> The endpoint dto type.
 * @param <T> The search request belonging to the endpoint.
 */
public interface
BaseEntityController<E, T extends SearchRequest> {

    /**
     * Finds all entity representations that match the search parameters in search request {@link T}. See the openAPI specification
     * for the specific entity to see all the legal parameters.
     *
     * @param request A wrapper object containing all the needed information to build a query.
     * @return A {@link CollectionModel} containing all the entity representations that match the search parameters.
     */
    CollectionModel<EntityModel<E>> findAll(T request);


    /**
     * Find an entity by its id
     *
     * @param id The id you want to search for
     * @return An {@link EntityModel} of the entity that matches that id.
     */
    EntityModel<E> findOne(long id);

    /**
     * Deletes or archives an entity based on specific entity business logic.
     *
     * @param request The delete request with all the needed information to validate the user.
     * @param id      The id of the entity that will be deleted
     * @return An empty {@link ResponseEntity} on success
     */
    ResponseEntity<String> archiveOrDelete(DeleteRequest request, long id);
}
