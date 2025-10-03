package com.jdrbibli.authservice.dto;

/**
 * Représente la requête de connexion (login) d'un utilisateur.
 * 
 * Contient le pseudo et le mot de passe nécessaires pour l'authentification.
 * Utilisé par AuthController pour valider les informations de connexion.
 * 
 */
public class LoginRequest {

    /** Pseudo ou nom d'utilisateur */
    private String pseudo;

    /** Mot de passe associé au pseudo */
    private String password;

    /** Constructeur par défaut */
    public LoginRequest() {
    }

    /** @return le pseudo de l'utilisateur */
    public String getPseudo() {
        return pseudo;
    }

    /** Définit le pseudo de l'utilisateur */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    /** @return le mot de passe de l'utilisateur */
    public String getPassword() {
        return password;
    }

    /** Définit le mot de passe de l'utilisateur */
    public void setPassword(String password) {
        this.password = password;
    }
}
