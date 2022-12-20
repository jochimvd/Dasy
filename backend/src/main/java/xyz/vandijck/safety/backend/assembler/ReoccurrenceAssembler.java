package xyz.vandijck.safety.backend.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.assembler.link.GuardedLink;
import xyz.vandijck.safety.backend.controller.ReoccurrenceController;
import xyz.vandijck.safety.backend.dto.ReoccurrenceDto;
import xyz.vandijck.safety.backend.entity.Reoccurrence;
import xyz.vandijck.safety.backend.policy.ReoccurrencePolicy;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.ReoccurrenceSearchRequest;
import xyz.vandijck.safety.backend.service.UserService;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembler that converts Reoccurrences into ReoccurrenceDto's and vice versa
 * Also provides access to all allowed links
 */
@Component
public class ReoccurrenceAssembler extends BaseAssembler<Reoccurrence, ReoccurrenceDto> {

    private final ReoccurrencePolicy reoccurrencePolicy;

    @Autowired
    public ReoccurrenceAssembler(ReoccurrencePolicy reoccurrencePolicy, UserService userService,
                                 ObjectMapper objectMapper, ModelMapper modelMapper) {
        super(reoccurrencePolicy, ReoccurrenceDto.class, userService, objectMapper, modelMapper);
        this.reoccurrencePolicy = reoccurrencePolicy;
    }

    @Override
    protected Stream<GuardedLink> entityLinks(ReoccurrenceDto reoccurrenceDto, Map<String, Object> extraInfo) {
        return Stream.of(
                GuardedLink.guard(
                        reoccurrencePolicy.allowGet(reoccurrenceDto.getId()),
                        WebMvcLinkBuilder.linkTo(methodOn(ReoccurrenceController.class).findOne(reoccurrenceDto.getId())).withSelfRel()
                ),
                GuardedLink.guard(
                        reoccurrencePolicy.allowPut(reoccurrenceDto),
                        linkTo(methodOn(ReoccurrenceController.class).update(reoccurrenceDto, reoccurrenceDto.getId())).withRel("put")
                ),
                // patch link is shown if the user can actually edit one or more fields on this reoccurrence
                GuardedLink.guard(
                        reoccurrencePolicy.canEdit(reoccurrenceDto.getId()),
                        linkTo(methodOn(ReoccurrenceController.class).patch(new JsonPatch(Collections.emptyList()), reoccurrenceDto.getId())).withRel("patch")
                ),
                GuardedLink.guard(
                        reoccurrencePolicy.allowDelete(reoccurrenceDto.getId()),
                        linkTo(methodOn(ReoccurrenceController.class).archiveOrDelete(new DeleteRequest(), reoccurrenceDto.getId())).withRel("delete")
                ),
                GuardedLink.guard(
                        reoccurrencePolicy.allowGet(reoccurrenceDto.getId()),
                        linkTo(methodOn(ReoccurrenceController.class).getEditableFields(reoccurrenceDto.getId())).withRel("editableFields")
                ),
                GuardedLink.guard(
                        reoccurrencePolicy.allowGetAll(new ReoccurrenceSearchRequest()),
                        linkTo(methodOn(ReoccurrenceController.class).findAll(new ReoccurrenceSearchRequest())).withRel("reoccurrences")
                )
        );
    }

    @Override
    protected Stream<GuardedLink> collectionLinks() {
        return Stream.concat(super.collectionLinks(),
                Stream.of(
                        GuardedLink.guard(
                                reoccurrencePolicy.allowPost(new ReoccurrenceDto()),
                                linkTo(methodOn(ReoccurrenceController.class).create(new ReoccurrenceDto())).withRel("post")
                        )));
    }

    @Override
    protected WebMvcLinkBuilder findAllLinkBuilder() {
        return linkTo(methodOn(ReoccurrenceController.class).findAll(new ReoccurrenceSearchRequest()));
    }

    @Override
    public Reoccurrence convertToEntity(ReoccurrenceDto reoccurrenceDto) {
        return modelMapper.map(reoccurrenceDto, Reoccurrence.class);
    }

    @Override
    public ReoccurrenceDto convertToDto(Reoccurrence reoccurrence) {
        return modelMapper.map(reoccurrence, ReoccurrenceDto.class);
    }

}
