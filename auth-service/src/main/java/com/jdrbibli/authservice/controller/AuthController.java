package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.*;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.security.JwtService;
import com.jdrbibli.authservice.security.JwtTokenProvider;
import com.jdrbibli.authservice.service.IUserService;

import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(IUserService userService,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    // ------------------- INSCRIPTION -------------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody InscriptionRequest request) {
        try {
            User newUser = userService.inscrireNewUser(request.getPseudo(), request.getEmail(), request.getPassword());
            String token = jwtService.generateToken(newUser.getPseudo());
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
            System.out.println(
                    "Tentative login pour pseudo: " + request.getPseudo() + ", password: " + request.getPassword());

            User user = userService.getUserByPseudo(request.getPseudo());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Utilisateur non trouvé"));
            }

            boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
            if (!matches) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Mot de passe incorrect"));
            }

            System.out.println("----- Test AuthenticationManager -----");

            if (user != null) {
                System.out.println("Utilisateur trouvé : " + user.getPseudo());
                matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
                System.out.println("Mot de passe correct ? " + matches);
            } else {
                System.out.println("Utilisateur non trouvé !");
            }

            // Authentification Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getPseudo(), request.getPassword()));

            String token = jwtService.generateToken(user.getPseudo());
            return ResponseEntity.ok(new AuthenticationResponse(token, userService.toDTO(user)));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors du login : " + e.getMessage()));
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
            String newToken = jwtService.generateToken(pseudo);
            User user = userService.getUserByPseudo(pseudo);
            return ResponseEntity.ok(new AuthenticationResponse(newToken, userService.toDTO(user)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Token invalide ou expiré"));
        }
    }

    // ------------------- UTILISATEUR CONNECTÉ -------------------
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Utilisateur non authentifié"));
            }
            String pseudo = userDetails.getUsername();
            User user = userService.getUserByPseudo(pseudo);
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

    @PutMapping("/profile/password")
    public ResponseEntity<?> changeProfilePassword(@RequestBody ChangePasswordProfileRequest request,
            Principal principal) {
        try {
            userService.changeProfilePassword(principal.getName(), request);
            String newToken = jwtTokenProvider.createToken(principal.getName());
            return ResponseEntity.ok(new ApiResponse("Mot de passe mis à jour avec succès", true, newToken));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erreur lors de la mise à jour du mot de passe", false, null));
        }
    }

    // ------------------- PROFIL -------------------
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateUserRequest request,
            Principal principal) {
        System.out.println("PUT /auth/profile appelé pour pseudo=" + principal.getName());
        try {
            // Récupération de l'utilisateur actuel
            User user = userService.getUserByPseudo(principal.getName());

            // Mise à jour des infos
            ReponseProfileChange response = userService.updateUserProfile(user.getId(), request.getPseudo(),
                    request.getEmail());

            // Génération d'un nouveau JWT
            String newToken = jwtService.generateToken(request.getPseudo());

            // Retourner le message + nouveau JWT
            return ResponseEntity.ok(Map.of(
                    "message", response.getMessage(),
                    "token", newToken));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage(), "token", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur serveur : " + e.getMessage(), "token", null));
        }
    }

    // ------------------- SUPPRESSION UTILISATEUR -------------------
    @DeleteMapping("/{pseudo}")
    public ResponseEntity<?> deleteUser(@PathVariable String pseudo, @RequestHeader("Authorization") String token) {
        try {
            String tokenPseudo = jwtService.extractPseudo(token.substring(7));
            if (!tokenPseudo.equals(pseudo)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Vous ne pouvez supprimer que votre propre compte."));
            }

            User user = userService.getUserByPseudo(pseudo);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Utilisateur non trouvé."));
            }
            userService.deleteUserById(user.getId());
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la suppression du compte : " + e.getMessage()));
        }
    }
}
