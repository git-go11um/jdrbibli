package com.jdrbibli.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdrbibli.authservice.dto.PasswordResetConfirmation;
import com.jdrbibli.authservice.dto.PasswordResetRequest;
import com.jdrbibli.authservice.service.UserService;
import com.jdrbibli.authservice.TestSecurityConfig;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class) // Ajout de la config sécurité test
public class PasswordResetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService; // Mock du service

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRequestResetSuccess() throws Exception {
        Mockito.doNothing().when(userService).requestPasswordReset(anyString());

        PasswordResetRequest request = new PasswordResetRequest();
        request.setPseudo("user123");

        mockMvc.perform(post("/api/auth/password-reset/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email de réinitialisation envoyé si le pseudo existe."));
    }

    @Test
    public void testRequestResetFailure() throws Exception {
        Mockito.doThrow(new RuntimeException("Erreur SMTP"))
                .when(userService).requestPasswordReset(anyString());

        PasswordResetRequest request = new PasswordResetRequest();
        request.setPseudo("user123");

        mockMvc.perform(post("/api/auth/password-reset/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Erreur lors de l'envoi de l'email de réinitialisation : Erreur SMTP"));
    }

    @Test
    public void testConfirmResetSuccess() throws Exception {
        // Mock la validation du code pour renvoyer true
        Mockito.when(userService.validateResetCode(anyString(), anyString())).thenReturn(true);
        // Mock le changement de mot de passe (void)
        Mockito.doNothing().when(userService).changePassword(anyString(), Mockito.any());

        // Construire l’objet PasswordResetConfirmation JSON
        PasswordResetConfirmation confirmation = new PasswordResetConfirmation();
        confirmation.setToken("token123");
        confirmation.setCode("code123");
        confirmation.setNewPassword("newPassword1!");

        mockMvc.perform(post("/api/auth/password-reset/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(confirmation)))
                .andExpect(status().isOk())
                .andExpect(content().string("Mot de passe mis à jour avec succès."));
    }

    @Test
    public void testConfirmResetInvalidCode() throws Exception {
        // Mock la validation pour renvoyer false
        Mockito.when(userService.validateResetCode(anyString(), anyString())).thenReturn(false);

        PasswordResetConfirmation confirmation = new PasswordResetConfirmation();
        confirmation.setToken("token123");
        confirmation.setCode("badcode");
        confirmation.setNewPassword("newPassword1!");

        mockMvc.perform(post("/api/auth/password-reset/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(confirmation)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Code de réinitialisation invalide ou expiré."));
    }

    @Test
    public void testConfirmResetThrowsException() throws Exception {
        // Mock pour lancer une exception
        Mockito.when(userService.validateResetCode(anyString(), anyString()))
                .thenThrow(new RuntimeException("Erreur inattendue"));

        PasswordResetConfirmation confirmation = new PasswordResetConfirmation();
        confirmation.setToken("token123");
        confirmation.setCode("code123");
        confirmation.setNewPassword("newPassword1!");

        mockMvc.perform(post("/api/auth/password-reset/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(confirmation)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erreur lors de la réinitialisation du mot de passe : Erreur inattendue"));
    }

}
