package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.service.UserProfileService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Récupérer tous les utilisateurs.
     */
    @GetMapping
    public List<UserProfile> getAllUsers() {
        return userProfileService.getAllUsers();
    }

    /**
     * Récupérer un utilisateur par ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getUserById(@PathVariable Long id) {
        return userProfileService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Créer un nouvel utilisateur.
     */
    @PostMapping
    public UserProfile createUser(@RequestBody UserProfile userProfile) {
        return userProfileService.createUser(userProfile);
    }

    /**
     * Supprimer un utilisateur par ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userProfileService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Rechercher un utilisateur par pseudo.
     */
    @GetMapping("/search")
    public ResponseEntity<UserProfile> searchUserByPseudo(@RequestParam String pseudo) {
        return userProfileService.findByPseudo(pseudo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupérer la liste des amis d’un utilisateur.
     */
    @GetMapping("/friends/{pseudo}")
    public ResponseEntity<List<FriendDTO>> getFriends(@PathVariable String pseudo) {
        List<FriendDTO> friends = userProfileService.getFriends(pseudo);
        return ResponseEntity.ok(friends);
    }

    @PutMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file, Principal principal) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Fichier vide");
        }
        try {
            String pseudo = principal.getName();

            boolean created = userProfileService.saveUserAvatar(pseudo, file);
            // => renvoie true si c'était le 1er avatar, false sinon

            return created
                    ? ResponseEntity.status(HttpStatus.CREATED).body("Avatar créé")
                    : ResponseEntity.ok("Avatar remplacé");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur upload avatar: " + e.getMessage());
        }
    }

    @GetMapping("/profile/avatar/{id}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long id) {
        try {
            UserProfile user = userProfileService.getUserProfileById(id); // méthode à exposer dans le service
            String avatarPath = user.getAvatarPath();

            if (avatarPath == null) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(avatarPath);
            byte[] image = Files.readAllBytes(path);

            // Détection du type MIME (peut renvoyer null si type inconnu)
            String mimeType = Files.probeContentType(path);
            MediaType mediaType = (mimeType != null)
                    ? MediaType.parseMediaType(mimeType)
                    : MediaType.APPLICATION_OCTET_STREAM;

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(image);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
