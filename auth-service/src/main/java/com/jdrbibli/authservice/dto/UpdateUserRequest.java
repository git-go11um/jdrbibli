package com.jdrbibli.authservice.dto;

/**
 * DTO utilisé pour la mise à jour des informations d'un utilisateur.
 * <p>
 * Contient les champs modifiables par l'utilisateur : pseudo et email.
 * </p>
 */
public class UpdateUserRequest {

    /** Nouveau pseudo de l'utilisateur */
    private String pseudo;

    /** Nouvelle adresse email de l'utilisateur */
    private String email;

    /** @return le pseudo */
    public String getPseudo() {
        return pseudo;
    }

    /** Définit le pseudo de l'utilisateur */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    /** @return l'email */
    public String getEmail() {
        return email;
    }

    /** Définit l'email de l'utilisateur */
    public void setEmail(String email) {
        this.email = email;
    }
}
