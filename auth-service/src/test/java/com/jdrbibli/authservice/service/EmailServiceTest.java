package com.jdrbibli.authservice.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void testSendPasswordResetEmail() {
        String toEmail = "user@example.com";
        String token = "123456";

        emailService.sendPasswordResetEmail(toEmail, token);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals(toEmail, sentMessage.getTo()[0]);
        assertEquals("Réinitialisation de votre mot de passe", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains(token));
        assertTrue(sentMessage.getText().contains("valable pendant 24 heures"));
    }
}
