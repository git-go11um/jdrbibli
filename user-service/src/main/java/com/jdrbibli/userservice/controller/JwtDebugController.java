package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class JwtDebugController {

    private final JwtService jwtService;

    public JwtDebugController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

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
