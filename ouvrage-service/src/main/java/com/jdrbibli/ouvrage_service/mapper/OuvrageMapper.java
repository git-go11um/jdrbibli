package com.jdrbibli.ouvrage_service.mapper;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre l'entité {@link Ouvrage} et son DTO {@link OuvrageDTO}.
 * <p>
 * Permet de transformer une entité Ouvrage en DTO pour l'API, et inversement.
 * Gère également la conversion des listes JSON stockées dans la base pour les scénarios contenus.
 */
@Component
public class OuvrageMapper {

    /**
     * Convertit une entité {@link Ouvrage} en {@link OuvrageDTO}.
     *
     * @param entity l'entité Ouvrage à convertir
     * @return le DTO correspondant, ou null si l'entité est null
     */
    public OuvrageDTO toDTO(Ouvrage entity) {
        if (entity == null)
            return null;

        OuvrageDTO dto = new OuvrageDTO();
        dto.setId(entity.getId());
        dto.setTitre(entity.getTitre());
        dto.setDescription(entity.getDescription());
        dto.setGammeId(entity.getGamme() != null ? entity.getGamme().getId() : null);
        dto.setGammeNom(entity.getGamme() != null ? entity.getGamme().getNom() : null);
        dto.setVersion(entity.getVersion());
        dto.setTypeOuvrage(entity.getTypeOuvrage());
        dto.setDatePublication(entity.getDatePublication());
        dto.setLangue(entity.getLangue());
        dto.setEditeur(entity.getEditeur());
        dto.setEtat(entity.getEtat());
        dto.setIsbn(entity.getIsbn());
        dto.setOuvrageLie(entity.getOuvrageLie());
        dto.setScenarioLie(entity.getScenarioLie());
        dto.setPret(entity.getPret());
        dto.setErrata(entity.getErrata());
        dto.setNotes(entity.getNotes());
        dto.setScenariosContenus(entity.getScenariosContenusList());
        dto.setAutresOuvragesGamme(entity.getAutresOuvragesGamme());
        dto.setImageUrl(entity.getImageUrl());
        dto.setOwnerId(entity.getOwnerId());

        return dto;
    }

    /**
     * Convertit un {@link OuvrageDTO} en entité {@link Ouvrage}.
     *
     * @param dto le DTO à convertir
     * @return l'entité Ouvrage correspondante, ou null si le DTO est null
     */
    public Ouvrage toEntity(OuvrageDTO dto) {
        if (dto == null)
            return null;

        Ouvrage entity = new Ouvrage();
        entity.setId(dto.getId());
        entity.setTitre(dto.getTitre());
        entity.setDescription(dto.getDescription());
        entity.setVersion(dto.getVersion());
        entity.setTypeOuvrage(dto.getTypeOuvrage());
        entity.setDatePublication(dto.getDatePublication());
        entity.setLangue(dto.getLangue());
        entity.setEditeur(dto.getEditeur());
        entity.setEtat(dto.getEtat());
        entity.setIsbn(dto.getIsbn());
        entity.setOuvrageLie(dto.getOuvrageLie());
        entity.setScenarioLie(dto.getScenarioLie());
        entity.setPret(dto.getPret());
        entity.setErrata(dto.getErrata());
        entity.setNotes(dto.getNotes());
        entity.setScenariosContenusList(dto.getScenariosContenus());
        entity.setAutresOuvragesGamme(dto.getAutresOuvragesGamme());
        entity.setImageUrl(dto.getImageUrl());
        entity.setOwnerId(dto.getOwnerId());

        return entity;
    }
}
