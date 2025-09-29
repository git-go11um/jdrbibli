package com.jdrbibli.ouvrage_service.controller;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.mapper.OuvrageMapper;
import com.jdrbibli.ouvrage_service.service.OuvrageService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

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

    @GetMapping("/friends/{friendId}/ouvrages")
    public ResponseEntity<List<OuvrageDTO>> getFriendLibrary(
            @PathVariable Long friendId,
            @RequestHeader("X-User-Id") Long currentUserId) {

        System.out.println(
                "[DEBUG] getFriendLibrary appelé avec friendId=" + friendId + ", currentUserId=" + currentUserId);

        boolean friends;
        System.out.println("[DEBUG] Appel user-service : http://localhost:8082/api/users/are-friends?userId="
                + currentUserId + "&friendId=" + friendId);
        try {
            friends = WebClient.create("http://localhost:8082") // URL du user-service
                    .get()
                    .uri("/api/users/are-friends?userId={userId}&friendId={friendId}", currentUserId, friendId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            System.out.println("[DEBUG] Résultat vérification amitié: " + friends);
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors de l'appel user-service : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if (!friends) {
            System.out.println("[DEBUG] Les utilisateurs ne sont pas amis, renvoi 403");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Ouvrage> ouvrages;
        try {
            ouvrages = ouvrageService.findByOwnerId(friendId);
            System.out.println("[DEBUG] Nombre d'ouvrages trouvés: " + ouvrages.size());
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors de la récupération des ouvrages : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        List<OuvrageDTO> dtos;
        try {
            dtos = ouvrages.stream()
                    .map(ouvrageMapper::toDTO)
                    .peek(dto -> System.out.println("[DEBUG] OuvrageDTO créé: " + dto))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors du mapping Ouvrage→DTO : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        System.out.println("[DEBUG] Renvoi " + dtos.size() + " DTOs");
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/friend/{friendId}/gamme/{gammeId}")
    public ResponseEntity<List<OuvrageDTO>> getOuvragesByFriendAndGamme(
            @PathVariable Long friendId,
            @PathVariable Long gammeId) {

        List<Ouvrage> ouvrages = ouvrageService.findByGammeIdAndOwnerId(gammeId, friendId);
        List<OuvrageDTO> ouvragesDTO = ouvrages.stream()
                .map(ouvrageMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ouvragesDTO);
    }

    @GetMapping("/friend/{friendId}/{ouvrageId}")
    public ResponseEntity<OuvrageDTO> getOuvrageFriend(
            @PathVariable Long friendId,
            @PathVariable Long ouvrageId) {

        Optional<Ouvrage> ouvrageOpt = ouvrageService.findByIdAndOwnerId(ouvrageId, friendId);
        if (ouvrageOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Ouvrage ouvrage = ouvrageOpt.get();
        return ResponseEntity.ok(ouvrageMapper.toDTO(ouvrage));
    }

}
