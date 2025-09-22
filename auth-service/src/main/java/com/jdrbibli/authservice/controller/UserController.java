package com.jdrbibli.authservice.controller;

import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.exception.UserNotFoundException;
import com.jdrbibli.authservice.service.UserService; //-- utilise UserService pour deleteWithCascade

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService; // -- change de IUserService à UserService pour deleteWithCascade

    public UserController(UserService userService) { // -- constructeur
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(userService.toDTO(user));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    // ENDPOINT DELETE CASCADE
    @DeleteMapping("/{id}/cascade") // -- endpoint à appeler depuis auth-service
    public ResponseEntity<?> deleteUserCascade(@PathVariable Long id) {
        try {
            boolean deleted = userService.deleteUserWithCascade(id); // -- méthode service
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
