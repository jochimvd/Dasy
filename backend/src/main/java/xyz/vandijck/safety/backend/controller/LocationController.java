package xyz.vandijck.safety.backend.controller;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.backend.assembler.LocationAssembler;
import xyz.vandijck.safety.backend.controller.exceptions.IdMismatchException;
import xyz.vandijck.safety.backend.dto.LocationDto;
import xyz.vandijck.safety.backend.entity.Location;
import xyz.vandijck.safety.backend.policy.LocationPolicy;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.LocationSearchRequest;
import xyz.vandijck.safety.backend.service.LocationService;
import xyz.vandijck.safety.backend.service.SearchDTO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Location controller, handles HTTP requests related to locations
 */
@RestController
@RequestMapping("/locations")
public class LocationController implements UpdatableEntityController<LocationDto, LocationSearchRequest> {

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationAssembler locationAssembler;

    @Autowired
    private LocationPolicy locationPolicy;

    /**
     * Finds all locations that match the search parameters in LocationSearchRequest. In a normal HTTP request these parameters
     * wil come from all the parameters from a query link, eg "/locations?name=location1" will look for all the locations
     * with name "location1". See the openAPI specification for all the legal parameters.
     *
     * @param request A wrapper object containing all the needed information to build a query
     * @return A CollectionModel containing all the locations that match the search parameters
     */
    @GetMapping
    public CollectionModel<EntityModel<LocationDto>> findAll(@Valid LocationSearchRequest request) {
        locationPolicy.authorizeGetAll(request);

        SearchDTO<Location> dto = locationService.findAll(request);
        return locationAssembler.toCollectionModelFromSearch(dto, request);
    }


    /**
     * Find a location by its id
     *
     * @param id The id you want to search for
     * @return An entitymodel of the location that matches that id.
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<LocationDto> findOne(@PathVariable long id) {
        locationPolicy.authorizeGet(id);
        return locationAssembler.toModel(locationService.findById(id));
    }

    /**
     * Create a location
     *
     * @param locationDto The information for the new location
     * @return A JSON representation of the newly created location
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<LocationDto> create(@RequestBody @Valid LocationDto locationDto) {
        locationPolicy.authorizePost(locationDto);
        return locationAssembler.toModel(locationService.save(locationAssembler.convertToEntity(locationDto)));
    }

    /**
     * Deletes a location if no observations are associated with it, or archives it when it does.
     *
     * @param request The delete request with all the needed information to validate the user.
     * @param id      The id of the location that will be deleted
     * @return An empty ResponseEntity on success
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> archiveOrDelete(@RequestBody @Valid DeleteRequest request, @PathVariable long id) {
        locationPolicy.authorizeDelete(id);
        locationService.archiveOrDeleteById(request, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Handles the put HTTP request for a location
     *
     * @param locationDto The new information for the location
     * @param id      The id of the location you want to update
     * @return A representation of the location with the updated information
     */
    @PutMapping(value = "/{id}")
    public EntityModel<LocationDto> update(@RequestBody @Valid LocationDto locationDto, @PathVariable long id) {
        if (locationDto.getId() != id) {
            throw new IdMismatchException();
        }
        locationPolicy.authorizePut(locationDto);
        Location newLocation = locationAssembler.convertToEntity(locationDto);

        return locationAssembler.toModel(locationService.update(newLocation));
    }

    /**
     * Handles the patch request for a location
     *
     * @param patch The new information for the location, fields are allowed to be left unspecified, these will then not be
     *              updated
     * @param id    The id of the location you want to update
     * @return A representation of the location with the new information
     */
    @PatchMapping(value = "/{id}")
    public EntityModel<LocationDto> patch(@RequestBody @Valid JsonPatch patch, @PathVariable long id) {
        locationPolicy.authorizePatch(id, patch);
        return locationAssembler.toModel(locationService.patch(patch, id));
    }

    /**
     * Returns a list of fields the user has the permissions to edit for this entity
     *
     * @param id The id of the location
     * @return A list of fields the user can edit
     */
    @GetMapping(value = "/{id}/edit")
    public ResponseEntity<Map<String, List<String>>> getEditableFields(@PathVariable long id) {
        locationPolicy.authorizeGet(id);
        return new ResponseEntity<>(
                Map.of("editableFields", locationPolicy.getEditableFields(id)),
                HttpStatus.OK);
    }
}