package com.jdrbibli.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service pour l'envoi d'emails.
 * Utilisé principalement pour envoyer des emails de réinitialisation de mot de
 * passe.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envoie un email pour la réinitialisation du mot de passe.
     *
     * @param toEmail l'adresse email du destinataire
     * @param token   le code de réinitialisation à inclure dans l'email
     */
    public void sendPasswordResetEmail(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();

        // Destinataire
        message.setTo(toEmail);
        // Sujet de l'email
        message.setSubject("Réinitialisation de votre mot de passe");

        // Contenu de l'email
        String text = "Bonjour,\n\n"
                + "Vous avez demandé à réinitialiser votre mot de passe.\n"
                + "Voici votre code de réinitialisation à copier/coller :\n\n"
                + token + "\n\n"
                + "Ce code est valable pendant 24 heures.\n\n"
                + "Si vous n'êtes pas à l'origine de cette demande, veuillez ignorer ce message.\n\n"
                + "Cordialement,\n"
                + "L'équipe JdrBibli";

        message.setText(text);

        // Envoi de l'email
        mailSender.send(message);
    }
}
