package xyz.vandijck.safety.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.entity.consequence.Consequence;
import xyz.vandijck.safety.entity.consequence.ConsequenceInput;
import xyz.vandijck.safety.entity.consequence.ConsequenceRepresentationModelAssembler;
import xyz.vandijck.safety.entity.consequence.ConsequenceRepresentationModelAssembler.ConsequenceModel;
import xyz.vandijck.safety.service.ConsequenceService;


@AllArgsConstructor
@RestController
@RequestMapping("/consequences")
public class ConsequenceController {

    private final ConsequenceService consequenceService;

    private final ConsequenceRepresentationModelAssembler consequenceLevelAssembler;


    @RequestMapping(method = RequestMethod.GET)
    CollectionModel<ConsequenceModel> all() {
        return consequenceLevelAssembler.toCollectionModel(consequenceService.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ConsequenceModel get(@PathVariable("id") long id) {
        return consequenceLevelAssembler.toModel(consequenceService.findById(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Void> create(@Valid @RequestBody ConsequenceInput consequenceInput) {
        Consequence consequence = consequenceService.create(consequenceInput);

        return ResponseEntity.created(
                linkTo(methodOn(ConsequenceController.class).get(consequence.getId())).toUri()).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    ResponseEntity<Void> update(@PathVariable("id") long id, @Valid @RequestBody ConsequenceInput consequenceInput) {
        consequenceService.update(id, consequenceInput);

        return ResponseEntity.noContent()
                .location(linkTo(methodOn(ConsequenceController.class).get(id)).toUri()).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> delete(@PathVariable("id") long id) {
        consequenceService.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
