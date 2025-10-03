package com.jdrbibli.authservice.dto;

/**
 * DTO représentant le profil d'un utilisateur.
 * 
 * Contient les informations basiques de l'utilisateur utilisées 
 * lors de la communication entre les microservices (auth-service et user-service).
 * 
 */
public class UserProfileDTO {

    /** Identifiant unique de l'utilisateur */
    private Long id;

    /** Pseudo (nom d'utilisateur) */
    private String pseudo;

    /** Adresse email de l'utilisateur */
    private String email;

    /** Constructeur par défaut */
    public UserProfileDTO() {
    }

    /**
     * Constructeur complet avec id, pseudo et email.
     *
     * @param id identifiant unique de l'utilisateur
     * @param pseudo pseudo de l'utilisateur
     * @param email email de l'utilisateur
     */
    public UserProfileDTO(Long id, String pseudo, String email) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
    }

    /**
     * Constructeur sans id (utilisé pour créer un profil avant l'attribution d'un id)
     *
     * @param pseudo pseudo de l'utilisateur
     * @param email email de l'utilisateur
     */
    public UserProfileDTO(String pseudo, String email) {
        this.pseudo = pseudo;
        this.email = email;
    }

    /** @return l'identifiant de l'utilisateur */
    public Long getId() {
        return id;
    }

    /** Définit l'identifiant de l'utilisateur */
    public void setId(Long id) {
        this.id = id;
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
}
