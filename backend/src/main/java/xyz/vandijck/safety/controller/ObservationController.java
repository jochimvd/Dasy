package xyz.vandijck.safety.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.entity.observation.Observation;
import xyz.vandijck.safety.entity.observation.ObservationInput;
import xyz.vandijck.safety.entity.observation.ObservationPatchInput;
import xyz.vandijck.safety.entity.observation.ObservationRepresentationModelAssembler;
import xyz.vandijck.safety.entity.observation.ObservationRepresentationModelAssembler.ObservationModel;
import xyz.vandijck.safety.service.ObservationService;


@AllArgsConstructor
@RestController
@RequestMapping("/observations")
public class ObservationController {

    private final ObservationService observationService;

    private final ObservationRepresentationModelAssembler observationAssembler;


    @RequestMapping(method = RequestMethod.GET)
    CollectionModel<ObservationModel> all() {
        return observationAssembler.toCollectionModel(observationService.findAll())
                .add(linkTo(methodOn(ObservationController.class).all()).withSelfRel()
                        .andAffordance(afford(methodOn(ObservationController.class).create(null))));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ObservationModel get(@PathVariable("id") long id) {
        return observationAssembler.toModel(observationService.findById(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Void> create(@Valid @RequestBody ObservationInput observationInput) {
        Observation observation = observationService.create(observationInput);

        return ResponseEntity.created(
                linkTo(methodOn(ObservationController.class).get(observation.getId())).toUri()).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> update(@PathVariable("id") long id, @Valid @RequestBody ObservationInput observationInput) {
        observationService.update(id, observationInput);

        return ResponseEntity.noContent()
                .location(linkTo(methodOn(ObservationController.class).get(id)).toUri()).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Void> patch(@PathVariable("id") long id, @Valid @RequestBody ObservationPatchInput observationPatchInput) {
        observationService.patch(id, observationPatchInput);

        return ResponseEntity.noContent()
                .location(linkTo(methodOn(ObservationController.class).get(id)).toUri()).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        observationService.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
