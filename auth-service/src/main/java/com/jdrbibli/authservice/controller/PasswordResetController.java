package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.PasswordResetRequest;
import com.jdrbibli.authservice.dto.PasswordResetConfirmation;
import com.jdrbibli.authservice.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/reset-password")
public class PasswordResetController {

    @Autowired
    private PasswordResetService resetService;

    @PostMapping("/request")
    public ResponseEntity<String> requestReset(@RequestBody PasswordResetRequest request) {
        resetService.createPasswordResetToken(request.getEmail());
        return ResponseEntity.ok("Email de réinitialisation envoyé si l'email existe.");
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmReset(@RequestBody PasswordResetConfirmation confirmation) {
        resetService.resetPassword(confirmation.getToken(), confirmation.getNewPassword());
        return ResponseEntity.ok("Mot de passe mis à jour avec succès.");
    }
}
