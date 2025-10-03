package com.jdrbibli.authservice.repository;

import com.jdrbibli.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository JPA pour gérer les utilisateurs.
 * 
 * Il étend JpaRepository pour bénéficier des opérations CRUD classiques.
 * Plusieurs méthodes personnalisées permettent de rechercher et de vérifier l'existence d'un utilisateur
 * par son pseudo ou son email.
 * 
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son email.
     * 
     * @param email l'email de l'utilisateur
     * @return Optional contenant le User si trouvé, sinon vide
     */
    Optional<User> findByEmail(String email);

    /**
     * Recherche un utilisateur par son pseudo (nom d'utilisateur unique).
     * 
     * @param pseudo le pseudo de l'utilisateur
     * @return Optional contenant le User si trouvé, sinon vide
     */
    Optional<User> findByPseudo(String pseudo);

    /**
     * Vérifie si un utilisateur existe avec un email donné.
     * 
     * @param email l'email à vérifier
     * @return true si un utilisateur avec cet email existe, sinon false
     */
    boolean existsByEmail(String email);

    /**
     * Vérifie si un utilisateur existe avec un pseudo donné.
     * 
     * @param pseudo le pseudo à vérifier
     * @return true si un utilisateur avec ce pseudo existe, sinon false
     */
    boolean existsByPseudo(String pseudo);
}
