package com.jdrbibli.authservice.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateUserRequestTest {

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        UpdateUserRequest request = new UpdateUserRequest();

        // Utilisation des setters
        request.setPseudo("nouveauPseudo");
        request.setEmail("email@test.com");

        // Vérification avec assertions
        assertThat(request.getPseudo()).isEqualTo("nouveauPseudo");
        assertThat(request.getEmail()).isEqualTo("email@test.com");
    }
}
