package com.jdrbibli.authservice.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String pseudo;
    private String motDePasse;

    public LoginRequest() {
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}
