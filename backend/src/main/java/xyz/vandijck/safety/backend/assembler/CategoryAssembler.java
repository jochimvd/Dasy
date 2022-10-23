package xyz.vandijck.safety.backend.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.assembler.link.GuardedLink;
import xyz.vandijck.safety.backend.controller.CategoryController;
import xyz.vandijck.safety.backend.dto.CategoryDto;
import xyz.vandijck.safety.backend.entity.Category;
import xyz.vandijck.safety.backend.policy.CategoryPolicy;
import xyz.vandijck.safety.backend.request.CategorySearchRequest;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.service.ConsequenceService;
import xyz.vandijck.safety.backend.service.SeverityService;
import xyz.vandijck.safety.backend.service.UserService;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembler that converts Categorys into CategoryDto's and vice versa
 * Also provides access to all allowed links
 */
@Component
public class CategoryAssembler extends BaseAssembler<Category, CategoryDto> {

    private final CategoryPolicy categoryPolicy;

    private final ConsequenceService consequenceService;

    private final SeverityService severityService;

    private final ConsequenceAssembler consequenceAssembler;

    private final SeverityAssembler severityAssembler;

    @Autowired
    public CategoryAssembler(CategoryPolicy categoryPolicy, UserService userService,
                             ConsequenceService consequenceService, ConsequenceAssembler consequenceAssembler,
                             SeverityService severityService, SeverityAssembler severityAssembler,
                             ObjectMapper objectMapper, ModelMapper modelMapper) {
        super(categoryPolicy, CategoryDto.class, userService, objectMapper, modelMapper);
        this.categoryPolicy = categoryPolicy;
        this.consequenceService = consequenceService;
        this.consequenceAssembler = consequenceAssembler;
        this.severityService = severityService;
        this.severityAssembler = severityAssembler;
    }

    @Override
    protected Stream<GuardedLink> entityLinks(CategoryDto categoryDto, Map<String, Object> extraInfo) {
        return Stream.of(
                GuardedLink.guard(
                        categoryPolicy.allowGet(categoryDto.getId()),
                        WebMvcLinkBuilder.linkTo(methodOn(CategoryController.class).findOne(categoryDto.getId())).withSelfRel()
                ),
                GuardedLink.guard(
                        categoryPolicy.allowPut(categoryDto),
                        linkTo(methodOn(CategoryController.class).update(categoryDto, categoryDto.getId())).withRel("put")
                ),
                // patch link is shown if the user can actually edit one or more fields on this category
                GuardedLink.guard(
                        categoryPolicy.canEdit(categoryDto.getId()),
                        linkTo(methodOn(CategoryController.class).patch(new JsonPatch(Collections.emptyList()), categoryDto.getId())).withRel("patch")
                ),
                GuardedLink.guard(
                        categoryPolicy.allowDelete(categoryDto.getId()),
                        linkTo(methodOn(CategoryController.class).archiveOrDelete(new DeleteRequest(), categoryDto.getId())).withRel("delete")
                ),
                GuardedLink.guard(
                        categoryPolicy.allowGet(categoryDto.getId()),
                        linkTo(methodOn(CategoryController.class).getEditableFields(categoryDto.getId())).withRel("editableFields")
                ),
                GuardedLink.guard(
                        categoryPolicy.allowGetAll(new CategorySearchRequest()),
                        linkTo(methodOn(CategoryController.class).findAll(new CategorySearchRequest())).withRel("categories")
                )
        );
    }

    @Override
    protected Stream<GuardedLink> collectionLinks() {
        return Stream.concat(super.collectionLinks(),
                Stream.of(
                        GuardedLink.guard(
                                categoryPolicy.allowPost(new CategoryDto()),
                                linkTo(methodOn(CategoryController.class).create(new CategoryDto())).withRel("post")
                        )));
    }

    @Override
    protected WebMvcLinkBuilder findAllLinkBuilder() {
        return linkTo(methodOn(CategoryController.class).findAll(new CategorySearchRequest()));
    }

    @Override
    public Category convertToEntity(CategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);
        category.setConsequence(consequenceService.findById(categoryDto.getConsequence().getId()));
        category.setSeverity(severityService.findById(categoryDto.getSeverity().getId()));
        return category;
    }

    @Override
    public CategoryDto convertToDto(Category category) {
        CategoryDto categoryDto = modelMapper.map(category, CategoryDto.class);
        categoryDto.setConsequence(consequenceAssembler.convertToDto(category.getConsequence()));
        categoryDto.setSeverity(severityAssembler.convertToDto(category.getSeverity()));
        return categoryDto;
    }

}
