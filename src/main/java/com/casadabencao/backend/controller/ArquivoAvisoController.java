// ArquivoAvisoController.java
package com.casadabencao.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/avisos/arquivos")
public class ArquivoAvisoController {

    @Value("${upload.aviso.path}")
    private String uploadDir;

    @PostMapping
    public ResponseEntity<String> uploadArquivo(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Arquivo vazio.");
        }

        String filename = System.currentTimeMillis() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
        Path destino = Paths.get(uploadDir).resolve(filename);

        Files.createDirectories(destino.getParent());
        Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        // Caminho acessível pelo frontend (ajuste conforme seu setup de estáticos se necessário)
        String urlPublica = "/uploads/avisos/" + filename;
        return ResponseEntity.ok(urlPublica);
    }
}
