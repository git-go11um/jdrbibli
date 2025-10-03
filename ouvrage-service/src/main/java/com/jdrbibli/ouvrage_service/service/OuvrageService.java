package com.jdrbibli.ouvrage_service.service;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.exception.ResourceNotFoundException;
import com.jdrbibli.ouvrage_service.mapper.OuvrageMapper;
import com.jdrbibli.ouvrage_service.repository.OuvrageRepository;
import com.jdrbibli.ouvrage_service.repository.GammeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour gérer les opérations sur les {@link Ouvrage}.
 * <p>
 * Ce service fournit des méthodes pour créer, mettre à jour, supprimer et récupérer des ouvrages,
 * ainsi que pour récupérer des ouvrages associés à une gamme spécifique.
 */
@Service
public class OuvrageService {

    private final OuvrageRepository ouvrageRepository;
    private final GammeRepository gammeRepository;
    private final OuvrageMapper ouvrageMapper;

    /**
     * Constructeur du service.
     *
     * @param ouvrageRepository le repository pour accéder aux données des ouvrages
     * @param gammeRepository   le repository pour accéder aux données des gammes
     * @param ouvrageMapper     le mapper pour convertir entre {@link Ouvrage} et {@link OuvrageDTO}
     */
    public OuvrageService(OuvrageRepository ouvrageRepository, GammeRepository gammeRepository,
                          OuvrageMapper ouvrageMapper) {
        this.ouvrageRepository = ouvrageRepository;
        this.gammeRepository = gammeRepository;
        this.ouvrageMapper = ouvrageMapper;
    }

    /**
     * Récupère tous les ouvrages appartenant à un utilisateur donné.
     *
     * @param ownerId l'identifiant du propriétaire
     * @return la liste des ouvrages de l'utilisateur
     */
    public List<Ouvrage> findByOwnerId(Long ownerId) {
        return ouvrageRepository.findByOwnerId(ownerId);
    }

    /**
     * Recherche un ouvrage par son identifiant.
     *
     * @param id l'identifiant de l'ouvrage
     * @return un {@link Optional} contenant l'ouvrage si trouvé
     */
    public Optional<Ouvrage> findById(Long id) {
        return ouvrageRepository.findById(id);
    }

    /**
     * Crée un ouvrage à partir d'un {@link OuvrageDTO}.
     *
     * @param dto le DTO contenant les informations de l'ouvrage
     * @return l'ouvrage créé et sauvegardé en base
     * @throws ResourceNotFoundException si la gamme associée n'existe pas
     * @throws IllegalArgumentException  si ownerId n'est pas défini
     */
    public Ouvrage createFromDTO(OuvrageDTO dto) {
        Gamme gamme = gammeRepository.findById(dto.getGammeId())
                .orElseThrow(() -> new ResourceNotFoundException("Gamme not found with id " + dto.getGammeId()));
        Ouvrage ouvrage = ouvrageMapper.toEntity(dto);
        ouvrage.setGamme(gamme);

        if (ouvrage.getScenariosContenusList() == null) {
            ouvrage.setScenariosContenusList(new ArrayList<>());
        }
        if (ouvrage.getAutresOuvragesGamme() == null) {
            ouvrage.setAutresOuvragesGamme(new ArrayList<>());
        }

        if (dto.getOwnerId() == null) {
            throw new IllegalArgumentException("OwnerId must be set");
        }

        return ouvrageRepository.save(ouvrage);
    }

    /**
     * Met à jour un ouvrage existant à partir d'un {@link OuvrageDTO}.
     *
     * @param id  l'identifiant de l'ouvrage à mettre à jour
     * @param dto le DTO contenant les nouvelles informations de l'ouvrage
     * @return l'ouvrage mis à jour
     * @throws ResourceNotFoundException si l'ouvrage ou la gamme associée n'existe pas
     */
    public Ouvrage updateFromDTO(Long id, OuvrageDTO dto) {
        Ouvrage existing = ouvrageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ouvrage not found with id " + id));

        Gamme gamme = gammeRepository.findById(dto.getGammeId())
                .orElseThrow(() -> new ResourceNotFoundException("Gamme not found with id " + dto.getGammeId()));

        existing.setTitre(dto.getTitre());
        existing.setDescription(dto.getDescription());
        existing.setVersion(dto.getVersion());
        existing.setTypeOuvrage(dto.getTypeOuvrage());
        existing.setDatePublication(dto.getDatePublication());
        existing.setLangue(dto.getLangue());
        existing.setEditeur(dto.getEditeur());
        existing.setEtat(dto.getEtat());
        existing.setIsbn(dto.getIsbn());
        existing.setOuvrageLie(dto.getOuvrageLie());
        existing.setScenarioLie(dto.getScenarioLie());
        existing.setPret(dto.getPret());
        existing.setErrata(dto.getErrata());
        existing.setNotes(dto.getNotes());
        existing.setImageUrl(dto.getImageUrl());
        existing.setGamme(gamme);
        existing.setScenariosContenusList(
                dto.getScenariosContenus() != null ? dto.getScenariosContenus() : new ArrayList<>());
        existing.setAutresOuvragesGamme(
                dto.getAutresOuvragesGamme() != null ? dto.getAutresOuvragesGamme() : new ArrayList<>());

        return ouvrageRepository.save(existing);
    }

    /**
     * Supprime un ouvrage par son identifiant.
     *
     * @param id l'identifiant de l'ouvrage à supprimer
     */
    public void deleteById(Long id) {
        ouvrageRepository.deleteById(id);
    }

    /**
     * Récupère tous les ouvrages appartenant à un utilisateur pour une gamme donnée.
     *
     * @param gammeId l'identifiant de la gamme
     * @param ownerId l'identifiant du propriétaire
     * @return la liste des ouvrages correspondant aux critères
     */
    public List<Ouvrage> findByGammeIdAndOwnerId(Long gammeId, Long ownerId) {
        return ouvrageRepository.findByGammeIdAndOwnerId(gammeId, ownerId);
    }

    /**
     * Récupère tous les ouvrages d'une gamme, sauf un ouvrage à exclure.
     *
     * @param gammeId   l'identifiant de la gamme
     * @param excludeId l'identifiant de l'ouvrage à exclure
     * @return la liste des {@link OuvrageDTO} des ouvrages de la gamme
     */
    public List<OuvrageDTO> getOuvragesByGamme(Long gammeId, Long excludeId) {
        return ouvrageRepository.findByGammeIdAndIdNot(gammeId, excludeId).stream()
                .map(ouvrageMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les ouvrages d'une gamme.
     *
     * @param gammeId l'identifiant de la gamme
     * @return la liste des ouvrages de la gamme
     */
    public List<Ouvrage> findByGammeId(Long gammeId) {
        return ouvrageRepository.findByGammeId(gammeId);
    }

    /**
     * Recherche un ouvrage par son identifiant et celui du propriétaire.
     *
     * @param ouvrageId l'identifiant de l'ouvrage
     * @param ownerId   l'identifiant du propriétaire
     * @return un {@link Optional} contenant l'ouvrage si trouvé
     */
    public Optional<Ouvrage> findByIdAndOwnerId(Long ouvrageId, Long ownerId) {
        return ouvrageRepository.findByIdAndOwnerId(ouvrageId, ownerId);
    }

}
