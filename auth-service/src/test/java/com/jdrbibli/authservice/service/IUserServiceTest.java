/* package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.client.AuditClient;
import com.jdrbibli.authservice.dto.*;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.exception.UserNotFoundException;
import com.jdrbibli.authservice.repository.UserRepository;
import com.jdrbibli.authservice.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.mail.MessagingException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private AuditClient auditClient;

    @InjectMocks
    private UserService userService; // Mockito injecte automatiquement les mocks

    @Test
    void inscrireNewUser_shouldReturnCreatedUser() {
        User user = new User();
        user.setPseudo("testUser");
        user.setEmail("test@example.com");
        user.setPassword("Password1!");

        when(passwordEncoder.encode("Password1!")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User created = userService.inscrireNewUser("testUser", "test@example.com", "Password1!");

        assertThat(created.getPseudo()).isEqualTo("testUser");
        assertThat(created.getEmail()).isEqualTo("test@example.com");

        verify(auditClient).logEvent(anyString(), anyString(), anyString());
    }

    @Test
    void login_shouldReturnUser_whenCredentialsValid() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password1!", "hashedPassword")).thenReturn(true);

        User loggedIn = userService.login("test@example.com", "Password1!");

        assertThat(loggedIn).isEqualTo(user);
        verify(auditClient).logEvent(anyString(), anyString(), anyString());
    }

    @Test
    void changePassword_shouldCallRepository() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setNewPassword("NewPass1!");
        request.setConfirmNewPassword("NewPass1!");

        User user = new User();
        user.setEmail("user@test.com");
        user.setPassword("oldHash");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewPass1!")).thenReturn("encodedNewPass");

        userService.changePassword("user@test.com", request);

        verify(userRepository).save(user);
        verify(auditClient).logEvent(anyString(), anyString(), anyString());
    }

    @Test
    void requestPasswordReset_shouldCallEmailService() throws MessagingException {
        User user = new User();
        user.setPseudo("userTest");

        when(userRepository.findByPseudo("userTest")).thenReturn(Optional.of(user));

        userService.requestPasswordReset("userTest");

        verify(emailService).sendPasswordResetEmail(anyString(), anyString());
        verify(auditClient).logEvent(anyString(), anyString(), contains("userTest"));
    }

    @Test
    void updateUserProfile_shouldReturnResponseWithNewToken() {
        User user = new User();
        user.setId(1L);
        user.setPseudo("oldPseudo");
        user.setEmail("old@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtTokenProvider.createToken("newPseudo")).thenReturn("jwtToken");

        ReponseProfileChange response = userService.updateUserProfile(1L, "newPseudo", "new@example.com");

        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getNewToken()).isEqualTo("jwtToken");

        verify(auditClient).logEvent(anyString(), anyString(), anyString());
    }

    @Test
    void changeProfilePassword_shouldUpdateUserPassword() {
        User user = new User();
        user.setPseudo("userTest");
        user.setPassword("oldHash");

        ChangePasswordProfileRequest request = new ChangePasswordProfileRequest();
        request.setCurrentPassword("OldPass1!");
        request.setNewPassword("NewPass1!");
        request.setConfirmNewPassword("NewPass1!");

        when(userRepository.findByPseudo("userTest")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("OldPass1!", "oldHash")).thenReturn(true);
        when(passwordEncoder.encode("NewPass1!")).thenReturn("encodedNewPass");

        userService.changeProfilePassword("userTest", request);

        verify(userRepository).save(user);
        verify(auditClient).logEvent(anyString(), anyString(), anyString());
    }

    @Test
    void getUserByPseudo_shouldThrowException_whenNotFound() {
        when(userRepository.findByPseudo("unknown")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserByPseudo("unknown"));
    }
}
 */