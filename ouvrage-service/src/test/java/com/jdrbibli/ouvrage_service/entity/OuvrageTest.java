package com.jdrbibli.ouvrage_service.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OuvrageTest {

    @Test
    void testGettersAndSetters() {
        Ouvrage ouvrage = new Ouvrage();

        Long id = 10L;
        String titre = "Manuel du joueur";
        String description = "Description du manuel";
        Gamme gamme = new Gamme();
        gamme.setId(1L);
        String version = "1.0";
        String typeOuvrage = "Livre";
        LocalDate datePublication = LocalDate.of(2020, 5, 20);
        String langue = "Français";
        String editeur = "Editions JDR";
        String etat = "Neuf";
        String isbn = "123-4567890123";
        String ouvrageLie = "Ouvrage lié 1";
        String scenarioLie = "Scenario lié A";
        Boolean pret = true;
        String errata = "Quelques errata";
        String notes = "Notes diverses";
        String ownerPseudo = "user1";

        List<String> scenariosContenus = List.of("Scénario 1", "Scénario 2");
        List<String> autresOuvragesGamme = List.of("Ouvrage 2", "Ouvrage 3");
        List<String> liensMedias = List.of("http://lien1.com", "http://lien2.com");

        // Set values
        ouvrage.setId(id);
        ouvrage.setTitre(titre);
        ouvrage.setDescription(description);
        ouvrage.setGamme(gamme);
        ouvrage.setVersion(version);
        ouvrage.setTypeOuvrage(typeOuvrage);
        ouvrage.setDatePublication(datePublication);
        ouvrage.setLangue(langue);
        ouvrage.setEditeur(editeur);
        ouvrage.setEtat(etat);
        ouvrage.setIsbn(isbn);
        ouvrage.setOuvrageLie(ouvrageLie);
        ouvrage.setScenarioLie(scenarioLie);
        ouvrage.setPret(pret);
        ouvrage.setErrata(errata);
        ouvrage.setNotes(notes);
        ouvrage.setOwnerPseudo(ownerPseudo);
        ouvrage.setScenariosContenus(scenariosContenus);
        ouvrage.setAutresOuvragesGamme(autresOuvragesGamme);
        ouvrage.setLiensMedias(liensMedias);

        // Assertions
        assertEquals(id, ouvrage.getId());
        assertEquals(titre, ouvrage.getTitre());
        assertEquals(description, ouvrage.getDescription());
        assertEquals(gamme, ouvrage.getGamme());
        assertEquals(version, ouvrage.getVersion());
        assertEquals(typeOuvrage, ouvrage.getTypeOuvrage());
        assertEquals(datePublication, ouvrage.getDatePublication());
        assertEquals(langue, ouvrage.getLangue());
        assertEquals(editeur, ouvrage.getEditeur());
        assertEquals(etat, ouvrage.getEtat());
        assertEquals(isbn, ouvrage.getIsbn());
        assertEquals(ouvrageLie, ouvrage.getOuvrageLie());
        assertEquals(scenarioLie, ouvrage.getScenarioLie());
        assertEquals(pret, ouvrage.getPret());
        assertEquals(errata, ouvrage.getErrata());
        assertEquals(notes, ouvrage.getNotes());
        assertEquals(ownerPseudo, ouvrage.getOwnerPseudo());
        assertEquals(scenariosContenus, ouvrage.getScenariosContenus());
        assertEquals(autresOuvragesGamme, ouvrage.getAutresOuvragesGamme());
        assertEquals(liensMedias, ouvrage.getLiensMedias());
    }
}
