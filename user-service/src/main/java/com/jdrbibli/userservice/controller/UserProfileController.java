package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.service.UserProfileService;
import com.jdrbibli.userservice.service.UserSyncService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserProfileController {

    private final UserProfileService userProfileService;
private final UserSyncService userSyncService;
    private final String uploadDir = "uploads/avatars/"; // Répertoire pour les avatars

    @Autowired
    public UserProfileController(UserProfileService userProfileService, UserSyncService userSyncService) {
        this.userProfileService = userProfileService;
        this.userSyncService = userSyncService;
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
}
