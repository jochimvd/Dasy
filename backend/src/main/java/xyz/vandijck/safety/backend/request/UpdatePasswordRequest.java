package xyz.vandijck.safety.backend.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class UpdatePasswordRequest {
    @NotBlank
    private String password;

    @NotBlank
    private String newPassword;

    // This may seem a little bit odd.
    // The reason we want to spare the refresh token belonging to the current user-device pair,
    // is that the user does not expect to get forcibly logged out after this.
    // We can't just get "the latest" refresh token, because that's not necessarily the token of the current
    // user-device pair.
    @NotNull
    private String refreshToken;

    /**
     * @param password     The old password to verify if it matches with the old password
     * @param newPassword  The new password
     * @param refreshToken The refresh token (see comment at the field)
     */
    public UpdatePasswordRequest(String password, String newPassword, String refreshToken) {
        this.password = password;
        this.newPassword = newPassword;
        this.refreshToken = refreshToken;
    }
}
