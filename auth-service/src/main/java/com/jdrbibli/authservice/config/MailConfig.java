package com.jdrbibli.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("localhost"); // Fake SMTP
        mailSender.setPort(25);          // port où Fake SMTP écoute

        mailSender.setUsername("");      // inutile
        mailSender.setPassword("");      // inutile

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "false"); // pas d'auth
        props.put("mail.smtp.starttls.enable", "false"); // pas de TLS
        props.put("mail.debug", "true"); // logs utiles

        return mailSender;
    }
}
