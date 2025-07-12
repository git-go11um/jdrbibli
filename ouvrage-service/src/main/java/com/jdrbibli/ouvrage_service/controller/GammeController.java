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
@RequestMapping("/gammes") // Route /api/ouvrage/gammes pour respecter la configuration du gateway
public class GammeController {

    private final GammeService gammeService;
    private final GammeMapper gammeMapper;

    public GammeController(GammeService gammeService, GammeMapper gammeMapper) {
        this.gammeService = gammeService;
        this.gammeMapper = gammeMapper;
    }

    /** Récupérer toutes les gammes */
    @GetMapping
    public ResponseEntity<List<GammeDTO>> getAll() {
        List<Gamme> gammes = gammeService.findAll();
        List<GammeDTO> gammesDTO = gammes.stream()
                .map(gammeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gammesDTO);
    }

    /** Récupérer une gamme par son ID */
    @GetMapping("/{id}")
    public ResponseEntity<GammeDTO> getById(@PathVariable Long id) {
        Optional<Gamme> gamme = gammeService.findById(id);
        if (gamme.isPresent()) {
            GammeDTO dto = gammeMapper.toDTO(gamme.get());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    /** Créer une nouvelle gamme */
    @PostMapping
    public ResponseEntity<GammeDTO> create(@RequestBody GammeDTO gammeDTO) {
        Gamme gamme = gammeMapper.toEntity(gammeDTO);
        Gamme saved = gammeService.save(gamme);
        GammeDTO savedDTO = gammeMapper.toDTO(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDTO);
    }

    /** Mettre à jour une gamme existante */
    @PutMapping("/{id}")
    public ResponseEntity<GammeDTO> update(@PathVariable Long id, @RequestBody GammeDTO gammeDTO) {
        Gamme gamme = gammeMapper.toEntity(gammeDTO);
        gamme.setId(id);
        Gamme updated = gammeService.save(gamme);
        GammeDTO updatedDTO = gammeMapper.toDTO(updated);
        return ResponseEntity.ok(updatedDTO);
    }

    /** Supprimer une gamme */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean force) {
        try {
            gammeService.deleteById(id, force);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
