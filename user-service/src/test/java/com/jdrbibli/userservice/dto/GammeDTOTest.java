package com.jdrbibli.userservice.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GammeDTOTest {

    @Test
    void testGettersAndSetters() {
        GammeDTO gamme = new GammeDTO();

        Long id = 1L;
        String title = "Gamme Test";

        // Création de quelques ouvrages fictifs
        OuvrageDTO ouvrage1 = new OuvrageDTO();
        ouvrage1.setId(101L);
        ouvrage1.setTitre("Ouvrage 1");

        OuvrageDTO ouvrage2 = new OuvrageDTO();
        ouvrage2.setId(102L);
        ouvrage2.setTitre("Ouvrage 2");

        List<OuvrageDTO> ouvrages = Arrays.asList(ouvrage1, ouvrage2);

        // Setters
        gamme.setId(id);
        gamme.setTitle(title);
        gamme.setOuvrages(ouvrages);

        // Getters + assertions
        assertEquals(id, gamme.getId(), "L'id de la gamme doit correspondre");
        assertEquals(title, gamme.getTitle(), "Le titre de la gamme doit correspondre");
        assertEquals(ouvrages, gamme.getOuvrages(), "La liste des ouvrages doit correspondre");
        assertEquals(2, gamme.getOuvrages().size(), "La taille de la liste doit être 2");
        assertEquals("Ouvrage 1", gamme.getOuvrages().get(0).getTitre(), "Titre du premier ouvrage");
        assertEquals("Ouvrage 2", gamme.getOuvrages().get(1).getTitre(), "Titre du deuxième ouvrage");
    }
}
