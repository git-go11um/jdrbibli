package com.jdrbibli.authservice.dto;

/**
 * Représente une requête de réinitialisation de mot de passe.
 * 
 * Contient le pseudo de l'utilisateur, le code de réinitialisation reçu et le nouveau mot de passe choisi.
 * Utilisé par les endpoints de réinitialisation de mot de passe pour valider et appliquer le changement.
 * 
 */
public class PasswordResetRequest {

    /** Pseudo de l'utilisateur */
    private String pseudo;

    /** Code de réinitialisation envoyé par email */
    private String code;

    /** Nouveau mot de passe à définir */
    private String newPassword;

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
