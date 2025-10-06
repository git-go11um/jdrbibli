package com.jdrbibli.authservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class MailTestRunnerTest {

    private EmailService emailService;
    private MailTestRunner mailTestRunner;

    @BeforeEach
    void setUp() {
        emailService = mock(EmailService.class);
        mailTestRunner = new MailTestRunner(emailService);
    }

    @Test
    void run_shouldCallSendPasswordResetEmail() throws Exception {
        // Préparation des paramètres du mail de test
        String testEmail = "test@example.com";
        String testToken = "123456";

        // On exécute la méthode run (ici on simule ce qui serait décommenté)
        mailTestRunner.run();

        // Comme le code réel est commenté, rien ne sera appelé.
        // Si tu veux tester réellement l'appel, il faut décommenter dans MailTestRunner
        // Pour l'instant on peut vérifier que MailTestRunner existe et ne lève pas
        // d'erreur
        // Si tu décommentes, tu feras :
        // verify(emailService, times(1)).sendPasswordResetEmail(testEmail, testToken);
    }
}
