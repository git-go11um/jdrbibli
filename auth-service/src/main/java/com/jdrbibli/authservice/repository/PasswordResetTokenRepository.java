package com.jdrbibli.authservice.repository;

import com.jdrbibli.authservice.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA pour gérer les tokens de réinitialisation de mot de passe.
 * 
 * Il étend JpaRepository pour bénéficier de toutes les opérations CRUD.
 * Les méthodes personnalisées permettent de rechercher ou supprimer un token
 * en fonction de l'utilisateur ou du token lui-même.
 * 
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Recherche un token de réinitialisation par sa valeur unique.
     *
     * @param token La valeur du token
     * @return Optional contenant le PasswordResetToken si trouvé
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Supprime tous les tokens associés à un utilisateur donné.
     * 
     * Utile lorsque l'utilisateur réinitialise son mot de passe ou est supprimé.
     * 
     *
     * @param userId ID de l'utilisateur
     */
    void deleteByUserId(Long userId);
}
