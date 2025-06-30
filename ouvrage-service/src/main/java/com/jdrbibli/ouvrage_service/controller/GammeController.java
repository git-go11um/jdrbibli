package com.jdrbibli.ouvrage_service.controller;

import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.service.GammeService;
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
    public List<Gamme> getAll() {
        return gammeService.findAll();
    }

    @GetMapping("/{id}")
    public Gamme getById(@PathVariable Long id) {
        return gammeService.findById(id).orElse(null);
    }

    @PostMapping
    public Gamme create(@RequestBody Gamme gamme) {
        return gammeService.save(gamme);
    }

    @PutMapping("/{id}")
    public Gamme update(@PathVariable Long id, @RequestBody Gamme gamme) {
        gamme.setId(id);
        return gammeService.save(gamme);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        gammeService.deleteById(id);
    }
}
