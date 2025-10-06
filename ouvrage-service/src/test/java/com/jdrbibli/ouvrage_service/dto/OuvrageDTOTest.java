package com.jdrbibli.ouvrage_service.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OuvrageDTOTest {

    @Test
    void testGettersAndSetters() {
        OuvrageDTO dto = new OuvrageDTO();

        dto.setId(1L);
        dto.setTitre("Titre1");
        dto.setDescription("Description1");
        dto.setGammeId(10L);
        dto.setVersion("v1");
        dto.setTypeOuvrage("Livre");
        dto.setDatePublication(LocalDate.of(2025, 10, 6));
        dto.setLangue("FR");
        dto.setEditeur("EditeurX");
        dto.setEtat("Neuf");
        dto.setIsbn("1234567890");
        dto.setOuvrageLie("OuvrageLie1");
        dto.setScenarioLie("ScenarioLie1");
        dto.setPret(true);
        dto.setErrata("ErrataX");
        dto.setNotes("NotesX");
        dto.setScenariosContenus(List.of("Scenario1", "Scenario2"));
        dto.setAutresOuvragesGamme(List.of("Ouvrage2", "Ouvrage3"));
        dto.setImageUrl("/images/ouvrage1.png");
        dto.setOwnerId(42L);
        dto.setGammeNom("GammeX");

        // Assertions
        assertEquals(1L, dto.getId());
        assertEquals("Titre1", dto.getTitre());
        assertEquals("Description1", dto.getDescription());
        assertEquals(10L, dto.getGammeId());
        assertEquals("v1", dto.getVersion());
        assertEquals("Livre", dto.getTypeOuvrage());
        assertEquals(LocalDate.of(2025, 10, 6), dto.getDatePublication());
        assertEquals("FR", dto.getLangue());
        assertEquals("EditeurX", dto.getEditeur());
        assertEquals("Neuf", dto.getEtat());
        assertEquals("1234567890", dto.getIsbn());
        assertEquals("OuvrageLie1", dto.getOuvrageLie());
        assertEquals("ScenarioLie1", dto.getScenarioLie());
        assertTrue(dto.getPret());
        assertEquals("ErrataX", dto.getErrata());
        assertEquals("NotesX", dto.getNotes());
        assertEquals(List.of("Scenario1", "Scenario2"), dto.getScenariosContenus());
        assertEquals(List.of("Ouvrage2", "Ouvrage3"), dto.getAutresOuvragesGamme());
        assertEquals("/images/ouvrage1.png", dto.getImageUrl());
        assertEquals(42L, dto.getOwnerId());
        assertEquals("GammeX", dto.getGammeNom());
    }
}
