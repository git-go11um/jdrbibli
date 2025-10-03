package com.jdrbibli.userservice.repository;

import com.jdrbibli.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository pour gérer les opérations CRUD et les requêtes spécifiques
 * liées aux entités {@link UserProfile}.
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * Cherche un utilisateur par son email.
     *
     * @param email l'email de l'utilisateur
     * @return un Optional contenant l'utilisateur si trouvé, sinon vide
     */
    Optional<UserProfile> findByEmail(String email);

    /**
     * Cherche un utilisateur par son pseudo.
     *
     * @param pseudo le pseudo de l'utilisateur
     * @return un Optional contenant l'utilisateur si trouvé, sinon vide
     */
    Optional<UserProfile> findByPseudo(String pseudo);
}
