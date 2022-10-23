package xyz.vandijck.safety.backend.controller.exceptions;

public class PasswordMismatchException extends BadRequestException {
    private static final long serialVersionUID = 5972519744847104684L;

    public PasswordMismatchException() {
        super("password", "passwordMismatch");
    }
}

