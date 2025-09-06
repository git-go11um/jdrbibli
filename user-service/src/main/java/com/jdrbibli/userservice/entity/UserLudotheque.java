package com.jdrbibli.userservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_ludotheque")
public class UserLudotheque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "ouvrage_id", nullable = false)
    private Ouvrage ouvrage;

    // Getters & Setters
}
