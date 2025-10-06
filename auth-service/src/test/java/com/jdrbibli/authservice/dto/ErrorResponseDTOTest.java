package com.jdrbibli.authservice.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseDTOTest {

    @Test
    void constructorAndGetters_shouldWorkCorrectly() {
        String message = "Erreur test";
        int status = 500;

        ErrorResponseDTO error = new ErrorResponseDTO(message, status);

        assertThat(error.getMessage()).isEqualTo(message);
        assertThat(error.getStatus()).isEqualTo(status);
        assertThat(error.getTimestamp()).isNotNull();
    }
}
