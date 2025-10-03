package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur de test pour vérifier et déboguer les JWT.
 * 
 * Fournit un endpoint pour tester la validité d'un token et extraire le pseudo associé.
 */
@RestController
@RequestMapping("/test")
public class JwtDebugController {

    private final JwtService jwtService;

    /**
     * Constructeur du contrôleur.
     *
     * @param jwtService service gérant la création et validation des JWT
     */
    public JwtDebugController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Vérifie la validité d'un JWT et extrait le pseudo associé.
     *
     * @param token le JWT à vérifier
     * @return ResponseEntity avec un message indiquant si le token est valide ou non
     *         et le pseudo associé si valide
     */
    @GetMapping("/verify-token")
    public ResponseEntity<String> verifyToken(@RequestParam String token) {
        try {
            String pseudo = jwtService.extractPseudo(token);
            boolean valid = jwtService.isTokenValid(token, pseudo);
            return ResponseEntity.ok("Token valide pour pseudo=" + pseudo + " ? " + valid);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erreur JWT : " + e.getMessage());
        }
    }
}
