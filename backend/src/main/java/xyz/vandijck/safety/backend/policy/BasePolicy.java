package xyz.vandijck.safety.backend.policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.controller.exceptions.UnauthorizedException;
import xyz.vandijck.safety.backend.entity.Role;
import xyz.vandijck.safety.backend.entity.User;
import xyz.vandijck.safety.backend.service.Patcher;
import xyz.vandijck.safety.backend.service.UserService;


/**
 * Superclass of all Policy classes with useful helper functions to simplify permission distribution
 */
@Component
public abstract class BasePolicy {

    @Autowired
    protected UserService userService;

    @Autowired
    protected Patcher patcher;


    /**
     * Checks if the action is allowed and throws an UnauthorizedException if not allowed
     *
     * @param allowed Whether the action is permitted
     */
    protected void authorize(boolean allowed) {
        if (!allowed) {
            throw new UnauthorizedException();
        }
    }

    /**
     * @return Whether anyone is allowed to perform this action
     */
    public boolean allowPublic() {
        return true;
    }

    /**
     * Authorize whether the current user may see public content
     */
    public void authorizePublic() {
        authorize(allowPublic());
    }


    /**
     * @return The {@link User} associated with the request
     */
    protected User getUser() {
        return userService.getCurrentUser();
    }

    /**
     * @return The role of the currently logged-in user, or Role.READER if not logged in
     */
    protected Role getCurrentRole() {
        return getUser().getRole();
    }

    /**
     * @return The id of the current user, or -1L if not logged in
     */
    protected long getCurrentId() {
        return getUser().getId();
    }

    /**
     * @return Whether there is currently a user logged in
     */
    public boolean isLoggedIn() {
        return getCurrentId() > 0;
    }

    /**
     * Checks if current user is admin
     */
    public boolean isAdmin(){
        return getCurrentRole() == Role.ADMIN;
    }

    /**
     * Helper function to check if the {@link Role} of the supplied {@link User} is high enough
     *
     * @param entityId The id of the entity that is used in the action
     * @param role     The role we want to check against
     * @return Whether the user has a high enough role
     */
    protected boolean isAuthorized(long entityId, Role role) {
        if (role == Role.PERSONAL) { // personal is a special role
            return roleFor(entityId) == Role.PERSONAL;
        }
        return allow(roleFor(entityId), role);
    }

    /**
     * Convenience method to prevent double roleFor calls
     *
     * @param entityId       The id of the entity
     * @param unlessPersonal The minimum role if the action is not personal
     * @return Whether it is allowed
     */
    protected boolean isPersonalOrAuthorized(long entityId, Role unlessPersonal) {
        Role currentRole = roleFor(entityId);
        return currentRole == Role.PERSONAL || allow(currentRole, unlessPersonal);
    }

    /**
     * @param current The role of the current user
     * @param minimum The minimum required role
     * @return Whether permission is granted
     */
    public boolean allow(Role current, Role minimum){
        return current != null && current.compareTo(minimum) >= 0;
    }

    /**
     * Get the {@link Role} of the supplied {@link User}
     * @return The user role
     */
    public Role roleFor(long entityId) {
        return getUser().getRole();
    }

    /**
     * Retrieves the role of the current user for the given user
     * @param userId The id of the user
     * @return The corresponding role
     */
    public Role userRole(long userId) {
        User currentUser = getUser();
        if (currentUser != null) {
            if (currentUser.getId() == userId) {
                return Role.PERSONAL;
            } else if (currentUser.getRole() == Role.ADMIN) {
                return Role.ADMIN;
            }
        }

        return Role.READER;
    }
}