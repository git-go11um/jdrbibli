package com.jdrbibli.userservice.mapper;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.entity.Ouvrage;

public class OuvrageMapper {
    public static OuvrageDTO toDTO(Ouvrage o) {
        OuvrageDTO dto = new OuvrageDTO();
        dto.setId(o.getId());
        dto.setTitre(o.getTitle()); // on mappe title → titre
        return dto;
    }
}
