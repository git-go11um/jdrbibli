package com.jdrbibli.authservice.dto;

public class LoginRequest {
    private String pseudo;
    private String password;

    public LoginRequest() {
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
