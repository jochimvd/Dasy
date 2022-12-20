package xyz.vandijck.safety.backend.controller.exceptions;

public class SiteNotFoundException extends NotFoundException {

    private static final long serialVersionUID = -2178788638995438693L;

    public SiteNotFoundException() {
        super("Site");
    }

}
