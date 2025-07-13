package com.jdrbibli.ouvrage_service.mapper;

import com.jdrbibli.ouvrage_service.dto.GammeDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import org.springframework.stereotype.Component;

@Component
public class GammeMapper {

    public GammeDTO toDTO(Gamme gamme) {
        if (gamme == null)
            return null;
        return new GammeDTO(
                gamme.getId(),
                gamme.getNom(),
                gamme.getDescription(),
                gamme.getOwnerPseudo() // ✅ on ajoute ownerPseudo
        );
    }

    public Gamme toEntity(GammeDTO dto) {
        if (dto == null)
            return null;
        Gamme gamme = new Gamme();
        System.out.println("Nom reçu du DTO : " + dto.getNom());
        gamme.setNom(dto.getNom());
        gamme.setDescription(dto.getDescription());
        gamme.setOwnerPseudo(dto.getOwnerPseudo()); // ✅ on ajoute ownerPseudo
        return gamme;
    }
}
