package xyz.vandijck.safety.backend.service;

import xyz.vandijck.safety.backend.controller.exceptions.EmailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Service responsible for sending mails.
 */
@Service
public class MailServiceImpl implements MailService {
    private JavaMailSender sender;
    private SpringTemplateEngine templateEngine;

    /**
     * Creates a new mail service.
     *
     * @param sender         The interface responsible for actually sending the mails.
     * @param templateEngine The template engine to be used to render the mails.
     */
    public MailServiceImpl(JavaMailSender sender, SpringTemplateEngine templateEngine) {
        this.sender = sender;
        this.templateEngine = templateEngine;
    }

    @Override
    @Async
    public void send(String subject, String to, String template, Map<String, Object> variables) {
        try {
            Context ctx = new Context();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            ctx.setVariables(variables);

            helper.setSubject(subject);
            helper.setTo(to);
            helper.setText(templateEngine.process(template, ctx), true);

            sender.send(msg);
        } catch (MessagingException e) {
            throw new EmailException(e);
        }
    }

    /**
     * Set sender, useful for testing.
     *
     * @param sender The sender
     */
    @Override
    public void setSender(JavaMailSender sender) {
        this.sender = sender;
    }
}
