package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.dto.ChangePasswordProfileRequest;
import com.jdrbibli.authservice.dto.ChangePasswordRequest;
import com.jdrbibli.authservice.dto.ReponseProfileChange;
import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.User;
import jakarta.mail.MessagingException;

/**
 * Interface définissant les services liés aux utilisateurs.
 * Contient l'inscription, la connexion, la gestion des mots de passe, et la modification de profil.
 */
public interface IUserService {

    /**
     * Crée un nouvel utilisateur avec un pseudo, un email et un mot de passe.
     */
    User inscrireNewUser(String pseudo, String email, String password);

    /**
     * Authentifie un utilisateur avec son email et son mot de passe.
     */
    User login(String email, String password);

    /**
     * Convertit un objet User en DTO pour exposer uniquement les informations nécessaires.
     */
    UserResponseDTO toDTO(User user);

    /**
     * Récupère un utilisateur à partir de son pseudo.
     */
    User getUserByPseudo(String pseudo);

    /**
     * Récupère un utilisateur à partir de son ID.
     */
    User getUserById(Long id); 

    /**
     * Change le mot de passe d'un utilisateur via son email.
     */
    void changePassword(String userEmail, ChangePasswordRequest request);

    /**
     * Vérifie que le code de réinitialisation pour le pseudo donné est valide.
     */
    boolean validateResetCode(String pseudo, String code);

    /**
     * Envoie un email pour demander la réinitialisation du mot de passe.
     */
    void requestPasswordReset(String pseudo) throws MessagingException;

    /**
     * Réinitialise le mot de passe d'un utilisateur en utilisant le code reçu par email.
     */
    void resetPassword(String pseudo, String resetCode, String newPassword);

    /**
     * Supprime un utilisateur par son ID.
     */
    void deleteUserById(Long userId);

    /**
     * Met à jour le profil d'un utilisateur (pseudo et email) et retourne la réponse incluant un nouveau token si nécessaire.
     */
    ReponseProfileChange updateUserProfile(Long userId, String newPseudo, String newEmail);

    /**
     * Change le mot de passe d'un utilisateur connecté à partir de son pseudo et de la requête de changement.
     */
    void changeProfilePassword(String userPseudo, ChangePasswordProfileRequest request);
}
