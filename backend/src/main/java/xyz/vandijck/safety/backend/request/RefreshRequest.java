package xyz.vandijck.safety.backend.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * Refresh token request for authentication.
 */
@Data
@Accessors(chain = true)
public class RefreshRequest {
    @NotBlank
    private String refreshToken;

    private boolean rememberMe;
}
