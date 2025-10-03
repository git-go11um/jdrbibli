package com.jdrbibli.userservice.entity;

import jakarta.persistence.*;

/**
 * Entité représentant un ouvrage appartenant à une gamme.
 * Chaque ouvrage a un titre, une description, une image et est lié à une Gamme.
 */
@Entity
@Table(name = "ouvrages")
public class Ouvrage {

    /** Identifiant unique de l'ouvrage. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titre de l'ouvrage. */
    private String title;

    /** Description détaillée de l'ouvrage. */
    private String description;

    /** URL de l'image associée à l'ouvrage. */
    private String imageUrl;

    /** La gamme à laquelle cet ouvrage appartient. */
    @ManyToOne
    @JoinColumn(name = "gamme_id")
    private Gamme gamme;

    /** Retourne l'identifiant de l'ouvrage. */
    public Long getId() {
        return id;
    }

    /** Définit l'identifiant de l'ouvrage. */
    public void setId(Long id) {
        this.id = id;
    }

    /** Retourne le titre de l'ouvrage. */
    public String getTitle() {
        return title;
    }

    /** Définit le titre de l'ouvrage. */
    public void setTitle(String title) {
        this.title = title;
    }

    /** Retourne la description de l'ouvrage. */
    public String getDescription() {
        return description;
    }

    /** Définit la description de l'ouvrage. */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Retourne l'URL de l'image de l'ouvrage. */
    public String getImageUrl() {
        return imageUrl;
    }

    /** Définit l'URL de l'image de l'ouvrage. */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /** Retourne la gamme à laquelle appartient l'ouvrage. */
    public Gamme getGamme() {
        return gamme;
    }

    /** Définit la gamme à laquelle appartient l'ouvrage. */
    public void setGamme(Gamme gamme) {
        this.gamme = gamme;
    }

}
