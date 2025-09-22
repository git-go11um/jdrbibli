package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.*;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.security.JwtService;
import com.jdrbibli.authservice.service.IUserService;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpMethod;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final WebClient webClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public AuthController(IUserService userService,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            WebClient webClient) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.webClient = webClient;
    }

    // ------------------- INSCRIPTION -------------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody InscriptionRequest request) {
        try {
            // 1️⃣ Création du User dans auth-service
            User newUser = userService.inscrireNewUser(request.getPseudo(), request.getEmail(), request.getPassword());

            // 2️⃣ Création du UserProfile côté user-service
            UserProfileDTO profileDto = new UserProfileDTO();
            profileDto.setId(newUser.getId()); // même ID que auth-service
            profileDto.setPseudo(newUser.getPseudo());
            profileDto.setEmail(newUser.getEmail());

            // Appel REST vers user-service (exemple avec WebClient)
            WebClient.create("http://localhost:8082") // URL du user-service
                    .post()
                    .uri("/api/users")
                    .bodyValue(profileDto)
                    .retrieve()
                    .bodyToMono(UserProfileDTO.class)
                    .block();

            // 3️⃣ Génération du token JWT
            String token = jwtService.generateToken(newUser.getPseudo(), newUser.getId());

            // 4️⃣ Retour de la réponse avec token et DTO
            return ResponseEntity.ok(new AuthenticationResponse(token, userService.toDTO(newUser)));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de l'inscription : " + e.getMessage()));
        }
    }

    // ------------------- LOGIN -------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Authentification Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getPseudo(), request.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Récupérer l'utilisateur à partir du pseudo (username)
            User user = userService.getUserByPseudo(userDetails.getUsername());

            // Si l'utilisateur est introuvable
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Utilisateur non trouvé"));
            }

            // Génération du token avec pseudo et ID de l'utilisateur
            String token = jwtService.generateToken(user.getPseudo(), user.getId());

            // Retourne la réponse avec le token et les informations de l'utilisateur
            return ResponseEntity.ok(new AuthenticationResponse(token, userService.toDTO(user)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Pseudo ou mot de passe incorrect"));
        }
    }

    // ------------------- REFRESH TOKEN -------------------
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Token manquant ou mal formé"));
        }

        String token = authHeader.substring(7);
        try {
            String pseudo = jwtService.extractPseudo(token);
            User user = userService.getUserByPseudo(pseudo);
            String newToken = jwtService.generateToken(pseudo, user.getId());
            return ResponseEntity.ok(new AuthenticationResponse(newToken, userService.toDTO(user)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Token invalide ou expiré"));
        }
    }

    // ------------------- UTILISATEUR CONNECTÉ -------------------
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Utilisateur non authentifié"));
            }

            // Récupérer l'objet User via le pseudo à partir de UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByPseudo(userDetails.getUsername());

            // Retourner les informations de l'utilisateur
            return ResponseEntity.ok(userService.toDTO(user));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la récupération du profil"));
        }
    }

    // ------------------- MOT DE PASSE -------------------
    @PostMapping("/password-reset/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> body) {
        String pseudo = body.get("pseudo");
        try {
            userService.requestPasswordReset(pseudo);
            return ResponseEntity.ok(Map.of("message", "Code de réinitialisation envoyé"));
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de l'envoi du mail : " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur serveur : " + e.getMessage()));
        }
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur serveur : " + e.getMessage()));
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            userService.resetPassword(request.getPseudo(), request.getCode(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur serveur : " + e.getMessage()));
        }
    }

    // ------------------- PROFIL -------------------
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateUserRequest request, Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByPseudo(userDetails.getUsername());

            ReponseProfileChange response = userService.updateUserProfile(user.getId(), request.getPseudo(),
                    request.getEmail());

            // Génération du token avec pseudo et ID
            String newToken = jwtService.generateToken(request.getPseudo(), user.getId());
            return ResponseEntity.ok(Map.of("message", response.getMessage(), "token", newToken));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur serveur : " + e.getMessage(), "token", null));
        }
    }

    @PutMapping("/profile/password")
    public ResponseEntity<?> changeProfilePassword(@RequestBody ChangePasswordProfileRequest request,
            Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Récupérer l'utilisateur à partir du pseudo
            User user = userService.getUserByPseudo(userDetails.getUsername());

            // Vérifier si l'utilisateur existe
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Utilisateur non trouvé"));
            }

            // Appel au service pour changer le mot de passe
            userService.changeProfilePassword(userDetails.getUsername(), request);

            // Génération du token avec le pseudo et l'ID de l'utilisateur
            String newToken = jwtService.generateToken(user.getPseudo(), user.getId());

            return ResponseEntity.ok(new ApiResponse("Mot de passe mis à jour avec succès", true, newToken));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erreur lors de la mise à jour du mot de passe", false, null));
        }
    }

    // ------------------- SUPPRESSION UTILISATEUR -------------------
    // Remplacer @DeleteMapping("/{pseudo}") par @DeleteMapping("/{id}")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            // Vérifie que le token correspond bien à l'utilisateur qui fait la requête
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Token manquant ou mal formé"));
            }

            String tokenPseudo = jwtService.extractPseudo(token.substring(7));
            User user = userService.getUserById(id); // Recherche par id et non pseudo

            // Vérifie que l'utilisateur existe
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Utilisateur non trouvé"));
            }

            // Vérifie que le token correspond bien à l'utilisateur qu'on tente de supprimer
            if (!tokenPseudo.equals(user.getPseudo())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Vous ne pouvez supprimer que votre propre compte."));
            }

            // 1️⃣ Supprime dans auth_db.users
            userService.deleteUserById(user.getId());

            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès dans les deux services"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la suppression du compte : " + e.getMessage()));
        }
    }

    // Méthode utilitaire pour passer l'Authorization dans l'appel REST
    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        return headers;
    }

}
