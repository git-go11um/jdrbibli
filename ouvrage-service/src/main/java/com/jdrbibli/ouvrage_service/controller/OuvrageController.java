package com.jdrbibli.ouvrage_service.controller;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.mapper.OuvrageMapper;
import com.jdrbibli.ouvrage_service.service.OuvrageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ouvrage/ouvrages")
public class OuvrageController {

    private final OuvrageService ouvrageService;
    private final OuvrageMapper ouvrageMapper;

    public OuvrageController(OuvrageService ouvrageService, OuvrageMapper ouvrageMapper) {
        this.ouvrageService = ouvrageService;
        this.ouvrageMapper = ouvrageMapper;
    }

    // GET /api/ouvrages - Récupérer tous les ouvrages de l’utilisateur connecté
    @GetMapping
    public ResponseEntity<List<OuvrageDTO>> getAll(@RequestHeader("X-User-Pseudo") String ownerPseudo) {
        List<Ouvrage> ouvrages = ouvrageService.findByOwnerPseudo(ownerPseudo);
        List<OuvrageDTO> dtos = ouvrages.stream()
                .map(ouvrageMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /api/ouvrages/{id} - Récupérer un ouvrage par ID s’il appartient à
    // l’utilisateur
    @GetMapping("/{id}")
    public ResponseEntity<OuvrageDTO> getById(@PathVariable Long id,
            @RequestHeader("X-User-Pseudo") String ownerPseudo) {
        Optional<Ouvrage> ouvrageOpt = ouvrageService.findById(id);
        if (ouvrageOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Ouvrage ouvrage = ouvrageOpt.get();
        if (!ownerPseudo.equals(ouvrage.getOwnerPseudo())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(ouvrageMapper.toDTO(ouvrage));
    }

    // GET /api/ouvrages/gammes/{gammeId} - Récupérer tous les ouvrages d'une gamme
    // pour l’utilisateur connecté
    @GetMapping("/gammes/{gammeId}")
    public ResponseEntity<List<OuvrageDTO>> getByGamme(
            @PathVariable Long gammeId,
            @RequestHeader("X-User-Pseudo") String ownerPseudo) {
        List<Ouvrage> ouvrages = ouvrageService.findByGammeIdAndOwnerPseudo(gammeId, ownerPseudo);
        List<OuvrageDTO> dtos = ouvrages.stream()
                .map(ouvrage -> {
                    OuvrageDTO dto = new OuvrageDTO();
                    // Mapper manuellement ou via un mapper
                    // Si tu as un mapper, préfère l’utiliser
                    dto.setId(ouvrage.getId());
                    dto.setTitre(ouvrage.getTitre());
                    dto.setDescription(ouvrage.getDescription());
                    dto.setGammeId(ouvrage.getGamme().getId());
                    dto.setOwnerPseudo(ouvrage.getOwnerPseudo());
                    // ... les autres propriétés ...
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // POST /api/ouvrages - Créer un ouvrage en assignant ownerPseudo
    @PostMapping
    public ResponseEntity<OuvrageDTO> create(@RequestBody OuvrageDTO dto,
            @RequestHeader("X-User-Pseudo") String ownerPseudo) {
        try {
            dto.setOwnerPseudo(ownerPseudo); // Assigne le propriétaire
            Ouvrage created = ouvrageService.createFromDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ouvrageMapper.toDTO(created));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/ouvrages/{id} - Mettre à jour un ouvrage si propriétaire
    @PutMapping("/{id}")
    public ResponseEntity<OuvrageDTO> update(@PathVariable Long id,
            @RequestBody OuvrageDTO dto,
            @RequestHeader("X-User-Pseudo") String ownerPseudo) {
        Optional<Ouvrage> existingOpt = ouvrageService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Ouvrage existing = existingOpt.get();
        if (!ownerPseudo.equals(existing.getOwnerPseudo())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            dto.setOwnerPseudo(ownerPseudo); // Garantir la cohérence
            Ouvrage updated = ouvrageService.updateFromDTO(id, dto);
            return ResponseEntity.ok(ouvrageMapper.toDTO(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /api/ouvrages/{id} - Supprimer un ouvrage si propriétaire
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
            @RequestHeader("X-User-Pseudo") String ownerPseudo) {
        Optional<Ouvrage> existingOpt = ouvrageService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Ouvrage existing = existingOpt.get();
        if (!ownerPseudo.equals(existing.getOwnerPseudo())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ouvrageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/gammes/{gammeId}/exclude/{id}")
    public ResponseEntity<List<OuvrageDTO>> getOuvragesByGamme(@PathVariable Long gammeId, @PathVariable Long id) {
        List<OuvrageDTO> ouvrages = ouvrageService.getOuvragesByGamme(gammeId, id);
        return ResponseEntity.ok(ouvrages);
    }

}
