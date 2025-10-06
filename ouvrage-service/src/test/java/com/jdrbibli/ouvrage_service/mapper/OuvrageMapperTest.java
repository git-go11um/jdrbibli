package com.jdrbibli.ouvrage_service.mapper;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OuvrageMapperTest {

    private OuvrageMapper ouvrageMapper;

    @BeforeEach
    void setUp() {
        ouvrageMapper = new OuvrageMapper();
    }

    @Test
    void toDTO_shouldMapAllFields() {
        // Arrange
        Gamme gamme = new Gamme();
        gamme.setId(1L);
        gamme.setNom("Gamme1");

        Ouvrage ouvrage = new Ouvrage();
        ouvrage.setId(100L);
        ouvrage.setTitre("TitreOuvrage");
        ouvrage.setDescription("Desc");
        ouvrage.setGamme(gamme);
        ouvrage.setVersion("v1");
        ouvrage.setTypeOuvrage("Livre");
        ouvrage.setDatePublication(LocalDate.of(2025, 10, 6));
        ouvrage.setLangue("FR");
        ouvrage.setEditeur("EditeurX");
        ouvrage.setEtat("Neuf");
        ouvrage.setIsbn("1234567890");
        ouvrage.setOuvrageLie("Ouvrage2");
        ouvrage.setScenarioLie("Scenario1");
        ouvrage.setPret(true);
        ouvrage.setErrata("Errata1");
        ouvrage.setNotes("Notes internes");
        ouvrage.setOwnerId(42L);
        ouvrage.setImageUrl("image.png");
        ouvrage.setScenariosContenusList(List.of("S1","S2"));
        ouvrage.setAutresOuvragesGamme(List.of("Ouv1","Ouv2"));

        // Act
        OuvrageDTO dto = ouvrageMapper.toDTO(ouvrage);

        // Assert
        assertNotNull(dto);
        assertEquals(ouvrage.getId(), dto.getId());
        assertEquals("TitreOuvrage", dto.getTitre());
        assertEquals("Gamme1", dto.getGammeNom());
        assertEquals(1L, dto.getGammeId());
        assertEquals(List.of("S1","S2"), dto.getScenariosContenus());
        assertEquals(List.of("Ouv1","Ouv2"), dto.getAutresOuvragesGamme());
        assertEquals("image.png", dto.getImageUrl());
        assertEquals(42L, dto.getOwnerId());
    }

    @Test
    void toEntity_shouldMapAllFields() {
        // Arrange
        OuvrageDTO dto = new OuvrageDTO();
        dto.setId(200L);
        dto.setTitre("TitreDTO");
        dto.setDescription("DescDTO");
        dto.setVersion("v2");
        dto.setTypeOuvrage("Module");
        dto.setDatePublication(LocalDate.of(2024, 5, 20));
        dto.setLangue("EN");
        dto.setEditeur("EditeurY");
        dto.setEtat("Bon");
        dto.setIsbn("0987654321");
        dto.setOuvrageLie("OuvrageLie");
        dto.setScenarioLie("ScenarioLie");
        dto.setPret(false);
        dto.setErrata("ErrataDTO");
        dto.setNotes("NotesDTO");
        dto.setOwnerId(7L);
        dto.setImageUrl("imageDTO.png");
        dto.setScenariosContenus(List.of("S3","S4"));
        dto.setAutresOuvragesGamme(List.of("Ouv3","Ouv4"));

        // Act
        Ouvrage entity = ouvrageMapper.toEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals(200L, entity.getId());
        assertEquals("TitreDTO", entity.getTitre());
        assertEquals("DescDTO", entity.getDescription());
        assertEquals(List.of("S3","S4"), entity.getScenariosContenusList());
        assertEquals(List.of("Ouv3","Ouv4"), entity.getAutresOuvragesGamme());
        assertEquals("imageDTO.png", entity.getImageUrl());
        assertEquals(7L, entity.getOwnerId());
    }

    @Test
    void toDTO_shouldReturnNullIfEntityIsNull() {
        assertNull(ouvrageMapper.toDTO(null));
    }

    @Test
    void toEntity_shouldReturnNullIfDTOIsNull() {
        assertNull(ouvrageMapper.toEntity(null));
    }
}
