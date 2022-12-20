package xyz.vandijck.safety.backend.controller;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.backend.assembler.SiteAssembler;
import xyz.vandijck.safety.backend.controller.exceptions.IdMismatchException;
import xyz.vandijck.safety.backend.dto.SiteDto;
import xyz.vandijck.safety.backend.entity.Site;
import xyz.vandijck.safety.backend.policy.SitePolicy;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.SiteSearchRequest;
import xyz.vandijck.safety.backend.service.SearchDTO;
import xyz.vandijck.safety.backend.service.SiteService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Site controller, handles HTTP requests related to sites
 */
@RestController
@RequestMapping("/sites")
public class SiteController implements UpdatableEntityController<SiteDto, SiteSearchRequest> {

    @Autowired
    private SiteService siteService;

    @Autowired
    private SiteAssembler siteAssembler;

    @Autowired
    private SitePolicy sitePolicy;

    /**
     * Finds all sites that match the search parameters in SiteSearchRequest. In a normal HTTP request these parameters
     * wil come from all the parameters from a query link, eg "/sites?name=site1" will look for all the sites
     * with name "site1". See the openAPI specification for all the legal parameters.
     *
     * @param request A wrapper object containing all the needed information to build a query
     * @return A CollectionModel containing all the sites that match the search parameters
     */
    @GetMapping
    public CollectionModel<EntityModel<SiteDto>> findAll(@Valid SiteSearchRequest request) {
        sitePolicy.authorizeGetAll(request);

        SearchDTO<Site> dto = siteService.findAll(request);
        return siteAssembler.toCollectionModelFromSearch(dto, request);
    }


    /**
     * Find a site by its id
     *
     * @param id The id you want to search for
     * @return An entitymodel of the site that matches that id.
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<SiteDto> findOne(@PathVariable long id) {
        sitePolicy.authorizeGet(id);
        return siteAssembler.toModel(siteService.findById(id));
    }

    /**
     * Create a site
     *
     * @param siteDto The information for the new site
     * @return A JSON representation of the newly created site
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<SiteDto> create(@RequestBody @Valid SiteDto siteDto) {
        sitePolicy.authorizePost(siteDto);
        return siteAssembler.toModel(siteService.save(siteAssembler.convertToEntity(siteDto)));
    }

    /**
     * Deletes a site if no observations are associated with it, or archives it when it does.
     *
     * @param request The delete request with all the needed information to validate the user.
     * @param id      The id of the site that will be deleted
     * @return An empty ResponseEntity on success
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> archiveOrDelete(@RequestBody(required = false) @Valid DeleteRequest request, @PathVariable long id) {
        sitePolicy.authorizeDelete(id);
        siteService.archiveOrDeleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Handles the put HTTP request for a site
     *
     * @param siteDto The new information for the site
     * @param id      The id of the site you want to update
     * @return A representation of the site with the updated information
     */
    @PutMapping(value = "/{id}")
    public EntityModel<SiteDto> update(@RequestBody @Valid SiteDto siteDto, @PathVariable long id) {
        if (siteDto.getId() != id) {
            throw new IdMismatchException();
        }
        sitePolicy.authorizePut(siteDto);
        Site newSite = siteAssembler.convertToEntity(siteDto);

        return siteAssembler.toModel(siteService.update(newSite));
    }

    /**
     * Handles the patch request for a site
     *
     * @param patch The new information for the site, fields are allowed to be left unspecified, these will then not be
     *              updated
     * @param id    The id of the site you want to update
     * @return A representation of the site with the new information
     */
    @PatchMapping(value = "/{id}")
    public EntityModel<SiteDto> patch(@RequestBody @Valid JsonPatch patch, @PathVariable long id) {
        sitePolicy.authorizePatch(id, patch);
        return siteAssembler.toModel(siteService.patch(patch, id));
    }

    /**
     * Returns a list of fields the user has the permissions to edit for this entity
     *
     * @param id The id of the site
     * @return A list of fields the user can edit
     */
    @GetMapping(value = "/{id}/edit")
    public ResponseEntity<Map<String, List<String>>> getEditableFields(@PathVariable long id) {
        sitePolicy.authorizeGet(id);
        return new ResponseEntity<>(
                Map.of("editableFields", sitePolicy.getEditableFields(id)),
                HttpStatus.OK);
    }
}