package com.jdrbibli.authservice.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Composant pour tester l'envoi d'email au démarrage de l'application.
 * Utile pour vérifier que EmailService fonctionne correctement.
 */
@Component
public class MailTestRunner implements CommandLineRunner {

    private final EmailService emailService;

    public MailTestRunner(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Exemple de test : envoi d'un mail de réinitialisation
        // Décommente pour tester au démarrage
        /*
        String testEmail = "test@example.com";
        String testToken = "123456";
        emailService.sendPasswordResetEmail(testEmail, testToken);
        System.out.println("✅ Email de test envoyé à " + testEmail);
        */
    }
}
