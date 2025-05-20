package com.casadabencao.backend.controller;

import com.casadabencao.backend.dto.ChangePasswordDto;
import com.casadabencao.backend.dto.ForgotPasswordDto;
import com.casadabencao.backend.dto.ResetPasswordDto;
import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.service.UsuarioService;
import com.casadabencao.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> getAll() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public Usuario getById(@PathVariable Long id) {
        return usuarioService.findById(id).orElse(null);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = usuarioService.save(usuario);
            return ResponseEntity.ok(novoUsuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Usuario update(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioService.update(id, usuario);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        usuarioService.delete(id);
    }

    @GetMapping("/profile")
    public ResponseEntity<Usuario> getProfile(Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioService.findByEmail(email);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/profile/image")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            String email = principal.getName(); // ou extraia do token, se usar JWT
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Cria pasta se não existir
            Path uploadPath = Paths.get("uploads/profiles");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            if (file.isEmpty() || !file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body("Arquivo inválido.");
            }


            // Gera nome único
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Atualiza o campo no banco
            String imagePath = "/uploads/profiles/" + filename;
            usuario.setProfileImageUrl(imagePath);
            usuarioRepository.save(usuario);

            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar imagem");
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Usuario updated, Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        Usuario atualizado = usuarioService.update(usuario.getId(), updated);
        return ResponseEntity.ok(atualizado);
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto, Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioService.findByEmail(email);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        if (!usuarioService.checkPassword(dto.getCurrentPassword(), usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha atual incorreta.");
        }

        usuarioService.changePassword(usuario, dto.getNewPassword());
        return ResponseEntity.ok("Senha alterada com sucesso.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDto dto) {
        try {
            usuarioService.sendResetToken(dto.getEmail());
            return ResponseEntity.ok("Email de redefinição enviado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto dto) {
        try {
            usuarioService.resetPassword(dto.getToken(), dto.getNewPassword());
            return ResponseEntity.ok("Senha redefinida com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}