package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    // Endpoint pour récupérer tous les utilisateurs
    @GetMapping
    public List<UserProfile> getAllUsers() {
        return userProfileService.getAllUsers();
    }

    // Endpoint pour récupérer un utilisateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getUserById(@PathVariable Long id) {
        return userProfileService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint pour récupérer le profil de l'utilisateur connecté (principal)
    @GetMapping("/me")
    public ResponseEntity<UserProfile> getCurrentUserProfile(Principal principal) {
        String username = principal.getName();
        Optional<UserProfile> userProfile = userProfileService.getUserByPseudo(username);
        return userProfile.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Endpoint pour créer un nouvel utilisateur
    @PostMapping
    public ResponseEntity<UserProfile> createUser(@RequestBody UserProfile userProfile) {
        try {
            UserProfile createdUser = userProfileService.createUser(userProfile);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            // Gestion d'erreur si l'utilisateur existe déjà ou autre
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Endpoint pour supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userProfileService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Endpoint pour l'upload d'image de profil
    @PostMapping("/me/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("image") MultipartFile imageFile, Principal principal) {
        try {
            // Récupérer le nom de l'utilisateur connecté
            String username = principal.getName();

            // Vérifier si l'extension de fichier est valide (facultatif)
            String contentType = imageFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is not a valid image.");
            }

            // Appeler la méthode du service pour stocker l'avatar et récupérer l'URL
            String imageUrl = userProfileService.storeUserAvatar(username, imageFile);

            return ResponseEntity.ok().body("Image uploaded successfully: " + imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading image: " + e.getMessage());
        }
    }

    // Endpoint pour récupérer l'avatar d'un utilisateur
    @GetMapping("/me/avatar")
    public ResponseEntity<String> getAvatar(Principal principal) {
        String username = principal.getName();
        Optional<String> avatarUrl = userProfileService.getUserAvatarUrl(username);

        return avatarUrl
                .map(url -> ResponseEntity.ok(url))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Avatar not found"));
    }

    // Endpoint pour mettre à jour les informations de profil (y compris avatar)
    @PutMapping("/me")
    public ResponseEntity<UserProfile> updateProfile(@RequestBody UserProfile userProfile, Principal principal) {
        String username = principal.getName();
        try {
            UserProfile updatedProfile = userProfileService.updateUserProfile(username, userProfile);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
