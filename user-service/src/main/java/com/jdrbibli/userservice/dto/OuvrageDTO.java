package com.jdrbibli.userservice.dto;

/**
 * Data Transfer Object représentant un ouvrage.
 * 
 * Contient un identifiant unique et un titre.
 */
public class OuvrageDTO {

    /** Identifiant unique de l'ouvrage */
    private Long id;

    /** Titre de l'ouvrage */
    private String titre;

    /** Retourne l'identifiant unique de l'ouvrage */
    public Long getId() {
        return id;
    }

    /** Définit l'identifiant unique de l'ouvrage */
    public void setId(Long id) {
        this.id = id;
    }

    /** Retourne le titre de l'ouvrage */
    public String getTitre() {
        return titre;
    }

    /** Définit le titre de l'ouvrage */
    public void setTitre(String titre) {
        this.titre = titre;
    }
}
