package com.jdrbibli.ouvrage_service.service;

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

    /** Récupérer toutes les gammes d’un utilisateur */
    public List<Gamme> findByOwnerId(Long ownerId) {
        return gammeRepository.findByOwnerId(ownerId);
    }

    public Optional<Gamme> findById(Long id) {
        return gammeRepository.findById(id);
    }

    public Gamme save(Gamme gamme) {
        // Vérifie que ownerId est bien défini
        if (gamme.getOwnerId() == null) {
            throw new IllegalArgumentException("OwnerId must be set");
        }
        return gammeRepository.save(gamme);
    }

    public void deleteById(Long id, boolean force) {
        // TODO: gérer la suppression avec contrainte si nécessaire
        gammeRepository.deleteById(id);
    }
}
