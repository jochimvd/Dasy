package xyz.vandijck.safety.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.vandijck.safety.backend.assembler.link.RootModel;
import xyz.vandijck.safety.backend.policy.UserPolicy;
import xyz.vandijck.safety.backend.request.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static xyz.vandijck.safety.backend.assembler.link.GuardedLink.guard;

@RestController
@RequestMapping("/")
public class RootController {
    private final UserPolicy userPolicy;
    @Autowired
    public RootController(UserPolicy userPolicy) {
        this.userPolicy = userPolicy;
    }

    @GetMapping("/")
    public RootModel index() {
        RootModel model = new RootModel();

        model.add(
                linkTo(methodOn(ObservationController.class).findAll(new ObservationSearchRequest())).withRel("observations"),
                linkTo(methodOn(CategoryController.class).findAll(new CategorySearchRequest())).withRel("categories"),
                linkTo(methodOn(LocationController.class).findAll(new LocationSearchRequest())).withRel("locations"),
                linkTo(methodOn(SeverityController.class).findAll(new SeveritySearchRequest())).withRel("severities"),
                linkTo(methodOn(ConsequenceController.class).findAll(new ConsequenceSearchRequest())).withRel("consequences")
        );

        model.add(guard(userPolicy.allowGetAll(new UserSearchRequest()),
                linkTo(methodOn(UserController.class).findAll(new UserSearchRequest())).withRel("users")));

        return model;
    }
}
