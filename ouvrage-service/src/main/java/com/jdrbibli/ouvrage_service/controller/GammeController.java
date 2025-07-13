package com.jdrbibli.ouvrage_service.controller;

import com.jdrbibli.ouvrage_service.dto.GammeDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.mapper.GammeMapper;
import com.jdrbibli.ouvrage_service.service.GammeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ouvrage/gammes") // Route /api/ouvrage/gammes pour respecter la configuration du gateway
public class GammeController {

    private final GammeService gammeService;
    private final GammeMapper gammeMapper;

    public GammeController(GammeService gammeService, GammeMapper gammeMapper) {
        this.gammeService = gammeService;
        this.gammeMapper = gammeMapper;
    }

    /** Récupérer toutes les gammes appartenant à l’utilisateur */
    @GetMapping
    public ResponseEntity<List<GammeDTO>> getAll(@RequestHeader("X-User-Pseudo") String ownerPseudo) {
        List<Gamme> gammes = gammeService.findByOwnerPseudo(ownerPseudo);
        List<GammeDTO> gammesDTO = gammes.stream()
                .map(gammeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gammesDTO);
    }

    /** Récupérer une gamme par son ID (si propriétaire) */
    @GetMapping("/{id}")
    public ResponseEntity<GammeDTO> getById(@PathVariable Long id, @RequestHeader("X-User-Pseudo") String ownerPseudo) {
        Optional<Gamme> gammeOpt = gammeService.findById(id);
        if (gammeOpt.isPresent()) {
            Gamme gamme = gammeOpt.get();
            if (!ownerPseudo.equals(gamme.getOwnerPseudo())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(gammeMapper.toDTO(gamme));
        }
        return ResponseEntity.notFound().build();
    }

    /** Créer une nouvelle gamme pour l’utilisateur connecté */
    @PostMapping
    public ResponseEntity<GammeDTO> create(@RequestBody GammeDTO gammeDTO,
            @RequestHeader("X-User-Pseudo") String ownerPseudo) {
        gammeDTO.setOwnerPseudo(ownerPseudo); // force l’ownerPseudo
        Gamme gamme = gammeMapper.toEntity(gammeDTO);
        Gamme saved = gammeService.save(gamme);
        return ResponseEntity.status(HttpStatus.CREATED).body(gammeMapper.toDTO(saved));
    }

    /** Mettre à jour une gamme existante (si propriétaire) */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody GammeDTO gammeDTO,
            @RequestHeader("X-User-Pseudo") String ownerPseudo) {
        Optional<Gamme> existingOpt = gammeService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Gamme existing = existingOpt.get();
        if (!ownerPseudo.equals(existing.getOwnerPseudo())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Mise à jour des champs modifiables
        existing.setNom(gammeDTO.getNom());
        existing.setDescription(gammeDTO.getDescription());

        Gamme saved = gammeService.save(existing);
        return ResponseEntity.ok(gammeMapper.toDTO(saved));
    }

    /** Supprimer une gamme (si propriétaire) */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean force,
            @RequestHeader("X-User-Pseudo") String ownerPseudo) {
        Optional<Gamme> existingOpt = gammeService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Gamme existing = existingOpt.get();
        if (!ownerPseudo.equals(existing.getOwnerPseudo())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            gammeService.deleteById(id, force);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
