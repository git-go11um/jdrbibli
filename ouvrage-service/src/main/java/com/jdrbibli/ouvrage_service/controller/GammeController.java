package com.jdrbibli.ouvrage_service.controller;

import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.service.GammeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/gammes")
public class GammeController {
    private final GammeService gammeService;

    public GammeController(GammeService gammeService) {
        this.gammeService = gammeService;
    }

    @GetMapping
    public List<Gamme> getAll() {
        return gammeService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Gamme> gamme = gammeService.findById(id);
        if (gamme.isPresent()) {
            return ResponseEntity.ok(gamme.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Gamme> create(@RequestBody Gamme gamme) {
        Gamme saved = gammeService.save(gamme);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gamme> update(@PathVariable Long id, @RequestBody Gamme gamme) {
        gamme.setId(id);
        Gamme updated = gammeService.save(gamme);
        return ResponseEntity.ok(updated);
    }

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
