package com.jdrbibli.ouvrage_service.service;

import com.jdrbibli.ouvrage_service.dto.GammeDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.exception.ResourceNotFoundException;
import com.jdrbibli.ouvrage_service.mapper.GammeMapper;
import com.jdrbibli.ouvrage_service.repository.GammeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GammeService {

    private final GammeRepository gammeRepository;
    private final GammeMapper gammeMapper;

    public GammeService(GammeRepository gammeRepository, GammeMapper gammeMapper) {
        this.gammeRepository = gammeRepository;
        this.gammeMapper = gammeMapper;
    }

    public List<Gamme> findByOwnerPseudo(String ownerPseudo) {
        return gammeRepository.findByOwnerPseudo(ownerPseudo);
    }

    public Optional<Gamme> findById(Long id) {
        return gammeRepository.findById(id);
    }

    public Gamme save(Gamme gamme) {
        // Vérifie que ownerPseudo est bien défini
        if (gamme.getOwnerPseudo() == null || gamme.getOwnerPseudo().isBlank()) {
            throw new IllegalArgumentException("OwnerPseudo must be set");
        }
        return gammeRepository.save(gamme);
    }

    public void deleteById(Long id, boolean force) {
        // TODO: gérer la suppression avec contrainte si nécessaire
        gammeRepository.deleteById(id);
    }
}
