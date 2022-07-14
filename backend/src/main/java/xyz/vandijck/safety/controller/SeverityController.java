package xyz.vandijck.safety.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.entity.severity.Severity;
import xyz.vandijck.safety.entity.severity.SeverityInput;
import xyz.vandijck.safety.entity.severity.SeverityRepresentationModelAssembler;
import xyz.vandijck.safety.entity.severity.SeverityRepresentationModelAssembler.SeverityModel;
import xyz.vandijck.safety.service.SeverityService;


@AllArgsConstructor
@RestController
@RequestMapping("/severities")
public class SeverityController {

    private final SeverityService severityService;

    private final SeverityRepresentationModelAssembler severityLevelAssembler;


    @RequestMapping(method = RequestMethod.GET)
    CollectionModel<SeverityModel> all() {
        return severityLevelAssembler.toCollectionModel(severityService.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    SeverityModel get(@PathVariable("id") long id) {
        return severityLevelAssembler.toModel(severityService.findById(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Void> create(@Valid @RequestBody SeverityInput severityInput) {
        Severity severity = severityService.create(severityInput);

        return ResponseEntity.created(
                linkTo(methodOn(SeverityController.class).get(severity.getId())).toUri()).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    ResponseEntity<Void> update(@PathVariable("id") long id, @Valid @RequestBody SeverityInput severityInput) {
        severityService.update(id, severityInput);

        return ResponseEntity.noContent()
                .location(linkTo(methodOn(SeverityController.class).get(id)).toUri()).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> delete(@PathVariable("id") long id) {
        severityService.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
