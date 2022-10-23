package xyz.vandijck.safety.backend.controller.exceptions;

import javax.mail.MessagingException;

public class EmailException extends RuntimeException {
    private static final long serialVersionUID = 5201287275085517635L;

    public EmailException(MessagingException e) {
        super(e);
    }
}
