package xyz.vandijck.safety.backend.service;

import com.github.fge.jsonpatch.JsonPatch;
import xyz.vandijck.safety.backend.controller.exceptions.BadPatchException;
import xyz.vandijck.safety.backend.controller.exceptions.BadRequestException;
import xyz.vandijck.safety.backend.entity.UniqueEntity;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.SearchRequest;

import java.util.List;

public interface EntityService<E extends UniqueEntity, T extends SearchRequest>
{
    /**
     * Finds all entities with type {@link E}.
     * @return List of the entities.
     */
    List<E> findAll();

    /**
     * Finds all the entities that match the search parameters represented in the request
     * @param request All the search parameters
     * @return A SearchDTO containing all the entities that match the parameters
     */
    SearchDTO<E> findAll(T request);

    /**
     * Finds the entity with type {@link E}.
     * @param id Id of the entity.
     * @return The entity.
     */
    E findById(long id);

    /**
     * Saves an entity of type {@link E}.
     * @param e The entity to be saved.
     * @return The saved entity.
     */
    E save(E e);

    /**
     * Updates an entity of class {@link E}
     * @param e The entity to be updated
     * @return The updated entity
     */
    default E update(E e){
        getByIdOrThrow(e.getId());
        return save(e);
    }

    /**
     * Deletes an entity of type {@link E}.
     * @param id Id of the entity to delete.
     */
    void deleteById(long id);

    /**
     * Patch an entity with type {@link E}.
     * @param patch The patch object containing the fields to be patched and the operations.
     * @param id The id of the entity to be patched.
     * @throws BadPatchException When the JsonPatch is invalid
     * @return The patched entity.
     */
    E patch(JsonPatch patch, long id) throws BadPatchException;

    /**
     * Find the entity with the given id, or throw an Exception if it is not present (never existed or archived)
     * @param id The id of the entity
     * @return The found entity if present and not archived
     * @throws BadRequestException When not present or archived
     */
    E getByIdOrThrow(long id) throws BadRequestException;

    /**
     * Archives or deletes an entity with type {@link E} if the password given in the request is correct.
     *
     * @param id Id of the entity to delete or archive
     * @return true when actually deleted, else the entity was archived
     */
    boolean archiveOrDeleteById(DeleteRequest request, long id);

    /**
     * Archives or deletes the entity with the given id
     * Should only be called after validating the delete request
     *
     * @param id The id of the entity
     * @return true when actually deleted, else the entity was archived
     */
    default boolean archiveOrDeleteById(long id) {
        deleteById(id);
        return true;
    }

}
