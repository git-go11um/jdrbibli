package com.jdrbibli.ouvrage_service.dto;

public class GammeDTO {
    private Long id;
    private String nom;
    private String description;
    private String ownerPseudo; // nouveau champ

    public GammeDTO() {
    }

    public GammeDTO(Long id, String nom, String description, String ownerPseudo) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.ownerPseudo = ownerPseudo;
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

    public String getOwnerPseudo() {
        return ownerPseudo;
    }

    public void setOwnerPseudo(String ownerPseudo) {
        this.ownerPseudo = ownerPseudo;
    }
}
