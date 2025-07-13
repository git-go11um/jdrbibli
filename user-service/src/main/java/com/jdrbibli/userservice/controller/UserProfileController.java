package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.service.UserProfileService;
import com.jdrbibli.userservice.service.UserSyncService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Autowired
    private UserSyncService userSyncService;

    @GetMapping
    public List<UserProfile> getAllUsers() {
        return userProfileService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getUserById(@PathVariable Long id) {
        return userProfileService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserProfile createUser(@RequestBody UserProfile userProfile) {
        return userProfileService.createUser(userProfile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userProfileService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Nouvel endpoint pour uploader l'avatar
    @PostMapping("/{pseudo}/avatar")
    public ResponseEntity<?> uploadAvatar(@PathVariable String pseudo, @RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = userProfileService.uploadAvatar(pseudo, file);
            return ResponseEntity.ok().body(java.util.Map.of("avatarUrl", avatarUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    // Endpoint pour récupérer l'avatar d'un utilisateur
    @GetMapping("/{pseudo}/avatar")
    public ResponseEntity<?> getAvatar(@PathVariable String pseudo) {
        try {
            String avatarUrl = userProfileService.getAvatarUrl(pseudo);
            return ResponseEntity.ok().body(java.util.Map.of("avatarUrl", avatarUrl));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint pour la synchronisation depuis l'auth-service
    @PostMapping("/sync")
    public ResponseEntity<?> syncUser(@RequestBody java.util.Map<String, String> userData) {
        try {
            String pseudo = userData.get("pseudo");
            String email = userData.get("email");

            Optional<UserProfile> existingUser = userProfileService.getUserByPseudo(pseudo);
            if (existingUser.isPresent()) {
                UserProfile user = existingUser.get();
                user.setEmail(email); // Met à jour l'email
                userProfileService.createUser(user); // save/update
                return ResponseEntity.ok().body("Utilisateur synchronisé et email mis à jour: " + pseudo);
            }

            // Créer le nouvel utilisateur
            UserProfile userProfile = new UserProfile();
            userProfile.setPseudo(pseudo);
            userProfile.setEmail(email);
            userProfile.setAvatarUrl(null);

            UserProfile savedUser = userProfileService.createUser(userProfile);
            return ResponseEntity.ok().body("Utilisateur synchronisé avec succès: " + savedUser.getPseudo());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    // Endpoint pour synchroniser tous les utilisateurs
    @PostMapping("/sync-all")
    public ResponseEntity<?> syncAllUsers() {
        try {
            userSyncService.syncAllUsersFromAuthService();
            return ResponseEntity.ok().body("Synchronisation terminée");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    // Endpoint pour créer un utilisateur manuellement (pour les utilisateurs
    // existants)
    @PostMapping("/create")
    public ResponseEntity<?> createUserByPseudo(@RequestParam String pseudo, @RequestParam String email) {
        try {
            // Vérifier si l'utilisateur existe déjà
            Optional<UserProfile> existingUser = userProfileService.getUserByPseudo(pseudo);
            if (existingUser.isPresent()) {
                UserProfile user = existingUser.get();
                user.setEmail(email); // Met à jour l'email
                userProfileService.createUser(user); // Utilise save/update
                return ResponseEntity.ok().body("Utilisateur synchronisé et email mis à jour: " + pseudo);
            }

            UserProfile userProfile = new UserProfile();
            userProfile.setPseudo(pseudo);
            userProfile.setEmail(email);
            userProfile.setAvatarUrl(null);

            UserProfile savedUser = userProfileService.createUser(userProfile);
            return ResponseEntity.ok()
                    .body(java.util.Map.of("message", "Utilisateur créé avec succès", "user", savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    // Endpoint pour vérifier si un utilisateur existe
    @GetMapping("/check/{pseudo}")
    public ResponseEntity<?> checkUserExists(@PathVariable String pseudo) {
        try {
            Optional<UserProfile> user = userProfileService.getUserByPseudo(pseudo);
            if (user.isPresent()) {
                return ResponseEntity.ok().body(java.util.Map.of("exists", true, "user", user.get()));
            } else {
                return ResponseEntity.ok().body(java.util.Map.of("exists", false));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
