package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.exception.UserNotFoundException;
import com.jdrbibli.authservice.service.UserService;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour gérer les utilisateurs.
 * <p>
 * Fournit des endpoints pour :
 * <ul>
 *     <li>Récupérer un utilisateur par son ID</li>
 *     <li>Supprimer un utilisateur et ses entités associées en cascade</li>
 * </ul>
 * </p>
 * <p>
 * Utilise {@link UserService} pour la logique métier.
 * </p>
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * Constructeur avec injection de {@link UserService}.
     *
     * @param userService service pour la gestion des utilisateurs
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Récupère un utilisateur par son ID.
     *
     * @param id ID de l'utilisateur
     * @return {@link ResponseEntity} contenant le {@link UserResponseDTO} si trouvé,
     *         ou un message d'erreur si non trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(userService.toDTO(user));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Supprime un utilisateur et toutes les entités associées (cascade).
     *
     * @param id ID de l'utilisateur
     * @return {@link ResponseEntity} avec un message de succès ou d'erreur
     */
    @DeleteMapping("/{id}/cascade")
    public ResponseEntity<?> deleteUserCascade(@PathVariable Long id) {
        try {
            boolean deleted = userService.deleteUserWithCascade(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès dans les deux services"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Utilisateur non trouvé ou déjà supprimé"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la suppression : " + e.getMessage()));
        }
    }
}
