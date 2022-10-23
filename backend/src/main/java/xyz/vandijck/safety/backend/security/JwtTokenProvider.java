package xyz.vandijck.safety.backend.security;

import xyz.vandijck.safety.backend.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Provides functionality to handle Jwt tokens (creation and getting its data).
 */
@Component
public class JwtTokenProvider
{
    @Value("${app.jwt-secret}")
    private String secret;

    @Value("${app.jwt-expiration}")
    private int expirationTime;

    /**
     * Creates a new Jwt token for a valid authentication.
     *
     * @param user The user.
     * @return The token as a string.
     */
    public String createToken(User user)
    {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime * 1000);

        return Jwts
                .builder()
                .setSubject(Long.toString(user.getId()))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * Gets the user id from a Jwt token. Throws if the token is invalid.
     *
     * @param token The Jwt token.
     * @return The id from the token.
     */
    public long getIdFromToken(String token)
    {
        return Long.parseLong(
                Jwts
                    .parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject()
        );
    }
}
