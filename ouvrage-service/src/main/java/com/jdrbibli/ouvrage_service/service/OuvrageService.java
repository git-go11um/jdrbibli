package com.jdrbibli.ouvrage_service.service;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.exception.ResourceNotFoundException;
import com.jdrbibli.ouvrage_service.mapper.OuvrageMapper;
import com.jdrbibli.ouvrage_service.repository.OuvrageRepository;
import com.jdrbibli.ouvrage_service.repository.GammeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OuvrageService {

    private final OuvrageRepository ouvrageRepository;
    private final GammeRepository gammeRepository;
    private final OuvrageMapper ouvrageMapper;

    public OuvrageService(OuvrageRepository ouvrageRepository, GammeRepository gammeRepository,
            OuvrageMapper ouvrageMapper) {
        this.ouvrageRepository = ouvrageRepository;
        this.gammeRepository = gammeRepository;
        this.ouvrageMapper = ouvrageMapper;
    }

    /** Récupérer tous les ouvrages d’un utilisateur */
    public List<Ouvrage> findByOwnerId(Long ownerId) {
        return ouvrageRepository.findByOwnerId(ownerId);
    }

    /** Récupérer un ouvrage par son ID */
    public Optional<Ouvrage> findById(Long id) {
        return ouvrageRepository.findById(id);
    }

    /** Créer un nouvel ouvrage depuis un DTO */
    public Ouvrage createFromDTO(OuvrageDTO dto) {
        Gamme gamme = gammeRepository.findById(dto.getGammeId())
                .orElseThrow(() -> new ResourceNotFoundException("Gamme not found with id " + dto.getGammeId()));
        Ouvrage ouvrage = ouvrageMapper.toEntity(dto);
        ouvrage.setGamme(gamme);

        if (dto.getOwnerId() == null) {
            throw new IllegalArgumentException("OwnerId must be set");
        }

        return ouvrageRepository.save(ouvrage);
    }

    /** Mettre à jour un ouvrage depuis un DTO */
    public Ouvrage updateFromDTO(Long id, OuvrageDTO dto) {
        Ouvrage existing = ouvrageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ouvrage not found with id " + id));

        Gamme gamme = gammeRepository.findById(dto.getGammeId())
                .orElseThrow(() -> new ResourceNotFoundException("Gamme not found with id " + dto.getGammeId()));

        // Conserver ownerId existant
        dto.setOwnerId(existing.getOwnerId());

        Ouvrage updated = ouvrageMapper.toEntity(dto);
        updated.setId(id);
        updated.setGamme(gamme);

        return ouvrageRepository.save(updated);
    }

    /** Supprimer un ouvrage */
    public void deleteById(Long id) {
        ouvrageRepository.deleteById(id);
    }

    /** Récupérer tous les ouvrages d’une gamme pour un owner */
    public List<Ouvrage> findByGammeIdAndOwnerId(Long gammeId, Long ownerId) {
        return ouvrageRepository.findByGammeIdAndOwnerId(gammeId, ownerId);
    }

    /** Récupérer tous les ouvrages d’une gamme sauf un ID spécifique */
    public List<OuvrageDTO> getOuvragesByGamme(Long gammeId, Long excludeId) {
        return ouvrageRepository.findByGammeIdAndIdNot(gammeId, excludeId).stream()
                .map(ouvrageMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<Ouvrage> findByGammeId(Long gammeId) {
        return ouvrageRepository.findByGammeId(gammeId);
    }

}
