package com.casadabencao.backend.controller;

import com.casadabencao.backend.dto.*;
import com.casadabencao.backend.model.Role;
import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.service.UsuarioService;
import com.casadabencao.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.casadabencao.backend.service.CloudinaryService;

import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
private CloudinaryService cloudinaryService;

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
        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (file.isEmpty() || !file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("Arquivo inválido.");
        }

        String imageUrl = cloudinaryService.uploadFile(file, "usuarios");
        usuario.setProfileImageUrl(imageUrl);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok().body(imageUrl);
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

    @GetMapping("/ministerios/{id}/membros")
    public ResponseEntity<List<UsuarioDto>> getMembrosPorMinisterio(@PathVariable Long id) {
        List<Usuario> usuarios = usuarioRepository.findByMinistries_Id(id);
        List<UsuarioDto> dtos = usuarios.stream()
                .map(UsuarioDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}/public")
    public ResponseEntity<?> getPublicProfile(@PathVariable Long id) {
        Optional<Usuario> usuarioOptional = usuarioService.findById(id);

        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOptional.get();
        UsuarioPublicDto dto = new UsuarioPublicDto(usuario);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/public/roles/{role}")
    public ResponseEntity<?> getByRole(@PathVariable String role) {
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            List<Usuario> usuarios = usuarioService.findByRole(roleEnum);
            List<UsuarioPublicDto> dtos = usuarios.stream()
                    .map(UsuarioPublicDto::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Cargo inválido.");
        }
    }

    @GetMapping("/public/membros")
    public ResponseEntity<Page<UsuarioPublicDto>> getMembros(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "") String search
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Usuario> membros = usuarioService.findMembros(search, pageable);
        Page<UsuarioPublicDto> dtoPage = membros.map(UsuarioPublicDto::new);
        return ResponseEntity.ok(dtoPage);
    }


    @GetMapping("/public/aniversariantes")
    public ResponseEntity<List<UsuarioPublicDto>> getAniversariantes() {
        List<Usuario> usuarios = usuarioService.findAniversariantesDoMes();
        List<UsuarioPublicDto> dtos = usuarios.stream().map(UsuarioPublicDto::new).toList();
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteOwnAccount(Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioService.findByEmail(email);

        usuarioService.delete(usuario.getId());
        return ResponseEntity.ok("Conta excluída com sucesso.");
    }
}
