package com.jdrbibli.userservice.dto;

/**
 * Data Transfer Object représentant un ami.
 * 
 * Contient les informations essentielles pour exposer un utilisateur
 * dans le contexte des relations d'amitié.
 */
public class FriendDTO {

    /** Identifiant unique de l'utilisateur */
    private Long id;

    /** Pseudo de l'utilisateur */
    private String pseudo;

    /** Adresse email de l'utilisateur */
    private String email;

    /** URL de l'avatar de l'utilisateur (peut être vide si aucun avatar) */
    private String avatarUrl;

    /** Retourne l'ID de l'utilisateur */
    public Long getId() { return id; }

    /** Définit l'ID de l'utilisateur */
    public void setId(Long id) { this.id = id; }

    /** Retourne le pseudo de l'utilisateur */
    public String getPseudo() { return pseudo; }

    /** Définit le pseudo de l'utilisateur */
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    /** Retourne l'adresse email de l'utilisateur */
    public String getEmail() { return email; }

    /** Définit l'adresse email de l'utilisateur */
    public void setEmail(String email) { this.email = email; }

    /** Retourne l'URL de l'avatar de l'utilisateur */
    public String getAvatarUrl() { return avatarUrl; }

    /** Définit l'URL de l'avatar de l'utilisateur */
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
