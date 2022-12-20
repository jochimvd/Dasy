package xyz.vandijck.safety.backend.controller;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.backend.assembler.TypeAssembler;
import xyz.vandijck.safety.backend.controller.exceptions.IdMismatchException;
import xyz.vandijck.safety.backend.dto.TypeDto;
import xyz.vandijck.safety.backend.entity.Type;
import xyz.vandijck.safety.backend.policy.TypePolicy;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.TypeSearchRequest;
import xyz.vandijck.safety.backend.service.SearchDTO;
import xyz.vandijck.safety.backend.service.TypeService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Type controller, handles HTTP requests related to types
 */
@RestController
@RequestMapping("/types")
public class TypeController implements UpdatableEntityController<TypeDto, TypeSearchRequest> {

    @Autowired
    private TypeService typeService;

    @Autowired
    private TypeAssembler typeAssembler;

    @Autowired
    private TypePolicy typePolicy;

    /**
     * Finds all types that match the search parameters in TypeSearchRequest. In a normal HTTP request these parameters
     * wil come from all the parameters from a query link, eg "/types?name=type1" will look for all the types
     * with name "type1". See the openAPI specification for all the legal parameters.
     *
     * @param request A wrapper object containing all the needed information to build a query
     * @return A CollectionModel containing all the types that match the search parameters
     */
    @GetMapping
    public CollectionModel<EntityModel<TypeDto>> findAll(@Valid TypeSearchRequest request) {
        typePolicy.authorizeGetAll(request);

        SearchDTO<Type> dto = typeService.findAll(request);
        return typeAssembler.toCollectionModelFromSearch(dto, request);
    }


    /**
     * Find a type by its id
     *
     * @param id The id you want to search for
     * @return An entitymodel of the type that matches that id.
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TypeDto> findOne(@PathVariable long id) {
        typePolicy.authorizeGet(id);
        return typeAssembler.toModel(typeService.findById(id));
    }

    /**
     * Create a type
     *
     * @param typeDto The information for the new type
     * @return A JSON representation of the newly created type
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<TypeDto> create(@RequestBody @Valid TypeDto typeDto) {
        typePolicy.authorizePost(typeDto);
        return typeAssembler.toModel(typeService.save(typeAssembler.convertToEntity(typeDto)));
    }

    /**
     * Deletes a type if no observations are associated with it, or archives it when it does.
     *
     * @param request The delete request with all the needed information to validate the user.
     * @param id      The id of the type that will be deleted
     * @return An empty ResponseEntity on success
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> archiveOrDelete(@RequestBody(required = false) @Valid DeleteRequest request, @PathVariable long id) {
        typePolicy.authorizeDelete(id);
        typeService.archiveOrDeleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Handles the put HTTP request for a type
     *
     * @param typeDto The new information for the type
     * @param id      The id of the type you want to update
     * @return A representation of the type with the updated information
     */
    @PutMapping(value = "/{id}")
    public EntityModel<TypeDto> update(@RequestBody @Valid TypeDto typeDto, @PathVariable long id) {
        if (typeDto.getId() != id) {
            throw new IdMismatchException();
        }
        typePolicy.authorizePut(typeDto);
        Type newType = typeAssembler.convertToEntity(typeDto);

        return typeAssembler.toModel(typeService.update(newType));
    }

    /**
     * Handles the patch request for a type
     *
     * @param patch The new information for the type, fields are allowed to be left unspecified, these will then not be
     *              updated
     * @param id    The id of the type you want to update
     * @return A representation of the type with the new information
     */
    @PatchMapping(value = "/{id}")
    public EntityModel<TypeDto> patch(@RequestBody @Valid JsonPatch patch, @PathVariable long id) {
        typePolicy.authorizePatch(id, patch);
        return typeAssembler.toModel(typeService.patch(patch, id));
    }

    /**
     * Returns a list of fields the user has the permissions to edit for this entity
     *
     * @param id The id of the type
     * @return A list of fields the user can edit
     */
    @GetMapping(value = "/{id}/edit")
    public ResponseEntity<Map<String, List<String>>> getEditableFields(@PathVariable long id) {
        typePolicy.authorizeGet(id);
        return new ResponseEntity<>(
                Map.of("editableFields", typePolicy.getEditableFields(id)),
                HttpStatus.OK);
    }
}