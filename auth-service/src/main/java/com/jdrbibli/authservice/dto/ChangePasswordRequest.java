package com.jdrbibli.authservice.dto;

/**
 * Représente la requête de changement de mot de passe pour un utilisateur.
 * <p>
 * Contient le nouveau mot de passe et sa confirmation. Utilisée lorsqu'un mot de passe
 * doit être modifié sans fournir le mot de passe actuel (ex. réinitialisation via code).
 * </p>
 */
public class ChangePasswordRequest {

    /** Nouveau mot de passe choisi par l'utilisateur */
    private String newPassword;

    /** Confirmation du nouveau mot de passe */
    private String confirmNewPassword;

    /** @return le nouveau mot de passe */
    public String getNewPassword() {
        return newPassword;
    }

    /** Définit le nouveau mot de passe */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /** @return la confirmation du nouveau mot de passe */
    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    /** Définit la confirmation du nouveau mot de passe */
    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
