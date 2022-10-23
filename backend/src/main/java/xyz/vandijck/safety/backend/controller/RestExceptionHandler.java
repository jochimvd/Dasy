package xyz.vandijck.safety.backend.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.search.exception.SearchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import xyz.vandijck.safety.backend.controller.exceptions.BadRequestException;
import xyz.vandijck.safety.backend.controller.exceptions.InternalJsonProcessingException;
import xyz.vandijck.safety.backend.controller.exceptions.NotFoundException;
import xyz.vandijck.safety.backend.controller.exceptions.UnauthorizedException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // fixed string as error-key in Error-response
    private static final String ERROR = "error";

    /**
     * Custom response for BindException (object could not be bound to input object).
     *
     * @param ex      The exception
     * @param headers The headers to be written to the response
     * @param status  The selected response status
     * @param request The current request
     * @return A response for this exception.
     */
    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleValidationCommon(ex.getBindingResult(), headers, status);
    }

    /**
     * Custom response for MethodArgumentNotValidException (validation of fields failed for input object).
     *
     * @param ex      The exception
     * @param headers The headers to be written to the response
     * @param status  The selected response status
     * @param request The current request
     * @return A response for this exception.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleValidationCommon(ex.getBindingResult(), headers, status);
    }

    /**
     * Custom response for HttpMessageNotReadableException.
     *
     * @param ex      The exception
     * @param headers The headers to be written to the response
     * @param status  The selected response status
     * @param request The current request
     * @return A response for this exception.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException cause = (InvalidFormatException) ex.getCause();
            Map<String, String> errors = cause.getPath().stream().collect(Collectors.toMap(JsonMappingException.Reference::getFieldName, e -> "missing"));
            return handleValidationCommon(errors, headers, status);
        } else {
            return handleExceptionInternal(ex, null, headers, status, request);
        }
    }

    /**
     * Common validation exception handler.
     *
     * @param result  Binding result of validation.
     * @param headers The headers to be written to the response
     * @param status  The selected response status
     * @return An object containing the errors.
     */
    private ResponseEntity<Object> handleValidationCommon(BindingResult result, HttpHeaders headers, HttpStatus status) {
        return handleValidationCommon(
                result.getFieldErrors().stream().collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> {
                            String defaultMessage = fe.getDefaultMessage();
                            return Objects.requireNonNullElseGet(
                                    defaultMessage,
                                    () -> "Supplied value for " + fe.getField() + " was invalid.");
                        },
                        (v1, v2) -> {
                            logger.warn("Multiple values detected for the same field (" + v1 + ", " + v2 + ")");
                            return v1;
                        })), headers, status
        );
    }

    /**
     * Creates a Map that represents a json response body with a given status
     *
     * @param status The status of the response
     * @return The map representing a json
     */
    private Map<String, Object> createBody(HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put(ERROR, status.getReasonPhrase());
        return body;
    }

    /**
     * Common validation exception handler.
     *
     * @param errors  A Map of strings that represent the fields that cause the errors and the corresponding reason.
     * @param headers The headers to be written to the response
     * @param status  The selected response status
     * @return An object containing the errors.
     */
    private ResponseEntity<Object> handleValidationCommon(Map<String, String> errors, HttpHeaders headers, HttpStatus status) {
        Map<String, Object> body = createBody(status);
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }

    /**
     * Converts not found errors to HTTP answers.
     *
     * @param ex      The exception
     * @param request The current request
     * @return A response for this exception.
     */
    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(Exception ex, WebRequest request) {
        Map<String, Object> body = createBody(HttpStatus.NOT_FOUND);

        body.put(ERROR, ((BadRequestException) ex).getKey() + " " + body.get(ERROR));
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Converts mismatches and violations to bad requests.
     *
     * @param ex      The exception
     * @param request The current request
     * @return A response for this exception.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex, WebRequest request) {
        return handleValidationCommon(Map.of(ex.getKey(), ex.getValue()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        return handleExceptionInternal(ex, createBody(HttpStatus.UNAUTHORIZED), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    protected ResponseEntity<Object> handleUnsupportedOperationException(UnsupportedOperationException ex, WebRequest request) {
        return handleExceptionInternal(ex, createBody(HttpStatus.FORBIDDEN), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    /**
     * Internal exception handler.
     *
     * @param ex      The exception
     * @param body    The body for the response
     * @param headers The headers for the response
     * @param status  The response status
     * @param request The current request
     * @return The response.
     */
    @ExceptionHandler({
            ConstraintViolationException.class,
            DataIntegrityViolationException.class,
            SearchException.class,
            InternalJsonProcessingException.class
    })
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.debug(ex);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }
}
