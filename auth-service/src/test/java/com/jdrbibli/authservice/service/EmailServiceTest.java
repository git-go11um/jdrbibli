package com.jdrbibli.authservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
    }

    @Test
    void sendPasswordResetEmail_shouldSendEmailWithCorrectFields() {
        String toEmail = "user@test.com";
        String token = "123456";

        emailService.sendPasswordResetEmail(toEmail, token);

        // Capture du message envoyé
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();

        assertThat(sentMessage.getTo()).containsExactly(toEmail);
        assertThat(sentMessage.getSubject()).isEqualTo("Réinitialisation de votre mot de passe");
        assertThat(sentMessage.getText()).contains(token);
        assertThat(sentMessage.getText()).contains("Bonjour");
        assertThat(sentMessage.getText()).contains("L'équipe JdrBibli");
    }
}
