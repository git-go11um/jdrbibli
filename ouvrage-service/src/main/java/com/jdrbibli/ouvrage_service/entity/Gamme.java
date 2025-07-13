package com.jdrbibli.ouvrage_service.entity;

import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "gammes") // <- indique explicitement la table à utiliser
public class Gamme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;

    // Nouveau champ pour savoir quel utilisateur a créé cette gamme
    @Column(nullable = false)
    private String ownerPseudo;

    // Une gamme possède plusieurs ouvrages
    @OneToMany(mappedBy = "gamme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ouvrage> ouvrages;

    // Getters
    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerPseudo() {
        return ownerPseudo;
    }

    public List<Ouvrage> getOuvrages() {
        return ouvrages;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwnerPseudo(String ownerPseudo) {
        this.ownerPseudo = ownerPseudo;
    }

    public void setOuvrages(List<Ouvrage> ouvrages) {
        this.ouvrages = ouvrages;
    }
}
