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

    public List<Ouvrage> findByOwnerPseudo(String ownerPseudo) {
        return ouvrageRepository.findByOwnerPseudo(ownerPseudo);
    }

    public Optional<Ouvrage> findById(Long id) {
        return ouvrageRepository.findById(id);
    }

    public Ouvrage createFromDTO(OuvrageDTO dto) {
        // Récupérer la gamme pour l'associer à l'ouvrage
        Gamme gamme = gammeRepository.findById(dto.getGammeId())
                .orElseThrow(() -> new ResourceNotFoundException("Gamme not found with id " + dto.getGammeId()));

        Ouvrage ouvrage = ouvrageMapper.toEntity(dto);
        ouvrage.setGamme(gamme);
        ouvrage.setOwnerPseudo(dto.getOwnerPseudo());
        // ownerPseudo doit être déjà dans dto.setOwnerPseudo()

        return ouvrageRepository.save(ouvrage);
    }

    public Ouvrage updateFromDTO(Long id, OuvrageDTO dto) {
        Ouvrage existing = ouvrageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ouvrage not found with id " + id));

        Gamme gamme = gammeRepository.findById(dto.getGammeId())
                .orElseThrow(() -> new ResourceNotFoundException("Gamme not found with id " + dto.getGammeId()));

        // Conserver ownerPseudo existant (sécurité)
        dto.setOwnerPseudo(existing.getOwnerPseudo());

        Ouvrage updated = ouvrageMapper.toEntity(dto);
        updated.setId(id);
        updated.setGamme(gamme);

        return ouvrageRepository.save(updated);
    }

    public void deleteById(Long id) {
        ouvrageRepository.deleteById(id);
    }

    public List<Ouvrage> findByGammeIdAndOwnerPseudo(Long gammeId, String ownerPseudo) {
        return ouvrageRepository.findByGammeIdAndOwnerPseudo(gammeId, ownerPseudo);
    }

}
