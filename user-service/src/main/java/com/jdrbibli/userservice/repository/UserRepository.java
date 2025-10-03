package com.jdrbibli.userservice.repository;

import com.jdrbibli.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour gérer les opérations CRUD et les requêtes spécifiques
 * liées aux entités {@link UserProfile}, avec des méthodes supplémentaires
 * pour récupérer les relations associées.
 */
@Repository
public interface UserRepository extends JpaRepository<UserProfile, Long> {

    /**
     * Cherche un utilisateur par son pseudo.
     *
     * @param pseudo le pseudo de l'utilisateur
     * @return un Optional contenant l'utilisateur si trouvé, sinon vide
     */
    Optional<UserProfile> findByPseudo(String pseudo);

    /**
     * Cherche un utilisateur par son email.
     *
     * @param email l'email de l'utilisateur
     * @return un Optional contenant l'utilisateur si trouvé, sinon vide
     */
    Optional<UserProfile> findByEmail(String email);

    /**
     * Récupère un utilisateur par son ID en incluant ses gammes
     * et les ouvrages associés à ces gammes pour éviter le lazy loading.
     *
     * @param userId l'ID de l'utilisateur
     * @return un Optional contenant l'utilisateur avec ses gammes et ouvrages
     *         si trouvé, sinon vide
     */
    @Query("SELECT u FROM UserProfile u " +
            "LEFT JOIN FETCH u.gammes g " +
            "LEFT JOIN FETCH g.ouvrages " +
            "WHERE u.id = :userId")
    Optional<UserProfile> findByIdWithGammesAndOuvrages(@Param("userId") Long userId);
}
