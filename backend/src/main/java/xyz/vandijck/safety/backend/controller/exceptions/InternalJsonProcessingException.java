package xyz.vandijck.safety.backend.controller.exceptions;

public class InternalJsonProcessingException extends RuntimeException {

    public InternalJsonProcessingException(String message) {
        super(message);
    }
}
