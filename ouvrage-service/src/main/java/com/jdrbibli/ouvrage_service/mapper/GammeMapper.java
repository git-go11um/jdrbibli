package com.jdrbibli.ouvrage_service.mapper;

import com.jdrbibli.ouvrage_service.dto.GammeDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import org.springframework.stereotype.Component;

@Component
public class GammeMapper {

    public GammeDTO toDTO(Gamme gamme) {
        if (gamme == null) return null;
        return new GammeDTO(gamme.getId(), gamme.getNom(), gamme.getDescription());
    }

    public Gamme toEntity(GammeDTO dto) {
        if (dto == null) return null;
        Gamme gamme = new Gamme();
        gamme.setId(dto.getId());
        gamme.setNom(dto.getNom());
        gamme.setDescription(dto.getDescription());
        return gamme;
    }
}
