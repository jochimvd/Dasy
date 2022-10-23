package xyz.vandijck.safety.backend.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class LoginRequest
{
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private boolean rememberMe;

    public LoginRequest(String email, String password) {
        setEmail(email);
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }
}
