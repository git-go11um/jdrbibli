package com.jdrbibli.userservice.mapper;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.entity.Ouvrage;

/**
 * Classe utilitaire pour mapper les entités {@link Ouvrage} vers leur DTO {@link OuvrageDTO}.
 */
public class OuvrageMapper {

    /**
     * Convertit un objet {@link Ouvrage} en {@link OuvrageDTO}.
     *
     * @param o l'entité {@link Ouvrage} à convertir
     * @return un {@link OuvrageDTO} contenant l'identifiant et le titre de l'ouvrage,
     *         ou null si l'entité est null
     */
    public static OuvrageDTO toDTO(Ouvrage o) {
        if (o == null) return null;

        OuvrageDTO dto = new OuvrageDTO();
        dto.setId(o.getId());
        dto.setTitre(o.getTitle());
        return dto;
    }
}
