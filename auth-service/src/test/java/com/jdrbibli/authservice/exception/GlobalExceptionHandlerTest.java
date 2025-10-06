package com.jdrbibli.authservice.exception;

import com.jdrbibli.authservice.dto.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleUserNotFoundException_ShouldReturn404() {
        UserNotFoundException ex = new UserNotFoundException("Utilisateur introuvable");

        ResponseEntity<ErrorResponseDTO> response = handler.handleUserNotFoundException(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Utilisateur introuvable");
        assertThat(response.getBody().getStatus()).isEqualTo(404);
    }

    @Test
    void handleBadCredentialsException_ShouldReturn401() {
        BadCredentialsException ex = new BadCredentialsException("Identifiants incorrects");

        ResponseEntity<ErrorResponseDTO> response = handler.handleBadCredentialsException(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(401);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Identifiants incorrects");
        assertThat(response.getBody().getStatus()).isEqualTo(401);
    }

    @Test
    void handleIllegalArgumentException_ShouldReturn400() {
        IllegalArgumentException ex = new IllegalArgumentException("Argument invalide");

        ResponseEntity<ErrorResponseDTO> response = handler.handleIllegalArgumentException(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Argument invalide");
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    void handleGenericException_ShouldReturn500() {
        Exception ex = new Exception("Erreur inattendue");

        ResponseEntity<ErrorResponseDTO> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Erreur interne du serveur");
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }
}
