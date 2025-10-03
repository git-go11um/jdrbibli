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

/**
 * Contrôleur REST pour gérer les {@link Gamme} dans le microservice {@code ouvrage-service}.
 * <p>
 * Fournit des endpoints pour :
 * <ul>
 *   <li>Récupérer toutes les gammes d'un utilisateur</li>
 *   <li>Récupérer une gamme par son identifiant</li>
 *   <li>Créer, mettre à jour et supprimer une gamme</li>
 *   <li>Récupérer les gammes publiques ou celles d'un ami</li>
 * </ul>
 * Chaque requête sécurisée utilise l'en-tête {@code X-User-Id} pour identifier le propriétaire.
 */
@RestController
@RequestMapping("/api/ouvrage/gammes")
public class GammeController {

    private final GammeService gammeService;
    private final GammeMapper gammeMapper;

    /**
     * Constructeur du contrôleur.
     *
     * @param gammeService service pour gérer les gammes.
     * @param gammeMapper  mapper pour convertir entre {@link Gamme} et {@link GammeDTO}.
     */
    public GammeController(GammeService gammeService, GammeMapper gammeMapper) {
        this.gammeService = gammeService;
        this.gammeMapper = gammeMapper;
    }

    /**
     * Récupère toutes les gammes pour un utilisateur donné.
     *
     * @param ownerId identifiant du propriétaire (X-User-Id)
     * @return liste de {@link GammeDTO}.
     */
    @GetMapping
    public ResponseEntity<List<GammeDTO>> getAll(@RequestHeader("X-User-Id") Long ownerId) {
        List<Gamme> gammes = gammeService.findByOwnerId(ownerId);
        List<GammeDTO> gammesDTO = gammes.stream()
                .map(gammeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gammesDTO);
    }

    /**
     * Récupère une gamme par son identifiant.
     *
     * @param id      identifiant de la gamme.
     * @param ownerId identifiant du propriétaire (X-User-Id)
     * @return la gamme correspondante ou 403/404 selon le cas.
     */
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

    /**
     * Crée une nouvelle gamme pour un utilisateur.
     *
     * @param gammeDTO données de la gamme.
     * @param ownerId  identifiant du propriétaire (X-User-Id)
     * @return la gamme créée avec statut 201.
     */
    @PostMapping
    public ResponseEntity<GammeDTO> create(@RequestBody GammeDTO gammeDTO,
            @RequestHeader("X-User-Id") Long ownerId) {
        gammeDTO.setOwnerId(ownerId);
        Gamme gamme = gammeMapper.toEntity(gammeDTO);
        Gamme saved = gammeService.save(gamme);
        return ResponseEntity.status(HttpStatus.CREATED).body(gammeMapper.toDTO(saved));
    }

    /**
     * Met à jour une gamme existante.
     *
     * @param id       identifiant de la gamme.
     * @param gammeDTO nouvelles données.
     * @param ownerId  identifiant du propriétaire (X-User-Id)
     * @return la gamme mise à jour ou 403/404 selon le cas.
     */
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

    /**
     * Supprime une gamme.
     *
     * @param id      identifiant de la gamme.
     * @param force   si vrai, suppression forcée même si des ouvrages existent.
     * @param ownerId identifiant du propriétaire (X-User-Id)
     * @return 204 si succès, 403 si non autorisé, 404 si non trouvé, 400 si erreur.
     */
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

    /**
     * Supprime toutes les gammes d'un utilisateur.
     *
     * @param ownerId identifiant du propriétaire.
     * @return 204 si succès.
     */
    @DeleteMapping("/by-owner/{ownerId}")
    public ResponseEntity<Void> deleteGammesByOwner(@PathVariable Long ownerId) {
        gammeService.deleteByOwnerId(ownerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupère les gammes publiques d'un utilisateur.
     *
     * @param ownerId identifiant du propriétaire.
     * @return liste de {@link GammeDTO}.
     */
    @GetMapping("/public/owner/{ownerId}")
    public ResponseEntity<List<GammeDTO>> getGammesByOwner(@PathVariable Long ownerId) {
        List<Gamme> gammes = gammeService.findByOwnerId(ownerId);
        List<GammeDTO> gammesDTO = gammes.stream().map(gammeMapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(gammesDTO);
    }

    /**
     * Récupère les gammes d'un ami.
     *
     * @param friendId identifiant de l'ami.
     * @return liste de {@link GammeDTO}.
     */
    @GetMapping("/friend/{friendId}")
    public ResponseEntity<List<GammeDTO>> getGammesByFriend(@PathVariable Long friendId) {
        List<Gamme> gammes = gammeService.findByOwnerId(friendId);
        List<GammeDTO> gammesDTO = gammes.stream()
                .map(gammeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gammesDTO);
    }

}
