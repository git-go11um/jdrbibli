package com.jdrbibli.authservice.dto;

/**
 * Représente la requête de changement de mot de passe pour un utilisateur connecté.
 * 
 * Contient le mot de passe actuel, le nouveau mot de passe et la confirmation du nouveau mot de passe.
 * Cette classe est utilisée lors de la modification du mot de passe via le profil utilisateur.
 * 
 */
public class ChangePasswordProfileRequest {

    /** Mot de passe actuel de l'utilisateur */
    private String currentPassword;

    /** Nouveau mot de passe choisi par l'utilisateur */
    private String newPassword;

    /** Confirmation du nouveau mot de passe */
    private String confirmNewPassword;

    /** @return le mot de passe actuel */
    public String getCurrentPassword() {
        return currentPassword;
    }

    /** Définit le mot de passe actuel */
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

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
