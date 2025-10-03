package com.jdrbibli.authservice.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

/**
 * Entité représentant un rôle attribué à un utilisateur.
 * 
 * Cette entité implémente {@link GrantedAuthority} pour être compatible avec
 * Spring Security.
 * 
 */
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    /** Identifiant unique du rôle */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom du rôle (ex: "ROLE_USER", "ROLE_ADMIN") */
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    /** Constructeur par défaut requis par JPA */
    public Role() {
    }

    /**
     * Constructeur avec nom de rôle.
     *
     * @param roleName le nom du rôle
     */
    public Role(String roleName) {
        this.roleName = roleName;
    }

    /** @return l'identifiant du rôle */
    public Long getId() {
        return id;
    }

    /** Définit l'identifiant du rôle */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return le nom du rôle */
    public String getRoleName() {
        return roleName;
    }

    /** Définit le nom du rôle */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Méthode héritée de {@link GrantedAuthority}.
     *
     * @return le nom du rôle (authority) utilisé par Spring Security
     */
    @Override
    public String getAuthority() {
        return roleName;
    }
}
