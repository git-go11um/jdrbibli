package com.jdrbibli.authservice.dto;

public class PasswordResetRequest {
    private String pseudo;

    public PasswordResetRequest() {}

    public PasswordResetRequest(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
}
