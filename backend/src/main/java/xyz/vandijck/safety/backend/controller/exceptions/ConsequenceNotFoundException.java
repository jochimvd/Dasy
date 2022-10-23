package xyz.vandijck.safety.backend.controller.exceptions;

public class ConsequenceNotFoundException extends NotFoundException {

    private static final long serialVersionUID = -2178788639476438693L;

    public ConsequenceNotFoundException() {
        super("Consequence");
    }

}
