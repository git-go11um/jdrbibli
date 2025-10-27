package com.jdrbibli.ouvrage_service.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

/**
 * Contrôleur public pour servir les fichiers d'uploads (images, PDF, etc.)
 * sans passer par la sécurité Spring.
 */
@RestController
@RequestMapping("/uploads/images")
public class PublicFileController {

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        File file = new File("/app/uploads/images/" + filename);

        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400") // 24h de cache
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
