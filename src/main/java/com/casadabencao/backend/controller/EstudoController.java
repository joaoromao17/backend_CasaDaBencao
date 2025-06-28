package com.casadabencao.backend.controller;

import com.casadabencao.backend.model.Estudo;
import com.casadabencao.backend.service.EstudoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.casadabencao.backend.service.CloudinaryService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/estudos")
public class EstudoController {

    @Autowired
    private EstudoService estudoService;

@Autowired
private CloudinaryService cloudinaryService;

    private final String uploadDir = "uploads/pdfs/";

    // GET - Lista todos os estudos
    @GetMapping
    public List<Estudo> getAllEstudos() {
        return estudoService.findAll();
    }

    // GET - Busca estudo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Estudo> getEstudoById(@PathVariable Long id) {
        Optional<Estudo> estudo = estudoService.findById(id);
        return estudo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Cria um novo estudo
    @PostMapping
    public Estudo createEstudo(@RequestBody Estudo estudo) {
        return estudoService.save(estudo);
    }

@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<Estudo> updateEstudo(
        @PathVariable Long id,
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("author") String author,
        @RequestParam("date") String date,
        @RequestParam("category") String category,
        @RequestParam(value = "pdf", required = false) MultipartFile pdfFile,
        @RequestParam(value = "pdfUrl", required = false) String pdfUrl) {

    Optional<Estudo> estudoOptional = estudoService.findById(id);
    if (estudoOptional.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    Estudo estudo = estudoOptional.get();
    estudo.setTitle(title);
    estudo.setDescription(description);
    estudo.setAuthor(author);
    estudo.setDate(LocalDate.parse(date));
    estudo.setCategory(category);

    try {
        if (pdfUrl != null && !pdfUrl.isBlank()) {
            estudo.setPdfUrl(pdfUrl);
        } else if (pdfFile != null && !pdfFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(pdfFile, "estudos");
            estudo.setPdfUrl(url);
        }
        // Caso nenhum dos dois seja enviado, mantém o PDF anterior
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    Estudo salvo = estudoService.save(estudo);
    return ResponseEntity.ok(salvo);
}




    // DELETE - Deleta um estudo pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstudo(@PathVariable Long id) {
        if (estudoService.existsById(id)) {
            estudoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Upload de estudo com PDF
@PostMapping("/upload")
public ResponseEntity<Estudo> uploadEstudoComPdf(
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("author") String author,
        @RequestParam("date") String date,
        @RequestParam("category") String category,
        @RequestParam(value = "pdf", required = false) MultipartFile pdfFile,
        @RequestParam(value = "pdfUrl", required = false) String pdfUrl) {

    try {
        Estudo estudo = new Estudo();
        estudo.setTitle(title);
        estudo.setDescription(description);
        estudo.setAuthor(author);
        estudo.setDate(LocalDate.parse(date));
        estudo.setCategory(category);

        if (pdfUrl != null && !pdfUrl.isBlank()) {
            estudo.setPdfUrl(pdfUrl);
        } else if (pdfFile != null && !pdfFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(pdfFile, "estudos");
            estudo.setPdfUrl(url);
        } else {
            return ResponseEntity.badRequest().build(); // nem link nem arquivo enviado
        }

        Estudo salvo = estudoService.save(estudo);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
}
