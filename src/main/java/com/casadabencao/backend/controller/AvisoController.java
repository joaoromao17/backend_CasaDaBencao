package com.casadabencao.backend.controller;

import com.casadabencao.backend.dto.AvisoDto;
import com.casadabencao.backend.dto.NovoAvisoDto;
import com.casadabencao.backend.security.JwtUtil;
import com.casadabencao.backend.service.AvisoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/avisos")
@CrossOrigin(origins = "http://localhost:3000")
public class AvisoController {

    private final AvisoService service;
    private final JwtUtil jwtUtil; // ✅ Adiciona aqui

    public AvisoController(AvisoService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil; // ✅ Recebe via construtor
    }

    @PostMapping
    public ResponseEntity<AvisoDto> criar(@Valid @RequestBody NovoAvisoDto dto,
                                          @RequestHeader("Authorization") String token) {
        Long autorId = jwtUtil.getUserIdFromToken(token); // ✅ agora sem erro
        return ResponseEntity.ok(service.criar(dto, autorId));
    }

    @GetMapping("/ativos")
    public List<AvisoDto> listarVisiveis() {
        return service.listarAvisosVisiveis();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

