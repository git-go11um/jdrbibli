package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.entity.Ouvrage;
import com.jdrbibli.userservice.service.OuvrageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ouvrages")
public class OuvrageController {
    private final OuvrageService ouvrageService;

    public OuvrageController(OuvrageService ouvrageService) {
        this.ouvrageService = ouvrageService;
    }

    @GetMapping
    public List<Ouvrage> getAllOuvrages() {
        return ouvrageService.getAllOuvrages();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ouvrage> getOuvrageById(@PathVariable Long id) {
        return ouvrageService.getOuvrageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Ouvrage createOuvrage(@RequestBody Ouvrage ouvrage) {
        return ouvrageService.createOuvrage(ouvrage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOuvrage(@PathVariable Long id) {
        ouvrageService.deleteOuvrage(id);
        return ResponseEntity.noContent().build();
    }
}
