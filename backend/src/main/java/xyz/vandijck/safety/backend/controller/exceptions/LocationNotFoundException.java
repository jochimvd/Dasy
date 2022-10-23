package xyz.vandijck.safety.backend.controller.exceptions;

public class LocationNotFoundException extends NotFoundException {

    private static final long serialVersionUID = -2178788638995438693L;

    public LocationNotFoundException() {
        super("Location");
    }

}
