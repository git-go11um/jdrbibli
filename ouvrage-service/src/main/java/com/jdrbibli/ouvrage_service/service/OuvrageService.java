package com.jdrbibli.ouvrage_service.service;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.exception.ResourceNotFoundException;
import com.jdrbibli.ouvrage_service.repository.GammeRepository;
import com.jdrbibli.ouvrage_service.repository.OuvrageRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
public class OuvrageService {

    private final OuvrageRepository ouvrageRepository;
    private final GammeRepository gammeRepository;

    public OuvrageService(OuvrageRepository ouvrageRepository, GammeRepository gammeRepository) {
        this.ouvrageRepository = ouvrageRepository;
        this.gammeRepository = gammeRepository;
    }

    public List<Ouvrage> findAll() {
        return ouvrageRepository.findAll();
    }

    public Optional<Ouvrage> findById(Long id) {
        return ouvrageRepository.findById(id);
    }

    public Ouvrage save(Ouvrage ouvrage) {
        Assert.notNull(ouvrage, "Ouvrage ne peut pas être null");
        return ouvrageRepository.save(ouvrage);
    }

    public void deleteById(Long id) {
        ouvrageRepository.deleteById(id);
    }

    /**
     * Création d'un Ouvrage à partir du DTO.
     * @param dto DTO avec données de l'ouvrage
     * @return l'ouvrage sauvegardé
     */
    public Ouvrage createFromDTO(OuvrageDTO dto) {
        Assert.notNull(dto, "OuvrageDTO ne peut pas être null");
        Gamme gamme = gammeRepository.findById(dto.getGammeId())
                .orElseThrow(() -> new ResourceNotFoundException("Gamme non trouvée avec l'id : " + dto.getGammeId()));

        Ouvrage ouvrage = new Ouvrage();
        remplirChampsDepuisDTO(ouvrage, dto, gamme);

        return ouvrageRepository.save(ouvrage);
    }

    /**
     * Mise à jour d'un Ouvrage existant à partir du DTO.
     * @param id identifiant de l'ouvrage à modifier
     * @param dto DTO avec données mises à jour
     * @return l'ouvrage mis à jour
     */
    public Ouvrage updateFromDTO(Long id, OuvrageDTO dto) {
        Assert.notNull(dto, "OuvrageDTO ne peut pas être null");
        Gamme gamme = gammeRepository.findById(dto.getGammeId())
                .orElseThrow(() -> new ResourceNotFoundException("Gamme non trouvée avec l'id : " + dto.getGammeId()));

        return ouvrageRepository.findById(id)
                .map(existing -> {
                    remplirChampsDepuisDTO(existing, dto, gamme);
                    return ouvrageRepository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Ouvrage non trouvé avec l'id : " + id));
    }

    /**
     * Méthode interne pour remplir un Ouvrage à partir d'un DTO et d'une Gamme.
     */
    private void remplirChampsDepuisDTO(Ouvrage ouvrage, OuvrageDTO dto, Gamme gamme) {
        ouvrage.setTitre(dto.getTitre());
        ouvrage.setDescription(dto.getDescription());
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
        ouvrage.setGamme(gamme);
    }
}
