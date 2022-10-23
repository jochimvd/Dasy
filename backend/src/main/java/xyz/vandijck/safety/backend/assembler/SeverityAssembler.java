package xyz.vandijck.safety.backend.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.assembler.link.GuardedLink;
import xyz.vandijck.safety.backend.controller.SeverityController;
import xyz.vandijck.safety.backend.dto.SeverityDto;
import xyz.vandijck.safety.backend.entity.Severity;
import xyz.vandijck.safety.backend.policy.SeverityPolicy;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.SeveritySearchRequest;
import xyz.vandijck.safety.backend.service.UserService;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembler that converts Severities into SeverityDto's and vice versa
 * Also provides access to all allowed links
 */
@Component
public class SeverityAssembler extends BaseAssembler<Severity, SeverityDto> {

    private final SeverityPolicy severityPolicy;

    @Autowired
    public SeverityAssembler(SeverityPolicy severityPolicy, UserService userService,
                             ObjectMapper objectMapper, ModelMapper modelMapper) {
        super(severityPolicy, SeverityDto.class, userService, objectMapper, modelMapper);
        this.severityPolicy = severityPolicy;
    }

    @Override
    protected Stream<GuardedLink> entityLinks(SeverityDto severityDto, Map<String, Object> extraInfo) {
        return Stream.of(
                GuardedLink.guard(
                        severityPolicy.allowGet(severityDto.getId()),
                        WebMvcLinkBuilder.linkTo(methodOn(SeverityController.class).findOne(severityDto.getId())).withSelfRel()
                ),
                GuardedLink.guard(
                        severityPolicy.allowPut(severityDto),
                        linkTo(methodOn(SeverityController.class).update(severityDto, severityDto.getId())).withRel("put")
                ),
                // patch link is shown if the user can actually edit one or more fields on this severity
                GuardedLink.guard(
                        severityPolicy.canEdit(severityDto.getId()),
                        linkTo(methodOn(SeverityController.class).patch(new JsonPatch(Collections.emptyList()), severityDto.getId())).withRel("patch")
                ),
                GuardedLink.guard(
                        severityPolicy.allowDelete(severityDto.getId()),
                        linkTo(methodOn(SeverityController.class).archiveOrDelete(new DeleteRequest(), severityDto.getId())).withRel("delete")
                ),
                GuardedLink.guard(
                        severityPolicy.allowGet(severityDto.getId()),
                        linkTo(methodOn(SeverityController.class).getEditableFields(severityDto.getId())).withRel("editableFields")
                ),
                GuardedLink.guard(
                        severityPolicy.allowGetAll(new SeveritySearchRequest()),
                        linkTo(methodOn(SeverityController.class).findAll(new SeveritySearchRequest())).withRel("severitys")
                )
        );
    }

    @Override
    protected Stream<GuardedLink> collectionLinks() {
        return Stream.concat(super.collectionLinks(),
                Stream.of(
                        GuardedLink.guard(
                                severityPolicy.allowPost(new SeverityDto()),
                                linkTo(methodOn(SeverityController.class).create(new SeverityDto())).withRel("post")
                        )));
    }

    @Override
    protected WebMvcLinkBuilder findAllLinkBuilder() {
        return linkTo(methodOn(SeverityController.class).findAll(new SeveritySearchRequest()));
    }

    @Override
    public Severity convertToEntity(SeverityDto severityDto) {
        return modelMapper.map(severityDto, Severity.class);
    }

    @Override
    public SeverityDto convertToDto(Severity severity) {
        return modelMapper.map(severity, SeverityDto.class);
    }

}
