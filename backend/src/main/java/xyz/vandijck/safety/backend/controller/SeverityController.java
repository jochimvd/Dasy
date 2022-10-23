package xyz.vandijck.safety.backend.controller;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.backend.assembler.SeverityAssembler;
import xyz.vandijck.safety.backend.controller.exceptions.IdMismatchException;
import xyz.vandijck.safety.backend.dto.SeverityDto;
import xyz.vandijck.safety.backend.entity.Severity;
import xyz.vandijck.safety.backend.policy.SeverityPolicy;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.SeveritySearchRequest;
import xyz.vandijck.safety.backend.service.SearchDTO;
import xyz.vandijck.safety.backend.service.SeverityService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Severity controller, handles HTTP requests related to severities
 */
@RestController
@RequestMapping("/severities")
public class SeverityController implements UpdatableEntityController<SeverityDto, SeveritySearchRequest> {

    @Autowired
    private SeverityService severityService;

    @Autowired
    private SeverityAssembler severityAssembler;

    @Autowired
    private SeverityPolicy severityPolicy;

    /**
     * Finds all severities that match the search parameters in SeveritySearchRequest. In a normal HTTP request these parameters
     * wil come from all the parameters from a query link, eg "/severities?name=severity1" will look for all the severities
     * with name "severity1". See the openAPI specification for all the legal parameters.
     *
     * @param request A wrapper object containing all the needed information to build a query
     * @return A CollectionModel containing all the severities that match the search parameters
     */
    @GetMapping
    public CollectionModel<EntityModel<SeverityDto>> findAll(@Valid SeveritySearchRequest request) {
        severityPolicy.authorizeGetAll(request);

        SearchDTO<Severity> dto = severityService.findAll(request);
        return severityAssembler.toCollectionModelFromSearch(dto, request);
    }


    /**
     * Find a severity by its id
     *
     * @param id The id you want to search for
     * @return An entitymodel of the severity that matches that id.
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<SeverityDto> findOne(@PathVariable long id) {
        severityPolicy.authorizeGet(id);
        return severityAssembler.toModel(severityService.findById(id));
    }

    /**
     * Create a severity
     *
     * @param severityDto The information for the new severity
     * @return A JSON representation of the newly created severity
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<SeverityDto> create(@RequestBody @Valid SeverityDto severityDto) {
        severityPolicy.authorizePost(severityDto);
        return severityAssembler.toModel(severityService.save(severityAssembler.convertToEntity(severityDto)));
    }

    /**
     * Deletes a severity if no observations are associated with it, or archives it when it does.
     *
     * @param request The delete request with all the needed information to validate the user.
     * @param id      The id of the severity that will be deleted
     * @return An empty ResponseEntity on success
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> archiveOrDelete(@RequestBody @Valid DeleteRequest request, @PathVariable long id) {
        severityPolicy.authorizeDelete(id);
        severityService.archiveOrDeleteById(request, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Handles the put HTTP request for a severity
     *
     * @param severityDto The new information for the severity
     * @param id      The id of the severity you want to update
     * @return A representation of the severity with the updated information
     */
    @PutMapping(value = "/{id}")
    public EntityModel<SeverityDto> update(@RequestBody @Valid SeverityDto severityDto, @PathVariable long id) {
        if (severityDto.getId() != id) {
            throw new IdMismatchException();
        }
        severityPolicy.authorizePut(severityDto);
        Severity newSeverity = severityAssembler.convertToEntity(severityDto);

        return severityAssembler.toModel(severityService.update(newSeverity));
    }

    /**
     * Handles the patch request for a severity
     *
     * @param patch The new information for the severity, fields are allowed to be left unspecified, these will then not be
     *              updated
     * @param id    The id of the severity you want to update
     * @return A representation of the severity with the new information
     */
    @PatchMapping(value = "/{id}")
    public EntityModel<SeverityDto> patch(@RequestBody @Valid JsonPatch patch, @PathVariable long id) {
        severityPolicy.authorizePatch(id, patch);
        return severityAssembler.toModel(severityService.patch(patch, id));
    }

    /**
     * Returns a list of fields the user has the permissions to edit for this entity
     *
     * @param id The id of the severity
     * @return A list of fields the user can edit
     */
    @GetMapping(value = "/{id}/edit")
    public ResponseEntity<Map<String, List<String>>> getEditableFields(@PathVariable long id) {
        severityPolicy.authorizeGet(id);
        return new ResponseEntity<>(
                Map.of("editableFields", severityPolicy.getEditableFields(id)),
                HttpStatus.OK);
    }
}