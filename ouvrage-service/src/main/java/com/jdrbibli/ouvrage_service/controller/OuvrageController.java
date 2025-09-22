package com.jdrbibli.ouvrage_service.controller;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.mapper.OuvrageMapper;
import com.jdrbibli.ouvrage_service.service.OuvrageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    @GetMapping
    public ResponseEntity<List<OuvrageDTO>> getAll(@RequestHeader("X-User-Id") Long ownerId) {
        List<Ouvrage> ouvrages = ouvrageService.findByOwnerId(ownerId);
        List<OuvrageDTO> dtos = ouvrages.stream()
                .map(ouvrageMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OuvrageDTO> getById(@PathVariable Long id,
            @RequestHeader("X-User-Id") Long ownerId) {
        Optional<Ouvrage> ouvrageOpt = ouvrageService.findById(id);
        if (ouvrageOpt.isEmpty())
            return ResponseEntity.notFound().build();

        Ouvrage ouvrage = ouvrageOpt.get();
        if (!ownerId.equals(ouvrage.getOwnerId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(ouvrageMapper.toDTO(ouvrage));
    }

    @GetMapping("/gammes/{gammeId}")
    public ResponseEntity<List<OuvrageDTO>> getByGamme(@PathVariable Long gammeId) {
        List<Ouvrage> ouvrages = ouvrageService.findByGammeId(gammeId); // version publique, sans ownerId
        List<OuvrageDTO> dtos = ouvrages.stream()
                .map(ouvrageMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<OuvrageDTO> create(@RequestBody OuvrageDTO dto,
            @RequestHeader("X-User-Id") Long ownerId) {
        dto.setOwnerId(ownerId); // obligatoire
        Ouvrage created = ouvrageService.createFromDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ouvrageMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OuvrageDTO> update(@PathVariable Long id,
            @RequestBody OuvrageDTO dto,
            @RequestHeader("X-User-Id") Long ownerId) {
        Optional<Ouvrage> existingOpt = ouvrageService.findById(id);
        if (existingOpt.isEmpty())
            return ResponseEntity.notFound().build();

        Ouvrage existing = existingOpt.get();
        if (!ownerId.equals(existing.getOwnerId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        dto.setOwnerId(ownerId);
        Ouvrage updated = ouvrageService.updateFromDTO(id, dto);
        return ResponseEntity.ok(ouvrageMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
            @RequestHeader("X-User-Id") Long ownerId) {
        Optional<Ouvrage> existingOpt = ouvrageService.findById(id);
        if (existingOpt.isEmpty())
            return ResponseEntity.notFound().build();

        Ouvrage existing = existingOpt.get();
        if (!ownerId.equals(existing.getOwnerId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        ouvrageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/gammes/{gammeId}/exclude/{id}")
    public ResponseEntity<List<OuvrageDTO>> getOuvragesByGamme(@PathVariable Long gammeId,
            @PathVariable Long id) {
        List<OuvrageDTO> ouvrages = ouvrageService.getOuvragesByGamme(gammeId, id);
        return ResponseEntity.ok(ouvrages);
    }

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Fichier vide");
            }

            // Générer un nom unique pour éviter les collisions
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/images"); // dossier où stocker les images
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Retourner l’URL ou chemin relatif
            String imageUrl = "/uploads/images/" + filename;
            return ResponseEntity.ok(imageUrl);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur");
        }
    }

}
