package com.jdrbibli.ouvrage_service.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GammeDTOTest {

    @Test
    void testConstructorAndGettersSetters() {
        // Création avec constructeur vide et setters
        GammeDTO gamme = new GammeDTO();
        gamme.setId(1L);
        gamme.setNom("Gamme1");
        gamme.setDescription("Description de la gamme");
        gamme.setOwnerId(42L);

        OuvrageDTO ouvrage = new OuvrageDTO();
        ouvrage.setId(100L);
        ouvrage.setTitre("Ouvrage1");

        gamme.setOuvrages(List.of(ouvrage));

        // Vérifications
        assertEquals(1L, gamme.getId());
        assertEquals("Gamme1", gamme.getNom());
        assertEquals("Description de la gamme", gamme.getDescription());
        assertEquals(42L, gamme.getOwnerId());

        List<OuvrageDTO> ouvrages = gamme.getOuvrages();
        assertNotNull(ouvrages);
        assertEquals(1, ouvrages.size());
        assertEquals("Ouvrage1", ouvrages.get(0).getTitre());

        // Création avec constructeur avec paramètres
        GammeDTO gamme2 = new GammeDTO(2L, "Gamme2", "Desc2", 99L);
        assertEquals(2L, gamme2.getId());
        assertEquals("Gamme2", gamme2.getNom());
        assertEquals("Desc2", gamme2.getDescription());
        assertEquals(99L, gamme2.getOwnerId());
        assertTrue(gamme2.getOuvrages().isEmpty());
    }
}
