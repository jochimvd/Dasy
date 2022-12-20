package xyz.vandijck.safety.backend.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.assembler.link.GuardedLink;
import xyz.vandijck.safety.backend.controller.ObservationController;
import xyz.vandijck.safety.backend.dto.ObservationDto;
import xyz.vandijck.safety.backend.entity.Observation;
import xyz.vandijck.safety.backend.policy.ObservationPolicy;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.ObservationSearchRequest;
import xyz.vandijck.safety.backend.service.*;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembler that converts Observations into ObservationDto's and vice versa
 * Also provides access to all allowed links
 */
@Component
public class ObservationAssembler extends BaseAssembler<Observation, ObservationDto> {

    private final ObservationPolicy observationPolicy;

    private final UserAssembler userAssembler;

    private final CategoryAssembler categoryAssembler;

    private final CategoryService categoryService;

    private final TypeAssembler typeAssembler;

    private final TypeService typeService;

    private final SiteService siteService;

    private final CompanyService companyService;

    @Autowired
    public ObservationAssembler(ObservationPolicy observationPolicy,
                                UserService userService, UserAssembler userAssembler,
                                CategoryService categoryService, CategoryAssembler categoryAssembler,
                                TypeService typeService, TypeAssembler typeAssembler,
                                SiteService siteService, CompanyService companyService,
                                ObjectMapper objectMapper, ModelMapper modelMapper) {
        super(observationPolicy, ObservationDto.class, userService, objectMapper, modelMapper);
        this.userAssembler = userAssembler;
        this.observationPolicy = observationPolicy;
        this.categoryService = categoryService;
        this.categoryAssembler = categoryAssembler;
        this.siteService = siteService;
        this.typeService = typeService;
        this.typeAssembler = typeAssembler;
        this.companyService = companyService;
    }

    @Override
    protected Stream<GuardedLink> entityLinks(ObservationDto observationDto, Map<String, Object> extraInfo) {
        return Stream.of(
                GuardedLink.guard(
                        observationPolicy.allowGet(observationDto.getId()),
                        WebMvcLinkBuilder.linkTo(methodOn(ObservationController.class).findOne(observationDto.getId())).withSelfRel()
                ),
                GuardedLink.guard(
                        observationPolicy.allowPut(observationDto),
                        linkTo(methodOn(ObservationController.class).update(observationDto, observationDto.getId())).withRel("put")
                ),
                // patch link is shown if the user can actually edit one or more fields on this observation
                GuardedLink.guard(
                        observationPolicy.canEdit(observationDto.getId()),
                        linkTo(methodOn(ObservationController.class).patch(new JsonPatch(Collections.emptyList()), observationDto.getId())).withRel("patch")
                ),
                GuardedLink.guard(
                        observationPolicy.allowDelete(observationDto.getId()),
                        linkTo(methodOn(ObservationController.class).archiveOrDelete(new DeleteRequest(), observationDto.getId())).withRel("delete")
                ),
                GuardedLink.guard(
                        observationPolicy.allowGet(observationDto.getId()),
                        linkTo(methodOn(ObservationController.class).getEditableFields(observationDto.getId())).withRel("editableFields")
                ),
                GuardedLink.guard(
                        observationPolicy.allowGetAll(new ObservationSearchRequest()),
                        linkTo(methodOn(ObservationController.class).findAll(new ObservationSearchRequest())).withRel("observations")
                )
        );
    }

    @Override
    protected Stream<GuardedLink> collectionLinks() {
        return Stream.concat(super.collectionLinks(),
                Stream.of(
                        GuardedLink.guard(
                                observationPolicy.allowPost(new ObservationDto()),
                                linkTo(methodOn(ObservationController.class).create(new ObservationDto())).withRel("post")
                        )));
    }

    @Override
    protected WebMvcLinkBuilder findAllLinkBuilder() {
        return linkTo(methodOn(ObservationController.class).findAll(new ObservationSearchRequest()));
    }

    @Override
    public Observation convertToEntity(ObservationDto observationDto) {
        Observation observation = modelMapper.map(observationDto, Observation.class);
        observation.setObserver(userService.findById(observationDto.getObserver().getId()));
        observation.setCategory(categoryService.findById(observationDto.getCategory().getId()));
        observation.setType(typeService.findById(observationDto.getType().getId()));
        observation.setSite(siteService.findElseCreate(observationDto.getSite()));
        observation.setObservedCompany(companyService.findElseCreate(observationDto.getObservedCompany()));
        return observation;
    }

    @Override
    public ObservationDto convertToDto(Observation observation) {
        ObservationDto observationDto = modelMapper.map(observation, ObservationDto.class);
        observationDto.setObserver(userAssembler.convertToDto(observation.getObserver()));
        observationDto.setCategory(categoryAssembler.convertToDto(observation.getCategory()));
        observationDto.setType(typeAssembler.convertToDto(observation.getType()));
        observationDto.setSite(observation.getSite().getName());
        observationDto.setObservedCompany(observation.getObservedCompany().getName());
        return observationDto;
    }

}
