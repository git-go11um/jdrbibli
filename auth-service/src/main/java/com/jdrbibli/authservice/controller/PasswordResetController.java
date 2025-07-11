package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.PasswordResetRequest;
import com.jdrbibli.authservice.dto.ChangePasswordRequest;
import com.jdrbibli.authservice.dto.PasswordResetConfirmation;
import com.jdrbibli.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    @Autowired
    private UserService userService; // Utilise le UserService modifié

    // Méthode pour demander la réinitialisation du mot de passe (envoie du code par
    // email)
    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestReset(@RequestBody PasswordResetRequest request) {
        // Demande la réinitialisation du mot de passe
        try {
            userService.requestPasswordReset(request.getPseudo());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email de réinitialisation envoyé si le pseudo existe.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Erreur lors de l'envoi de l'email de réinitialisation : " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Méthode pour confirmer la réinitialisation du mot de passe avec le code
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmReset(@RequestBody PasswordResetConfirmation confirmation) {
        try {
            // Validation du code de réinitialisation
            boolean isValid = userService.validateResetCode(confirmation.getToken(), confirmation.getCode());
            if (!isValid) {
                return ResponseEntity.badRequest().body("Code de réinitialisation invalide ou expiré.");
            }

            // Si le code est valide, réinitialiser le mot de passe
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setNewPassword(confirmation.getNewPassword());
            request.setConfirmNewPassword(confirmation.getNewPassword());

            userService.changePassword(confirmation.getToken(), request);
            return ResponseEntity.ok("Mot de passe mis à jour avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Erreur lors de la réinitialisation du mot de passe : " + e.getMessage());
        }
    }

}
