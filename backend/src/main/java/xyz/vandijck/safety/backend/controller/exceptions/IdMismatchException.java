package xyz.vandijck.safety.backend.controller.exceptions;

public class IdMismatchException extends BadRequestException {

    private static final long serialVersionUID = -608667539965558508L;

    public IdMismatchException() {
        super("id",  "mismatch");
    }

}
