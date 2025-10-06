package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.*;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.security.JwtService;
import com.jdrbibli.authservice.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;

class AuthControllerTest {

    private AuthController authController;
    private IUserService userService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        userService = mock(IUserService.class);
        authenticationManager = mock(AuthenticationManager.class);
        jwtService = mock(JwtService.class);
        WebClient webClient = mock(WebClient.class);

        authController = new AuthController(userService, authenticationManager, jwtService, null, webClient);
    }

    @Test
    void requestPasswordReset_shouldReturnOk() throws Exception {
        doNothing().when(userService).requestPasswordReset("user");

        ResponseEntity<?> response = authController.requestPasswordReset(Map.of("pseudo", "user"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void validateResetCode_shouldReturnOk_whenValid() throws Exception {
        when(userService.validateResetCode("user", "1234")).thenReturn(true);

        PasswordResetRequest request = new PasswordResetRequest();
        request.setPseudo("user");
        request.setCode("1234");

        ResponseEntity<?> response = authController.validateResetCode(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void validateResetCode_shouldReturnBadRequest_whenInvalid() throws Exception {
        when(userService.validateResetCode("user", "1234")).thenReturn(false);

        PasswordResetRequest request = new PasswordResetRequest();
        request.setPseudo("user");
        request.setCode("1234");

        ResponseEntity<?> response = authController.validateResetCode(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void resetPassword_shouldReturnOk_whenSuccess() throws Exception {
        doNothing().when(userService).resetPassword("user", "code", "newPass");

        PasswordResetRequest request = new PasswordResetRequest();
        request.setPseudo("user");
        request.setCode("code");
        request.setNewPassword("newPass");

        ResponseEntity<?> response = authController.resetPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void updateUserProfile_shouldReturnOk_whenSuccess() throws Exception {
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user");

        User user = new User();
        user.setId(1L);
        when(userService.getUserByPseudo("user")).thenReturn(user);

        ReponseProfileChange responseChange = new ReponseProfileChange();
        responseChange.setMessage("ok");
        when(userService.updateUserProfile(anyLong(), any(), any())).thenReturn(responseChange);

        when(jwtService.generateToken("newPseudo", 1L)).thenReturn("token");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPseudo("newPseudo");
        request.setEmail("newEmail@test.com");

        ResponseEntity<?> response = authController.updateUserProfile(request, auth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void changeProfilePassword_shouldReturnOk_whenSuccess() throws Exception {
        // Mock Authentication
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user");

        // Mock User
        User user = new User();
        user.setId(1L);
        user.setPseudo("user");
        when(userService.getUserByPseudo("user")).thenReturn(user);

        // Mock service calls
        doNothing().when(userService).changeProfilePassword(eq("user"), any(ChangePasswordProfileRequest.class));
        when(jwtService.generateToken("user", 1L)).thenReturn("token");

        // Création de l'objet de requête avec setters
        ChangePasswordProfileRequest req = new ChangePasswordProfileRequest();
        req.setCurrentPassword("old");
        req.setNewPassword("new");
        req.setConfirmNewPassword("new");

        // Appel de la méthode
        ResponseEntity<?> response = authController.changeProfilePassword(req, auth);

        // Assertions
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse body = (ApiResponse) response.getBody();
        assertThat(body.isSuccess()).isTrue();
        assertThat(body.getToken()).isEqualTo("token");
    }

    @Test
    void deleteUser_shouldReturnOk_whenSuccess() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setPseudo("user");

        when(jwtService.extractPseudo("userToken")).thenReturn("user");
        when(userService.getUserById(1L)).thenReturn(user);
        doNothing().when(userService).deleteUserById(1L);

        ResponseEntity<?> response = authController.deleteUser(1L, "Bearer userToken");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
