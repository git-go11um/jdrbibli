package com.jdrbibli.ouvrage_service.mapper;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import org.springframework.stereotype.Component;

@Component
public class OuvrageMapper {

    public OuvrageDTO toDTO(Ouvrage entity) {
        if (entity == null) return null;

        OuvrageDTO dto = new OuvrageDTO();
        dto.setId(entity.getId());
        dto.setTitre(entity.getTitre());
        dto.setDescription(entity.getDescription());
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
        dto.setScenariosContenus(entity.getScenariosContenus());
        dto.setAutresOuvragesGamme(entity.getAutresOuvragesGamme());

        if (entity.getGamme() != null) {
            dto.setGammeId(entity.getGamme().getId());
        }

        return dto;
    }

    public Ouvrage toEntity(OuvrageDTO dto, Gamme gamme) {
        if (dto == null) return null;

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
        entity.setScenariosContenus(dto.getScenariosContenus());
        entity.setAutresOuvragesGamme(dto.getAutresOuvragesGamme());
        entity.setGamme(gamme);

        return entity;
    }
}
