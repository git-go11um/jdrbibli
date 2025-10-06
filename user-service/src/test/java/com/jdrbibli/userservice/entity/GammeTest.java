package com.jdrbibli.userservice.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GammeTest {

    @Test
    void testGettersAndSetters() {
        Gamme gamme = new Gamme();

        Long id = 1L;
        String title = "Gamme Test";
        String description = "Description de la gamme";
        UserProfile user = new UserProfile();
        user.setId(10L);

        // Création de quelques ouvrages fictifs
        Ouvrage ouvrage1 = new Ouvrage();
        ouvrage1.setId(101L);
        Ouvrage ouvrage2 = new Ouvrage();
        ouvrage2.setId(102L);

        Set<Ouvrage> ouvrages = new HashSet<>();
        ouvrages.add(ouvrage1);
        ouvrages.add(ouvrage2);

        // Setters
        gamme.setId(id);
        gamme.setTitle(title);
        gamme.setDescription(description);
        gamme.setUser(user);
        gamme.setOuvrages(ouvrages);

        // Getters + assertions
        assertEquals(id, gamme.getId(), "L'id doit correspondre");
        assertEquals(title, gamme.getTitle(), "Le titre doit correspondre");
        assertEquals(description, gamme.getDescription(), "La description doit correspondre");
        assertEquals(user, gamme.getUser(), "Le propriétaire doit correspondre");
        assertEquals(ouvrages, gamme.getOuvrages(), "Les ouvrages doivent correspondre");
        assertEquals(2, gamme.getOuvrages().size(), "La taille des ouvrages doit être 2");
    }

    @Test
    void testOuvrageAddition() {
        Gamme gamme = new Gamme();
        Ouvrage ouvrage = new Ouvrage();
        ouvrage.setId(200L);

        // Ajouter ouvrage à la gamme
        Set<Ouvrage> ouvrages = new HashSet<>();
        ouvrages.add(ouvrage);
        gamme.setOuvrages(ouvrages);

        assertTrue(gamme.getOuvrages().contains(ouvrage), "L'ouvrage doit être présent dans la gamme");
    }
}
