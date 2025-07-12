package com.jdrbibli.authservice.dto;

public class ChangePasswordProfileRequest {
    private String currentPassword; // mot de passe actuel
    private String newPassword; // nouveau mot de passe
    private String confirmNewPassword; // confirmation du nouveau mot de passe

    // Getter et Setter pour currentPassword
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    // Getter et Setter pour newPassword
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    // Getter et Setter pour confirmNewPassword
    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
