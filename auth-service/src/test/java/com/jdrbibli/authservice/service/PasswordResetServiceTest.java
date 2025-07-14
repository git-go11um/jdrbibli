package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.entity.PasswordResetToken;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.repository.PasswordResetTokenRepository;
import com.jdrbibli.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPasswordResetToken_UserExists_ShouldCreateTokenAndSendEmail() {
        String pseudo = "testUser";
        User user = new User();
        user.setPseudo(pseudo);
        user.setEmail("test@example.com");

        when(userRepository.findByPseudo(pseudo)).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(i -> i.getArgument(0));

        passwordResetService.createPasswordResetToken(pseudo);

        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1)).sendPasswordResetEmail(eq(user.getEmail()), anyString());
    }

    @Test
    void createPasswordResetToken_UserNotFound_ShouldThrowException() {
        String pseudo = "unknownUser";

        when(userRepository.findByPseudo(pseudo)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            passwordResetService.createPasswordResetToken(pseudo);
        });

        assertEquals("Utilisateur non trouvé avec ce pseudo.", ex.getMessage());
        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void resetPassword_ValidToken_ShouldUpdatePasswordAndDeleteToken() {
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";
        User user = new User();
        user.setPassword("oldHashedPassword");

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        resetToken.setUser(user);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(newPassword)).thenReturn("hashedNewPassword");

        passwordResetService.resetPassword(token, newPassword);

        assertEquals("hashedNewPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(tokenRepository, times(1)).delete(resetToken);
    }

    @Test
    void resetPassword_TokenExpired_ShouldThrowException() {
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";
        User user = new User();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().minusHours(1)); // déjà expiré
        resetToken.setUser(user);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            passwordResetService.resetPassword(token, newPassword);
        });

        assertEquals("Le token est expiré.", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    void resetPassword_TokenInvalid_ShouldThrowException() {
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword123";

        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            passwordResetService.resetPassword(token, newPassword);
        });

        assertEquals("Token invalide.", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }
}
