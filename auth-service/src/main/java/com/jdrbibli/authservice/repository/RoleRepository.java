package com.jdrbibli.authservice.repository;

import com.jdrbibli.authservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository JPA pour gérer les rôles des utilisateurs.
 * 
 * Il étend JpaRepository pour bénéficier des opérations CRUD de base.
 * La méthode personnalisée permet de rechercher un rôle par son nom unique.
 * 
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Recherche un rôle par son nom.
     *
     * @param roleName le nom du rôle (ex : "ROLE_USER", "ROLE_ADMIN")
     * @return Optional contenant le Role si trouvé, sinon vide
     */
    Optional<Role> findByRoleName(String roleName);
}
