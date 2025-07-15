package com.jdrbibli.ouvrage_service.mapper;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OuvrageMapperTest {

    private final OuvrageMapper mapper = new OuvrageMapper();

    @Test
    void testToDTO_and_toEntity_basic() {
        Gamme gamme = new Gamme();
        gamme.setId(10L);

        Ouvrage entity = new Ouvrage();
        entity.setId(1L);
        entity.setTitre("Titre test");
        entity.setDescription("Description test");
        entity.setGamme(gamme);
        entity.setVersion("1.0");
        entity.setTypeOuvrage("Livre");
        entity.setDatePublication(LocalDate.of(2020, 5, 20));
        entity.setLangue("FR");
        entity.setEditeur("Editeur test");
        entity.setEtat("Bon");
        entity.setIsbn("1234567890");
        entity.setOuvrageLie("Ouvrage lié");
        entity.setScenarioLie("Scenario lié");
        entity.setPret(true);
        entity.setErrata("Errata test");
        entity.setNotes("Notes test");
        entity.setScenariosContenus(List.of("Scenario1", "Scenario2"));
        entity.setAutresOuvragesGamme(List.of("Ouvrage1"));
        entity.setLiensMedias(List.of("http://media1.com"));
        entity.setOwnerPseudo("ownerUser");

        OuvrageDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getTitre(), dto.getTitre());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(gamme.getId(), dto.getGammeId());
        assertEquals(entity.getVersion(), dto.getVersion());
        assertEquals(entity.getTypeOuvrage(), dto.getTypeOuvrage());
        assertEquals(entity.getDatePublication(), dto.getDatePublication());
        assertEquals(entity.getLangue(), dto.getLangue());
        assertEquals(entity.getEditeur(), dto.getEditeur());
        assertEquals(entity.getEtat(), dto.getEtat());
        assertEquals(entity.getIsbn(), dto.getIsbn());
        assertEquals(entity.getOuvrageLie(), dto.getOuvrageLie());
        assertEquals(entity.getScenarioLie(), dto.getScenarioLie());
        assertEquals(entity.getPret(), dto.getPret());
        assertEquals(entity.getErrata(), dto.getErrata());
        assertEquals(entity.getNotes(), dto.getNotes());
        assertEquals(entity.getScenariosContenus(), dto.getScenariosContenus());
        assertEquals(entity.getAutresOuvragesGamme(), dto.getAutresOuvragesGamme());
        assertEquals(entity.getLiensMedias(), dto.getLiensMedias());
        assertEquals(entity.getOwnerPseudo(), dto.getOwnerPseudo());

        // Maintenant test inverse toEntity
        Ouvrage entityFromDto = mapper.toEntity(dto);

        assertNotNull(entityFromDto);
        assertEquals(dto.getId(), entityFromDto.getId());
        assertEquals(dto.getTitre(), entityFromDto.getTitre());
        assertEquals(dto.getDescription(), entityFromDto.getDescription());
        assertNull(entityFromDto.getGamme()); // gamme non mappée dans toEntity (à gérer ailleurs)
        assertEquals(dto.getVersion(), entityFromDto.getVersion());
        assertEquals(dto.getTypeOuvrage(), entityFromDto.getTypeOuvrage());
        assertEquals(dto.getDatePublication(), entityFromDto.getDatePublication());
        assertEquals(dto.getLangue(), entityFromDto.getLangue());
        assertEquals(dto.getEditeur(), entityFromDto.getEditeur());
        assertEquals(dto.getEtat(), entityFromDto.getEtat());
        assertEquals(dto.getIsbn(), entityFromDto.getIsbn());
        assertEquals(dto.getOuvrageLie(), entityFromDto.getOuvrageLie());
        assertEquals(dto.getScenarioLie(), entityFromDto.getScenarioLie());
        assertEquals(dto.getPret(), entityFromDto.getPret());
        assertEquals(dto.getErrata(), entityFromDto.getErrata());
        assertEquals(dto.getNotes(), entityFromDto.getNotes());
        assertEquals(dto.getScenariosContenus(), entityFromDto.getScenariosContenus());
        assertEquals(dto.getAutresOuvragesGamme(), entityFromDto.getAutresOuvragesGamme());
        assertEquals(dto.getLiensMedias(), entityFromDto.getLiensMedias());
        assertEquals(dto.getOwnerPseudo(), entityFromDto.getOwnerPseudo());
    }

    @Test
    void testToDTO_null() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void testToEntity_null() {
        assertNull(mapper.toEntity(null));
    }
}
