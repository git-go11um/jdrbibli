package com.jdrbibli.authservice.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PasswordResetConfirmationTest {

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        // Utilisation du constructeur par défaut et des setters
        PasswordResetConfirmation confirmation = new PasswordResetConfirmation();
        confirmation.setPseudo("testUser");
        confirmation.setCode("1234");
        confirmation.setNewPassword("newPass");

        assertThat(confirmation.getPseudo()).isEqualTo("testUser");
        assertThat(confirmation.getCode()).isEqualTo("1234");
        assertThat(confirmation.getNewPassword()).isEqualTo("newPass");
    }

    @Test
    void constructorWithParameters_shouldSetFieldsCorrectly() {
        // Utilisation du constructeur complet
        PasswordResetConfirmation confirmation = new PasswordResetConfirmation("testUser", "1234", "newPass");

        assertThat(confirmation.getPseudo()).isEqualTo("testUser");
        assertThat(confirmation.getCode()).isEqualTo("1234");
        assertThat(confirmation.getNewPassword()).isEqualTo("newPass");
    }
}
