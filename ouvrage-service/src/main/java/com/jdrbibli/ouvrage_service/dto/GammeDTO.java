package com.jdrbibli.ouvrage_service.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object représentant une {@link com.jdrbibli.ouvrage_service.entity.Gamme}.
 * 
 * Utilisé pour transférer les données de la gamme entre le backend et le frontend,
 * incluant la liste des {@link OuvrageDTO} associés.
 */
public class GammeDTO {

    /** Identifiant unique de la gamme */
    private Long id;

    /** Nom de la gamme */
    private String nom;

    /** Description de la gamme */
    private String description;

    /** Identifiant de l'utilisateur propriétaire de la gamme */
    private Long ownerId;

    /** Liste des ouvrages associés à cette gamme */
    private List<OuvrageDTO> ouvrages = new ArrayList<>();

    /**
     * Constructeur vide requis pour la sérialisation/desérialisation JSON.
     */
    public GammeDTO() {
    }

    /**
     * Constructeur avec paramètres principaux.
     *
     * @param id          identifiant de la gamme
     * @param nom         nom de la gamme
     * @param description description de la gamme
     * @param ownerId     identifiant du propriétaire
     */
    public GammeDTO(Long id, String nom, String description, Long ownerId) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.ownerId = ownerId;
    }

    /** @return l'identifiant de la gamme */
    public Long getId() {
        return id;
    }

    /** @param id nouvel identifiant de la gamme */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return le nom de la gamme */
    public String getNom() {
        return nom;
    }

    /** @param nom nouveau nom de la gamme */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /** @return la description de la gamme */
    public String getDescription() {
        return description;
    }

    /** @param description nouvelle description de la gamme */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return l'identifiant du propriétaire */
    public Long getOwnerId() {
        return ownerId;
    }

    /** @param ownerId nouvel identifiant du propriétaire */
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    /** @return la liste des ouvrages associés à cette gamme */
    public List<OuvrageDTO> getOuvrages() {
        return ouvrages;
    }

    /** @param ouvrages nouvelle liste des ouvrages associés */
    public void setOuvrages(List<OuvrageDTO> ouvrages) {
        this.ouvrages = ouvrages;
    }
}
