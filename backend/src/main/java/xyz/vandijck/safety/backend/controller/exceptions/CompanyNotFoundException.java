package xyz.vandijck.safety.backend.controller.exceptions;

public class CompanyNotFoundException extends NotFoundException {

    private static final long serialVersionUID = -2178298338995438693L;

    public CompanyNotFoundException() {
        super("Company");
    }

}
