package xyz.vandijck.safety.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.entity.category.Category;
import xyz.vandijck.safety.entity.category.CategoryInput;
import xyz.vandijck.safety.entity.category.CategoryRepresentationModelAssembler;
import xyz.vandijck.safety.entity.category.CategoryRepresentationModelAssembler.CategoryModel;
import xyz.vandijck.safety.service.CategoryService;


@AllArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final CategoryRepresentationModelAssembler categoryAssembler;


    @RequestMapping(method = RequestMethod.GET)
    CollectionModel<CategoryModel> all() {
        return categoryAssembler.toCollectionModel(categoryService.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    CategoryModel get(@PathVariable("id") long id) {
        return categoryAssembler.toModel(categoryService.findById(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Void> create(@Valid @RequestBody CategoryInput categoryInput) {
        Category category = categoryService.create(categoryInput);

        return ResponseEntity.created(
                linkTo(methodOn(CategoryController.class).get(category.getId())).toUri()).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    ResponseEntity<Void> update(@PathVariable("id") long id, @Valid @RequestBody CategoryInput categoryInput) {
        categoryService.update(id, categoryInput);

        return ResponseEntity.noContent()
                .location(linkTo(methodOn(CategoryController.class).get(id)).toUri()).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> delete(@PathVariable("id") long id) {
        categoryService.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
