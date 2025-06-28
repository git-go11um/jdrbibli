package com.jdrbibli.userservice.service;

import com.jdrbibli.userservice.entity.Ouvrage;
import com.jdrbibli.userservice.repository.OuvrageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OuvrageService {
    private final OuvrageRepository ouvrageRepository;

    public OuvrageService(OuvrageRepository ouvrageRepository) {
        this.ouvrageRepository = ouvrageRepository;
    }

    public List<Ouvrage> getAllOuvrages() {
        return ouvrageRepository.findAll();
    }

    public Optional<Ouvrage> getOuvrageById(Long id) {
        return ouvrageRepository.findById(id);
    }

    public Ouvrage createOuvrage(Ouvrage ouvrage) {
        return ouvrageRepository.save(ouvrage);
    }

    public void deleteOuvrage(Long id) {
        ouvrageRepository.deleteById(id);
    }
}
