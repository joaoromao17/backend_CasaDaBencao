package com.casadabencao.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final String BASE_DIR = "uploads"; // caminho relativo ou absoluto

    @GetMapping("/{type}/{filename:.+}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String type,
            @PathVariable String filename
    ) {
        try {
            Path imagePath = Paths.get(BASE_DIR, type).resolve(filename).normalize();
            Resource resource = new UrlResource(imagePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaTypeFactory.getMediaType(resource)
                            .orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

