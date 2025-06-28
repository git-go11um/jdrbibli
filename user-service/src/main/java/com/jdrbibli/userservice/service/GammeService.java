package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.entity.Gamme;
import com.jdrbibli.userservice.repository.GammeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GammeService {
    private final GammeRepository gammeRepository;

    public GammeService(GammeRepository gammeRepository) {
        this.gammeRepository = gammeRepository;
    }

    public List<Gamme> getAllGammes() {
        return gammeRepository.findAll();
    }

    public Optional<Gamme> getGammeById(Long id) {
        return gammeRepository.findById(id);
    }

    public Gamme createGamme(Gamme gamme) {
        return gammeRepository.save(gamme);
    }

    public void deleteGamme(Long id) {
        gammeRepository.deleteById(id);
    }
}
