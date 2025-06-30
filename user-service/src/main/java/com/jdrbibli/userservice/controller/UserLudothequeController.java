package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.repository.UserProfileRepository;
import com.jdrbibli.userservice.service.UserLudothequeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserLudothequeController {

    private final UserProfileRepository userProfileRepository;
    private final UserLudothequeService userLudothequeService;

    public UserLudothequeController(UserProfileRepository userProfileRepository,
                                    UserLudothequeService userLudothequeService) {
        this.userProfileRepository = userProfileRepository;
        this.userLudothequeService = userLudothequeService;
    }

    @PostMapping("/{userId}/ludotheque/{ouvrageId}")
    public ResponseEntity<?> addOuvrageToLudotheque(@PathVariable Long userId, @PathVariable Long ouvrageId) {
        Optional<UserProfile> userOpt = userProfileRepository.findById(userId);

        if (userOpt.isPresent()) {
            UserProfile user = userOpt.get();

            // On va chercher l'ouvrage via WebClient
            OuvrageDTO ouvrage = userLudothequeService.getOuvrageById(ouvrageId);
            if (ouvrage != null) {
                user.getOuvrageIds().add(ouvrageId); // On stocke juste l'ID
                userProfileRepository.save(user);
                return ResponseEntity.ok("Ouvrage ajouté à la ludothèque !");
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}/ludotheque/{ouvrageId}")
    public ResponseEntity<?> removeOuvrageFromLudotheque(@PathVariable Long userId, @PathVariable Long ouvrageId) {
        Optional<UserProfile> userOpt = userProfileRepository.findById(userId);

        if (userOpt.isPresent()) {
            UserProfile user = userOpt.get();
            user.getOuvrageIds().remove(ouvrageId); // Supprime l'ID
            userProfileRepository.save(user);
            return ResponseEntity.ok("Ouvrage retiré de la ludothèque !");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
