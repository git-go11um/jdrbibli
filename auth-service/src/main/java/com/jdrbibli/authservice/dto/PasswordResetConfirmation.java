package com.jdrbibli.authservice.dto;

/**
 * Représente les informations nécessaires pour confirmer une réinitialisation
 * de mot de passe.
 * 
 * Utilisé lors de la validation du code de réinitialisation et de la définition
 * d'un nouveau mot de passe.
 * 
 */
public class PasswordResetConfirmation {

    /** Pseudo de l'utilisateur pour lequel le mot de passe est réinitialisé */
    private String pseudo;

    /** Code de réinitialisation envoyé par email */
    private String code;

    /** Nouveau mot de passe choisi par l'utilisateur */
    private String newPassword;

    /** Constructeur par défaut */
    public PasswordResetConfirmation() {
    }

    /**
     * Constructeur complet
     * 
     * @param pseudo      pseudo de l'utilisateur
     * @param code        code de réinitialisation
     * @param newPassword nouveau mot de passe
     */
    public PasswordResetConfirmation(String pseudo, String code, String newPassword) {
        this.pseudo = pseudo;
        this.code = code;
        this.newPassword = newPassword;
    }

    /** @return le pseudo de l'utilisateur */
    public String getPseudo() {
        return pseudo;
    }

    /** Définit le pseudo de l'utilisateur */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    /** @return le code de réinitialisation */
    public String getCode() {
        return code;
    }

    /** Définit le code de réinitialisation */
    public void setCode(String code) {
        this.code = code;
    }

    /** @return le nouveau mot de passe */
    public String getNewPassword() {
        return newPassword;
    }

    /** Définit le nouveau mot de passe */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
