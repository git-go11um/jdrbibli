package com.jdrbibli.ouvrage_service.dto;

public class GammeDTO {
    private Long id;
    private String nom;
    private String description;
    private Long ownerId; // <-- remplace ownerPseudo

    public GammeDTO() {
    }

    public GammeDTO(Long id, String nom, String description, Long ownerId) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.ownerId = ownerId;
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
