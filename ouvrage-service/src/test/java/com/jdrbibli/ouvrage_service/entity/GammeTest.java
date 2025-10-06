package com.jdrbibli.ouvrage_service.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GammeTest {

    @Test
    void testGettersAndSetters() {
        Gamme gamme = new Gamme();

        gamme.setId(1L);
        gamme.setNom("Gamme1");
        gamme.setDescription("Description de la gamme");
        gamme.setOwnerId(42L);

        Ouvrage ouvrage1 = new Ouvrage();
        ouvrage1.setTitre("Ouvrage1");
        Ouvrage ouvrage2 = new Ouvrage();
        ouvrage2.setTitre("Ouvrage2");

        gamme.setOuvrages(List.of(ouvrage1, ouvrage2));

        // Assertions
        assertEquals(1L, gamme.getId());
        assertEquals("Gamme1", gamme.getNom());
        assertEquals("Description de la gamme", gamme.getDescription());
        assertEquals(42L, gamme.getOwnerId());
        assertNotNull(gamme.getOuvrages());
        assertEquals(2, gamme.getOuvrages().size());
        assertEquals("Ouvrage1", gamme.getOuvrages().get(0).getTitre());
        assertEquals("Ouvrage2", gamme.getOuvrages().get(1).getTitre());
    }
}
