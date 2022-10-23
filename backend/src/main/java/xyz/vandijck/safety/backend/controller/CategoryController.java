package xyz.vandijck.safety.backend.controller;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.backend.assembler.CategoryAssembler;
import xyz.vandijck.safety.backend.controller.exceptions.IdMismatchException;
import xyz.vandijck.safety.backend.dto.CategoryDto;
import xyz.vandijck.safety.backend.entity.Category;
import xyz.vandijck.safety.backend.policy.CategoryPolicy;
import xyz.vandijck.safety.backend.request.CategorySearchRequest;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.service.CategoryService;
import xyz.vandijck.safety.backend.service.SearchDTO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Category controller, handles HTTP requests related to categories
 */
@RestController
@RequestMapping("/categories")
public class CategoryController implements UpdatableEntityController<CategoryDto, CategorySearchRequest> {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryAssembler categoryAssembler;

    @Autowired
    private CategoryPolicy categoryPolicy;

    /**
     * Finds all categories that match the search parameters in CategorySearchRequest. In a normal HTTP request these parameters
     * wil come from all the parameters from a query link, eg "/categories?name=category1" will look for all the categories
     * with name "category1". See the openAPI specification for all the legal parameters.
     *
     * @param request A wrapper object containing all the needed information to build a query
     * @return A CollectionModel containing all the categories that match the search parameters
     */
    @GetMapping
    public CollectionModel<EntityModel<CategoryDto>> findAll(@Valid CategorySearchRequest request) {
        categoryPolicy.authorizeGetAll(request);

        SearchDTO<Category> dto = categoryService.findAll(request);
        return categoryAssembler.toCollectionModelFromSearch(dto, request);
    }


    /**
     * Find a category by its id
     *
     * @param id The id you want to search for
     * @return An entitymodel of the category that matches that id.
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<CategoryDto> findOne(@PathVariable long id) {
        categoryPolicy.authorizeGet(id);
        return categoryAssembler.toModel(categoryService.findById(id));
    }

    /**
     * Create a category
     *
     * @param categoryDto The information for the new category
     * @return A JSON representation of the newly created category
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<CategoryDto> create(@RequestBody @Valid CategoryDto categoryDto) {
        categoryPolicy.authorizePost(categoryDto);
        return categoryAssembler.toModel(categoryService.save(categoryAssembler.convertToEntity(categoryDto)));
    }

    /**
     * Deletes a category if no observations are associated with it, or archives it when it does.
     *
     * @param request The delete request with all the needed information to validate the user.
     * @param id      The id of the category that will be deleted
     * @return An empty ResponseEntity on success
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> archiveOrDelete(@RequestBody @Valid DeleteRequest request, @PathVariable long id) {
        categoryPolicy.authorizeDelete(id);
        categoryService.archiveOrDeleteById(request, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Handles the put HTTP request for a category
     *
     * @param categoryDto The new information for the category
     * @param id      The id of the category you want to update
     * @return A representation of the category with the updated information
     */
    @PutMapping(value = "/{id}")
    public EntityModel<CategoryDto> update(@RequestBody @Valid CategoryDto categoryDto, @PathVariable long id) {
        if (categoryDto.getId() != id) {
            throw new IdMismatchException();
        }
        categoryPolicy.authorizePut(categoryDto);
        Category newCategory = categoryAssembler.convertToEntity(categoryDto);

        return categoryAssembler.toModel(categoryService.update(newCategory));
    }

    /**
     * Handles the patch request for a category
     *
     * @param patch The new information for the category, fields are allowed to be left unspecified, these will then not be
     *              updated
     * @param id    The id of the category you want to update
     * @return A representation of the category with the new information
     */
    @PatchMapping(value = "/{id}")
    public EntityModel<CategoryDto> patch(@RequestBody @Valid JsonPatch patch, @PathVariable long id) {
        categoryPolicy.authorizePatch(id, patch);
        return categoryAssembler.toModel(categoryService.patch(patch, id));
    }

    /**
     * Returns a list of fields the user has the permissions to edit for this entity
     *
     * @param id The id of the category
     * @return A list of fields the user can edit
     */
    @GetMapping(value = "/{id}/edit")
    public ResponseEntity<Map<String, List<String>>> getEditableFields(@PathVariable long id) {
        categoryPolicy.authorizeGet(id);
        return new ResponseEntity<>(
                Map.of("editableFields", categoryPolicy.getEditableFields(id)),
                HttpStatus.OK);
    }
}