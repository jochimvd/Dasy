package xyz.vandijck.safety.backend.controller.exceptions;

public class UserEmailNotFoundException extends NotFoundException {

    private static final long serialVersionUID = 8657014963531193207L;

    public UserEmailNotFoundException() {
        super("userEmail");
    }
}
