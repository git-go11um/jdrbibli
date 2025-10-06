package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.PasswordResetConfirmation;
import com.jdrbibli.authservice.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PasswordResetControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private PasswordResetController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void verifyCode_shouldReturnOk_whenCodeValid() throws Exception {
        PasswordResetConfirmation confirmation = new PasswordResetConfirmation();
        confirmation.setPseudo("user");
        confirmation.setCode("1234");

        when(userService.validateResetCode("user", "1234")).thenReturn(true);

        ResponseEntity<Map<String, String>> response = controller.verifyCode(confirmation);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry("message", "Code valide, vous pouvez maintenant changer votre mot de passe.");
    }

    @Test
    void verifyCode_shouldReturnBadRequest_whenCodeInvalid() throws Exception {
        PasswordResetConfirmation confirmation = new PasswordResetConfirmation();
        confirmation.setPseudo("user");
        confirmation.setCode("1234");

        when(userService.validateResetCode("user", "1234")).thenReturn(false);

        ResponseEntity<Map<String, String>> response = controller.verifyCode(confirmation);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("message", "Code invalide ou expiré.");
    }

    @Test
    void confirmReset_shouldReturnOk_whenSuccess() throws Exception {
        PasswordResetConfirmation confirmation = new PasswordResetConfirmation();
        confirmation.setPseudo("user");
        confirmation.setCode("1234");
        confirmation.setNewPassword("newPass");

        doNothing().when(userService).resetPassword("user", "1234", "newPass");

        ResponseEntity<Map<String, String>> response = controller.confirmReset(confirmation);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry("message", "Mot de passe mis à jour avec succès.");
    }

    @Test
    void confirmReset_shouldReturnInternalServerError_whenException() throws Exception {
        PasswordResetConfirmation confirmation = new PasswordResetConfirmation();
        confirmation.setPseudo("user");
        confirmation.setCode("1234");
        confirmation.setNewPassword("newPass");

        doThrow(new RuntimeException("Erreur")).when(userService).resetPassword("user", "1234", "newPass");

        ResponseEntity<Map<String, String>> response = controller.confirmReset(confirmation);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody().get("message")).contains("Erreur lors de la réinitialisation du mot de passe");
    }
}
