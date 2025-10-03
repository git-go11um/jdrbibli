package com.jdrbibli.ouvrage_service.mapper;

import com.jdrbibli.ouvrage_service.dto.GammeDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre l'entité {@link Gamme} et son DTO {@link GammeDTO}.
 * 
 * Permet de transformer une entité Gamme en DTO pour l'API, et inversement.
 * Inclut également le mapping des ouvrages associés via {@link OuvrageMapper}.
 */
@Component
public class GammeMapper {

    private final OuvrageMapper ouvrageMapper;

    /**
     * Constructeur avec injection de l'OuvrageMapper.
     *
     * @param ouvrageMapper le mapper pour les ouvrages associés
     */
    public GammeMapper(OuvrageMapper ouvrageMapper) {
        this.ouvrageMapper = ouvrageMapper;
    }

    /**
     * Convertit une entité {@link Gamme} en {@link GammeDTO}.
     * Les ouvrages associés sont également convertis en DTOs.
     *
     * @param gamme l'entité Gamme à convertir
     * @return le DTO correspondant, ou null si l'entité est null
     */
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

    /**
     * Convertit un {@link GammeDTO} en entité {@link Gamme}.
     * Les ouvrages ne sont pas mappés ici.
     *
     * @param dto le DTO à convertir
     * @return l'entité Gamme correspondante, ou null si le DTO est null
     */
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
