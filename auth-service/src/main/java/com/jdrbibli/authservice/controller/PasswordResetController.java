package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.PasswordResetRequest;
import com.jdrbibli.authservice.dto.ChangePasswordRequest;
import com.jdrbibli.authservice.dto.PasswordResetConfirmation;
import com.jdrbibli.authservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/password-reset")
public class PasswordResetController {

    @Autowired
    private IUserService userService; // Utilise le UserService modifié

    // -------------------------------------------------
    // 🔹 Endpoint pour demander la réinitialisation du mot de passe
    // -------------------------------------------------
    /*
     * @PostMapping("/request")
     * public ResponseEntity<Map<String, String>> requestReset(@RequestBody
     * PasswordResetRequest request) {
     * try {
     * userService.requestPasswordReset(request.getPseudo());
     * Map<String, String> response = new HashMap<>();
     * response.put("message",
     * "Email de réinitialisation envoyé si le pseudo existe.");
     * return ResponseEntity.ok(response);
     * } catch (Exception e) {
     * Map<String, String> response = new HashMap<>();
     * response.put("message", "Erreur lors de l'envoi de l'email : " +
     * e.getMessage());
     * return ResponseEntity.status(500).body(response);
     * }
     * }
     */

    // -------------------------------------------------
    // 🔹 Endpoint pour vérifier le code de réinitialisation
    // -------------------------------------------------

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody PasswordResetConfirmation confirmation) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean isValid = userService.validateResetCode(confirmation.getPseudo(), confirmation.getCode());
            if (!isValid) {
                response.put("message", "Code invalide ou expiré.");
                return ResponseEntity.badRequest().body(response);
            }
            response.put("message", "Code valide, vous pouvez maintenant changer votre mot de passe.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Erreur lors de la vérification du code : " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // -------------------------------------------------
    // 🔹 Endpoint pour confirmer et changer le mot de passe
    // -------------------------------------------------

    @PostMapping("/confirm")
    public ResponseEntity<Map<String, String>> confirmReset(@RequestBody PasswordResetConfirmation confirmation) {
        try {
            userService.resetPassword(
                    confirmation.getPseudo(),
                    confirmation.getCode(),
                    confirmation.getNewPassword());

            // Retour JSON au lieu de plain text
            return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour avec succès."));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Erreur lors de la réinitialisation du mot de passe : " + e.getMessage()));
        }
    }

}
