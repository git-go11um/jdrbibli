package com.jdrbibli.userservice.dto;

import java.util.List;

/**
 * Data Transfer Object représentant une gamme.
 * 
 * Une gamme contient un identifiant, un titre et une liste d'ouvrages associés.
 */
public class GammeDTO {

    /** Identifiant unique de la gamme */
    private Long id;

    /** Titre de la gamme */
    private String title;

    /** Liste des ouvrages associés à cette gamme */
    private List<OuvrageDTO> ouvrages;

    /** Retourne l'identifiant unique de la gamme */
    public Long getId() {
        return id;
    }

    /** Définit l'identifiant unique de la gamme */
    public void setId(Long id) {
        this.id = id;
    }

    /** Retourne le titre de la gamme */
    public String getTitle() {
        return title;
    }

    /** Définit le titre de la gamme */
    public void setTitle(String title) {
        this.title = title;
    }

    /** Retourne la liste des ouvrages associés */
    public List<OuvrageDTO> getOuvrages() {
        return ouvrages;
    }

    /** Définit la liste des ouvrages associés */
    public void setOuvrages(List<OuvrageDTO> ouvrages) {
        this.ouvrages = ouvrages;
    }
}
