package xyz.vandijck.safety.backend.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class UpdateEmailRequest {

    @NotBlank
    private String password;

    @NotNull
    @Email(message = "emailInvalid")
    private String email;

    @NotNull
    @Email(message = "emailInvalid")
    private String newEmail;

    public UpdateEmailRequest(String password, String email, String newEmail) {
        this.password = password;
        setEmail(email);
        setNewEmail(newEmail);
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail.toLowerCase();
    }
}
