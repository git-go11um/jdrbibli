package com.jdrbibli.ouvrage_service.service;

import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.repository.OuvrageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OuvrageService {
    private final OuvrageRepository ouvrageRepository;

    public OuvrageService(OuvrageRepository ouvrageRepository) {
        this.ouvrageRepository = ouvrageRepository;
    }

    public List<Ouvrage> findAll() {
        return ouvrageRepository.findAll();
    }

    public Optional<Ouvrage> findById(Long id) {
        return ouvrageRepository.findById(id);
    }

    public Ouvrage save(Ouvrage ouvrage) {
        return ouvrageRepository.save(ouvrage);
    }

    public void deleteById(Long id) {
        ouvrageRepository.deleteById(id);
    }
}
