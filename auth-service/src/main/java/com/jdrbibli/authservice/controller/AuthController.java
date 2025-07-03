package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.AuthenticationResponse;
import com.jdrbibli.authservice.dto.ChangePasswordRequest;
import com.jdrbibli.authservice.dto.InscriptionRequest;
import com.jdrbibli.authservice.dto.LoginRequest;
import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.security.JwtService;
import com.jdrbibli.authservice.service.IUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(IUserService userService,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Inscription
    @PostMapping("/inscription")
    public ResponseEntity<AuthenticationResponse> inscrire(@RequestBody InscriptionRequest request) {
        User newUser = userService.inscrireNewUser(request.getPseudo(), request.getEmail(), request.getMotDePasse());
        String token = jwtService.generateToken(newUser.getPseudo());
        return ResponseEntity.ok(new AuthenticationResponse(token, userService.toDTO(newUser)));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        // Authentifier l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPseudo(), request.getMotDePasse()));

        User user = userService.getUserByPseudo(request.getPseudo());
        String token = jwtService.generateToken(user.getPseudo());

        return ResponseEntity.ok(new AuthenticationResponse(token, userService.toDTO(user)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        // Le token arrive en header "Authorization: Bearer <token>"
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token manquant ou mal formé");
        }

        String token = authorizationHeader.substring(7); // on enlève "Bearer "

        try {
            // Extraire le pseudo du token (même s’il est expiré, il faut catch
            // ExpiredJwtException)
            String pseudo = jwtService.extractPseudo(token);

            // Ici on pourrait vérifier si le token n’est pas complètement invalide
            // (blacklist, etc.)
            // mais pour l’instant on se base sur la signature valide

            // Générer un nouveau token
            String newToken = jwtService.generateToken(pseudo);

            // Récupérer user + créer réponse
            User user = userService.getUserByPseudo(pseudo);
            AuthenticationResponse response = new AuthenticationResponse(newToken, userService.toDTO(user));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalide ou expiré");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        String pseudo = authentication.getName();
        User user = userService.getUserByPseudo(pseudo);
        return ResponseEntity.ok(userService.toDTO(user));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Principal connectedUser) {
        userService.changePassword(connectedUser.getName(), request);
        return ResponseEntity.ok().body(Map.of("message", "Mot de passe changé avec succès"));
    }

}
