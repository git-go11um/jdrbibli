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

/**
 * Contrôleur REST pour gérer les {@link Ouvrage} dans le microservice
 * {@code ouvrage-service}.
 * 
 * Fournit des endpoints pour :
 * <ul>
 * <li>Récupérer tous les ouvrages d'un utilisateur</li>
 * <li>Récupérer un ouvrage par son identifiant ou par sa gamme</li>
 * <li>Créer, mettre à jour et supprimer un ouvrage</li>
 * <li>Uploader une image pour un ouvrage</li>
 * <li>Récupérer la bibliothèque d’un ami et ses ouvrages par gamme</li>
 * </ul>
 * Chaque requête sécurisée utilise l'en-tête {@code X-User-Id} pour identifier
 * le propriétaire.
 */
@RestController
@RequestMapping("/api/ouvrage/ouvrages")
public class OuvrageController {

    private final OuvrageService ouvrageService;
    private final OuvrageMapper ouvrageMapper;

    /**
     * Constructeur du contrôleur.
     *
     * @param ouvrageService service pour gérer les ouvrages.
     * @param ouvrageMapper  mapper pour convertir entre {@link Ouvrage} et
     *                       {@link OuvrageDTO}.
     */
    public OuvrageController(OuvrageService ouvrageService, OuvrageMapper ouvrageMapper) {
        this.ouvrageService = ouvrageService;
        this.ouvrageMapper = ouvrageMapper;
    }

    /**
     * Récupère tous les ouvrages pour un utilisateur donné.
     *
     * @param ownerId identifiant du propriétaire (X-User-Id)
     * @return liste de {@link OuvrageDTO}.
     */
    @GetMapping
    public ResponseEntity<List<OuvrageDTO>> getAll(@RequestHeader("X-User-Id") Long ownerId) {
        List<Ouvrage> ouvrages = ouvrageService.findByOwnerId(ownerId);
        List<OuvrageDTO> dtos = ouvrages.stream()
                .map(ouvrageMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Récupère un ouvrage par son identifiant.
     *
     * @param id      identifiant de l'ouvrage.
     * @param ownerId identifiant du propriétaire (X-User-Id)
     * @return l'ouvrage correspondant ou 403/404 selon le cas.
     */
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

    /**
     * Récupère tous les ouvrages d'une gamme donnée.
     *
     * @param gammeId identifiant de la gamme
     * @return liste de {@link OuvrageDTO}.
     */
    @GetMapping("/gammes/{gammeId}")
    public ResponseEntity<List<OuvrageDTO>> getByGamme(@PathVariable Long gammeId) {
        List<Ouvrage> ouvrages = ouvrageService.findByGammeId(gammeId);
        List<OuvrageDTO> dtos = ouvrages.stream()
                .map(ouvrageMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Crée un nouvel ouvrage pour un utilisateur.
     *
     * @param dto     données de l'ouvrage
     * @param ownerId identifiant du propriétaire (X-User-Id)
     * @return l'ouvrage créé avec statut 201.
     */
    @PostMapping
    public ResponseEntity<OuvrageDTO> create(@RequestBody OuvrageDTO dto,
            @RequestHeader("X-User-Id") Long ownerId) {
        dto.setOwnerId(ownerId);
        Ouvrage created = ouvrageService.createFromDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ouvrageMapper.toDTO(created));
    }

    /**
     * Met à jour un ouvrage existant.
     *
     * @param id      identifiant de l'ouvrage.
     * @param dto     nouvelles données.
     * @param ownerId identifiant du propriétaire (X-User-Id)
     * @return l'ouvrage mis à jour ou 403/404 selon le cas.
     */
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

    /**
     * Supprime un ouvrage.
     *
     * @param id      identifiant de l'ouvrage
     * @param ownerId identifiant du propriétaire (X-User-Id)
     * @return 204 si succès, 403 si non autorisé, 404 si non trouvé.
     */
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

    /**
     * Récupère les ouvrages d'une gamme donnée en excluant un ouvrage spécifique.
     *
     * @param gammeId identifiant de la gamme
     * @param id      identifiant de l'ouvrage à exclure
     * @return liste de {@link OuvrageDTO}.
     */
    @GetMapping("/gammes/{gammeId}/exclude/{id}")
    public ResponseEntity<List<OuvrageDTO>> getOuvragesByGamme(@PathVariable Long gammeId,
            @PathVariable Long id) {
        List<OuvrageDTO> ouvrages = ouvrageService.getOuvragesByGamme(gammeId, id);
        return ResponseEntity.ok(ouvrages);
    }

    /**
     * Upload d'une image pour un ouvrage.
     *
     * @param file fichier image
     * @return URL de l'image stockée ou message d'erreur.
     */
    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Fichier vide");
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/images");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/images/" + filename;
            return ResponseEntity.ok(imageUrl);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur");
        }
    }

    /**
     * Récupère la bibliothèque d’un ami si les utilisateurs sont amis.
     *
     * @param friendId      identifiant de l'ami
     * @param currentUserId identifiant de l'utilisateur courant (X-User-Id)
     * @return liste de {@link OuvrageDTO} ou 403/500 selon le cas.
     */
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
            friends = WebClient.create("http://user-service:8082")
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

    /**
     * Récupère les ouvrages d'un ami pour une gamme spécifique.
     *
     * @param friendId identifiant de l'ami
     * @param gammeId  identifiant de la gamme
     * @return liste de {@link OuvrageDTO}.
     */
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

    /**
     * Récupère un ouvrage précis d'un ami.
     *
     * @param friendId  identifiant de l'ami
     * @param ouvrageId identifiant de l'ouvrage
     * @return {@link OuvrageDTO} ou 404 si non trouvé.
     */
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
