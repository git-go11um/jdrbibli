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

    public Ouvrage updateOuvrage(Long id, Ouvrage updatedOuvrage) {
    return ouvrageRepository.findById(id)
            .map(existing -> {
                existing.setTitre(updatedOuvrage.getTitre());
                existing.setDescription(updatedOuvrage.getDescription());
                existing.setDatePublication(updatedOuvrage.getDatePublication());
                existing.setVersion(updatedOuvrage.getVersion());
                existing.setTypeOuvrage(updatedOuvrage.getTypeOuvrage());
                existing.setLangue(updatedOuvrage.getLangue());
                existing.setEditeur(updatedOuvrage.getEditeur());
                existing.setEtat(updatedOuvrage.getEtat());
                existing.setIsbn(updatedOuvrage.getIsbn());
                existing.setOuvrageLie(updatedOuvrage.getOuvrageLie());
                existing.setScenarioLie(updatedOuvrage.getScenarioLie());
                existing.setPret(updatedOuvrage.getPret());
                existing.setErrata(updatedOuvrage.getErrata());
                existing.setNotes(updatedOuvrage.getNotes());
                existing.setScenariosContenus(updatedOuvrage.getScenariosContenus());
                existing.setAutresOuvragesGamme(updatedOuvrage.getAutresOuvragesGamme());
                existing.setGamme(updatedOuvrage.getGamme());
                return ouvrageRepository.save(existing);
            })
            .orElseThrow(() -> new RuntimeException("Ouvrage not found with id " + id));
}



}
