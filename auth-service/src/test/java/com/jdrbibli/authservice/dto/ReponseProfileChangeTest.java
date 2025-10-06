package com.jdrbibli.authservice.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReponseProfileChangeTest {

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        ReponseProfileChange response = new ReponseProfileChange();

        // Utilisation des setters
        response.setMessage("Profil mis à jour");
        response.setNewToken("token123");

        // Vérification avec assertions
        assertThat(response.getMessage()).isEqualTo("Profil mis à jour");
        assertThat(response.getNewToken()).isEqualTo("token123");
    }

    @Test
    void constructorWithParams_shouldSetFieldsCorrectly() {
        ReponseProfileChange response = new ReponseProfileChange("Succès", "token456");

        assertThat(response.getMessage()).isEqualTo("Succès");
        assertThat(response.getNewToken()).isEqualTo("token456");
    }
}
