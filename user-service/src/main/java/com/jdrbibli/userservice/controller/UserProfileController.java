package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.UserProfileDTO;
import com.jdrbibli.userservice.entity.User;
import com.jdrbibli.userservice.service.UserProfileService;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping({ "/api/users", "/user" })
public class UserProfileController {

    private static final Logger log = LoggerFactory.getLogger(UserProfileController.class);

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /** Récupérer tous les utilisateurs */
    @GetMapping
    public List<User> getAllUsers() {
        return userProfileService.getAllUsers();
    }

    /** Récupérer un utilisateur par ID */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userProfileService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Supprimer un utilisateur par pseudo */
    @DeleteMapping("/by-pseudo/{pseudo}")
    public ResponseEntity<Void> deleteUserByPseudo(@PathVariable String pseudo) {
        userProfileService.deleteUserByPseudo(pseudo);
        return ResponseEntity.noContent().build();
    }

    /** Mettre à jour un utilisateur existant par ID */
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserProfileDTO dto) {

        return userProfileService.updateUser(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Rechercher un utilisateur par pseudo */
    @GetMapping("/search")
    public ResponseEntity<UserProfileDTO> searchUserByPseudo(@RequestParam String pseudo) {
        return userProfileService.findByPseudo(pseudo)
                .map(user -> ResponseEntity.ok(new UserProfileDTO(user.getPseudo(), user.getEmail())))
                .orElse(ResponseEntity.notFound().build());
    }

    /** Récupérer la liste des amis d’un utilisateur */
    @GetMapping("/friends/{pseudo}")
    public ResponseEntity<List<FriendDTO>> getFriends(@PathVariable String pseudo) {
        List<FriendDTO> friends = userProfileService.getFriends(pseudo);
        return ResponseEntity.ok(friends);
    }

    /** Upload de l’avatar */
    @PutMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "X-User-Name", required = false) String pseudo) {

        if (file.isEmpty()) {
            log.warn("Tentative d'upload d'un avatar vide");
            return ResponseEntity.badRequest().body("Fichier vide");
        }

        if (pseudo == null || pseudo.isEmpty()) {
            log.error("Header X-User-Name manquant ou vide");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
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

    /** Récupérer l’avatar d’un utilisateur par son ID */
    @GetMapping("/profile/avatar/{id}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long id) {
        try {
            User user = userProfileService.getUserProfileById(id);
            String avatarPath = user.getAvatarPath();

            if (avatarPath == null) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(avatarPath);
            byte[] image = Files.readAllBytes(path);

            String mimeType = Files.probeContentType(path);
            MediaType mediaType = (mimeType != null)
                    ? MediaType.parseMediaType(mimeType)
                    : MediaType.APPLICATION_OCTET_STREAM;

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(image);
        } catch (IOException e) {
            log.error("Erreur IO lors de la récupération de l'avatar", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<UserProfileDTO> createUser(@RequestBody UserProfileDTO dto) {
        UserProfileDTO created = userProfileService.createUser(dto);
        return ResponseEntity.ok(created);
    }

}
