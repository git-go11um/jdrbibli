package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.PasswordResetConfirmation;
import com.jdrbibli.authservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour gérer la réinitialisation des mots de passe.
 * 
 * Fournit des endpoints pour :
 * <ul>
 *     <li>Vérifier la validité d'un code de réinitialisation</li>
 *     <li>Confirmer et appliquer la réinitialisation du mot de passe</li>
 * </ul>
 * 
 * 
 * Communique avec {@link IUserService} pour effectuer la logique métier.
 * 
 */
@RestController
@RequestMapping("/auth/password-reset")
public class PasswordResetController {

    @Autowired
    private IUserService userService;

    /**
     * Vérifie si le code de réinitialisation fourni est valide.
     *
     * @param confirmation objet {@link PasswordResetConfirmation} contenant le pseudo et le code
     * @return {@link ResponseEntity} avec message JSON indiquant si le code est valide ou non
     */
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

    /**
     * Confirme et applique la réinitialisation du mot de passe après validation du code.
     *
     * @param confirmation objet {@link PasswordResetConfirmation} contenant pseudo, code et nouveau mot de passe
     * @return {@link ResponseEntity} avec message JSON indiquant le succès ou l'échec de l'opération
     */
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, String>> confirmReset(@RequestBody PasswordResetConfirmation confirmation) {
        try {
            userService.resetPassword(
                    confirmation.getPseudo(),
                    confirmation.getCode(),
                    confirmation.getNewPassword());

            return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour avec succès."));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Erreur lors de la réinitialisation du mot de passe : " + e.getMessage()));
        }
    }
}
