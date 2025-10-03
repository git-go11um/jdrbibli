package com.jdrbibli.ouvrage_service.service;

import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.exception.ResourceNotFoundException;
import com.jdrbibli.ouvrage_service.mapper.GammeMapper;
import com.jdrbibli.ouvrage_service.repository.GammeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer les opérations sur les {@link Gamme}.
 * 
 * Ce service fournit des méthodes pour créer, lire, mettre à jour et supprimer
 * des gammes, ainsi que pour récupérer toutes les gammes appartenant à un utilisateur.
 */
@Service
public class GammeService {

    private final GammeRepository gammeRepository;
    private final GammeMapper gammeMapper;

    /**
     * Constructeur du service.
     *
     * @param gammeRepository le repository pour accéder aux données des gammes
     * @param gammeMapper     le mapper pour convertir entre {@link Gamme} et {@link com.jdrbibli.ouvrage_service.dto.GammeDTO}
     */
    public GammeService(GammeRepository gammeRepository, GammeMapper gammeMapper) {
        this.gammeRepository = gammeRepository;
        this.gammeMapper = gammeMapper;
    }

    /**
     * Récupère toutes les gammes appartenant à un utilisateur donné.
     *
     * @param ownerId l'identifiant du propriétaire
     * @return la liste des gammes de l'utilisateur
     */
    public List<Gamme> findByOwnerId(Long ownerId) {
        return gammeRepository.findByOwnerId(ownerId);
    }

    /**
     * Recherche une gamme par son identifiant.
     *
     * @param id l'identifiant de la gamme
     * @return un {@link Optional} contenant la gamme si trouvée
     */
    public Optional<Gamme> findById(Long id) {
        return gammeRepository.findById(id);
    }

    /**
     * Sauvegarde une gamme.
     * 
     * Vérifie que l'identifiant du propriétaire est défini avant la sauvegarde.
     *
     * @param gamme la gamme à sauvegarder
     * @return la gamme sauvegardée
     * @throws IllegalArgumentException si ownerId est null
     */
    public Gamme save(Gamme gamme) {
        if (gamme.getOwnerId() == null) {
            throw new IllegalArgumentException("OwnerId must be set");
        }
        return gammeRepository.save(gamme);
    }

    /**
     * Supprime une gamme par son identifiant.
     * 
     * Si {@code force} est false et que la gamme contient des ouvrages, une
     * exception est levée. Sinon, la gamme et ses ouvrages associés (via cascade) sont supprimés.
     *
     * @param id    l'identifiant de la gamme à supprimer
     * @param force true pour forcer la suppression même si des ouvrages existent
     * @throws ResourceNotFoundException si la gamme n'existe pas
     * @throws RuntimeException          si la gamme contient des ouvrages et que force=false
     */
    @Transactional
    public void deleteById(Long id, boolean force) {
        Gamme gamme = gammeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gamme non trouvée : " + id));

        if (!force && gamme.getOuvrages() != null && !gamme.getOuvrages().isEmpty()) {
            throw new RuntimeException("La gamme contient des ouvrages. Utilisez force=true pour supprimer.");
        }

        gammeRepository.delete(gamme);
    }

    /**
     * Supprime toutes les gammes appartenant à un utilisateur donné.
     *
     * @param ownerId l'identifiant du propriétaire dont les gammes doivent être supprimées
     */
    @Transactional
    public void deleteByOwnerId(Long ownerId) {
        List<Gamme> gammes = gammeRepository.findByOwnerId(ownerId);
        for (Gamme gamme : gammes) {
            gammeRepository.delete(gamme);
        }
    }

}
