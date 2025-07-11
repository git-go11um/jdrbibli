package com.jdrbibli.authservice.dto;

public class UpdateUserRequest {
    private String pseudo;
    private String email;

    // Getters et setters
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
