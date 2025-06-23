package com.casadabencao.backend.controller;

import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.casadabencao.backend.service.CloudinaryService;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class ProfileImageController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
private CloudinaryService cloudinaryService;

@PostMapping("/{id}/upload-profile-image")
public ResponseEntity<?> uploadProfileImage(@PathVariable Long id, @RequestParam("image") MultipartFile imageFile) {
    try {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        String imageUrl = cloudinaryService.uploadFile(imageFile, "usuarios");
        usuario.setProfileImageUrl(imageUrl);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok().body(imageUrl);
    } catch (IOException e) {
        return ResponseEntity.internalServerError().body("Erro ao salvar imagem: " + e.getMessage());
    }
}
}
