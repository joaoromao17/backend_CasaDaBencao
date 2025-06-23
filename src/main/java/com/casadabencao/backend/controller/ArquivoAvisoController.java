package com.casadabencao.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.casadabencao.backend.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/avisos/arquivos")
public class ArquivoAvisoController {

    @Autowired
    private CloudinaryService cloudinaryService;

@PostMapping
public ResponseEntity<String> uploadArquivo(@RequestParam("file") MultipartFile file) throws IOException {
    if (file.isEmpty()) {
        return ResponseEntity.badRequest().body("Arquivo vazio.");
    }

    String urlPublica = cloudinaryService.uploadFile(file, "avisos");
    return ResponseEntity.ok(urlPublica);
}
}
