package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.repository.UserProfileRepository;
import com.jdrbibli.userservice.service.UserLudothequeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Contrôleur pour gérer la ludothèque d'un utilisateur.
 * 
 * Permet d'ajouter ou de retirer des ouvrages de la ludothèque
 * d'un utilisateur spécifique.
 */
@RestController
@RequestMapping("/api/users")
public class UserLudothequeController {

    private final UserProfileRepository userProfileRepository;
    private final UserLudothequeService userLudothequeService;

    /**
     * Constructeur du contrôleur.
     *
     * @param userProfileRepository repository pour accéder aux profils utilisateurs
     * @param userLudothequeService service pour gérer les ouvrages de la ludothèque
     */
    public UserLudothequeController(UserProfileRepository userProfileRepository,
                                    UserLudothequeService userLudothequeService) {
        this.userProfileRepository = userProfileRepository;
        this.userLudothequeService = userLudothequeService;
    }

    /**
     * Ajoute un ouvrage à la ludothèque de l'utilisateur.
     *
     * @param userId    ID de l'utilisateur
     * @param ouvrageId ID de l'ouvrage à ajouter
     * @return ResponseEntity indiquant le succès ou un statut NOT_FOUND si l'utilisateur ou l'ouvrage n'existe pas
     */
    @PostMapping("/{userId}/ludotheque/{ouvrageId}")
    public ResponseEntity<?> addOuvrageToLudotheque(@PathVariable Long userId, @PathVariable Long ouvrageId) {
        Optional<UserProfile> userOpt = userProfileRepository.findById(userId);

        if (userOpt.isPresent()) {
            UserProfile user = userOpt.get();

            OuvrageDTO ouvrage = userLudothequeService.getOuvrageById(ouvrageId);
            if (ouvrage != null) {
                user.getOuvrageIds().add(ouvrageId);
                userProfileRepository.save(user);
                return ResponseEntity.ok("Ouvrage ajouté à la ludothèque !");
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retire un ouvrage de la ludothèque de l'utilisateur.
     *
     * @param userId    ID de l'utilisateur
     * @param ouvrageId ID de l'ouvrage à retirer
     * @return ResponseEntity indiquant le succès ou un statut NOT_FOUND si l'utilisateur n'existe pas
     */
    @DeleteMapping("/{userId}/ludotheque/{ouvrageId}")
    public ResponseEntity<?> removeOuvrageFromLudotheque(@PathVariable Long userId, @PathVariable Long ouvrageId) {
        Optional<UserProfile> userOpt = userProfileRepository.findById(userId);

        if (userOpt.isPresent()) {
            UserProfile user = userOpt.get();
            user.getOuvrageIds().remove(ouvrageId);
            userProfileRepository.save(user);
            return ResponseEntity.ok("Ouvrage retiré de la ludothèque !");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
