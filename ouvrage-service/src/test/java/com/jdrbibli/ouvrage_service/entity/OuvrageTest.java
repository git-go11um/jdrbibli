package com.jdrbibli.ouvrage_service.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OuvrageTest {

    @Test
    void testGettersAndSetters() {
        Ouvrage ouvrage = new Ouvrage();

        ouvrage.setId(1L);
        ouvrage.setTitre("Ouvrage1");
        ouvrage.setDescription("Description de l'ouvrage");
        ouvrage.setVersion("1.0");
        ouvrage.setTypeOuvrage("Livre");
        ouvrage.setDatePublication(LocalDate.of(2025, 10, 6));
        ouvrage.setLangue("Français");
        ouvrage.setEditeur("Éditeur Test");
        ouvrage.setEtat("Neuf");
        ouvrage.setIsbn("1234567890");
        ouvrage.setOuvrageLie("Ouvrage2");
        ouvrage.setScenarioLie("Scenario1");
        ouvrage.setPret(true);
        ouvrage.setErrata("Errata test");
        ouvrage.setNotes("Notes test");
        ouvrage.setOwnerId(42L);
        ouvrage.setImageUrl("http://example.com/image.png");

        // Test Scenarios Contenus via JSON
        List<String> scenarios = List.of("Scenario A", "Scenario B");
        ouvrage.setScenariosContenusList(scenarios);
        List<String> retrievedScenarios = ouvrage.getScenariosContenusList();

        // Test Autres Ouvrages
        List<String> autresOuvrages = List.of("Ouvrage3", "Ouvrage4");
        ouvrage.setAutresOuvragesGamme(autresOuvrages);

        // Assertions
        assertEquals(1L, ouvrage.getId());
        assertEquals("Ouvrage1", ouvrage.getTitre());
        assertEquals("Description de l'ouvrage", ouvrage.getDescription());
        assertEquals("1.0", ouvrage.getVersion());
        assertEquals("Livre", ouvrage.getTypeOuvrage());
        assertEquals(LocalDate.of(2025, 10, 6), ouvrage.getDatePublication());
        assertEquals("Français", ouvrage.getLangue());
        assertEquals("Éditeur Test", ouvrage.getEditeur());
        assertEquals("Neuf", ouvrage.getEtat());
        assertEquals("1234567890", ouvrage.getIsbn());
        assertEquals("Ouvrage2", ouvrage.getOuvrageLie());
        assertEquals("Scenario1", ouvrage.getScenarioLie());
        assertTrue(ouvrage.getPret());
        assertEquals("Errata test", ouvrage.getErrata());
        assertEquals("Notes test", ouvrage.getNotes());
        assertEquals(42L, ouvrage.getOwnerId());
        assertEquals("http://example.com/image.png", ouvrage.getImageUrl());

        assertNotNull(retrievedScenarios);
        assertEquals(2, retrievedScenarios.size());
        assertEquals("Scenario A", retrievedScenarios.get(0));
        assertEquals("Scenario B", retrievedScenarios.get(1));

        assertNotNull(ouvrage.getAutresOuvragesGamme());
        assertEquals(2, ouvrage.getAutresOuvragesGamme().size());
        assertEquals("Ouvrage3", ouvrage.getAutresOuvragesGamme().get(0));
        assertEquals("Ouvrage4", ouvrage.getAutresOuvragesGamme().get(1));
    }
}
