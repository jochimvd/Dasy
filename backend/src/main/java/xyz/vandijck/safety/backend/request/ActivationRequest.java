package xyz.vandijck.safety.backend.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class ActivationRequest {
    @Email(message = "emailInvalid")
    @NotNull
    private String email;

    // Multipurpose: can also be used to set a new email address.
    // Safety: the signature also signs this newEmail parameter.
    @Email
    private String newEmail;

    @NotNull
    private String signature;

    private long expiration;

    public ActivationRequest setEmail(String email) {
        this.email = email.toLowerCase();
        return this;
    }
}
