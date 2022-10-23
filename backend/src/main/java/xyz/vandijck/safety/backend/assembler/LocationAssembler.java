package xyz.vandijck.safety.backend.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.assembler.link.GuardedLink;
import xyz.vandijck.safety.backend.controller.LocationController;
import xyz.vandijck.safety.backend.dto.LocationDto;
import xyz.vandijck.safety.backend.entity.Location;
import xyz.vandijck.safety.backend.policy.LocationPolicy;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.LocationSearchRequest;
import xyz.vandijck.safety.backend.service.UserService;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembler that converts Locations into LocationDto's and vice versa
 * Also provides access to all allowed links
 */
@Component
public class LocationAssembler extends BaseAssembler<Location, LocationDto> {

    private final LocationPolicy locationPolicy;

    @Autowired
    public LocationAssembler(LocationPolicy locationPolicy, UserService userService,
                             ObjectMapper objectMapper, ModelMapper modelMapper) {
        super(locationPolicy, LocationDto.class, userService, objectMapper, modelMapper);
        this.locationPolicy = locationPolicy;
    }

    @Override
    protected Stream<GuardedLink> entityLinks(LocationDto locationDto, Map<String, Object> extraInfo) {
        return Stream.of(
                GuardedLink.guard(
                        locationPolicy.allowGet(locationDto.getId()),
                        WebMvcLinkBuilder.linkTo(methodOn(LocationController.class).findOne(locationDto.getId())).withSelfRel()
                ),
                GuardedLink.guard(
                        locationPolicy.allowPut(locationDto),
                        linkTo(methodOn(LocationController.class).update(locationDto, locationDto.getId())).withRel("put")
                ),
                // patch link is shown if the user can actually edit one or more fields on this location
                GuardedLink.guard(
                        locationPolicy.canEdit(locationDto.getId()),
                        linkTo(methodOn(LocationController.class).patch(new JsonPatch(Collections.emptyList()), locationDto.getId())).withRel("patch")
                ),
                GuardedLink.guard(
                        locationPolicy.allowDelete(locationDto.getId()),
                        linkTo(methodOn(LocationController.class).archiveOrDelete(new DeleteRequest(), locationDto.getId())).withRel("delete")
                ),
                GuardedLink.guard(
                        locationPolicy.allowGet(locationDto.getId()),
                        linkTo(methodOn(LocationController.class).getEditableFields(locationDto.getId())).withRel("editableFields")
                ),
                GuardedLink.guard(
                        locationPolicy.allowGetAll(new LocationSearchRequest()),
                        linkTo(methodOn(LocationController.class).findAll(new LocationSearchRequest())).withRel("locations")
                )
        );
    }

    @Override
    protected Stream<GuardedLink> collectionLinks() {
        return Stream.concat(super.collectionLinks(),
                Stream.of(
                        GuardedLink.guard(
                                locationPolicy.allowPost(new LocationDto()),
                                linkTo(methodOn(LocationController.class).create(new LocationDto())).withRel("post")
                        )));
    }

    @Override
    protected WebMvcLinkBuilder findAllLinkBuilder() {
        return linkTo(methodOn(LocationController.class).findAll(new LocationSearchRequest()));
    }

    @Override
    public Location convertToEntity(LocationDto locationDto) {
        return modelMapper.map(locationDto, Location.class);
    }

    @Override
    public LocationDto convertToDto(Location location) {
        return modelMapper.map(location, LocationDto.class);
    }

}
