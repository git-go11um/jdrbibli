package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.exception.UserNotFoundException;
import com.jdrbibli.authservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void getUserById_shouldReturnUser_whenFound() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setPseudo("user");
        UserResponseDTO dto = new UserResponseDTO();
        when(userService.getUserById(1L)).thenReturn(user);
        when(userService.toDTO(user)).thenReturn(dto);

        ResponseEntity<?> response = userController.getUserById(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void getUserById_shouldReturnNotFound_whenUserNotFound() throws Exception {
        when(userService.getUserById(1L)).thenThrow(new UserNotFoundException("Utilisateur non trouvé"));

        ResponseEntity<?> response = userController.getUserById(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        String message = (String) body.get("message");
        assertThat(message).contains("Utilisateur non trouvé");
    }

    @Test
    void deleteUserCascade_shouldReturnOk_whenDeleted() throws Exception {
        when(userService.deleteUserWithCascade(1L)).thenReturn(true);

        ResponseEntity<?> response = userController.deleteUserCascade(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        String message = (String) body.get("message");
        assertThat(message).contains("Utilisateur supprimé avec succès");
    }

    @Test
    void deleteUserCascade_shouldReturnNotFound_whenUserNotFound() throws Exception {
        when(userService.deleteUserWithCascade(1L)).thenReturn(false);

        ResponseEntity<?> response = userController.deleteUserCascade(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        String message = (String) body.get("message");
        assertThat(message).contains("Utilisateur non trouvé");
    }

    @Test
    void deleteUserCascade_shouldReturnInternalServerError_whenExceptionThrown() throws Exception {
        when(userService.deleteUserWithCascade(1L)).thenThrow(new RuntimeException("Erreur interne"));

        ResponseEntity<?> response = userController.deleteUserCascade(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        String message = (String) body.get("message");
        assertThat(message).contains("Erreur interne");
    }
}
