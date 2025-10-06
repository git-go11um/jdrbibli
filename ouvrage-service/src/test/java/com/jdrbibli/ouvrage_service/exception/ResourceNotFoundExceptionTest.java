package com.jdrbibli.ouvrage_service.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        // Arrange
        String message = "Ressource non trouvée";

        // Act
        ResourceNotFoundException ex = new ResourceNotFoundException(message);

        // Assert
        assertEquals(message, ex.getMessage());
    }

    @Test
    void exceptionIsRuntimeException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Test");
        assertTrue(ex instanceof RuntimeException);
    }
}
