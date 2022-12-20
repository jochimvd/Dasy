package xyz.vandijck.safety.backend.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.vandijck.safety.backend.controller.exceptions.*;
import xyz.vandijck.safety.backend.dto.UpdateRoleDto;
import xyz.vandijck.safety.backend.entity.*;
import xyz.vandijck.safety.backend.repository.RefreshTokenRepository;
import xyz.vandijck.safety.backend.repository.ResetTokenRepository;
import xyz.vandijck.safety.backend.repository.UserRepository;
import xyz.vandijck.safety.backend.request.*;
import xyz.vandijck.safety.backend.response.LoginResponse;
import xyz.vandijck.safety.backend.security.JwtTokenProvider;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    // User database repository.
    private final UserRepository userRepository;

    @Autowired
    // Reset token repository.
    private ResetTokenRepository resetTokenRepository;

    @Autowired
    // Refresh token repository.
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    // Default password encoder.
    private PasswordEncoder passwordEncoder;

    @Autowired
    // Default authentication manager.
    private AuthenticationManager authenticationManager;

    @Autowired
    // Default Jwt token provider.
    private JwtTokenProvider tokenProvider;

    @PersistenceContext
    // Default entity manager, useful for searching.
    private EntityManager entityManager;

    @Autowired
    // Default signed URL service.
    private URLService URLService;

    @Autowired
    // Default mail service.
    private MailService mailService;

    @Value("${app.user.activation-time}")
    private int activationTime;

    @Value("${app.user.reset-time}")
    private int resetTime;

    @Value("${app.user.refresh-time}")
    private int refreshTime;

    @Value("${app.user.remember-me-time}")
    private int rememberMeTime;

    @Value("${app.user.activation-mail}")
    private boolean activationMail;

    private static final User ANONYMOUS = new User()
            .setFirstName("Anonymous").setLastName("IsNotLoggedIn").setRole(Role.READER).setId(-1L);

    public UserServiceImpl(UserRepository repository) {
        this.userRepository = repository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream().filter(user -> !user.isArchived()).collect(Collectors.toList());
    }

    @Override
    public User findById(long id) {
        return getByIdOrThrow(id);
    }

    @Override
    public void updateRole(UpdateRoleDto updateRoleDto) {
        User user = getByIdOrThrow(updateRoleDto.getUserId());
        user.setRole(updateRoleDto.getRole());
        save(user);
    }


    @Override
    public void deleteById(long id) {
        throw new UnsupportedOperationException("use archiveOrDelete instead");
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(long id) {
        User user = getByIdOrThrow(id);
        if (user.isArchived()) {
            throw new UserNotFoundException();
        }

        user.setLanguages(Collections.emptySet());
        user.setPhoneNumber("archived");
        user.getAddress().setStreetWithNumber("archived");
        user.setPicturePermission(false);
        user.setEmail(user.getId() + "archived@example.com");
        user.setPassword("archived");
        user.setRole(Role.READER);
        user.setActivated(false);
        user.setSpecialInfo("archived");
        user.setFirstName("archived");
        user.setLastName("archived");
        user.setArchived(true);
        userRepository.save(user);
        return false;
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(DeleteRequest request, long id) {
        validateDeleteRequest(request);
        return archiveOrDeleteById(id);
    }

    @Override
    public void validateDeleteRequest(DeleteRequest request) {
        if (!passwordEncoder.matches(request.getConfirmMessage(), getCurrentUser().getPassword())) {
            throw new PasswordMismatchException();
        }
    }

    @Override
    public User patch(JsonPatch patch, long id) {
        throw new UnsupportedOperationException("Users are not patchable via JsonPatch.");
    }

    @Override
    public SearchDTO<User> findAll(UserSearchRequest request) {
        return SearchHelper.findAll(request, entityManager, User.class);
    }

    /**
     * Setup login and its response for a user.
     *
     * @param user       The user to log in.
     * @param rememberMe Remember me?
     * @return The login response.
     */
    private LoginResponse setupLoginFor(User user, boolean rememberMe) {
        // Generate a refresh token.
        RefreshToken rt = new RefreshToken();
        String strToken = fillToken(rt, user, rememberMe ? rememberMeTime : refreshTime);
        refreshTokenRepository.save(rt);

        // Can be propagated without issue.
        String token = tokenProvider.createToken(user);
        return new LoginResponse(token, strToken);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // If this fails, will return a 403 to the user.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        return setupLoginFor(user, request.isRememberMe());
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenRepository.deleteByToken(hashBase64Token(request.getRefreshToken()));
    }

    @Override
    @Transactional
    public LoginResponse refresh(RefreshRequest request) {
        byte[] dbToken = hashBase64Token(request.getRefreshToken());
        RefreshToken rt = refreshTokenRepository.findByToken(dbToken).orElseThrow(() -> {
            throw new UnauthorizedException("refresh token invalid");
        });
        refreshTokenRepository.deleteById(rt.getId());
        return setupLoginFor(rt.getUser(), request.isRememberMe());
    }

    /**
     * Hash a buffer using our hash algorithm of choice.
     *
     * @param buffer The buffer
     * @return The hashed buffer
     */
    @SneakyThrows(NoSuchAlgorithmException.class) // This would be a programming error.
    private byte[] hashBuffer(byte[] buffer) {
        MessageDigest sha = MessageDigest.getInstance("SHA-512");
        buffer = sha.digest(buffer);
        return buffer;
    }

    /**
     * Hash a base64 input token, so we can compare against the db.
     *
     * @param token The base64 input token
     * @return The hashed buffer
     */
    private byte[] hashBase64Token(String token) {
        return hashBuffer(Base64.decodeBase64(token));
    }

    /**
     * Fill in token data to base.
     *
     * @param base     Token base object.
     * @param user     User for which this token is assigned.
     * @param validity How long (in seconds) this token is valid.
     * @return The encoded form of the token to be used at client side.
     */
    private String fillToken(TokenBase base, User user, int validity) {
        // Generate a secure token and its hash.
        SecureRandom sr = new SecureRandom();
        byte[] buffer = new byte[TokenBase.NUM_BYTES_SECRET];
        sr.nextBytes(buffer);
        String originalToken = Base64.encodeBase64String(buffer);

        buffer = hashBuffer(buffer);

        // Update database
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validity * 1000);
        base.setUser(user);
        base.setToken(buffer);
        base.setExpiration(expiration);

        return originalToken;
    }

    @Override
    @Transactional
    public void requestPasswordReset(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || user.isArchived()) {
            throw new UserEmailNotFoundException();
        }

        // Generate a secure token and its hash.
        ResetToken rt = new ResetToken();
        String strToken = fillToken(rt, user, resetTime);

        // Update database
        resetTokenRepository.deleteByUser(user);
        resetTokenRepository.save(rt);

        // Setup mail
        Map<String, String> parameters = new HashMap<>();
        parameters.put("email", user.getEmail());
        parameters.put("token", strToken);
        String url = URLService.generate("password-reset", parameters);
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", user.getFullName());
        variables.put("url", url);
        mailService.send("DASY App account", request.getEmail(), "password-reset", variables);
    }

    @Override
    public void doPasswordReset(ResetPasswordActionRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || user.isArchived()) {
            throw new UserEmailNotFoundException();
        }

        byte[] userToken = hashBase64Token(request.getToken());

        ResetToken rt = resetTokenRepository.findByUser(user);
        Date now = new Date();
        if (rt == null || now.getTime() > rt.getExpiration().getTime() || !Arrays.equals(userToken, rt.getToken())) {
            throw new BadRequestException("token", "invalid");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        resetTokenRepository.delete(rt);
        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    public User signup(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new UserEmailAlreadyTakenException();
        }

        user.setActivated(!activationMail);

        if (activationMail) {
            Map<String, String> parameters = Map.of("email", user.getEmail());
            String url = URLService.generateSigned("activate", activationTime, parameters);
            Map<String, Object> variables = Map.of("name", user.getFullName(), "url", url);
            mailService.send("DASY App account", user.getEmail(), "activate", variables);
        }

        // Create user account
        user.setRole(Role.READER);
        user.setRegistrationDate(LocalDate.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public boolean activate(ActivationRequest request) {
        // Parameters from the signed URL.
        Map<String, String> parameters = new HashMap<>();
        parameters.put("email", request.getEmail());
        if (request.getNewEmail() != null)
            parameters.put("newEmail", request.getNewEmail());

        // Check if user exists and is valid request.
        User user = userRepository.findByEmail(request.getEmail());
        if (
            // Don't leak whether an account exists or not.
                user == null
                        // Validate signature
                        || !URLService.validate(request.getSignature(), request.getExpiration(), parameters)
                        // Validate in case of new email address that the email is not yet used
                        || (request.getNewEmail() != null && userRepository.findByEmail(request.getNewEmail()) != null)
        ) {
            return false;
        }

        // Valid request. Activate the user.
        user.setActivated(true);
        // In case of updating an email: this is the completion part: the user clicked a link to activate
        // their account update.
        if (request.getNewEmail() != null)
            user.setEmail(request.getNewEmail());
        userRepository.save(user);

        return true;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(long id, UpdateUserRequest newValues) {
        User oldValues = getByIdOrThrow(id);

        if(newValues.getLanguages() == null || newValues.getLanguages().isEmpty()
        || newValues.getAddress() == null || newValues.getPhoneNumber() == null){
            throw new BadRequestException("updateUser", "invalidFullUserValues");
        }

        // Hibernate expects a modifiable set, and we don't use newValues after this anyway, so no copy required
        oldValues.setLanguages(newValues.getLanguages());
        oldValues.setAddress(newValues.getAddress().toAddress());
        oldValues.setPhoneNumber(newValues.getPhoneNumber());

        oldValues.setFirstName(newValues.getFirstName());
        oldValues.setLastName(newValues.getLastName());
        oldValues.setSpecialInfo(newValues.getSpecialInfo());

        return userRepository.save(oldValues);
    }

    @Override
    @Transactional
    public void updatePassword(UpdatePasswordRequest request, long id) {
        User user = getByIdOrThrow(id);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException();
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        refreshTokenRepository.deleteByTokenNotAndUser(hashBase64Token(request.getRefreshToken()), user);

        Map<String, Object> variables = Map.of("name", user.getFullName());
        mailService.send("DASY App password change", user.getEmail(), "password-update", variables);
    }

    @Override
    public User findByEmail(String email) {
        User user =  userRepository.findByEmail(email);
        if(user == null){
            throw new UserEmailNotFoundException();
        }
        return user;
    }

    @Override
    public void updateEmail(UpdateEmailRequest request) {
        User user = findByEmail(request.getEmail());
        if (userRepository.findByEmail(request.getNewEmail()) != null) {
            throw new UserEmailAlreadyTakenException();
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException();
        }

        // Email to old email address
        Map<String, Object> variables = Map.of("name", user.getFullName(), "email", request.getNewEmail());
        mailService.send("DASY App email change", user.getEmail(), "email-change-old", variables);

        // Email to new email address
        Map<String, String> parameters = Map.of("email", user.getEmail(), "newEmail", request.getNewEmail());
        String url = URLService.generateSigned("activate", activationTime, parameters);
        variables = Map.of("name", user.getFullName(), "email", request.getNewEmail(), "url", url);
        mailService.send("DASY App email change", request.getNewEmail(), "email-change-new", variables);
    }

    /**
     * Retrieves the user that corresponds to the given ID, if it exists
     *
     * @param id The id of the user
     * @return The actual user is present
     * @throws UserNotFoundException if there is no user for the id
     */
    @Override
    public User getByIdOrThrow(long id) throws UserNotFoundException {
        Optional<User> maybeUser = userRepository.findById(id);
        if (maybeUser.isEmpty()) {
            throw new UserNotFoundException();
        } else {
            return maybeUser.get();
        }
    }

    /**
     * Returns the currently signed in user for the request
     *
     * @return The {@link User}, or ANONYMOUS if not logged in
     */
    @Override
    public User getCurrentUser() {
        try {
            // returns the currently signed-in user for the request
            return (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        } catch (Exception e) {
            // there is no signed-in user
            return ANONYMOUS;
        }
    }

    // setters for unit testing purposes only
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setResetTokenRepository(ResetTokenRepository resetTokenRepository) {
        this.resetTokenRepository = resetTokenRepository;
    }

    public void setRefreshTokenRepository(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void setURLService(URLService URLService) {
        this.URLService = URLService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

}
