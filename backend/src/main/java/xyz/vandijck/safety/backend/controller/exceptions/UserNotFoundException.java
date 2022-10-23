package xyz.vandijck.safety.backend.controller.exceptions;

public class UserNotFoundException extends NotFoundException {

    private static final long serialVersionUID = 1608211377934169677L;

    public UserNotFoundException() {
        super("user");
    }

}
