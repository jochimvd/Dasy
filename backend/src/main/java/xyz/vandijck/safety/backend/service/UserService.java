package xyz.vandijck.safety.backend.service;

import xyz.vandijck.safety.backend.dto.UpdateRoleDto;
import xyz.vandijck.safety.backend.entity.User;
import xyz.vandijck.safety.backend.request.*;
import xyz.vandijck.safety.backend.response.LoginResponse;

/**
 * Interface for all the operations possible on users
 */
public interface UserService extends EntityService<User, UserSearchRequest> {

//    SearchDTO<User> findAll(EmailUserSearchRequest emailUserSearchRequest);

    User findByEmail(String email);

    LoginResponse login(LoginRequest request);

    void logout(LogoutRequest request);

    LoginResponse refresh(RefreshRequest request);

    void requestPasswordReset(ResetPasswordRequest request);

    void doPasswordReset(ResetPasswordActionRequest request);

    User signup(User request);

    /**
     * Activation request: either a new user activation or the activation of a new email address.
     * In case of a new email address, the @link{ActivationRequest#newEmail} field must be filled in.
     *
     * @param request Activation request
     * @return True if success, false on failure.
     */
    boolean activate(ActivationRequest request);

    User update(long id, UpdateUserRequest newValues);

    void updatePassword(UpdatePasswordRequest request, long id);

    void updateEmail(UpdateEmailRequest request);

    void validateDeleteRequest(DeleteRequest request);

    User getCurrentUser();

    void updateRole(UpdateRoleDto updateRoleDto);

}
