package com.jdrbibli.authservice.dto;

public class UserProfileDTO {
    private String pseudo;
    private String email;

    public UserProfileDTO() {
    }

    public UserProfileDTO(String pseudo, String email) {
        this.pseudo = pseudo;
        this.email = email;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
