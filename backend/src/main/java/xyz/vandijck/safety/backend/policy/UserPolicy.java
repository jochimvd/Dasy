package xyz.vandijck.safety.backend.policy;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.dto.UpdateRoleDto;
import xyz.vandijck.safety.backend.dto.UserDto;
import xyz.vandijck.safety.backend.entity.Role;
import xyz.vandijck.safety.backend.entity.User;
import xyz.vandijck.safety.backend.request.UpdateUserRequest;
import xyz.vandijck.safety.backend.request.UserSearchRequest;

import java.util.Collections;
import java.util.List;

/**
 * Policy for operations concerning Users
 */
@Component
public class UserPolicy extends CRUDPolicy<UserDto, UserSearchRequest> {

    private static final List<String> ADMIN_EDITABLE_FIELDS;
    private static final List<String> PERSONAL_EDITABLE_FIELDS;

    static {
        ADMIN_EDITABLE_FIELDS = List.of(
                "firstName",
                "lastName",
                "gender",
                "dateOfBirth",
                "address",
                "languages",
                "phoneNumber",
                "picturePermission",
                "specialInfo"
        );
        PERSONAL_EDITABLE_FIELDS = List.of(
                "firstName",
                "lastName",
                "gender",
                "dateOfBirth",
                "address",
                "languages",
                "phoneNumber",
                "picturePermission",
                "specialInfo"
        );
    }

    @Override
    public boolean allowGet(long id) {
        User user = userService.getByIdOrThrow(id);
        if (user.isArchived()) {
            return isAuthorized(id, Role.ADMIN);
        } else {
            return isAuthorized(id, Role.PERSONAL) || isAuthorized(id, Role.READER);
        }
    }

    @Override
    public boolean allowGetAll(UserSearchRequest searchRequest) {
        return isAdmin();
    }

    @Override
    public boolean allowPost(UserDto userDto) {
        return allowPublic();
    }

    @Override
    public boolean allowPut(UserDto entity) {
        return false;
    }

    public boolean allowPut(UpdateUserRequest userDto) {
        return isAuthorized(userDto.getId(), Role.PERSONAL) || isAuthorized(userDto.getId(), Role.ADMIN);
    }

    public void authorizePut(UpdateUserRequest updateUserRequest){
        authorize(allowPut(updateUserRequest));
    }

    @Override
    public boolean allowDelete(long id) {
        return isAuthorized(id, Role.PERSONAL) || isAuthorized(id, Role.ADMIN);
    }

    /**
     * @param id    ignored
     * @param patch ignored
     * @return Always false as the edits on users are more fine-grained and do not fit the JsonPatch structure
     */
    @Override
    public boolean allowPatch(long id, JsonPatch patch) {
        return false;
    }

    @Override
    public Role roleFor(long entityId) {
        return userRole(entityId);
    }

    /**
     * @param userId The id of the user to be updated
     * @return Whether the current user is allowed to change the password of that user
     */
    public boolean allowUpdatePassword(long userId) {
        return isAuthorized(userId, Role.PERSONAL);
    }

    public void authorizeUpdatePassword(long userId) {
        authorize(allowUpdatePassword(userId));
    }

    /**
     * @return Whether the current user is allowed to change their email
     */
    public boolean allowUpdateEmail(long userId) {
        return isAuthorized(userId, Role.PERSONAL);
    }

    public void authorizeUpdateEmail(long userId) {
        authorize(allowUpdateEmail(userId));
    }

    /**
     * Checks whether the current user is allowed to perform this role update
     *
     * @param updateRoleDto The values describing the update
     * @return True when it is allowed
     */
    public boolean allowUpdateRole(UpdateRoleDto updateRoleDto) {
        if (isAuthorized(updateRoleDto.getUserId(), Role.ADMIN)) {
            return true;
        }
        return false;
    }


    public void authorizeUpdateRole(UpdateRoleDto updateRoleDto) {
        authorize(allowUpdateRole(updateRoleDto));
    }

    @Override
    public List<String> getEditableFields(long userId) {
        Role role = roleFor(userId);
        if (role == Role.PERSONAL) {
            return PERSONAL_EDITABLE_FIELDS;
        } else if (allow(role, Role.ADMIN)) {
            return ADMIN_EDITABLE_FIELDS;
        }
        return Collections.emptyList();
    }

    public void authorizeGetSkills(long id) {
        authorize(allowGetSkills(id));
    }

    public boolean allowGetSkills(long id) {
        return allowGet(id);
    }
}
