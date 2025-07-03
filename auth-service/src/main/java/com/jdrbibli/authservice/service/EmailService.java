package com.jdrbibli.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Réinitialisation de votre mot de passe");
        
        String text = "Bonjour,\n\n"
                + "Vous avez demandé à réinitialiser votre mot de passe.\n"
                + "Voici votre code de réinitialisation à copier/coller :\n\n"
                + token + "\n\n"
                + "Ce code est valable pendant 24 heures.\n\n"
                + "Si vous n'êtes pas à l'origine de cette demande, veuillez ignorer ce message.\n\n"
                + "Cordialement,\n"
                + "L'équipe JdrBibli";
        
        message.setText(text);

        mailSender.send(message);
    }
}
