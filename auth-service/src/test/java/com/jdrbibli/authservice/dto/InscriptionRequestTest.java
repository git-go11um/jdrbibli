package com.jdrbibli.authservice.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class InscriptionRequestTest {

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        InscriptionRequest request = new InscriptionRequest();

        request.setPseudo("testUser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        assertThat(request.getPseudo()).isEqualTo("testUser");
        assertThat(request.getEmail()).isEqualTo("test@example.com");
        assertThat(request.getPassword()).isEqualTo("password123");
    }
}
