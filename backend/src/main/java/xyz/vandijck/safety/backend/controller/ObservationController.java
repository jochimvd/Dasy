package xyz.vandijck.safety.backend.controller;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.backend.assembler.ObservationAssembler;
import xyz.vandijck.safety.backend.controller.exceptions.IdMismatchException;
import xyz.vandijck.safety.backend.dto.ObservationDto;
import xyz.vandijck.safety.backend.entity.Observation;
import xyz.vandijck.safety.backend.policy.ObservationPolicy;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.ObservationSearchRequest;
import xyz.vandijck.safety.backend.service.ObservationService;
import xyz.vandijck.safety.backend.service.SearchDTO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Observation controller, handles HTTP requests related to observations
 */
@RestController
@RequestMapping("/observations")
public class ObservationController implements UpdatableEntityController<ObservationDto, ObservationSearchRequest> {

    @Autowired
    private ObservationService observationService;

    @Autowired
    private ObservationAssembler observationAssembler;

    @Autowired
    private ObservationPolicy observationPolicy;

    /**
     * Finds all observations that match the search parameters in ObservationSearchRequest. In a normal HTTP request these parameters
     * wil come from all the parameters from a query link, eg "/observations?name=observation1" will look for all the observations
     * with name "observation1". See the openAPI specification for all the legal parameters.
     *
     * @param request A wrapper object containing all the needed information to build a query
     * @return A CollectionModel containing all the observations that match the search parameters
     */
    @GetMapping
    public CollectionModel<EntityModel<ObservationDto>> findAll(@Valid ObservationSearchRequest request) {
        observationPolicy.authorizeGetAll(request);

        SearchDTO<Observation> dto = observationService.findAll(request);
        return observationAssembler.toCollectionModelFromSearch(dto, request);
    }

    /**
     * Find an observation by its id
     *
     * @param id The id you want to search for
     * @return An entitymodel of the observation that matches that id.
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<ObservationDto> findOne(@PathVariable long id) {
        observationPolicy.authorizeGet(id);
        return observationAssembler.toModel(observationService.findById(id));
    }

    /**
     * Create an observation
     *
     * @param observationDto The information for the new observation
     * @return A JSON representation of the newly created observation
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<ObservationDto> create(@RequestBody @Valid ObservationDto observationDto) {
        observationPolicy.authorizePost(observationDto);
        return observationAssembler.toModel(observationService.save(observationAssembler.convertToEntity(observationDto)));
    }

    /**
     * Deletes an observation if no observations are associated with it, or archives it when it does.
     *
     * @param request The delete request with all the needed information to validate the user.
     * @param id      The id of the observation that will be deleted
     * @return An empty ResponseEntity on success
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> archiveOrDelete(@RequestBody @Valid DeleteRequest request, @PathVariable long id) {
        observationPolicy.authorizeDelete(id);
        observationService.archiveOrDeleteById(request, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Handles the put HTTP request for a observation
     *
     * @param observationDto The new information for the observation
     * @param id      The id of the observation you want to update
     * @return A representation of the observation with the updated information
     */
    @PutMapping(value = "/{id}")
    public EntityModel<ObservationDto> update(@RequestBody @Valid ObservationDto observationDto, @PathVariable long id) {
        if (observationDto.getId() != id) {
            throw new IdMismatchException();
        }
        observationPolicy.authorizePut(observationDto);
        Observation newObservation = observationAssembler.convertToEntity(observationDto);

        return observationAssembler.toModel(observationService.update(newObservation));
    }

    /**
     * Handles the patch request for a observation
     *
     * @param patch The new information for the observation, fields are allowed to be left unspecified, these will then not be
     *              updated
     * @param id    The id of the observation you want to update
     * @return A representation of the observation with the new information
     */
    @PatchMapping(value = "/{id}")
    public EntityModel<ObservationDto> patch(@RequestBody @Valid JsonPatch patch, @PathVariable long id) {
        observationPolicy.authorizePatch(id, patch);
        return observationAssembler.toModel(observationService.patch(patch, id));
    }

    /**
     * Returns a list of fields the user has the permissions to edit for this entity
     *
     * @param id The id of the observation
     * @return A list of fields the user can edit
     */
    @GetMapping(value = "/{id}/edit")
    public ResponseEntity<Map<String, List<String>>> getEditableFields(@PathVariable long id) {
        observationPolicy.authorizeGet(id);
        return new ResponseEntity<>(
                Map.of("editableFields", observationPolicy.getEditableFields(id)),
                HttpStatus.OK);
    }
}