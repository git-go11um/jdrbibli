package com.jdrbibli.ouvrage_service.mapper;

import com.jdrbibli.ouvrage_service.dto.GammeDTO;
import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GammeMapperTest {

    private OuvrageMapper ouvrageMapper;
    private GammeMapper gammeMapper;

    @BeforeEach
    void setUp() {
        ouvrageMapper = mock(OuvrageMapper.class);
        gammeMapper = new GammeMapper(ouvrageMapper);
    }

    @Test
    void toDTO_shouldMapGammeWithoutOuvrages() {
        // Arrange
        Gamme gamme = new Gamme();
        gamme.setId(1L);
        gamme.setNom("Gamme1");
        gamme.setDescription("Desc");
        gamme.setOwnerId(10L);

        // Act
        GammeDTO dto = gammeMapper.toDTO(gamme);

        // Assert
        assertNotNull(dto);
        assertEquals(gamme.getId(), dto.getId());
        assertEquals(gamme.getNom(), dto.getNom());
        assertEquals(gamme.getDescription(), dto.getDescription());
        assertEquals(gamme.getOwnerId(), dto.getOwnerId());
        assertTrue(dto.getOuvrages().isEmpty());
    }

    @Test
    void toDTO_shouldMapGammeWithOuvrages() {
        // Arrange
        Gamme gamme = new Gamme();
        gamme.setId(2L);
        Ouvrage ouvrage = new Ouvrage();
        ouvrage.setId(100L);
        gamme.setOuvrages(List.of(ouvrage));

        OuvrageDTO ouvrageDTO = new OuvrageDTO();
        ouvrageDTO.setId(100L);

        when(ouvrageMapper.toDTO(ouvrage)).thenReturn(ouvrageDTO);

        // Act
        GammeDTO dto = gammeMapper.toDTO(gamme);

        // Assert
        assertNotNull(dto.getOuvrages());
        assertEquals(1, dto.getOuvrages().size());
        assertEquals(100L, dto.getOuvrages().get(0).getId());
    }

    @Test
    void toDTO_shouldReturnNullIfGammeIsNull() {
        assertNull(gammeMapper.toDTO(null));
    }

    @Test
    void toEntity_shouldMapGammeDTOToEntity() {
        // Arrange
        GammeDTO dto = new GammeDTO();
        dto.setNom("NomGamme");
        dto.setDescription("DescriptionGamme");
        dto.setOwnerId(5L);

        // Act
        Gamme gamme = gammeMapper.toEntity(dto);

        // Assert
        assertNotNull(gamme);
        assertEquals(dto.getNom(), gamme.getNom());
        assertEquals(dto.getDescription(), gamme.getDescription());
        assertEquals(dto.getOwnerId(), gamme.getOwnerId());
    }

    @Test
    void toEntity_shouldReturnNullIfDTOIsNull() {
        assertNull(gammeMapper.toEntity(null));
    }
}
