package xyz.vandijck.safety.backend.controller.exceptions;

public class TypeNotFoundException extends NotFoundException {

    private static final long serialVersionUID = -2178788639479456293L;

    public TypeNotFoundException() {
        super("Type");
    }

}
