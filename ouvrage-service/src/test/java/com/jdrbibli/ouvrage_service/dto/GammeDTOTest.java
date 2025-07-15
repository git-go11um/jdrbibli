package com.jdrbibli.ouvrage_service.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GammeDTOTest {

    @Test
    void testGettersAndSetters() {
        GammeDTO dto = new GammeDTO();

        Long id = 1L;
        String nom = "Gamme Test";
        String description = "Description de la gamme";
        String ownerPseudo = "userTest";

        dto.setId(id);
        dto.setNom(nom);
        dto.setDescription(description);
        dto.setOwnerPseudo(ownerPseudo);

        assertEquals(id, dto.getId());
        assertEquals(nom, dto.getNom());
        assertEquals(description, dto.getDescription());
        assertEquals(ownerPseudo, dto.getOwnerPseudo());
    }

    @Test
    void testConstructor() {
        Long id = 2L;
        String nom = "Autre Gamme";
        String description = "Une autre description";
        String ownerPseudo = "user2";

        GammeDTO dto = new GammeDTO(id, nom, description, ownerPseudo);

        assertEquals(id, dto.getId());
        assertEquals(nom, dto.getNom());
        assertEquals(description, dto.getDescription());
        assertEquals(ownerPseudo, dto.getOwnerPseudo());
    }
}
