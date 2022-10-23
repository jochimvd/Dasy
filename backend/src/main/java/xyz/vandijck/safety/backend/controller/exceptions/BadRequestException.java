package xyz.vandijck.safety.backend.controller.exceptions;

/**
 * Generic bad request.
 */
public class BadRequestException extends RuntimeException
{
    private final String key;
    private final String value;

    /**
     * Creates a new bad request exception.
     *
     * @param key   The key (see API specs).
     * @param value A short symbolic error meaning. (e.g. "emailInvalid", "emailTaken", ...)
     */
    public BadRequestException(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    /**
     * Creates a new bad request exception based on another {@link BadRequestException}.
     *
     * @param ex The {@link BadRequestException} where both the key and the value will be copied from.
     */
    public BadRequestException(BadRequestException ex) {
        this(ex.getKey(), ex.getValue());
    }

    /**
     * Gets the key
     *
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the value
     *
     * @return The value
     */
    public String getValue()
    {
        return value;
    }
}
