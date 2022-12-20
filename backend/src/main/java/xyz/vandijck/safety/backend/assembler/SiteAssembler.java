package xyz.vandijck.safety.backend.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.assembler.link.GuardedLink;
import xyz.vandijck.safety.backend.controller.SiteController;
import xyz.vandijck.safety.backend.dto.SiteDto;
import xyz.vandijck.safety.backend.entity.Site;
import xyz.vandijck.safety.backend.policy.SitePolicy;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.SiteSearchRequest;
import xyz.vandijck.safety.backend.service.UserService;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembler that converts Sites into SiteDto's and vice versa
 * Also provides access to all allowed links
 */
@Component
public class SiteAssembler extends BaseAssembler<Site, SiteDto> {

    private final SitePolicy sitePolicy;

    @Autowired
    public SiteAssembler(SitePolicy sitePolicy, UserService userService,
                         ObjectMapper objectMapper, ModelMapper modelMapper) {
        super(sitePolicy, SiteDto.class, userService, objectMapper, modelMapper);
        this.sitePolicy = sitePolicy;
    }

    @Override
    protected Stream<GuardedLink> entityLinks(SiteDto siteDto, Map<String, Object> extraInfo) {
        return Stream.of(
                GuardedLink.guard(
                        sitePolicy.allowGet(siteDto.getId()),
                        WebMvcLinkBuilder.linkTo(methodOn(SiteController.class).findOne(siteDto.getId())).withSelfRel()
                ),
                GuardedLink.guard(
                        sitePolicy.allowPut(siteDto),
                        linkTo(methodOn(SiteController.class).update(siteDto, siteDto.getId())).withRel("put")
                ),
                // patch link is shown if the user can actually edit one or more fields on this site
                GuardedLink.guard(
                        sitePolicy.canEdit(siteDto.getId()),
                        linkTo(methodOn(SiteController.class).patch(new JsonPatch(Collections.emptyList()), siteDto.getId())).withRel("patch")
                ),
                GuardedLink.guard(
                        sitePolicy.allowDelete(siteDto.getId()),
                        linkTo(methodOn(SiteController.class).archiveOrDelete(new DeleteRequest(), siteDto.getId())).withRel("delete")
                ),
                GuardedLink.guard(
                        sitePolicy.allowGet(siteDto.getId()),
                        linkTo(methodOn(SiteController.class).getEditableFields(siteDto.getId())).withRel("editableFields")
                ),
                GuardedLink.guard(
                        sitePolicy.allowGetAll(new SiteSearchRequest()),
                        linkTo(methodOn(SiteController.class).findAll(new SiteSearchRequest())).withRel("sites")
                )
        );
    }

    @Override
    protected Stream<GuardedLink> collectionLinks() {
        return Stream.concat(super.collectionLinks(),
                Stream.of(
                        GuardedLink.guard(
                                sitePolicy.allowPost(new SiteDto()),
                                linkTo(methodOn(SiteController.class).create(new SiteDto())).withRel("post")
                        )));
    }

    @Override
    protected WebMvcLinkBuilder findAllLinkBuilder() {
        return linkTo(methodOn(SiteController.class).findAll(new SiteSearchRequest()));
    }

    @Override
    public Site convertToEntity(SiteDto siteDto) {
        return modelMapper.map(siteDto, Site.class);
    }

    @Override
    public SiteDto convertToDto(Site site) {
        return modelMapper.map(site, SiteDto.class);
    }

}
