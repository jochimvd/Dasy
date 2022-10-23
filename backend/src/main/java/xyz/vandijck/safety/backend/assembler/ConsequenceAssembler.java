package xyz.vandijck.safety.backend.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.assembler.link.GuardedLink;
import xyz.vandijck.safety.backend.controller.ConsequenceController;
import xyz.vandijck.safety.backend.dto.ConsequenceDto;
import xyz.vandijck.safety.backend.entity.Consequence;
import xyz.vandijck.safety.backend.policy.ConsequencePolicy;
import xyz.vandijck.safety.backend.request.ConsequenceSearchRequest;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.service.UserService;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembler that converts Consequences into ConsequenceDto's and vice versa
 * Also provides access to all allowed links
 */
@Component
public class ConsequenceAssembler extends BaseAssembler<Consequence, ConsequenceDto> {

    private final ConsequencePolicy consequencePolicy;

    @Autowired
    public ConsequenceAssembler(ConsequencePolicy consequencePolicy, UserService userService,
                                ObjectMapper objectMapper, ModelMapper modelMapper) {
        super(consequencePolicy, ConsequenceDto.class, userService, objectMapper, modelMapper);
        this.consequencePolicy = consequencePolicy;
    }

    @Override
    protected Stream<GuardedLink> entityLinks(ConsequenceDto consequenceDto, Map<String, Object> extraInfo) {
        return Stream.of(
                GuardedLink.guard(
                        consequencePolicy.allowGet(consequenceDto.getId()),
                        WebMvcLinkBuilder.linkTo(methodOn(ConsequenceController.class).findOne(consequenceDto.getId())).withSelfRel()
                ),
                GuardedLink.guard(
                        consequencePolicy.allowPut(consequenceDto),
                        linkTo(methodOn(ConsequenceController.class).update(consequenceDto, consequenceDto.getId())).withRel("put")
                ),
                // patch link is shown if the user can actually edit one or more fields on this consequence
                GuardedLink.guard(
                        consequencePolicy.canEdit(consequenceDto.getId()),
                        linkTo(methodOn(ConsequenceController.class).patch(new JsonPatch(Collections.emptyList()), consequenceDto.getId())).withRel("patch")
                ),
                GuardedLink.guard(
                        consequencePolicy.allowDelete(consequenceDto.getId()),
                        linkTo(methodOn(ConsequenceController.class).archiveOrDelete(new DeleteRequest(), consequenceDto.getId())).withRel("delete")
                ),
                GuardedLink.guard(
                        consequencePolicy.allowGet(consequenceDto.getId()),
                        linkTo(methodOn(ConsequenceController.class).getEditableFields(consequenceDto.getId())).withRel("editableFields")
                ),
                GuardedLink.guard(
                        consequencePolicy.allowGetAll(new ConsequenceSearchRequest()),
                        linkTo(methodOn(ConsequenceController.class).findAll(new ConsequenceSearchRequest())).withRel("consequences")
                )
        );
    }

    @Override
    protected Stream<GuardedLink> collectionLinks() {
        return Stream.concat(super.collectionLinks(),
                Stream.of(
                        GuardedLink.guard(
                                consequencePolicy.allowPost(new ConsequenceDto()),
                                linkTo(methodOn(ConsequenceController.class).create(new ConsequenceDto())).withRel("post")
                        )));
    }

    @Override
    protected WebMvcLinkBuilder findAllLinkBuilder() {
        return linkTo(methodOn(ConsequenceController.class).findAll(new ConsequenceSearchRequest()));
    }

    @Override
    public Consequence convertToEntity(ConsequenceDto consequenceDto) {
        return modelMapper.map(consequenceDto, Consequence.class);
    }

    @Override
    public ConsequenceDto convertToDto(Consequence consequence) {
        return modelMapper.map(consequence, ConsequenceDto.class);
    }

}
