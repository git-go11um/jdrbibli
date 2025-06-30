package com.jdrbibli.ouvrage_service.controller;

import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.service.OuvrageService;
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
    public List<Ouvrage> getAll() {
        return ouvrageService.findAll();
    }

    @GetMapping("/{id}")
    public Ouvrage getById(@PathVariable Long id) {
        return ouvrageService.findById(id).orElse(null);
    }

    @PostMapping
    public Ouvrage create(@RequestBody Ouvrage ouvrage) {
        return ouvrageService.save(ouvrage);
    }

    @PutMapping("/{id}")
    public Ouvrage update(@PathVariable Long id, @RequestBody Ouvrage ouvrage) {
        ouvrage.setId(id);
        return ouvrageService.save(ouvrage);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ouvrageService.deleteById(id);
    }
}
