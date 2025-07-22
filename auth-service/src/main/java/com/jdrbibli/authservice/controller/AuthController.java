package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.AuthenticationResponse;
import com.jdrbibli.authservice.dto.ChangePasswordProfileRequest;
import com.jdrbibli.authservice.dto.ChangePasswordRequest;
import com.jdrbibli.authservice.dto.InscriptionRequest;
import com.jdrbibli.authservice.dto.LoginRequest;
import com.jdrbibli.authservice.dto.PasswordResetRequest;
import com.jdrbibli.authservice.dto.UpdateUserRequest;
import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.dto.ReponseProfileChange;
import com.jdrbibli.authservice.dto.ChangePasswordProfileRequest;
import com.jdrbibli.authservice.dto.ApiResponse;
import com.jdrbibli.authservice.security.JwtTokenProvider;

import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.security.JwtService;
import com.jdrbibli.authservice.service.IUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.BadCredentialsException;
/* import java.lang.IllegalArgumentException; */

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injection du PasswordEncoder

    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(IUserService userService,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Inscription
    @PostMapping("/register")
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

    @PostMapping("/validate-reset-code")
    public ResponseEntity<?> validateResetCode(@RequestBody PasswordResetRequest request) {
        try {
            boolean isValid = userService.validateResetCode(request.getPseudo(), request.getCode());
            if (isValid) {
                return ResponseEntity.ok(Map.of("message", "Code valide"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Code invalide"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur serveur : " + e.getMessage()));
        }
    }

    // Réinitialiser le mot de passe
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            // Appel du service pour réinitialiser le mot de passe
            userService.resetPassword(request.getPseudo(), request.getCode(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès"));
        } catch (Exception e) {
            // Si une erreur se produit, retourner une erreur serveur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur serveur : " + e.getMessage()));
        }
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> body) {
        try {
            String pseudo = body.get("pseudo");
            userService.requestPasswordReset(pseudo);
            return ResponseEntity.ok(Map.of("message", "Code de réinitialisation envoyé"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur serveur : " + e.getMessage()));
        }
    }

    // Route de suppression du compte utilisateur
    @DeleteMapping("/{pseudo}")
    public ResponseEntity<?> deleteUser(@PathVariable String pseudo, @RequestHeader("Authorization") String token) {
        try {
            // Extraire le pseudo (subject) du token
            String tokenPseudo = jwtService.extractPseudo(token.substring(7)); // Enlève "Bearer " du token

            if (!tokenPseudo.equals(pseudo)) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Vous ne pouvez supprimer que votre propre compte.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            // Trouver l'utilisateur par son pseudo et le supprimer
            User user = userService.getUserByPseudo(pseudo); // Récupérer l'utilisateur par son pseudo
            if (user != null) {
                userService.deleteUserById(user.getId()); // Supprimer l'utilisateur par son ID

                Map<String, String> successResponse = new HashMap<>();
                successResponse.put("message", "Utilisateur supprimé avec succès.");
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, String> notFoundResponse = new HashMap<>();
                notFoundResponse.put("message", "Utilisateur non trouvé.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erreur lors de la suppression du compte.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Route pour mettre à jour le profil (pseudo et email)
    @PutMapping("/profile")
    public ResponseEntity<ReponseProfileChange> updateUserProfile(@RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((User) userDetails).getId();

        try {
            userService.updateUserProfile(userId, request.getPseudo(), request.getEmail());
            return ResponseEntity.ok(new ReponseProfileChange("Profil mis à jour avec succès"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ReponseProfileChange(e.getMessage()));
        }
    }

    // Nouvelle méthode pour modifier le mot de passe via le profil connecté
    @PutMapping("/profile/password")
    public ResponseEntity<?> changeProfilePassword(
            @RequestBody ChangePasswordProfileRequest request,
            Principal principal) {

        String email = principal.getName();

        try {
            userService.changeProfilePassword(email, request);
            String newToken = jwtTokenProvider.createToken(email); // 🔑 Nouveau token après changement
            return ResponseEntity.ok(
                    new ApiResponse("Mot de passe mis à jour avec succès", true, newToken));
        } catch (IllegalArgumentException | BadCredentialsException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage(), false, null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erreur lors de la mise à jour du mot de passe", false, null));
        }
    }

}
