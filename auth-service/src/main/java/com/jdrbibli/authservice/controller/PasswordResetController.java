package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.PasswordResetRequest;
import com.jdrbibli.authservice.dto.PasswordResetConfirmation;
import com.jdrbibli.authservice.service.PasswordResetService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/password-reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService resetService;

    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestReset(@RequestBody PasswordResetRequest request) {
        resetService.createPasswordResetToken(request.getPseudo());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Email de réinitialisation envoyé si le pseudo existe.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmReset(@RequestBody PasswordResetConfirmation confirmation) {
        resetService.resetPassword(confirmation.getToken(), confirmation.getNewPassword());
        return ResponseEntity.ok("Mot de passe mis à jour avec succès.");
    }
}
