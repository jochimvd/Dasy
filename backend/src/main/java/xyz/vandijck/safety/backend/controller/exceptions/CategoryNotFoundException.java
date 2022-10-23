package xyz.vandijck.safety.backend.controller.exceptions;

public class CategoryNotFoundException extends NotFoundException {

    private static final long serialVersionUID = -2178788634515438693L;

    public CategoryNotFoundException() {
        super("Category");
    }

}
