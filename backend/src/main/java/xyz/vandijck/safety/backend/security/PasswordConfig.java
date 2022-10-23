package xyz.vandijck.safety.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password algorithm configuration.
 * Can't be in the security config because of circular references.
 */
@Configuration
public class PasswordConfig
{
    /**
     * Gets a password encoder.
     *
     * @return The password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
