package com.jdrbibli.authservice.dto;

public class UserProfileDTO {
    private Long id; // <-- ajouté
    private String pseudo;
    private String email;

    public UserProfileDTO() {
    }

    public UserProfileDTO(Long id, String pseudo, String email) { // <-- ajouté
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
    }

    public UserProfileDTO(String pseudo, String email) {
        this.pseudo = pseudo;
        this.email = email;
    }

    // Getters / Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
