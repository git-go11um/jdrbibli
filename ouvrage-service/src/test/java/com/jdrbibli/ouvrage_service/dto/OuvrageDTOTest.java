package com.jdrbibli.ouvrage_service.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OuvrageDTOTest {

    @Test
    void testGettersAndSetters() {
        OuvrageDTO dto = new OuvrageDTO();

        Long id = 10L;
        String titre = "Titre Test";
        String description = "Description Test";
        Long gammeId = 5L;
        String version = "1.0";
        String typeOuvrage = "Livre";
        LocalDate datePublication = LocalDate.of(2023, 7, 14);
        String langue = "Français";
        String editeur = "Éditeur Test";
        String etat = "Neuf";
        String isbn = "978-3-16-148410-0";
        String ouvrageLie = "Ouvrage lié";
        String scenarioLie = "Scénario lié";
        Boolean pret = Boolean.TRUE;
        String errata = "Pas d'errata";
        String notes = "Quelques notes";
        List<String> scenariosContenus = new ArrayList<>(List.of("Scénario 1", "Scénario 2"));
        List<String> autresOuvragesGamme = new ArrayList<>(List.of("Ouvrage A", "Ouvrage B"));
        List<String> liensMedias = new ArrayList<>(List.of("http://media1.com", "http://media2.com"));
        String ownerPseudo = "userTest";

        dto.setId(id);
        dto.setTitre(titre);
        dto.setDescription(description);
        dto.setGammeId(gammeId);
        dto.setVersion(version);
        dto.setTypeOuvrage(typeOuvrage);
        dto.setDatePublication(datePublication);
        dto.setLangue(langue);
        dto.setEditeur(editeur);
        dto.setEtat(etat);
        dto.setIsbn(isbn);
        dto.setOuvrageLie(ouvrageLie);
        dto.setScenarioLie(scenarioLie);
        dto.setPret(pret);
        dto.setErrata(errata);
        dto.setNotes(notes);
        dto.setScenariosContenus(scenariosContenus);
        dto.setAutresOuvragesGamme(autresOuvragesGamme);
        dto.setLiensMedias(liensMedias);
        dto.setOwnerPseudo(ownerPseudo);

        assertEquals(id, dto.getId());
        assertEquals(titre, dto.getTitre());
        assertEquals(description, dto.getDescription());
        assertEquals(gammeId, dto.getGammeId());
        assertEquals(version, dto.getVersion());
        assertEquals(typeOuvrage, dto.getTypeOuvrage());
        assertEquals(datePublication, dto.getDatePublication());
        assertEquals(langue, dto.getLangue());
        assertEquals(editeur, dto.getEditeur());
        assertEquals(etat, dto.getEtat());
        assertEquals(isbn, dto.getIsbn());
        assertEquals(ouvrageLie, dto.getOuvrageLie());
        assertEquals(scenarioLie, dto.getScenarioLie());
        assertEquals(pret, dto.getPret());
        assertEquals(errata, dto.getErrata());
        assertEquals(notes, dto.getNotes());
        assertEquals(scenariosContenus, dto.getScenariosContenus());
        assertEquals(autresOuvragesGamme, dto.getAutresOuvragesGamme());
        assertEquals(liensMedias, dto.getLiensMedias());
        assertEquals(ownerPseudo, dto.getOwnerPseudo());
    }
}
