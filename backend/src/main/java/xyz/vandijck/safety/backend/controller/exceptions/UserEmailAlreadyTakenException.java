package xyz.vandijck.safety.backend.controller.exceptions;

public class UserEmailAlreadyTakenException extends BadRequestException {

    public UserEmailAlreadyTakenException(){
        super("email", "emailTaken");
    }
}
