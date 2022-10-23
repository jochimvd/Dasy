package xyz.vandijck.safety.backend.service;

import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;

public interface MailService {
    /**
     * Sends a mail.
     *
     * @param subject   Subject of the mail.
     * @param to        Receiver of the mail.
     * @param template  The mail HTML template.
     * @param variables Map of variables in the template.
     */
    void send(String subject, String to, String template, Map<String, Object> variables);

    /**
     * Set the sender.
     *
     * @param sender The sender.
     */
    void setSender(JavaMailSender sender);
}
