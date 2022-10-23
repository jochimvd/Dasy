package xyz.vandijck.safety.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import xyz.vandijck.safety.backend.entity.Role;

import javax.validation.constraints.NotNull;

/**
 * DTO that contains information describing an update to a role of a user
 */
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class UpdateRoleDto {

    // id of the user whose role is updated, required
    @NotNull(message = "isEmpty")
    private Long userId;

    private Role role;

    public UpdateRoleDto(Long userId, Role role){
        this.userId = userId;
        this.role = role;
    }

    /**
     * @return Whether this DTO contains a sensible update
     */
    @JsonIgnore
    public boolean isValidRoleUpdate(){
        return true; // TODO check admin demotion
    }
}
