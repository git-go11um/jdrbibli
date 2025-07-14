package com.jdrbibli.authservice.dto;

import java.util.Set;

public class UserResponseDTO {
    private Long id;
    private String pseudo;
    private String email;
    private Set<String> roles;

    public UserResponseDTO(Long id, String pseudo, String email, Set<String> roles) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
        this.roles = roles;
    }

    public UserResponseDTO() {
        // constructeur par défaut requis pour les frameworks et tests
    }

    // Getters et setters

    public Long getId() {
        return id;
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

    public Set<String> getRoles() {
        return roles;
    }
}
