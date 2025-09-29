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
@RequestMapping("/api/ouvrage/gammes")
public class GammeController {

    private final GammeService gammeService;
    private final GammeMapper gammeMapper;

    public GammeController(GammeService gammeService, GammeMapper gammeMapper) {
        this.gammeService = gammeService;
        this.gammeMapper = gammeMapper;
    }

    @GetMapping
    public ResponseEntity<List<GammeDTO>> getAll(@RequestHeader("X-User-Id") Long ownerId) {
        List<Gamme> gammes = gammeService.findByOwnerId(ownerId);
        List<GammeDTO> gammesDTO = gammes.stream()
                .map(gammeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gammesDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GammeDTO> getById(@PathVariable Long id, @RequestHeader("X-User-Id") Long ownerId) {
        Optional<Gamme> gammeOpt = gammeService.findById(id);
        if (gammeOpt.isPresent()) {
            Gamme gamme = gammeOpt.get();
            if (!ownerId.equals(gamme.getOwnerId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(gammeMapper.toDTO(gamme));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<GammeDTO> create(@RequestBody GammeDTO gammeDTO,
            @RequestHeader("X-User-Id") Long ownerId) {
        gammeDTO.setOwnerId(ownerId);
        Gamme gamme = gammeMapper.toEntity(gammeDTO);
        Gamme saved = gammeService.save(gamme);
        return ResponseEntity.status(HttpStatus.CREATED).body(gammeMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody GammeDTO gammeDTO,
            @RequestHeader("X-User-Id") Long ownerId) {
        Optional<Gamme> existingOpt = gammeService.findById(id);
        if (existingOpt.isEmpty())
            return ResponseEntity.notFound().build();

        Gamme existing = existingOpt.get();
        if (!ownerId.equals(existing.getOwnerId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        existing.setNom(gammeDTO.getNom());
        existing.setDescription(gammeDTO.getDescription());

        Gamme saved = gammeService.save(existing);
        return ResponseEntity.ok(gammeMapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean force,
            @RequestHeader("X-User-Id") Long ownerId) {
        Optional<Gamme> existingOpt = gammeService.findById(id);
        if (existingOpt.isEmpty())
            return ResponseEntity.notFound().build();

        Gamme existing = existingOpt.get();
        if (!ownerId.equals(existing.getOwnerId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        try {
            gammeService.deleteById(id, force);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/by-owner/{ownerId}")
    public ResponseEntity<Void> deleteGammesByOwner(@PathVariable Long ownerId) {
        gammeService.deleteByOwnerId(ownerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public/owner/{ownerId}")
    public ResponseEntity<List<GammeDTO>> getGammesByOwner(@PathVariable Long ownerId) {
        List<Gamme> gammes = gammeService.findByOwnerId(ownerId);
        List<GammeDTO> gammesDTO = gammes.stream().map(gammeMapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(gammesDTO);
    }

    @GetMapping("/friend/{friendId}")
    public ResponseEntity<List<GammeDTO>> getGammesByFriend(@PathVariable Long friendId) {
        List<Gamme> gammes = gammeService.findByOwnerId(friendId);
        List<GammeDTO> gammesDTO = gammes.stream()
                .map(gammeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gammesDTO);
    }

}
