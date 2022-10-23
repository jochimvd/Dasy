package xyz.vandijck.safety.backend.security;

import xyz.vandijck.safety.backend.controller.exceptions.UserNotFoundException;
import xyz.vandijck.safety.backend.entity.User;
import xyz.vandijck.safety.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Core service that is used in the authentication to load user-specific data.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private final UserRepository userRepository;

    /**
     * Creates a new default user details service.
     *
     * @param userRepository The user repository
     */
    public UserDetailsServiceImpl(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by its unique natural identifier.
     * Called a username in Spring, but is actually an email in our case.
     *
     * @param email The email of the user.
     * @return A user if found, otherwise an exception is thrown
     */
    @Override
    public UserDetails loadUserByUsername(String email)
    {
        // Our "username" is actually the "email".
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        return user;
    }

    /**
     * Loads a user by its unique identifier in the database.
     * Required because an email may change during an active session, but we don't want to invalidate the session
     * because of that.
     *
     * @param id The unique identifier in the database.
     * @return A user if found, null otherwise.
     */
    public UserDetails loadUserById(long id)
    {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            throw new UserNotFoundException();

        return user.get();
    }
}
