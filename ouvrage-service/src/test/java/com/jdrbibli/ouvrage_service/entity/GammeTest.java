package com.jdrbibli.ouvrage_service.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GammeTest {

    @Test
    void testGettersAndSetters() {
        Gamme gamme = new Gamme();

        Long id = 123L;
        String nom = "Fantasy";
        String description = "Gamme fantasy pour JDR";
        String ownerPseudo = "user123";
        Ouvrage ouvrage = new Ouvrage(); // Tu peux créer un ouvrage minimal ou un mock si nécessaire

        gamme.setId(id);
        gamme.setNom(nom);
        gamme.setDescription(description);
        gamme.setOwnerPseudo(ownerPseudo);
        gamme.setOuvrages(List.of(ouvrage));

        assertEquals(id, gamme.getId());
        assertEquals(nom, gamme.getNom());
        assertEquals(description, gamme.getDescription());
        assertEquals(ownerPseudo, gamme.getOwnerPseudo());
        assertNotNull(gamme.getOuvrages());
        assertEquals(1, gamme.getOuvrages().size());
        assertSame(ouvrage, gamme.getOuvrages().get(0));
    }
}
