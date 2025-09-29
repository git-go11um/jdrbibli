package com.jdrbibli.ouvrage_service.mapper;

import com.jdrbibli.ouvrage_service.dto.GammeDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class GammeMapper {

    private final OuvrageMapper ouvrageMapper;

    public GammeMapper(OuvrageMapper ouvrageMapper) {
        this.ouvrageMapper = ouvrageMapper;
    }

    public GammeDTO toDTO(Gamme gamme) {
        if (gamme == null)
            return null;

        GammeDTO dto = new GammeDTO(
                gamme.getId(),
                gamme.getNom(),
                gamme.getDescription(),
                gamme.getOwnerId());

        if (gamme.getOuvrages() != null) {
            dto.setOuvrages(
                    gamme.getOuvrages().stream()
                            .map(ouvrageMapper::toDTO)
                            .collect(Collectors.toList()));
        }

        return dto;
    }

    public Gamme toEntity(GammeDTO dto) {
        if (dto == null)
            return null;
        Gamme gamme = new Gamme();
        gamme.setNom(dto.getNom());
        gamme.setDescription(dto.getDescription());
        gamme.setOwnerId(dto.getOwnerId());
        return gamme;
    }
}
