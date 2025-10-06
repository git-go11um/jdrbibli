package com.jdrbibli.ouvrage_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_shouldReturnNotFoundResponse() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Ressource introuvable");

        // Act
        ResponseEntity<Object> response = handler.handleResourceNotFound(ex);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Resource Not Found", body.get("error"));
        assertEquals("Ressource introuvable", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleRuntimeException_shouldReturnBadRequestResponse() {
        // Arrange
        RuntimeException ex = new RuntimeException("Erreur runtime");

        // Act
        ResponseEntity<Object> response = handler.handleRuntimeException(ex);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Runtime Error", body.get("error"));
        assertEquals("Erreur runtime", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleGenericException_shouldReturnInternalServerErrorResponse() {
        // Arrange
        Exception ex = new Exception("Erreur générique");

        // Act
        ResponseEntity<Object> response = handler.handleGenericException(ex);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("Erreur générique", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }
}
