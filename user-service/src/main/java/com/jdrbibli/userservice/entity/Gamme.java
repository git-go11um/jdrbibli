package com.jdrbibli.userservice.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "gammes")
public class Gamme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @OneToMany(mappedBy = "gamme")
    private List<Ouvrage> ouvrages; // liste des ouvrages liés à cette gamme

    // getters et setters
}
