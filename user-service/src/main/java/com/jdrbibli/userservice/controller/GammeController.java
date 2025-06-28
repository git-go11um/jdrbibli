package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.entity.Gamme;
import com.jdrbibli.userservice.service.GammeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gammes")
public class GammeController {
    private final GammeService gammeService;

    public GammeController(GammeService gammeService) {
        this.gammeService = gammeService;
    }

    @GetMapping
    public List<Gamme> getAllGammes() {
        return gammeService.getAllGammes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gamme> getGammeById(@PathVariable Long id) {
        return gammeService.getGammeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Gamme createGamme(@RequestBody Gamme gamme) {
        return gammeService.createGamme(gamme);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGamme(@PathVariable Long id) {
        gammeService.deleteGamme(id);
        return ResponseEntity.noContent().build();
    }
}
