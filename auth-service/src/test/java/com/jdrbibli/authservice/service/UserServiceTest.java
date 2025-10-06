package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.client.AuditClient;
import com.jdrbibli.authservice.dto.*;
import com.jdrbibli.authservice.entity.Role;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.exception.BadCredentialsException;
import com.jdrbibli.authservice.exception.UserNotFoundException;
import com.jdrbibli.authservice.repository.PasswordResetTokenRepository;
import com.jdrbibli.authservice.repository.UserRepository;
import com.jdrbibli.authservice.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private AuditClient auditClient;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "john", "john@example.com", "hashedpwd", new HashSet<>(), null, null);
        // Forcer l'injection du mock auditClient pour éviter NullPointerException
        ReflectionTestUtils.setField(userService, "auditClient", auditClient);
    }

    @Test
    void testInscrireNewUser() {
        when(passwordEncoder.encode("Password1!")).thenReturn("hashedpwd");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.inscrireNewUser("john", "john@example.com", "Password1!");

        assertNotNull(result);
        assertEquals("john", result.getPseudo());
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditClient).logEvent(eq("auth-service"), eq("USER_CREATED"), contains("john"));
    }

    @Test
    void testLoginSuccess() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password1!", "hashedpwd")).thenReturn(true);

        User result = userService.login("john@example.com", "Password1!");
        assertEquals(testUser, result);
    }

    @Test
    void testLoginWrongPassword() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", "hashedpwd")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.login("john@example.com", "wrong"));
    }

    @Test
    void testGetUserByIdFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        User result = userService.getUserById(1L);
        assertEquals(testUser, result);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void testToDTO() {
        Role role = new Role("ADMIN");
        testUser.getRoles().add(role);

        UserResponseDTO dto = userService.toDTO(testUser);
        assertEquals(testUser.getId(), dto.getId());
        assertEquals(testUser.getPseudo(), dto.getPseudo());
        assertTrue(dto.getRoles().contains("ADMIN"));
    }

    // -------------------------
    // changePassword
    // -------------------------
    @Test
    void testChangePasswordSuccess() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setNewPassword("NewPass1!");
        request.setConfirmNewPassword("NewPass1!");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("NewPass1!")).thenReturn("encodedNewPass");

        userService.changePassword("john@example.com", request);

        assertEquals("encodedNewPass", testUser.getPassword());
        verify(userRepository).save(testUser);
        verify(auditClient).logEvent(eq("auth-service"), eq("PASSWORD_CHANGED"), contains("john"));
    }

    @Test
    void testChangePasswordMismatchThrows() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setNewPassword("NewPass1!");
        request.setConfirmNewPassword("DiffPass1!");

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword("john@example.com", request));
        assertEquals("Les deux mots de passe ne correspondent pas", ex.getMessage());
    }

    // -------------------------
    // validateResetCode
    // -------------------------
    @Test
    void testValidateResetCodeSuccess() {
        testUser.setResetCode("ABC123");
        testUser.setResetPasswordCodeExpiration(System.currentTimeMillis() + 10000);
        when(userRepository.findByPseudo("john")).thenReturn(Optional.of(testUser));

        boolean valid = userService.validateResetCode("john", "ABC123");
        assertThat(valid).isTrue();
    }

    @Test
    void testValidateResetCodeExpiredOrMismatch() {
        testUser.setResetCode("ABC123");
        testUser.setResetPasswordCodeExpiration(System.currentTimeMillis() - 1000);
        when(userRepository.findByPseudo("john")).thenReturn(Optional.of(testUser));

        boolean valid = userService.validateResetCode("john", "ABC123");
        assertThat(valid).isFalse();

        valid = userService.validateResetCode("john", "WRONG");
        assertThat(valid).isFalse();
    }

    // -------------------------
    // resetPassword
    // -------------------------
    @Test
    void testResetPasswordSuccess() {
        testUser.setResetCode("CODE123");
        testUser.setResetPasswordCodeExpiration(System.currentTimeMillis() + 5000);
        when(userRepository.findByPseudo("john")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("NewPass1!")).thenReturn("encodedNewPass");

        userService.resetPassword("john", "CODE123", "NewPass1!");

        assertEquals("encodedNewPass", testUser.getPassword());
        assertNull(testUser.getResetCode());
        assertNull(testUser.getResetPasswordCodeExpiration());
        verify(userRepository).save(testUser);
        verify(auditClient).logEvent(eq("auth-service"), eq("PASSWORD_RESET"), contains("john"));
    }

    @Test
    void testResetPasswordInvalidCodeThrows() {
        testUser.setResetCode("CODE123");
        testUser.setResetPasswordCodeExpiration(System.currentTimeMillis() + 5000);
        when(userRepository.findByPseudo("john")).thenReturn(Optional.of(testUser));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> userService.resetPassword("john", "WRONGCODE", "NewPass1!"));
        assertEquals("Le code de réinitialisation est invalide ou a expiré.", ex.getMessage());
    }

    // -------------------------
    // updateUserProfile
    // -------------------------
    @Test
    void testUpdateUserProfileSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.createToken("newJohn")).thenReturn("jwtToken");

        ReponseProfileChange response = userService.updateUserProfile(1L, "newJohn", "new@example.com");

        assertEquals("Profil mis à jour avec succès.", response.getMessage());
        assertEquals("jwtToken", response.getNewToken());
        assertEquals("newJohn", testUser.getPseudo());
        assertEquals("new@example.com", testUser.getEmail());
        verify(userRepository).save(testUser);
        verify(auditClient).logEvent(eq("auth-service"), eq("USER_UPDATED"), contains("id: 1"));
    }

    @Test
    void testUpdateUserProfileNoChange() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ReponseProfileChange response = userService.updateUserProfile(1L, "john", "john@example.com");

        assertEquals("Aucun changement détecté.", response.getMessage());
        assertNull(response.getNewToken());
        verify(userRepository, never()).save(any());
    }
}
