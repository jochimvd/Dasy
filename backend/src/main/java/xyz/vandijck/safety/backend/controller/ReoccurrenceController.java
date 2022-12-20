package xyz.vandijck.safety.backend.controller;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.backend.assembler.ReoccurrenceAssembler;
import xyz.vandijck.safety.backend.controller.exceptions.IdMismatchException;
import xyz.vandijck.safety.backend.dto.ReoccurrenceDto;
import xyz.vandijck.safety.backend.entity.Reoccurrence;
import xyz.vandijck.safety.backend.policy.ReoccurrencePolicy;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.ReoccurrenceSearchRequest;
import xyz.vandijck.safety.backend.service.ReoccurrenceService;
import xyz.vandijck.safety.backend.service.SearchDTO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Reoccurrence controller, handles HTTP requests related to reoccurrences
 */
@RestController
@RequestMapping("/reoccurrences")
public class ReoccurrenceController implements UpdatableEntityController<ReoccurrenceDto, ReoccurrenceSearchRequest> {

    @Autowired
    private ReoccurrenceService reoccurrenceService;

    @Autowired
    private ReoccurrenceAssembler reoccurrenceAssembler;

    @Autowired
    private ReoccurrencePolicy reoccurrencePolicy;

    /**
     * Finds all reoccurrences that match the search parameters in ReoccurrenceSearchRequest. In a normal HTTP request these parameters
     * wil come from all the parameters from a query link, eg "/reoccurrences?name=reoccurrence1" will look for all the reoccurrences
     * with name "reoccurrence1". See the openAPI specification for all the legal parameters.
     *
     * @param request A wrapper object containing all the needed information to build a query
     * @return A CollectionModel containing all the reoccurrences that match the search parameters
     */
    @GetMapping
    public CollectionModel<EntityModel<ReoccurrenceDto>> findAll(@Valid ReoccurrenceSearchRequest request) {
        reoccurrencePolicy.authorizeGetAll(request);

        SearchDTO<Reoccurrence> dto = reoccurrenceService.findAll(request);
        return reoccurrenceAssembler.toCollectionModelFromSearch(dto, request);
    }


    /**
     * Find a reoccurrence by its id
     *
     * @param id The id you want to search for
     * @return An entitymodel of the reoccurrence that matches that id.
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<ReoccurrenceDto> findOne(@PathVariable long id) {
        reoccurrencePolicy.authorizeGet(id);
        return reoccurrenceAssembler.toModel(reoccurrenceService.findById(id));
    }

    /**
     * Create a reoccurrence
     *
     * @param reoccurrenceDto The information for the new reoccurrence
     * @return A JSON representation of the newly created reoccurrence
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<ReoccurrenceDto> create(@RequestBody @Valid ReoccurrenceDto reoccurrenceDto) {
        reoccurrencePolicy.authorizePost(reoccurrenceDto);
        return reoccurrenceAssembler.toModel(reoccurrenceService.save(reoccurrenceAssembler.convertToEntity(reoccurrenceDto)));
    }

    /**
     * Deletes a reoccurrence if no observations are associated with it, or archives it when it does.
     *
     * @param request The delete request with all the needed information to validate the user.
     * @param id      The id of the reoccurrence that will be deleted
     * @return An empty ResponseEntity on success
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> archiveOrDelete(@RequestBody(required = false) @Valid DeleteRequest request, @PathVariable long id) {
        reoccurrencePolicy.authorizeDelete(id);
        reoccurrenceService.archiveOrDeleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Handles the put HTTP request for a reoccurrence
     *
     * @param reoccurrenceDto The new information for the reoccurrence
     * @param id      The id of the reoccurrence you want to update
     * @return A representation of the reoccurrence with the updated information
     */
    @PutMapping(value = "/{id}")
    public EntityModel<ReoccurrenceDto> update(@RequestBody @Valid ReoccurrenceDto reoccurrenceDto, @PathVariable long id) {
        if (reoccurrenceDto.getId() != id) {
            throw new IdMismatchException();
        }
        reoccurrencePolicy.authorizePut(reoccurrenceDto);
        Reoccurrence newReoccurrence = reoccurrenceAssembler.convertToEntity(reoccurrenceDto);

        return reoccurrenceAssembler.toModel(reoccurrenceService.update(newReoccurrence));
    }

    /**
     * Handles the patch request for a reoccurrence
     *
     * @param patch The new information for the reoccurrence, fields are allowed to be left unspecified, these will then not be
     *              updated
     * @param id    The id of the reoccurrence you want to update
     * @return A representation of the reoccurrence with the new information
     */
    @PatchMapping(value = "/{id}")
    public EntityModel<ReoccurrenceDto> patch(@RequestBody @Valid JsonPatch patch, @PathVariable long id) {
        reoccurrencePolicy.authorizePatch(id, patch);
        return reoccurrenceAssembler.toModel(reoccurrenceService.patch(patch, id));
    }

    /**
     * Returns a list of fields the user has the permissions to edit for this entity
     *
     * @param id The id of the reoccurrence
     * @return A list of fields the user can edit
     */
    @GetMapping(value = "/{id}/edit")
    public ResponseEntity<Map<String, List<String>>> getEditableFields(@PathVariable long id) {
        reoccurrencePolicy.authorizeGet(id);
        return new ResponseEntity<>(
                Map.of("editableFields", reoccurrencePolicy.getEditableFields(id)),
                HttpStatus.OK);
    }
}