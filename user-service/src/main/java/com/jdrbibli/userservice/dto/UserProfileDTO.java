package com.jdrbibli.userservice.dto;

/**
 * Data Transfer Object représentant un profil utilisateur.
 * <p>
 * Contient l'identifiant, le pseudo, l'email et l'URL de l'avatar.
 */
public class UserProfileDTO {

    /** Identifiant unique de l'utilisateur */
    private Long id;

    /** Pseudo de l'utilisateur */
    private String pseudo;

    /** Email de l'utilisateur */
    private String email;

    /** URL vers l'avatar de l'utilisateur */
    private String avatarUrl;

    /** Constructeur vide */
    public UserProfileDTO() {
    }

    /** Constructeur avec id, pseudo et email */
    public UserProfileDTO(Long id, String pseudo, String email) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
    }

    /** Constructeur avec pseudo, email et avatarUrl */
    public UserProfileDTO(String pseudo, String email, String avatarUrl) {
        this.pseudo = pseudo;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    /** Constructeur complet avec id, pseudo, email et avatarUrl */
    public UserProfileDTO(Long id, String pseudo, String email, String avatarUrl) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
