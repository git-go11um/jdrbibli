package com.jdrbibli.ouvrage_service.mapper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.jdrbibli.ouvrage_service.dto.GammeDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;

class GammeMapperTest {

    private final GammeMapper mapper = new GammeMapper();

    @Test
    void testToDTO_and_toEntity_basic() {
        Gamme gamme = new Gamme();
        gamme.setId(1L);
        gamme.setNom("NomTest");
        gamme.setDescription("DescTest");
        gamme.setOwnerPseudo("owner");

        GammeDTO dto = mapper.toDTO(gamme);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("NomTest", dto.getNom());
        assertEquals("DescTest", dto.getDescription());
        assertEquals("owner", dto.getOwnerPseudo());

        Gamme fromDto = mapper.toEntity(dto);
        assertNotNull(fromDto);
        assertEquals("NomTest", fromDto.getNom());
        assertEquals("DescTest", fromDto.getDescription());
        assertEquals("owner", fromDto.getOwnerPseudo());
    }

    @Test
    void testToDTO_null() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void testToEntity_null() {
        assertNull(mapper.toEntity(null));
    }
}
