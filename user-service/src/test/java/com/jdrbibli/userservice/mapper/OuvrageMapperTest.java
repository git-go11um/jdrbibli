package com.jdrbibli.userservice.mapper;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.entity.Ouvrage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OuvrageMapperTest {

    @Test
    void toDTO_shouldMapOuvrageToOuvrageDTO() {
        Ouvrage ouvrage = new Ouvrage();
        ouvrage.setId(42L);
        ouvrage.setTitle("Mon Ouvrage");

        OuvrageDTO dto = OuvrageMapper.toDTO(ouvrage);

        assertNotNull(dto);
        assertEquals(42L, dto.getId());
        assertEquals("Mon Ouvrage", dto.getTitre());
    }

    @Test
    void toDTO_shouldReturnNullIfOuvrageIsNull() {
        OuvrageDTO dto = OuvrageMapper.toDTO(null);
        assertNull(dto);
    }
}
