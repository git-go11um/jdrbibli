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
    private Gamme gamme; // si tu veux aussi gérer la table gammes

    // getters et setters
}
