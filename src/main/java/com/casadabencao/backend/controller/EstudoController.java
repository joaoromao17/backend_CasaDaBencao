package com.casadabencao.backend.controller;

import com.casadabencao.backend.model.Estudo;
import com.casadabencao.backend.repository.EstudoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
@CrossOrigin(origins = "http://localhost:3000")
public class EstudoController {

    @Autowired
    private EstudoRepository estudoRepository;

    private final String uploadDir = "uploads/pdfs/";

    // GET - Lista todos os estudos
    @GetMapping
    public List<Estudo> getAllEstudos() {
        return estudoRepository.findAll();
    }

    // GET - Busca estudo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Estudo> getEstudoById(@PathVariable Long id) {
        Optional<Estudo> estudo = estudoRepository.findById(id);
        return estudo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Cria um novo estudo
    @PostMapping
    public Estudo createEstudo(@RequestBody Estudo estudo) {
        return estudoRepository.save(estudo);
    }

    // PUT - Atualiza um estudo existente
    @PutMapping("/{id}")
    public ResponseEntity<Estudo> updateEstudo(@PathVariable Long id, @RequestBody Estudo estudoAtualizado) {
        Optional<Estudo> estudoOptional = estudoRepository.findById(id);
        if (estudoOptional.isPresent()) {
            Estudo estudo = estudoOptional.get();
            estudo.setTitle(estudoAtualizado.getTitle());
            estudo.setDescription(estudoAtualizado.getDescription());
            estudo.setAuthor(estudoAtualizado.getAuthor());
            estudo.setDate(estudoAtualizado.getDate());
            estudo.setCategory(estudoAtualizado.getCategory());
            estudo.setPdfUrl(estudoAtualizado.getPdfUrl());

            return ResponseEntity.ok(estudoRepository.save(estudo));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Deleta um estudo pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstudo(@PathVariable Long id) {
        if (estudoRepository.existsById(id)) {
            estudoRepository.deleteById(id);
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
            @RequestParam("pdf") MultipartFile pdfFile) {

        try {
            // Gera um nome único para o arquivo
            String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(pdfFile.getOriginalFilename());
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(pdfFile.getInputStream(), filePath);

            // Cria o estudo e salva no banco
            Estudo estudo = new Estudo();
            estudo.setTitle(title);
            estudo.setDescription(description);
            estudo.setAuthor(author);
            estudo.setDate(LocalDate.parse(date));
            estudo.setCategory(category);
            estudo.setPdfUrl("http://localhost:8080/" + uploadDir + filename);

            Estudo salvo = estudoRepository.save(estudo);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
