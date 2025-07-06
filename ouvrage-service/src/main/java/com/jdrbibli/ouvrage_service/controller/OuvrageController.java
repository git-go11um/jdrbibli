package com.jdrbibli.ouvrage_service.controller;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.mapper.OuvrageMapper;
import com.jdrbibli.ouvrage_service.repository.GammeRepository;
import com.jdrbibli.ouvrage_service.service.OuvrageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ouvrages")
public class OuvrageController {

    private final OuvrageService ouvrageService;
    private final GammeRepository gammeRepository;
    private final OuvrageMapper ouvrageMapper;

    public OuvrageController(OuvrageService ouvrageService, GammeRepository gammeRepository, OuvrageMapper ouvrageMapper) {
        this.ouvrageService = ouvrageService;
        this.gammeRepository = gammeRepository;
        this.ouvrageMapper = ouvrageMapper;
    }

    @GetMapping
    public List<OuvrageDTO> getAll() {
        return ouvrageService.findAll()
                .stream()
                .map(ouvrageMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public OuvrageDTO getById(@PathVariable Long id) {
        return ouvrageService.findById(id)
                .map(ouvrageMapper::toDTO)
                .orElse(null);
    }

    @PostMapping
    public OuvrageDTO create(@RequestBody OuvrageDTO dto) {
        Gamme gamme = gammeRepository.findById(dto.getGammeId())
                .orElseThrow(() -> new RuntimeException("Gamme non trouvée"));

        Ouvrage ouvrage = ouvrageMapper.toEntity(dto, gamme);
        Ouvrage saved = ouvrageService.save(ouvrage);
        return ouvrageMapper.toDTO(saved);
    }

    @PutMapping("/{id}")
    public OuvrageDTO update(@PathVariable Long id, @RequestBody OuvrageDTO dto) {
        Gamme gamme = gammeRepository.findById(dto.getGammeId())
                .orElseThrow(() -> new RuntimeException("Gamme non trouvée"));

        Ouvrage updatedOuvrage = ouvrageMapper.toEntity(dto, gamme);
        Ouvrage updated = ouvrageService.updateOuvrage(id, updatedOuvrage);
        return ouvrageMapper.toDTO(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ouvrageService.deleteById(id);
    }
}
