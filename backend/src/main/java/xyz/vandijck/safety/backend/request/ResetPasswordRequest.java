package xyz.vandijck.safety.backend.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class ResetPasswordRequest {
    @Email(message = "emailInvalid")
    @NotBlank
    private String email;

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }
}
