package com.jdrbibli.userservice.dto;

import java.util.List;

public class GammeDTO {
    private Long id;
    private String title;
    private List<OuvrageDTO> ouvrages;

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<OuvrageDTO> getOuvrages() {
        return ouvrages;
    }

    public void setOuvrages(List<OuvrageDTO> ouvrages) {
        this.ouvrages = ouvrages;
    }
}
