package com.jdrbibli.ouvrage_service.controller;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.repository.GammeRepository;
import com.jdrbibli.ouvrage_service.service.OuvrageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ouvrages")
public class OuvrageController {
    private final OuvrageService ouvrageService;

    private final GammeRepository gammeRepository;

    public OuvrageController(OuvrageService ouvrageService, GammeRepository gammeRepository) {
        this.ouvrageService = ouvrageService;
        this.gammeRepository = gammeRepository;
    }

    @GetMapping
    public List<Ouvrage> getAll() {
        return ouvrageService.findAll();
    }

    @GetMapping("/{id}")
    public Ouvrage getById(@PathVariable Long id) {
        return ouvrageService.findById(id).orElse(null);
    }

    @PostMapping
    public Ouvrage create(@RequestBody OuvrageDTO dto) {
        Gamme gamme = gammeRepository.findById(dto.getGammeId())
                .orElseThrow(() -> new RuntimeException("Gamme non trouvée"));

        Ouvrage ouvrage = new Ouvrage();
        ouvrage.setGamme(gamme);
        ouvrage.setVersion(dto.getVersion());
        ouvrage.setTypeOuvrage(dto.getTypeOuvrage());
        ouvrage.setDatePublication(dto.getDatePublication());
        ouvrage.setLangue(dto.getLangue());
        ouvrage.setEditeur(dto.getEditeur());
        ouvrage.setEtat(dto.getEtat());
        ouvrage.setIsbn(dto.getIsbn());
        ouvrage.setOuvrageLie(dto.getOuvrageLie());
        ouvrage.setScenarioLie(dto.getScenarioLie());
        ouvrage.setPret(dto.getPret());
        ouvrage.setErrata(dto.getErrata());
        ouvrage.setNotes(dto.getNotes());
        ouvrage.setScenariosContenus(dto.getScenariosContenus());
        ouvrage.setAutresOuvragesGamme(dto.getAutresOuvragesGamme());

        return ouvrageService.save(ouvrage);
    }

    

    @PutMapping("/{id}")
public Ouvrage update(@PathVariable Long id, @RequestBody OuvrageDTO dto) {
    Gamme gamme = gammeRepository.findById(dto.getGammeId())
            .orElseThrow(() -> new RuntimeException("Gamme non trouvée"));

    // construire un Ouvrage partiel à partir du DTO
    Ouvrage updatedOuvrage = new Ouvrage();
    updatedOuvrage.setGamme(gamme);
    updatedOuvrage.setVersion(dto.getVersion());
    updatedOuvrage.setTypeOuvrage(dto.getTypeOuvrage());
    updatedOuvrage.setDatePublication(dto.getDatePublication());
    updatedOuvrage.setLangue(dto.getLangue());
    updatedOuvrage.setEditeur(dto.getEditeur());
    updatedOuvrage.setEtat(dto.getEtat());
    updatedOuvrage.setIsbn(dto.getIsbn());
    updatedOuvrage.setOuvrageLie(dto.getOuvrageLie());
    updatedOuvrage.setScenarioLie(dto.getScenarioLie());
    updatedOuvrage.setPret(dto.getPret());
    updatedOuvrage.setErrata(dto.getErrata());
    updatedOuvrage.setNotes(dto.getNotes());
    updatedOuvrage.setScenariosContenus(dto.getScenariosContenus());
    updatedOuvrage.setAutresOuvragesGamme(dto.getAutresOuvragesGamme());

    return ouvrageService.updateOuvrage(id, updatedOuvrage);
}


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ouvrageService.deleteById(id);
    }
}
