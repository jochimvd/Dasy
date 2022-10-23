package xyz.vandijck.safety.backend.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.assembler.link.GuardedLink;
import xyz.vandijck.safety.backend.controller.UserController;
import xyz.vandijck.safety.backend.dto.UserDto;
import xyz.vandijck.safety.backend.entity.User;
import xyz.vandijck.safety.backend.policy.UserPolicy;
import xyz.vandijck.safety.backend.request.*;
import xyz.vandijck.safety.backend.service.UserService;

import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static xyz.vandijck.safety.backend.assembler.link.GuardedLink.guard;

/**
 * Assembler that converts User into UserDto and vice versa
 * Also provides access to all useful allowed links (so not all user-related links)
 */
@Component
public class UserAssembler extends BaseAssembler<User, UserDto> {
    protected final UserPolicy userPolicy;

    @Autowired
    public UserAssembler(UserPolicy userPolicy, UserService userService,
                         ObjectMapper objectMapper,ModelMapper modelMapper) {
        super(userPolicy, UserDto.class, userService, objectMapper, modelMapper);
        this.userPolicy = userPolicy;
    }

    @Override
    protected Stream<GuardedLink> entityLinks(UserDto user, Map<String, Object> extraInfo) {
        return Stream.of(
                guard(
                        userPolicy.allowGet(user.getId()),
                        WebMvcLinkBuilder.linkTo(methodOn(UserController.class).findOne(user.getId())).withSelfRel()
                ),
                guard(
                        userPolicy.allowGetAll(new UserSearchRequest()),
                        linkTo(methodOn(UserController.class).findAll(new UserSearchRequest())).withRel("users")
                ),
                guard(
                        userPolicy.allowPut(new UpdateUserRequest().setId(user.getId())),
                        linkTo(methodOn(UserController.class).update(new UpdateUserRequest(), user.getId())).withRel("put")
                ),
                guard(
                        userPolicy.allowDelete(user.getId()),
                        linkTo(methodOn(UserController.class).archiveOrDelete(new DeleteRequest(), user.getId())).withRel("delete")
                ),
                guard(
                        userPolicy.canEdit(user.getId()),
                        linkTo(methodOn(UserController.class).getEditableFields(user.getId())).withRel("editableFields")
                ),
                guard(
                        userPolicy.allowUpdateEmail(user.getId()),
                        linkTo(methodOn(UserController.class).updateEmail(new UpdateEmailRequest())).withRel("updateEmail")
                ),
                guard(
                        userPolicy.allowUpdatePassword(user.getId()),
                        linkTo(methodOn(UserController.class).updatePassword(new UpdatePasswordRequest(), user.getId())).withRel("updatePassword")
                )
        );
    }

    @Override
    public CollectionModel<EntityModel<UserDto>> toCollectionModelWithIterables(
            Iterable<? extends User> users, PagedModel.PageMetadata metadata, Iterable<Link> extra) {
        return toCollectionModelWithIterables(users, metadata, extra, userPolicy::roleFor);
    }

    @Override
    protected Stream<GuardedLink> collectionLinks() {
        return Stream.concat(super.collectionLinks(),
                Stream.of(
                        guard(userPolicy.allowPost(new UserDto()),
                                linkTo(methodOn(UserController.class).signup(new UserSignupRequest())).withRel("post"))));
    }

    @Override
    protected WebMvcLinkBuilder findAllLinkBuilder() {
        return linkTo(methodOn(UserController.class).findAll(new UserSearchRequest()));
    }

    @Override
    public UserDto convertToDto(User entity) {
        return modelMapper.map(entity, UserDto.class);
    }

    @Override
    public User convertToEntity(UserDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
