package com.jdrbibli.authservice.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MailTestRunner implements CommandLineRunner {

    private final EmailService emailService;

    public MailTestRunner(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void run(String... args) throws Exception {
/*         emailService.sendTestEmail();   */ // Envoi du mail de test au démarrage
    }
}

