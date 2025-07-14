package com.jdrbibli.authservice.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class ErrorResponseDTOTest {

    @Test
    void testConstructorAndGetters() {
        String message = "Erreur interne";
        int status = 500;

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(message, status);

        assertEquals(message, errorResponse.getMessage());
        assertEquals(status, errorResponse.getStatus());

        // On vérifie que le timestamp a été initialisé à une date proche de maintenant
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTimestamp().isAfter(before));
        assertTrue(errorResponse.getTimestamp().isBefore(after));
    }
}
