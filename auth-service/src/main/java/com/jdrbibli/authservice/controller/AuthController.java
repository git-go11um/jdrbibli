package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.*;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.security.JwtService;
import com.jdrbibli.authservice.service.IUserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Contrôleur principal pour l'authentification et la gestion des utilisateurs.
 * 
 * Cette classe expose les endpoints REST pour :
 * <ul>
 * <li>Inscription et création de compte</li>
 * <li>Authentification / login</li>
 * <li>Gestion des tokens JWT (génération et refresh)</li>
 * <li>Réinitialisation de mot de passe</li>
 * <li>Mise à jour du profil utilisateur</li>
 * <li>Suppression du compte utilisateur</li>
 * </ul>
 * 
 * 
 * Elle communique avec {@link IUserService} pour la logique métier,
 * {@link JwtService} pour les tokens JWT,
 * et utilise {@link WebClient} ou {@link RestTemplate} pour interagir avec
 * d'autres microservices
 * (ex. user-service pour la création de profil).
 * 
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final WebClient webClient;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${app.user-service-url}")
    private String userServiceUrl;

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

    /**
     * Endpoint pour inscrire un nouvel utilisateur.
     * 
     * Crée l'utilisateur dans auth-service et le profil dans user-service,
     * puis retourne un token JWT et les informations de l'utilisateur.
     *
     * @param request objet {@link InscriptionRequest} contenant pseudo, email et
     *                mot de passe
     * @return {@link ResponseEntity} avec {@link AuthenticationResponse} ou message
     *         d'erreur
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody InscriptionRequest request) {
        try {
            User newUser = userService.inscrireNewUser(request.getPseudo(), request.getEmail(), request.getPassword());

            UserProfileDTO profileDto = new UserProfileDTO();
            profileDto.setId(newUser.getId());
            profileDto.setPseudo(newUser.getPseudo());
            profileDto.setEmail(newUser.getEmail());

            WebClient.create(userServiceUrl)
                    .post()
                    .uri("/users")
                    .bodyValue(profileDto)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            String token = jwtService.generateToken(newUser.getPseudo(), newUser.getId());
            return ResponseEntity.ok(new AuthenticationResponse(token, userService.toDTO(newUser)));

        } catch (Exception e) {
            logger.error("Erreur lors de l'inscription", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de l'inscription : " + e.getMessage()));
        }
    }

    /**
     * Endpoint pour authentifier un utilisateur.
     *
     * @param request objet {@link LoginRequest} contenant pseudo et mot de passe
     * @return {@link ResponseEntity} avec {@link AuthenticationResponse} ou message
     *         d'erreur
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getPseudo(), request.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByPseudo(userDetails.getUsername());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Utilisateur non trouvé"));
            }

            String token = jwtService.generateToken(user.getPseudo(), user.getId());
            return ResponseEntity.ok(new AuthenticationResponse(token, userService.toDTO(user)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Pseudo ou mot de passe incorrect"));
        }
    }

    /**
     * Endpoint pour rafraîchir un token JWT existant.
     *
     * @param authHeader header Authorization contenant le token existant
     * @return {@link ResponseEntity} avec un nouveau token ou message d'erreur
     */
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

    /**
     * Endpoint pour récupérer les informations de l'utilisateur connecté.
     *
     * @param authentication objet Spring Security Authentication
     * @return {@link ResponseEntity} avec les données de l'utilisateur ou message
     *         d'erreur
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Utilisateur non authentifié"));
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByPseudo(userDetails.getUsername());
            return ResponseEntity.ok(userService.toDTO(user));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la récupération du profil"));
        }
    }

    /**
     * Endpoint pour demander la réinitialisation du mot de passe.
     *
     * @param body Map contenant le pseudo de l'utilisateur
     * @return {@link ResponseEntity} avec message de succès ou erreur
     */
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

    /**
     * Endpoint pour valider le code de réinitialisation du mot de passe.
     *
     * @param request objet {@link PasswordResetRequest} contenant pseudo et code
     * @return {@link ResponseEntity} indiquant si le code est valide ou non
     */
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

    /**
     * Endpoint pour réinitialiser le mot de passe après validation du code.
     *
     * @param request objet {@link PasswordResetRequest} contenant pseudo, code et
     *                nouveau mot de passe
     * @return {@link ResponseEntity} avec message de succès ou erreur
     */
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

    /**
     * Endpoint pour mettre à jour le profil utilisateur (pseudo / email).
     *
     * @param request        objet {@link UpdateUserRequest} avec nouvelles valeurs
     * @param authentication objet Spring Security Authentication
     * @return {@link ResponseEntity} avec message de succès et nouveau token JWT
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateUserRequest request, Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByPseudo(userDetails.getUsername());

            ReponseProfileChange response = userService.updateUserProfile(user.getId(), request.getPseudo(),
                    request.getEmail());

            String newToken = jwtService.generateToken(request.getPseudo(), user.getId());
            return ResponseEntity.ok(Map.of("message", response.getMessage(), "token", newToken));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur serveur : " + e.getMessage(), "token", null));
        }
    }

    /**
     * Endpoint pour changer le mot de passe depuis le profil utilisateur.
     *
     * @param request        objet {@link ChangePasswordProfileRequest} contenant
     *                       l'ancien et le nouveau mot de passe
     * @param authentication objet Spring Security Authentication
     * @return {@link ResponseEntity} avec message et token mis à jour
     */
    @PutMapping("/profile/password")
    public ResponseEntity<?> changeProfilePassword(@RequestBody ChangePasswordProfileRequest request,
            Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByPseudo(userDetails.getUsername());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Utilisateur non trouvé"));
            }

            userService.changeProfilePassword(userDetails.getUsername(), request);
            String newToken = jwtService.generateToken(user.getPseudo(), user.getId());

            return ResponseEntity.ok(new ApiResponse("Mot de passe mis à jour avec succès", true, newToken));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erreur lors de la mise à jour du mot de passe", false, null));
        }
    }

    /**
     * Endpoint pour supprimer le compte utilisateur.
     *
     * @param id    identifiant de l'utilisateur à supprimer
     * @param token header Authorization contenant le token JWT
     * @return {@link ResponseEntity} avec message de succès ou erreur
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Token manquant ou mal formé"));
            }

            String tokenPseudo = jwtService.extractPseudo(token.substring(7));
            User user = userService.getUserById(id);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Utilisateur non trouvé"));
            }

            if (!tokenPseudo.equals(user.getPseudo())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Vous ne pouvez supprimer que votre propre compte."));
            }

            userService.deleteUserById(user.getId());
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès dans les deux services"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la suppression du compte : " + e.getMessage()));
        }
    }

    /**
     * Méthode utilitaire pour créer les headers HTTP avec Authorization.
     *
     * @param token token JWT
     * @return {@link HttpHeaders} avec header Authorization
     */
    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        return headers;
    }
}
