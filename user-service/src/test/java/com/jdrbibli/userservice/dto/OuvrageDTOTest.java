package com.jdrbibli.userservice.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class OuvrageDTOTest {

    @Test
    void testGettersAndSetters() {
        OuvrageDTO ouvrage = new OuvrageDTO();

        Long id = 123L;
        String titre = "Le titre de l'ouvrage";

        ouvrage.setId(id);
        ouvrage.setTitre(titre);

        assertEquals(id, ouvrage.getId());
        assertEquals(titre, ouvrage.getTitre());
    }
}
