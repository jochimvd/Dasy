package xyz.vandijck.safety.backend.controller.exceptions;

public class SeverityNotFoundException extends NotFoundException {

    private static final long serialVersionUID = -2178788408954338693L;

    public SeverityNotFoundException() {
        super("Severity");
    }

}
