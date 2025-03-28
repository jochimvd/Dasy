package xyz.vandijck.safety.backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import xyz.vandijck.safety.backend.controller.exceptions.UnauthorizedException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class that handles Jwt on each request.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    /**
     * Executes the authentication validation.
     *
     * @param httpServletRequest  The request.
     * @param httpServletResponse The response.
     * @param filterChain         This method is one in the chain, see it as a middleware system.
     * @throws ServletException If the servlet itself crashes.
     * @throws IOException      If the connection somehow crashes.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException
    {
        try
        {
            String token = getToken(httpServletRequest);
            if (token != null)
            {
                long id = tokenProvider.getIdFromToken(token);

                UserDetails userDetails = userDetailsService.loadUserById(id);
                if (userDetails.isEnabled())
                {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, null);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
        catch (ExpiredJwtException ex)
        {
            resolver.resolveException(httpServletRequest, httpServletResponse, null, new UnauthorizedException("jwt token expired"));
        }
        catch (Exception ex)
        {
            logger.debug(ex);
            resolver.resolveException(httpServletRequest, httpServletResponse, null, new UnauthorizedException("authentication failed"));
        }
    }

    /**
     * Gets the bearer token from the request if it has one.
     *
     * @param httpServletRequest The request
     * @return The bearer token if the request has one, null otherwise.
     */
    private String getToken(HttpServletRequest httpServletRequest)
    {
        String token = httpServletRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer "))
            return token.substring(7);

        return null;
    }
}
