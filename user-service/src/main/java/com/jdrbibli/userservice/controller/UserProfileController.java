package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.UserProfileDTO;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.service.UserProfileService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des profils utilisateurs.
 * 
 * Permet de créer, lire, mettre à jour et supprimer des utilisateurs,
 * gérer les avatars, rechercher par pseudo, et vérifier les relations d'amitié.
 */
@RestController
@RequestMapping({ "/users", "/user" })
public class UserProfileController {

    @PostConstruct
    public void init() {
        log.info("✅ UserProfileController initialisé et prêt à gérer /users/**");
    }

    private static final Logger log = LoggerFactory.getLogger(UserProfileController.class);

    private final UserProfileService userProfileService;

    /**
     * Constructeur du contrôleur.
     *
     * @param userProfileService service pour gérer les profils utilisateurs
     */
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Récupère tous les utilisateurs.
     *
     * @return liste de tous les profils utilisateurs
     */
    @GetMapping
    public List<UserProfile> getAllUsers() {
        return userProfileService.getAllUsers();
    }

    /**
     * Récupère un utilisateur par son ID.
     *
     * @param id ID de l'utilisateur
     * @return ResponseEntity contenant le profil ou NOT_FOUND
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getUserById(@PathVariable Long id) {
        return userProfileService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprime un utilisateur par son pseudo.
     *
     * @param pseudo pseudo de l'utilisateur
     * @return ResponseEntity avec statut noContent
     */
    @DeleteMapping("/by-pseudo/{pseudo}")
    public ResponseEntity<Void> deleteUserByPseudo(@PathVariable String pseudo) {
        userProfileService.deleteUserByPseudo(pseudo);
        return ResponseEntity.noContent().build();
    }

    /**
     * Met à jour un utilisateur à partir d'un DTO.
     *
     * @param id  ID de l'utilisateur
     * @param dto données à mettre à jour
     * @return ResponseEntity contenant le DTO mis à jour ou NOT_FOUND
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserProfileDTO dto) {

        return userProfileService.updateUser(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Recherche un utilisateur par pseudo.
     *
     * @param pseudo pseudo à rechercher
     * @return ResponseEntity contenant le DTO ou NOT_FOUND
     */
    @GetMapping("/search")
    public ResponseEntity<UserProfileDTO> searchUserByPseudo(@RequestParam String pseudo) {
        return userProfileService.findByPseudo(pseudo)
                .map(user -> new UserProfileDTO(
                        user.getId(),
                        user.getPseudo(),
                        user.getEmail(),
                        (user.getAvatarPath() != null && !user.getAvatarPath().isBlank())
                                ? "/api/users/profile/avatar/" + user.getId()
                                : ""))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère les amis d'un utilisateur.
     *
     * @param pseudo pseudo de l'utilisateur
     * @return liste des amis sous forme de DTO
     */
    @GetMapping("/friends/{pseudo}")
    public ResponseEntity<List<FriendDTO>> getFriends(@PathVariable String pseudo) {
        List<FriendDTO> friends = userProfileService.getFriends(pseudo);
        return ResponseEntity.ok(friends);
    }

    /**
     * Upload ou remplacement de l'avatar de l'utilisateur authentifié.
     *
     * @param file           fichier image à uploader
     * @param authentication objet d'authentification Spring Security
     * @return ResponseEntity avec statut CREATED ou OK, ou erreur
     */
    @PutMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        if (file.isEmpty()) {
            log.warn("Tentative d'upload d'un avatar vide");
            return ResponseEntity.badRequest().body("Fichier vide");
        }

        if (authentication == null || authentication.getPrincipal() == null) {
            log.error("Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        String pseudo;

        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            pseudo = (String) principal;
        } else if (principal instanceof UserDetails) {
            pseudo = ((UserDetails) principal).getUsername();
        } else {
            log.error("Type de principal inattendu : {}", principal.getClass());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne : type principal inattendu");
        }

        try {
            boolean created = userProfileService.saveUserAvatar(pseudo, file);
            return created
                    ? ResponseEntity.status(HttpStatus.CREATED).body("Avatar créé")
                    : ResponseEntity.ok("Avatar remplacé");
        } catch (Exception e) {
            log.error("Erreur lors de l'upload de l'avatar", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur upload avatar: " + e.getMessage());
        }
    }

    /**
     * Récupère l'avatar d'un utilisateur par son ID.
     *
     * @param id ID de l'utilisateur
     * @return ResponseEntity contenant l'image ou NOT_FOUND
     */
    @GetMapping("/profile/avatar/{id}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long id) {
        try {
            UserProfile profile = userProfileService.getUserProfileById(id);
            String avatarPath = profile.getAvatarPath();

            if (avatarPath == null || avatarPath.isBlank()) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(avatarPath);
            byte[] image = Files.readAllBytes(path);

            String mimeType = Files.probeContentType(path);
            MediaType mediaType = (mimeType != null) ? MediaType.parseMediaType(mimeType)
                    : MediaType.APPLICATION_OCTET_STREAM;

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(image);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crée un nouvel utilisateur.
     *
     * @param dto données de l'utilisateur à créer
     * @return ResponseEntity contenant le DTO créé
     */
    @PostMapping
    public ResponseEntity<UserProfileDTO> createUser(@RequestBody UserProfileDTO dto) {
        UserProfileDTO created = userProfileService.createUser(dto);
        return ResponseEntity.ok(created);
    }

    /**
     * Récupère le profil de l'utilisateur authentifié.
     *
     * @param authentication objet d'authentification Spring Security
     * @return ResponseEntity contenant le DTO du profil ou UNAUTHORIZED
     */
    @GetMapping("/profile/me")
    public ResponseEntity<UserProfileDTO> getMyProfile(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            log.warn("❌ Aucun utilisateur authentifié (authentication ou principal null)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String pseudo;
        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            pseudo = (String) principal;
        } else if (principal instanceof UserDetails) {
            pseudo = ((UserDetails) principal).getUsername();
        } else {
            log.error("⚠️ Type de principal inattendu : {}", principal.getClass());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        log.info("🎯 Auth principal reçu: {}", pseudo);

        try {
            UserProfile profile = userProfileService.findByPseudo(pseudo)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            UserProfileDTO dto = new UserProfileDTO(
                    profile.getPseudo(),
                    profile.getEmail(),
                    (profile.getAvatarPath() != null && !profile.getAvatarPath().isBlank())
                            ? "/api/users/profile/avatar/" + profile.getId()
                            : "");

            log.info("✅ Profil retourné pour {} (email={})", dto.getPseudo(), dto.getEmail());
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération du profil pour '{}': {}", pseudo, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Supprime un utilisateur par son ID.
     *
     * @param id ID de l'utilisateur
     * @return ResponseEntity avec noContent ou NOT_FOUND
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userProfileService.deleteUserById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprime un utilisateur par son ID avec cascade sur ses gammes et ludothèque.
     *
     * @param id ID de l'utilisateur
     * @return ResponseEntity avec noContent ou NOT_FOUND
     */
    @DeleteMapping("/{id}/cascade")
    public ResponseEntity<Void> deleteUserWithCascade(@PathVariable Long id) {
        boolean deleted = userProfileService.deleteUserByIdWithCascade(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint test pour la suppression en cascade.
     *
     * @param id ID de l'utilisateur
     * @return ResponseEntity avec un message de test
     */
    @GetMapping("/{id}/cascade")
    public ResponseEntity<String> testCascade(@PathVariable Long id) {
        return ResponseEntity.ok("OK cascade " + id);
    }

    /**
     * Vérifie si deux utilisateurs sont amis.
     *
     * @param userId   ID du premier utilisateur
     * @param friendId ID du second utilisateur
     * @return ResponseEntity contenant true ou false
     */
    @GetMapping("/are-friends")
    public ResponseEntity<Boolean> areFriends(
            @RequestParam Long userId,
            @RequestParam Long friendId) {
        boolean result = userProfileService.areFriends(userId, friendId);
        return ResponseEntity.ok(result);
    }

}
