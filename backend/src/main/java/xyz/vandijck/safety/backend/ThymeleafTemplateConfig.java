package xyz.vandijck.safety.backend;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;

/**
 * Configuration for the Thymeleaf templating engine.
 * Used for mails in our case.
 */
@Configuration
public class ThymeleafTemplateConfig
{
    /**
     * HTML template settings and location resolver.
     *
     * @return A template resolver.
     */
    @Bean
    public SpringResourceTemplateResolver htmlTemplateResolver()
    {
        SpringResourceTemplateResolver emailTemplateResolver = new SpringResourceTemplateResolver();
        emailTemplateResolver.setPrefix("classpath:/emails/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
        emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return emailTemplateResolver;
    }

    /**
     * Default configuration of the template engine.
     *
     * @return Template engine configuration.
     */
    @Bean
    public SpringTemplateEngine springTemplateEngine()
    {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }
}
