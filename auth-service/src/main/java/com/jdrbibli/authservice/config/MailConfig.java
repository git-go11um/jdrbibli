package com.jdrbibli.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration du service d'envoi d'e-mails.
 * 
 * Cette classe fournit un {@link JavaMailSender} configuré pour un serveur SMTP local.
 * Dans le contexte actuel, il s'agit d'un serveur SMTP fictif (Fake SMTP) pour les tests.
 * 
 */
@Configuration
public class MailConfig {

    /**
     * Configure et retourne un {@link JavaMailSender}.
     * 
     * La configuration utilise :
     * <ul>
     *     <li>Host : localhost</li>
     *     <li>Port : 25</li>
     *     <li>Pas d'authentification SMTP</li>
     *     <li>Pas de TLS</li>
     *     <li>Logs activés pour le débogage</li>
     * </ul>
     *
     * @return un {@link JavaMailSender} prêt à l'utilisation pour l'envoi d'e-mails
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("localhost"); // Fake SMTP
        mailSender.setPort(25);          // port où Fake SMTP écoute


        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "false"); // pas d'auth
        props.put("mail.smtp.starttls.enable", "false"); // pas de TLS
        props.put("mail.debug", "true"); // logs utiles

        return mailSender;
    }
}
