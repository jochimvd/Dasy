package xyz.vandijck.safety.backend.request;

import xyz.vandijck.safety.backend.dto.UserDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class UserSignupRequest extends UserDto
{
    @NotBlank
    @ToString.Exclude
    private String password;
}
