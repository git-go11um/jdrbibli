package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.InscriptionRequest;
import com.jdrbibli.authservice.entity.Utilisateur;
import com.jdrbibli.authservice.service.IUtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUtilisateurService utilisateurService;

    @PostMapping("/inscription")
    public ResponseEntity<Utilisateur> inscrireUtilisateur(@Valid @RequestBody InscriptionRequest request) {
        Utilisateur nouvelUtilisateur = utilisateurService.inscrireNouvelUtilisateur(
                request.getPseudo(),
                request.getEmail(),
                request.getMotDePasse()
        );
        return ResponseEntity.ok(nouvelUtilisateur);
    }
}
