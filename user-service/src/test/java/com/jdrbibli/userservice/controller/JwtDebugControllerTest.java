package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtDebugControllerTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtDebugController jwtDebugController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void verifyToken_shouldReturnValidMessage_whenTokenIsValid() {
        String token = "dummyToken";
        String pseudo = "user123";

        when(jwtService.extractPseudo(token)).thenReturn(pseudo);
        when(jwtService.isTokenValid(token, pseudo)).thenReturn(true);

        ResponseEntity<String> response = jwtDebugController.verifyToken(token);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Token valide pour pseudo=" + pseudo + " ? true");

        verify(jwtService).extractPseudo(token);
        verify(jwtService).isTokenValid(token, pseudo);
    }

    @Test
    void verifyToken_shouldReturnUnauthorized_whenExceptionThrown() {
        String token = "invalidToken";

        when(jwtService.extractPseudo(token)).thenThrow(new RuntimeException("Token invalide"));

        ResponseEntity<String> response = jwtDebugController.verifyToken(token);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).contains("Erreur JWT : Token invalide");

        verify(jwtService).extractPseudo(token);
        verify(jwtService, never()).isTokenValid(anyString(), anyString());
    }
}
