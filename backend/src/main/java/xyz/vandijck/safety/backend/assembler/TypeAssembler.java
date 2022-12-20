package xyz.vandijck.safety.backend.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.assembler.link.GuardedLink;
import xyz.vandijck.safety.backend.controller.TypeController;
import xyz.vandijck.safety.backend.dto.TypeDto;
import xyz.vandijck.safety.backend.entity.Type;
import xyz.vandijck.safety.backend.policy.TypePolicy;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.TypeSearchRequest;
import xyz.vandijck.safety.backend.service.UserService;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembler that converts Types into TypeDto's and vice versa
 * Also provides access to all allowed links
 */
@Component
public class TypeAssembler extends BaseAssembler<Type, TypeDto> {

    private final TypePolicy typePolicy;

    @Autowired
    public TypeAssembler(TypePolicy typePolicy, UserService userService,
                         ObjectMapper objectMapper, ModelMapper modelMapper) {
        super(typePolicy, TypeDto.class, userService, objectMapper, modelMapper);
        this.typePolicy = typePolicy;
    }

    @Override
    protected Stream<GuardedLink> entityLinks(TypeDto typeDto, Map<String, Object> extraInfo) {
        return Stream.of(
                GuardedLink.guard(
                        typePolicy.allowGet(typeDto.getId()),
                        WebMvcLinkBuilder.linkTo(methodOn(TypeController.class).findOne(typeDto.getId())).withSelfRel()
                ),
                GuardedLink.guard(
                        typePolicy.allowPut(typeDto),
                        linkTo(methodOn(TypeController.class).update(typeDto, typeDto.getId())).withRel("put")
                ),
                // patch link is shown if the user can actually edit one or more fields on this type
                GuardedLink.guard(
                        typePolicy.canEdit(typeDto.getId()),
                        linkTo(methodOn(TypeController.class).patch(new JsonPatch(Collections.emptyList()), typeDto.getId())).withRel("patch")
                ),
                GuardedLink.guard(
                        typePolicy.allowDelete(typeDto.getId()),
                        linkTo(methodOn(TypeController.class).archiveOrDelete(new DeleteRequest(), typeDto.getId())).withRel("delete")
                ),
                GuardedLink.guard(
                        typePolicy.allowGet(typeDto.getId()),
                        linkTo(methodOn(TypeController.class).getEditableFields(typeDto.getId())).withRel("editableFields")
                ),
                GuardedLink.guard(
                        typePolicy.allowGetAll(new TypeSearchRequest()),
                        linkTo(methodOn(TypeController.class).findAll(new TypeSearchRequest())).withRel("types")
                )
        );
    }

    @Override
    protected Stream<GuardedLink> collectionLinks() {
        return Stream.concat(super.collectionLinks(),
                Stream.of(
                        GuardedLink.guard(
                                typePolicy.allowPost(new TypeDto()),
                                linkTo(methodOn(TypeController.class).create(new TypeDto())).withRel("post")
                        )));
    }

    @Override
    protected WebMvcLinkBuilder findAllLinkBuilder() {
        return linkTo(methodOn(TypeController.class).findAll(new TypeSearchRequest()));
    }

    @Override
    public Type convertToEntity(TypeDto typeDto) {
        return modelMapper.map(typeDto, Type.class);
    }

    @Override
    public TypeDto convertToDto(Type type) {
        return modelMapper.map(type, TypeDto.class);
    }

}
