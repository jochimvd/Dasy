package xyz.vandijck.safety.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.backend.assembler.UserAssembler;
import xyz.vandijck.safety.backend.controller.exceptions.BadRequestException;
import xyz.vandijck.safety.backend.controller.exceptions.IdMismatchException;
import xyz.vandijck.safety.backend.dto.UpdateRoleDto;
import xyz.vandijck.safety.backend.dto.UserDto;
import xyz.vandijck.safety.backend.entity.User;
import xyz.vandijck.safety.backend.policy.UserPolicy;
import xyz.vandijck.safety.backend.request.*;
import xyz.vandijck.safety.backend.response.EmptyResponse;
import xyz.vandijck.safety.backend.response.LoginResponse;
import xyz.vandijck.safety.backend.service.SearchDTO;
import xyz.vandijck.safety.backend.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * User controller, handles authentication and user CRUD actions.
 */
@RestController
@RequestMapping("/users")
public class UserController implements BaseEntityController<UserDto, UserSearchRequest> {
    @Autowired
    private UserService userService;

    // Default user resource assembler for hypermedia responses.
    @Autowired
    private UserAssembler userAssembler;

    @Autowired
    // All policies regarding users
    private UserPolicy userPolicy;

    /**
     * Listing of all users.
     *
     * @param request The search/listing request.
     * @return All users that match the query.
     */
    @GetMapping
    public CollectionModel<EntityModel<UserDto>> findAll(@Valid UserSearchRequest request) {
        userPolicy.authorizeGetAll(request);
        SearchDTO<User> dto = userService.findAll(request);
        return userAssembler.toCollectionModelFromSearch(dto, request);
    }

    /**
     * Gets a single user.
     *
     * @param id The id
     * @return The single user object
     */
    @GetMapping("/{id}")
    public EntityModel<UserDto> findOne(@PathVariable long id) {
        userPolicy.authorizeGet(id);
        return userAssembler.toModel(userService.findById(id));
    }

    /**
     * Deletes a single user.
     *
     * @param id The id
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> archiveOrDelete(@RequestBody @Valid DeleteRequest request, @PathVariable long id) {
        userPolicy.authorizeDelete(id);
        userService.archiveOrDeleteById(request, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Login
     *
     * @param request Login request, containing email and password.
     * @return A response containing the authentication state.
     */
    @PostMapping("/login")
    @ResponseBody
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return userService.login(request);
    }

    /**
     * Refresh token
     *
     * @param request Refresh request, containing the refresh token.
     * @return A response containing the authentication state.
     */
    @PostMapping("/refresh")
    @ResponseBody
    public LoginResponse refresh(@RequestBody @Valid RefreshRequest request) {
        return userService.refresh(request);
    }

    /**
     * Logout
     *
     * @return Nothing on success.
     */
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<EmptyResponse> logout(@RequestBody @Valid LogoutRequest request) {
        userService.logout(request);
        return new ResponseEntity<>(new EmptyResponse(), HttpStatus.OK);
    }

    /**
     * Requests changing the email of a user
     *
     * @param request The update email request containing the new email.
     * @return Nothing on success, else Exceptions will be thrown
     */
    @PostMapping("/email-request")
    @ResponseBody
    public ResponseEntity<EmptyResponse> updateEmail(@RequestBody @Valid UpdateEmailRequest request) {
        userPolicy.authorizeUpdateEmail(userService.findByEmail(request.getEmail()).getId());
        userService.updateEmail(request);
        return new ResponseEntity<>(new EmptyResponse(), HttpStatus.OK);
    }

    /**
     * Reset password request
     *
     * @param request The reset password request, containing the email of the user.
     * @return Nothing on success.
     */
    @PostMapping("/password-request")
    @ResponseBody
    public ResponseEntity<EmptyResponse> requestPasswordReset(@RequestBody @Valid ResetPasswordRequest request) {
        userService.requestPasswordReset(request);
        return new ResponseEntity<>(new EmptyResponse(), HttpStatus.OK);
    }

    /**
     * Reset password with a token
     *
     * @param request The reset password action, containing the email of the user, the token and the new password.
     * @return Nothing on success.
     */
    @PostMapping("/password-reset")
    @ResponseBody
    public ResponseEntity<EmptyResponse> doPasswordReset(@RequestBody @Valid ResetPasswordActionRequest request) {
        userService.doPasswordReset(request);
        return new ResponseEntity<>(new EmptyResponse(), HttpStatus.OK);
    }

    /**
     * Activates a user.
     *
     * @param request The activation request.
     */
    @PostMapping("/activate")
    public ResponseEntity<EmptyResponse> activate(@RequestBody @Valid ActivationRequest request) {
        boolean result = userService.activate(request);
        return new ResponseEntity<>(new EmptyResponse(), result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a new user.
     *
     * @param request The signup request containing the filled in fields.
     * @return The created user object.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserDto> signup(@RequestBody @Valid UserSignupRequest request) {
        return userAssembler.toModel(userService.signup(userAssembler.convertToEntity(request)));
    }

    /**
     * Updates the requested user, should he exist.
     *
     * @param dto The user dto containing the new values.
     * @return The updated user object.
     */
    @PutMapping("/{id}")
    public EntityModel<UserDto> update(@RequestBody @Valid UpdateUserRequest dto, @PathVariable long id) {
        if (dto.getId() != id) {
            throw new IdMismatchException();
        }
        userPolicy.authorizePut(dto);
        return userAssembler.toModel(userService.update(id, dto));
    }

    /**
     * Updates the password of a user.
     *
     * @param request The update password request containing the new password.
     * @param id      The user id.
     * @return Nothing on success.
     */
    @PatchMapping("/{id}")
    @ResponseBody
    public ResponseEntity<EmptyResponse> updatePassword(@RequestBody @Valid UpdatePasswordRequest request, @PathVariable long id) {
        userPolicy.authorizeUpdatePassword(id);
        userService.updatePassword(request, id);
        return new ResponseEntity<>(new EmptyResponse(), HttpStatus.OK);
    }

    /**
     * Updates the role of a user
     * @param updateRoleDto The values describing the role update
     * @param id The id of the user whose role is updated
     * @return Nothing on success
     */
    @PatchMapping("/{id}/promote")
    public ResponseEntity<EmptyResponse> updateRole(@RequestBody @Valid UpdateRoleDto updateRoleDto, @PathVariable long id){
        if(!updateRoleDto.isValidRoleUpdate() || id != updateRoleDto.getUserId()){
            throw new BadRequestException("updateRole", "invalidValues");
        }
        userPolicy.authorizeUpdateRole(updateRoleDto);
        userService.updateRole(updateRoleDto);
        return new ResponseEntity<>(new EmptyResponse(), HttpStatus.OK);
    }

    /**
     * Returns a list of fields the user has the permissions to edit for this entity
     *
     * @param id The id of the session
     * @return A list of fields the user can edit
     */
    @GetMapping(value = "/{id}/edit")
    public ResponseEntity<Map<String, List<String>>> getEditableFields(@PathVariable long id) {
        userPolicy.authorizeGet(id);
        return new ResponseEntity<>(
                Map.of("editableFields", userPolicy.getEditableFields(id)),
                HttpStatus.OK);
    }
}
