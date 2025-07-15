package com.jdrbibli.authservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.repository.UserRepository;
import com.jdrbibli.authservice.repository.PasswordResetTokenRepository;

import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRequestPasswordReset() throws Exception {
        String pseudo = "testuser";
        User user = new User();
        user.setPseudo(pseudo);
        user.setEmail("testuser@example.com");

        when(userRepository.findByPseudo(pseudo)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // On appelle la méthode à tester
        userService.requestPasswordReset(pseudo);

        // Vérifier que l'utilisateur a un resetCode non null et expiration mise à jour
        assertNotNull(user.getResetCode());
        assertNotNull(user.getResetPasswordCodeExpiration());
        assertTrue(user.getResetPasswordCodeExpiration() > System.currentTimeMillis());

        // Vérifier que userRepository.save a été appelé pour sauvegarder le user
        // modifié
        verify(userRepository, times(1)).save(user);

        // Vérifier que mailSender.send a été appelé une fois avec mimeMessage
        verify(mailSender, times(1)).send(mimeMessage);
    }
}
