package com.casadabencao.backend.controller;

import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class ProfileImageController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/{id}/upload-profile-image")
    public ResponseEntity<?> uploadProfileImage(@PathVariable Long id, @RequestParam("image") MultipartFile imageFile) {
        try {
            Usuario usuario = usuarioRepository.findById(id).orElse(null);
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }

            // Cria nome único para o arquivo
            String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String filename = "profile_" + UUID.randomUUID() + extension;

            // Caminho real para salvar (dentro de static/images/profiles)
            File uploadDir = new ClassPathResource("static/images/profiles").getFile();
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File dest = new File(uploadDir, filename);
            imageFile.transferTo(dest);

            // Atualiza o profileImageUrl com a URL acessível publicamente
            String imageUrl = "/images/profiles/" + filename;
            usuario.setProfileImageUrl(imageUrl);
            usuarioRepository.save(usuario);

            return ResponseEntity.ok().body(imageUrl);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao salvar imagem: " + e.getMessage());
        }
    }
}
