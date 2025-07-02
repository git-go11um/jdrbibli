package com.jdrbibli.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Réinitialisation de votre mot de passe";
        String resetUrl = "http://localhost:4200/reset-password?token=" + token;
        String text = "Bonjour,\n\n" +
                "Vous avez demandé à réinitialiser votre mot de passe.\n" +
                "Veuillez cliquer sur le lien suivant pour le faire :\n" + resetUrl + "\n\n" +
                "Ce lien est valide pendant 24 heures.\n\n" +
                "Si vous n'êtes pas à l'origine de cette demande, ignorez ce message.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}
