package xyz.vandijck.safety.backend.controller;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.backend.assembler.ConsequenceAssembler;
import xyz.vandijck.safety.backend.controller.exceptions.IdMismatchException;
import xyz.vandijck.safety.backend.dto.ConsequenceDto;
import xyz.vandijck.safety.backend.entity.Consequence;
import xyz.vandijck.safety.backend.policy.ConsequencePolicy;
import xyz.vandijck.safety.backend.request.ConsequenceSearchRequest;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.service.ConsequenceService;
import xyz.vandijck.safety.backend.service.SearchDTO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Consequence controller, handles HTTP requests related to consequences
 */
@RestController
@RequestMapping("/consequences")
public class ConsequenceController implements UpdatableEntityController<ConsequenceDto, ConsequenceSearchRequest> {

    @Autowired
    private ConsequenceService consequenceService;

    @Autowired
    private ConsequenceAssembler consequenceAssembler;

    @Autowired
    private ConsequencePolicy consequencePolicy;

    /**
     * Finds all consequences that match the search parameters in ConsequenceSearchRequest. In a normal HTTP request these parameters
     * wil come from all the parameters from a query link, eg "/consequences?name=consequence1" will look for all the consequences
     * with name "consequence1". See the openAPI specification for all the legal parameters.
     *
     * @param request A wrapper object containing all the needed information to build a query
     * @return A CollectionModel containing all the consequences that match the search parameters
     */
    @GetMapping
    public CollectionModel<EntityModel<ConsequenceDto>> findAll(@Valid ConsequenceSearchRequest request) {
        consequencePolicy.authorizeGetAll(request);

        SearchDTO<Consequence> dto = consequenceService.findAll(request);
        return consequenceAssembler.toCollectionModelFromSearch(dto, request);
    }


    /**
     * Find a consequence by its id
     *
     * @param id The id you want to search for
     * @return An entitymodel of the consequence that matches that id.
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<ConsequenceDto> findOne(@PathVariable long id) {
        consequencePolicy.authorizeGet(id);
        return consequenceAssembler.toModel(consequenceService.findById(id));
    }

    /**
     * Create a consequence
     *
     * @param consequenceDto The information for the new consequence
     * @return A JSON representation of the newly created consequence
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<ConsequenceDto> create(@RequestBody @Valid ConsequenceDto consequenceDto) {
        consequencePolicy.authorizePost(consequenceDto);
        return consequenceAssembler.toModel(consequenceService.save(consequenceAssembler.convertToEntity(consequenceDto)));
    }

    /**
     * Deletes a consequence if no observations are associated with it, or archives it when it does.
     *
     * @param request The delete request with all the needed information to validate the user.
     * @param id      The id of the consequence that will be deleted
     * @return An empty ResponseEntity on success
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> archiveOrDelete(@RequestBody @Valid DeleteRequest request, @PathVariable long id) {
        consequencePolicy.authorizeDelete(id);
        consequenceService.archiveOrDeleteById(request, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Handles the put HTTP request for a consequence
     *
     * @param consequenceDto The new information for the consequence
     * @param id      The id of the consequence you want to update
     * @return A representation of the consequence with the updated information
     */
    @PutMapping(value = "/{id}")
    public EntityModel<ConsequenceDto> update(@RequestBody @Valid ConsequenceDto consequenceDto, @PathVariable long id) {
        if (consequenceDto.getId() != id) {
            throw new IdMismatchException();
        }
        consequencePolicy.authorizePut(consequenceDto);
        Consequence newConsequence = consequenceAssembler.convertToEntity(consequenceDto);

        return consequenceAssembler.toModel(consequenceService.update(newConsequence));
    }

    /**
     * Handles the patch request for a consequence
     *
     * @param patch The new information for the consequence, fields are allowed to be left unspecified, these will then not be
     *              updated
     * @param id    The id of the consequence you want to update
     * @return A representation of the consequence with the new information
     */
    @PatchMapping(value = "/{id}")
    public EntityModel<ConsequenceDto> patch(@RequestBody @Valid JsonPatch patch, @PathVariable long id) {
        consequencePolicy.authorizePatch(id, patch);
        return consequenceAssembler.toModel(consequenceService.patch(patch, id));
    }

    /**
     * Returns a list of fields the user has the permissions to edit for this entity
     *
     * @param id The id of the consequence
     * @return A list of fields the user can edit
     */
    @GetMapping(value = "/{id}/edit")
    public ResponseEntity<Map<String, List<String>>> getEditableFields(@PathVariable long id) {
        consequencePolicy.authorizeGet(id);
        return new ResponseEntity<>(
                Map.of("editableFields", consequencePolicy.getEditableFields(id)),
                HttpStatus.OK);
    }
}