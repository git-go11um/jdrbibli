package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.entity.PasswordResetToken;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.repository.PasswordResetTokenRepository;
import com.jdrbibli.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {

    private PasswordResetTokenRepository tokenRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        tokenRepository = mock(PasswordResetTokenRepository.class);
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        emailService = mock(EmailService.class);

        passwordResetService = new PasswordResetService();
        // Injection manuelle des dépendances
        passwordResetService.setTokenRepository(tokenRepository);
        passwordResetService.setUserRepository(userRepository);
        passwordResetService.setPasswordEncoder(passwordEncoder);
        passwordResetService.setEmailService(emailService);
    }

    @Test
    void createPasswordResetToken_shouldThrow_whenUserNotFound() {
        String pseudo = "unknownUser";
        when(userRepository.findByPseudo(pseudo)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> passwordResetService.createPasswordResetToken(pseudo));
    }

    @Test
    void createPasswordResetToken_shouldSaveTokenAndSendEmail() {
        String pseudo = "testUser";
        User user = new User();
        user.setEmail("user@test.com");

        when(userRepository.findByPseudo(pseudo)).thenReturn(Optional.of(user));

        passwordResetService.createPasswordResetToken(pseudo);

        // Vérifie que le token a été sauvegardé
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertThat(savedToken.getUser()).isEqualTo(user);
        assertThat(savedToken.getExpiryDate()).isAfter(LocalDateTime.now());

        // Vérifie que l'email a été envoyé
        verify(emailService, times(1)).sendPasswordResetEmail(eq(user.getEmail()), anyString());
    }

    @Test
    void resetPassword_shouldThrow_whenTokenInvalid() {
        String token = "invalid-token";
        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> passwordResetService.resetPassword(token, "newPass"));
    }

    @Test
    void resetPassword_shouldThrow_whenTokenExpired() {
        String token = "expired-token";
        User user = new User();
        PasswordResetToken resetToken = new PasswordResetToken(token, LocalDateTime.now().minusHours(1), user);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        assertThrows(RuntimeException.class, () -> passwordResetService.resetPassword(token, "newPass"));
    }

    @Test
    void resetPassword_shouldUpdatePasswordAndDeleteToken() {
        String token = "valid-token";
        User user = new User();
        user.setPassword("oldPass");
        PasswordResetToken resetToken = new PasswordResetToken(token, LocalDateTime.now().plusHours(1), user);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");

        passwordResetService.resetPassword(token, "newPass");

        // Vérifie que le mot de passe a été mis à jour
        assertThat(user.getPassword()).isEqualTo("encodedPass");

        // Vérifie que l'utilisateur a été sauvegardé
        verify(userRepository, times(1)).save(user);

        // Vérifie que le token a été supprimé
        verify(tokenRepository, times(1)).delete(resetToken);
    }
}
