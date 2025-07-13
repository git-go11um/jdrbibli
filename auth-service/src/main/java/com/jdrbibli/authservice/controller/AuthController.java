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
import org.springframework.web.reactive.function.client.WebClient;

import java.security.Principal;
import java.util.List;
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
    private final WebClient.Builder webClientBuilder;

    public AuthController(IUserService userService,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            WebClient.Builder webClientBuilder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.webClientBuilder = webClientBuilder;
    }

    // Inscription
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> inscrire(@RequestBody InscriptionRequest request) {
        User newUser = userService.inscrireNewUser(request.getPseudo(), request.getEmail(), request.getMotDePasse());
        String token = jwtService.generateToken(newUser.getPseudo());
        // Synchroniser avec le user-service
        syncUserToUserService(newUser);
        return ResponseEntity.ok(new AuthenticationResponse(token, userService.toDTO(newUser)));
    }

    // Nouvelle méthode pour synchroniser l'utilisateur vers le user-service
    private void syncUserToUserService(User user) {
        try {
            System.out.println("Synchronisation vers user-service pour " + user.getPseudo() + " / " + user.getEmail());
            webClientBuilder.baseUrl("http://localhost:8082")
                    .build()
                    .post()
                    .uri("/api/users/sync")
                    .bodyValue(java.util.Map.of(
                            "pseudo", user.getPseudo(),
                            "email", user.getEmail()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(
                            result -> System.out
                                    .println("Utilisateur synchronisé vers user-service: " + user.getPseudo()),
                            error -> System.err.println("Erreur lors de la synchronisation: " + error.getMessage()));
        } catch (Exception e) {
            System.err.println("Erreur lors de la synchronisation vers user-service: " + e.getMessage());
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        // Authentifier l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPseudo(), request.getMotDePasse()));

        User user = userService.getUserByPseudo(request.getPseudo());
        String token = jwtService.generateToken(user.getPseudo());
        // Synchronise aussi à chaque login
        syncUserToUserService(user);

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
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Vous ne pouvez supprimer que votre propre compte.");
            }

            // Trouver l'utilisateur par son pseudo et le supprimer
            User user = userService.getUserByPseudo(pseudo); // Récupérer l'utilisateur par son pseudo
            if (user != null) {
                userService.deleteUserById(user.getId()); // Supprimer l'utilisateur par son ID
                return ResponseEntity.ok("Utilisateur supprimé avec succès.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Utilisateur non trouvé.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // <-- ajoute ça
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression du compte.");
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

        String email = principal.getName(); // Récupérer l'email de l'utilisateur connecté

        try {
            // Appeler le service pour changer le mot de passe
            userService.changeProfilePassword(email, request);

            // Générer un nouveau token JWT après la mise à jour du mot de passe
            String newToken = jwtTokenProvider.createToken(email); // Générer le token

            // Retourner la réponse avec un message de succès et le nouveau token
            return ResponseEntity.ok(new ApiResponse("Mot de passe mis à jour avec succès", true, newToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erreur lors de la mise à jour du mot de passe", false, null));
        }
    }

    // Endpoint pour lister tous les utilisateurs (pour la synchronisation)
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> userDTOs = users.stream()
                .map(userService::toDTO)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

}
