package com.jdrbibli.userservice.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OuvrageTest {

    @Test
    void testGettersAndSetters() {
        Ouvrage ouvrage = new Ouvrage();

        Long id = 1L;
        String title = "Titre de l'ouvrage";
        String description = "Description de l'ouvrage";
        String imageUrl = "http://image.url/ouvrage.png";
        Gamme gamme = new Gamme();
        gamme.setId(10L);

        // Setters
        ouvrage.setId(id);
        ouvrage.setTitle(title);
        ouvrage.setDescription(description);
        ouvrage.setImageUrl(imageUrl);
        ouvrage.setGamme(gamme);

        // Getters + assertions
        assertEquals(id, ouvrage.getId(), "L'id doit correspondre");
        assertEquals(title, ouvrage.getTitle(), "Le titre doit correspondre");
        assertEquals(description, ouvrage.getDescription(), "La description doit correspondre");
        assertEquals(imageUrl, ouvrage.getImageUrl(), "L'imageUrl doit correspondre");
        assertEquals(gamme, ouvrage.getGamme(), "La gamme doit correspondre");
    }
}
