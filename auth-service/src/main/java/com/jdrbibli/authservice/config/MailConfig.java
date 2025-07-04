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

        mailSender.setHost("sandbox.smtp.mailtrap.io");
        mailSender.setPort(2525);

        mailSender.setUsername("v.haumont@gmail.com"); // ton username Mailtrap
        mailSender.setPassword("0ul@l@5&cur1T&");   // ton password Mailtrap

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS activé
        props.put("mail.debug", "true"); // utile pour debug

        return mailSender;
    }
}
