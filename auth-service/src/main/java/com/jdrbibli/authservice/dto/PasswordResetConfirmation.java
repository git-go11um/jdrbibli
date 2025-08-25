package com.jdrbibli.authservice.dto;

public class PasswordResetConfirmation {

    private String pseudo;
    private String code;
    private String newPassword;

    public PasswordResetConfirmation() {
    }

    // Constructeurs

    public PasswordResetConfirmation(String pseudo, String code, String newPassword) {
        this.pseudo = pseudo;
        this.code = code;
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

    

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
