package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.AuthenticationResponse;
import com.jdrbibli.authservice.dto.InscriptionRequest;
import com.jdrbibli.authservice.dto.LoginRequest;
import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.security.JwtService;
import com.jdrbibli.authservice.service.IUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IUserService userService;

    @PostMapping("/inscription")
    public ResponseEntity<User> inscrireUser(@Valid @RequestBody InscriptionRequest request) {
        User nouvelUser = userService.inscrireNewUser(
                request.getPseudo(),
                request.getEmail(),
                request.getMotDePasse());
        return ResponseEntity.ok(nouvelUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        // Créer un token d’authentification Spring à partir du pseudo et mot de passe
        // reçus
        var authToken = new UsernamePasswordAuthenticationToken(request.getPseudo(), request.getMotDePasse());

        // Tenter l’authentification (va lancer une exception si ça échoue)
        authenticationManager.authenticate(authToken);

        // Récupérer l’utilisateur complet (avec email, rôles, etc.)
        User user = userService.getUserByPseudo(request.getPseudo());

        // Générer le JWT avec le pseudo comme subject
        String jwt = jwtService.generateToken(user.getPseudo());

        // Construire la réponse avec le token et infos utilisateur
        AuthenticationResponse response = new AuthenticationResponse(jwt, user.getPseudo(), user.getEmail());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUser(@PathVariable Long id) {
        userService.supprimerUser(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
