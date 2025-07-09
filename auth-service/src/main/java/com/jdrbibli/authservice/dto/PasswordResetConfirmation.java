package com.jdrbibli.authservice.dto;

public class PasswordResetConfirmation {

    private String pseudo; // Ajout du pseudo pour identifier l'utilisateur
    private String code; // Code de réinitialisation envoyé par email
    private String token; // Le token généré pour la réinitialisation
    private String newPassword; // Nouveau mot de passe

    // Constructeurs

    public PasswordResetConfirmation() {
    }

    public PasswordResetConfirmation(String pseudo, String code, String token, String newPassword) {
        this.pseudo = pseudo;
        this.code = code;
        this.token = token;
        this.newPassword = newPassword;
    }

    // Getters et setters

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
