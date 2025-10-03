package com.jdrbibli.authservice.dto;

import java.util.Set;

/**
 * DTO représentant la réponse contenant les informations d'un utilisateur.
 * <p>
 * Utilisé pour renvoyer les détails d'un utilisateur via les endpoints de l'API AuthService.
 * Contient le pseudo, l'email, l'identifiant et les rôles de l'utilisateur.
 * </p>
 */
public class UserResponseDTO {

    /** Identifiant unique de l'utilisateur */
    private Long id;

    /** Pseudo (nom d'utilisateur) */
    private String pseudo;

    /** Adresse email de l'utilisateur */
    private String email;

    /** Ensemble des rôles de l'utilisateur (ex: "ROLE_USER", "ROLE_ADMIN") */
    private Set<String> roles;

    /** Constructeur complet */
    public UserResponseDTO(Long id, String pseudo, String email, Set<String> roles) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
        this.roles = roles;
    }

    /** Constructeur par défaut */
    public UserResponseDTO() {
    }

    /** @return l'identifiant de l'utilisateur */
    public Long getId() {
        return id;
    }

    /** @return le pseudo */
    public String getPseudo() {
        return pseudo;
    }

    /** Définit le pseudo de l'utilisateur */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    /** @return l'adresse email */
    public String getEmail() {
        return email;
    }

    /** Définit l'adresse email de l'utilisateur */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return l'ensemble des rôles */
    public Set<String> getRoles() {
        return roles;
    }
}
