package com.jdrbibli.ouvrage_service.controller;

import com.jdrbibli.ouvrage_service.dto.OuvrageDTO;
import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import com.jdrbibli.ouvrage_service.mapper.OuvrageMapper;
import com.jdrbibli.ouvrage_service.service.OuvrageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ouvrages")
public class OuvrageController {

    private final OuvrageService ouvrageService;
    private final OuvrageMapper ouvrageMapper;

    public OuvrageController(OuvrageService ouvrageService, OuvrageMapper ouvrageMapper) {
        this.ouvrageService = ouvrageService;
        this.ouvrageMapper = ouvrageMapper;
    }

    // GET /api/ouvrages
    @GetMapping
    public List<OuvrageDTO> getAll() {
        return ouvrageService.findAll()
                .stream()
                .map(ouvrageMapper::toDTO)
                .collect(Collectors.toList());
    }

    // GET /api/ouvrages/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OuvrageDTO> getById(@PathVariable Long id) {
        return ouvrageService.findById(id)
                .map(ouvrageMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/ouvrages
    @PostMapping
    public ResponseEntity<OuvrageDTO> create(@RequestBody OuvrageDTO dto) {
        try {
            Ouvrage created = ouvrageService.createFromDTO(dto);
            return new ResponseEntity<>(ouvrageMapper.toDTO(created), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // PUT /api/ouvrages/{id}
    @PutMapping("/{id}")
    public ResponseEntity<OuvrageDTO> update(@PathVariable Long id, @RequestBody OuvrageDTO dto) {
        try {
            Ouvrage updated = ouvrageService.updateFromDTO(id, dto);
            return ResponseEntity.ok(ouvrageMapper.toDTO(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/ouvrages/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (ouvrageService.findById(id).isPresent()) {
            ouvrageService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
