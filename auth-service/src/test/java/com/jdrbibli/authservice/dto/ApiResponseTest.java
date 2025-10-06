package com.jdrbibli.authservice.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires pour la classe {@link ApiResponse}.
 * 
 * Vérifie les constructeurs, getters et setters.
 */
class ApiResponseTest {

    @Test
    void constructorWithMessage_shouldInitializeFieldsCorrectly() {
        // Arrange & Act
        ApiResponse response = new ApiResponse("Opération réussie");

        // Assert
        assertThat(response.getMessage()).isEqualTo("Opération réussie");
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getToken()).isNull();
    }

    @Test
    void fullConstructor_shouldInitializeAllFields() {
        // Arrange & Act
        ApiResponse response = new ApiResponse("Erreur", false, "jwt-token");

        // Assert
        assertThat(response.getMessage()).isEqualTo("Erreur");
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        // Arrange
        ApiResponse response = new ApiResponse("Initial");

        // Act
        response.setMessage("Nouveau message");
        response.setSuccess(false);
        response.setToken("nouveau-token");

        // Assert
        assertThat(response.getMessage()).isEqualTo("Nouveau message");
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getToken()).isEqualTo("nouveau-token");
    }
}
