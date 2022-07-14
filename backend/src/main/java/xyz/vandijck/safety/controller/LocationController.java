package xyz.vandijck.safety.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.entity.location.Location;
import xyz.vandijck.safety.entity.location.LocationInput;
import xyz.vandijck.safety.entity.location.LocationRepresentationModelAssembler;
import xyz.vandijck.safety.entity.location.LocationRepresentationModelAssembler.LocationModel;
import xyz.vandijck.safety.service.LocationService;


@AllArgsConstructor
@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    private final LocationRepresentationModelAssembler locationAssembler;


    @RequestMapping(method = RequestMethod.GET)
    CollectionModel<LocationModel> all() {
        return locationAssembler.toCollectionModel(locationService.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    LocationModel get(@PathVariable("id") long id) {
        return locationAssembler.toModel(locationService.findById(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Void> create(@Valid @RequestBody LocationInput locationInput) {
        Location location = locationService.create(locationInput);

        return ResponseEntity.created(
                linkTo(methodOn(LocationController.class).get(location.getId())).toUri()).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    ResponseEntity<Void> update(@PathVariable("id") long id, @Valid @RequestBody LocationInput locationInput) {
        locationService.update(id, locationInput);

        return ResponseEntity.noContent()
                .location(linkTo(methodOn(LocationController.class).get(id)).toUri()).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> delete(@PathVariable("id") long id) {
        locationService.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
