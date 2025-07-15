package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
