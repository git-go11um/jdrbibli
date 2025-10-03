package com.jdrbibli.ouvrage_service.entity;

import java.util.List;
import jakarta.persistence.*;

/**
 * Entité représentant une Gamme d'ouvrages.
 * 
 * Une Gamme est possédée par un utilisateur (ownerId) et contient une liste d'ouvrages associés.
 * La relation avec les ouvrages est de type OneToMany, avec suppression en cascade et orphanRemoval activé.
 */
@Entity
@Table(name = "gammes")
public class Gamme {

    /** Identifiant unique de la gamme */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom de la gamme */
    private String nom;

    /** Description de la gamme */
    private String description;

    /** Identifiant du propriétaire (utilisateur) de la gamme */
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    /** Liste des ouvrages appartenant à cette gamme */
    @OneToMany(mappedBy = "gamme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ouvrage> ouvrages;

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
    public List<Ouvrage> getOuvrages() {
        return ouvrages;
    }

    /** @param ouvrages nouvelle liste d'ouvrages associés */
    public void setOuvrages(List<Ouvrage> ouvrages) {
        this.ouvrages = ouvrages;
    }
}
