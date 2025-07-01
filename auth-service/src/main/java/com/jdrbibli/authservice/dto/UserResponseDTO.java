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

    // Getters et setters

    public Long getId() { return id; }
    public String getPseudo() { return pseudo; }
    public String getEmail() { return email; }
    public Set<String> getRoles() { return roles; }
}
