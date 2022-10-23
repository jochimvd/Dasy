package xyz.vandijck.safety.backend.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * Logout request.
 */
@Data
@Accessors(chain = true)
public class LogoutRequest {
    @NotBlank
    private String refreshToken;
}
