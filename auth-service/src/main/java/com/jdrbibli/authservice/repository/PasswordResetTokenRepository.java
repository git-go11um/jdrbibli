package com.jdrbibli.authservice.repository;

import com.jdrbibli.authservice.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Méthode pour trouver un token par sa valeur
    Optional<PasswordResetToken> findByToken(String token);

    // Méthode pour supprimer tous les tokens associés à un utilisateur (par
    // user_id)
    void deleteByUserId(Long userId);
}
