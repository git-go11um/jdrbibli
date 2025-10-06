package com.jdrbibli.authservice.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PasswordResetRequestTest {

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        // Création de l'objet et utilisation des setters
        PasswordResetRequest request = new PasswordResetRequest();
        request.setPseudo("userTest");
        request.setCode("5678");
        request.setNewPassword("newPassword123");

        // Vérification avec assertions
        assertThat(request.getPseudo()).isEqualTo("userTest");
        assertThat(request.getCode()).isEqualTo("5678");
        assertThat(request.getNewPassword()).isEqualTo("newPassword123");
    }
}
