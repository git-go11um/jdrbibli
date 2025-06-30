package com.jdrbibli.ouvrage_service.service;

import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.repository.GammeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GammeService {
    private final GammeRepository gammeRepository;

    public GammeService(GammeRepository gammeRepository) {
        this.gammeRepository = gammeRepository;
    }

    public List<Gamme> findAll() {
        return gammeRepository.findAll();
    }

    public Optional<Gamme> findById(Long id) {
        return gammeRepository.findById(id);
    }

    public Gamme save(Gamme gamme) {
        return gammeRepository.save(gamme);
    }

    public void deleteById(Long id) {
        gammeRepository.deleteById(id);
    }
}
