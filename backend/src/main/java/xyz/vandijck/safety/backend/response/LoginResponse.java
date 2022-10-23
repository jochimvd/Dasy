package xyz.vandijck.safety.backend.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response sent when a user successfully logs in
 */
@Data
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;

    /**
     * Login response
     *
     * @param accessToken     The Jwt token
     * @param refreshToken The refresh token
     */
    public LoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
