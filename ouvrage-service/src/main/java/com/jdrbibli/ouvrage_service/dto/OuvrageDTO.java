package com.jdrbibli.ouvrage_service.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OuvrageDTO {
    private Long gammeId;
    private String version;
    private String typeOuvrage;
    private LocalDate datePublication;
    private String langue;
    private String editeur;
    private String etat;
    private String isbn;
    private String ouvrageLie;
    private String scenarioLie;
    private Boolean pret;
    private String errata;
    private String notes;
    private List<String> scenariosContenus;
    private List<String> autresOuvragesGamme;
   
}
