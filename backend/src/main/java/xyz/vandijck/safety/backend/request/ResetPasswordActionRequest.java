package xyz.vandijck.safety.backend.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class ResetPasswordActionRequest
{
    @Email(message = "emailInvalid")
    @NotBlank
    private String email;

    @NotBlank
    private String token;

    @NotBlank
    private String password;

    public ResetPasswordActionRequest(String email, String token, String password) {
        setEmail(email);
        this.token = token;
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }
}
