package com.casadabencao.backend.controller;

import com.casadabencao.backend.dto.MinisterioDto;
import com.casadabencao.backend.model.Ministerio;
import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.service.MinisterioService;
import com.casadabencao.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/ministerios")
public class MinisterioController {

    @Autowired
    private MinisterioService ministerioService;

    @Autowired
    private UsuarioService usuarioService;

    // ⚠️ Substituído para retornar DTOs
    @GetMapping
    public ResponseEntity<List<MinisterioDto>> getAll() {
        return ResponseEntity.ok(ministerioService.getAllWithLeaders());
    }

    @GetMapping("/{id}")
    public Ministerio getById(@PathVariable Long id) {
        return ministerioService.findById(id).orElse(null);
    }

    @PostMapping
    public ResponseEntity<?> createMinistry(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String meetingDay,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) List<Long> leaderIds,
            @RequestParam(required = false) List<Long> viceLeaders,
            @RequestParam(required = false) List<String> activities
    ) {
        try {
            Ministerio ministerio = new Ministerio();
            ministerio.setName(name);
            ministerio.setDescription(description);
            ministerio.setMeetingDay(meetingDay);

            // Upload da imagem
            if (image != null && !image.isEmpty() && image.getContentType().startsWith("image/")) {
                Path uploadPath = Paths.get("uploads/ministerios");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String imageUrl = "/images/ministerios/" + filename;
                ministerio.setImageUrl(imageUrl);
            } else {
                // Imagem padrão caso nenhuma seja enviada
                ministerio.setImageUrl("/uploads/ministerios/ministerio_default.jpg");
            }

            // Atividades
            if (activities != null) {
                List<String> atividadesFiltradas = activities.stream()
                        .filter(a -> a != null && !a.trim().isEmpty())
                        .toList();
                ministerio.setAtividades(atividadesFiltradas);
            }

            // Buscar líderes
            if (leaderIds != null) {
                List<Usuario> leaders = leaderIds.stream()
                        .map(usuarioService::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();
                ministerio.setLeaders(leaders);
            }

            // Buscar vice-líderes
            if (viceLeaders != null) {
                List<Usuario> vices = viceLeaders.stream()
                        .map(usuarioService::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();
                ministerio.setViceLeaders(vices);
            }

            Ministerio salvo = ministerioService.save(ministerio);
            return ResponseEntity.ok(salvo);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e.getMessage());
        }
    }



    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ministerioService.delete(id);
    }

    // Você pode manter esse como está, ou até mesmo remover pois está duplicado com o de cima
    @GetMapping("/resumo")
    public ResponseEntity<List<MinisterioDto>> getMinisteriosComResumo() {
        return ResponseEntity.ok(ministerioService.getAllWithLeaders());
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestPart("dto") MinisterioDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Principal principal
    ) {
        Usuario usuario = usuarioService.findByEmail(principal.getName());

        Ministerio ministerio = ministerioService.findById(id).orElse(null);
        if (ministerio == null) return ResponseEntity.notFound().build();

        boolean isLider = ministerio.getLeaders().contains(usuario);
        boolean isVice = ministerio.getViceLeaders().contains(usuario);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(role -> role.name().equals("ROLE_ADMIN"));
        boolean isPastor = usuario.getRoles().stream().anyMatch(role -> role.name().equals("ROLE_PASTOR"));

        if (!(isLider || isVice || isAdmin || isPastor)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Ministerio atualizadoFinal = ministerioService.updateFromDto(id, dto, image);
        return ResponseEntity.ok(atualizadoFinal);
    }

}

