package xyz.vandijck.safety.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import xyz.vandijck.safety.backend.controller.exceptions.BadRequestException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service that lets its users create URLS and
 * signed URLs: URLs that contain data and a secure signature validating that data.
 */
@Service
public class URLService {
    private final String secret;
    private final String base;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Creates a new URL service.
     *
     * @param secret The secret signing key.
     * @param base   The base URL to append to.
     */
    public URLService(@Value("${app.signed-url-secret}") String secret, @Value("${app.signed-url-base}") String base) {
        this.secret = secret;
        this.base = base;
    }

    /**
     * Sign parameters with HMAC.
     *
     * @param parameters Parameters.
     * @return The signature.
     */
    private String signParameters(Map<String, String> parameters) {
        try {
            // Order of a map is non-deterministic in Java, but we need determinism to calculate the signature.
            LinkedHashMap<String, String> sortedParameters = new LinkedHashMap<>();
            parameters.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> sortedParameters.put(e.getKey(), e.getValue()));

            // Serialize the parameters to JSON.
            String jsonResult = objectMapper.writer().writeValueAsString(sortedParameters);

            // Generate the HMAC
            byte[] key = Base64.decodeBase64(secret);
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA512");
            mac.init(keySpec);
            return Base64.encodeBase64String(mac.doFinal(jsonResult.getBytes()));
        } catch (IllegalArgumentException | JsonProcessingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new BadRequestException("url", "invalid");
        }
    }

    /**
     * Get modified parameters: changed such that the validation parameters are added.
     *
     * @param expiration The expiration time.
     * @param parameters The parameters.
     * @return The modified parameters.
     */
    private Map<String, String> getModifiedParameters(long expiration, Map<String, String> parameters) {
        // Sanity check
        if (parameters.containsKey("signature") || parameters.containsKey("expiration")) {
            throw new IllegalArgumentException();
        }

        // No modification in-place, because that's not in the contract of this method.
        Map<String, String> modified = new HashMap<>(parameters);
        modified.put("expiration", Long.toString(expiration));
        return modified;
    }

    /**
     * Generate signed URL.
     *
     * @param route        The base URL of the route.
     * @param secondsValid The amount of seconds this URL is valid.
     * @param parameters   The URL parameters that need to be signed.
     * @return A signed URL.
     */
    public String generateSigned(String route, int secondsValid, Map<String, String> parameters) {
        // Calculate expiration
        Date now = new Date();
        Date expiration = new Date(now.getTime() + secondsValid * 1000);
        Map<String, String> modified = getModifiedParameters(expiration.getTime(), parameters);

        String signature = signParameters(modified);
        modified.put("signature", signature);

        return generate(route, modified);
    }

    /**
     * Generate URL
     *
     * @param route      Route
     * @param parameters URL parameters
     * @return The URL
     */
    public String generate(String route, Map<String, String> parameters) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(base).path(route);
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            builder.queryParam(entry.getKey(), URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return builder.toUriString();
    }

    /**
     * String equality check that is safe against timing side channels.
     *
     * @param a String a.
     * @param b String b.
     * @return True if equal, false otherwise.
     */
    private boolean timingSecureStringCheck(String a, String b) {
        // Can only leak the length of the string.
        if (a.length() != b.length())
            return false;

        int result = 0;
        for (int i = 0, l = a.length(); i < l; ++i) {
            result |= a.charAt(i) ^ b.charAt(i);
        }

        return result == 0;
    }

    /**
     * Validate the signed URL: valid signature and not expired.
     *
     * @param signature  The signature.
     * @param expiration Expiration time.
     * @param parameters The URL parameters that were signed.
     * @return True if valid, false otherwise.
     */
    public boolean validate(String signature, long expiration, Map<String, String> parameters) {
        // Expiration check.
        Date now = new Date();
        if (now.getTime() > expiration) {
            return false;
        }

        // Check the parameters' signature.
        String mySignature = signParameters(getModifiedParameters(expiration, parameters));
        return timingSecureStringCheck(mySignature, signature);
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
