package com.jdrbibli.userservice.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OuvrageDTOTest {

    @Test
    void testGettersAndSetters() {
        OuvrageDTO ouvrage = new OuvrageDTO();

        Long id = 100L;
        String titre = "Titre de l'ouvrage";

        // Utilisation des setters
        ouvrage.setId(id);
        ouvrage.setTitre(titre);

        // Vérification des getters
        assertEquals(id, ouvrage.getId(), "L'id doit correspondre");
        assertEquals(titre, ouvrage.getTitre(), "Le titre doit correspondre");
    }
}
