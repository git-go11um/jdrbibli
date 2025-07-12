package com.jdrbibli.ouvrage_service.service;

import com.jdrbibli.ouvrage_service.dto.GammeDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.repository.GammeRepository;
import com.jdrbibli.ouvrage_service.repository.OuvrageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GammeService {

    private final GammeRepository gammeRepository;
    private final OuvrageRepository ouvrageRepository;

    public GammeService(GammeRepository gammeRepository, OuvrageRepository ouvrageRepository) {
        this.gammeRepository = gammeRepository;
        this.ouvrageRepository = ouvrageRepository;
    }

    /** Retourne la liste de toutes les gammes */
    public List<Gamme> findAll() {
        return gammeRepository.findAll();
    }

    /** Retourne une gamme par son ID */
    public Optional<Gamme> findById(Long id) {
        return gammeRepository.findById(id);
    }

    /** Sauvegarde ou met à jour une gamme */
    public Gamme save(Gamme gamme) {
        if (gamme == null) {
            throw new IllegalArgumentException("La gamme ne peut pas être nulle");
        }

        if (gamme.getId() != null) {
            Optional<Gamme> existingOpt = gammeRepository.findById(gamme.getId());
            if (existingOpt.isPresent()) {
                Gamme existing = existingOpt.get();
                gamme.setOuvrages(existing.getOuvrages()); // conserve la collection ouvrages
            }
        }

        return gammeRepository.save(gamme);
    }

    /**
     * Supprime une gamme par son ID.
     * Si la gamme contient des ouvrages, il faut passer force=true pour confirmer
     * la suppression.
     * En cas de suppression forcée, supprime d'abord les ouvrages liés (cascade
     * manuelle).
     */
    @Transactional
    public void deleteById(Long id, boolean force) {
        long count = ouvrageRepository.countByGammeId(id);
        System.out.println("Suppression gamme id=" + id + ", ouvrages liés = " + count + ", force=" + force);

        if (count > 0 && !force) {
            System.out.println("Refus suppression sans force");
            throw new RuntimeException("La gamme contient des ouvrages, suppression interdite sans force=true");
        }

        if (count > 0 && force) {
            System.out.println("Suppression des ouvrages liés");
            ouvrageRepository.deleteByGammeId(id);
        }

        System.out.println("Suppression gamme");
        gammeRepository.deleteById(id);
    }

    /** Convertit une Gamme en GammeDTO */
    public GammeDTO toDTO(Gamme gamme) {
        if (gamme == null)
            return null;
        return new GammeDTO(gamme.getId(), gamme.getNom(), gamme.getDescription());
    }

    /** Convertit une liste de Gamme en liste de GammeDTO */
    public List<GammeDTO> toDTOList(List<Gamme> gammes) {
        return gammes.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
