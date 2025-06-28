package com.jdrbibli.userservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ouvrages")
public class Ouvrage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "gamme_id")
    private Gamme gamme;

    // Getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Gamme getGamme() { return gamme; }
    public void setGamme(Gamme gamme) { this.gamme = gamme; }
}
