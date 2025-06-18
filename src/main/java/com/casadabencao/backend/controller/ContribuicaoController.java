package com.casadabencao.backend.controller;

import com.casadabencao.backend.model.Contribuicao;
import com.casadabencao.backend.service.ContribuicaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/contribuicoes")
@CrossOrigin(origins = "http://localhost:3000") // Ajuste para produção conforme necessário
public class ContribuicaoController {

    @Autowired
    private ContribuicaoService service;

    @GetMapping
    public List<Contribuicao> listarTodas() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Contribuicao buscarPorId(@PathVariable Long id) {
        return service.findById(id)
                .orElseThrow(() -> new RuntimeException("Contribuição não encontrada com ID: " + id));
    }

    @PostMapping
    public Contribuicao criar(@Valid @RequestBody Contribuicao nova) {
        return service.save(nova);
    }

    @PutMapping("/{id}")
    public Contribuicao atualizar(@PathVariable Long id, @Valid @RequestBody Contribuicao nova) {
        return service.update(id, nova);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deleteById(id);
    }

    @PostMapping("/{id}/adicionar-valor")
    public Contribuicao adicionarValor(@PathVariable Long id, @RequestParam BigDecimal valor) {
        return service.adicionarContribuicao(id, valor);
    }

    @PostMapping("/{id}/imagem")
    public Contribuicao uploadImagem(
            @PathVariable Long id,
            @RequestParam("imagem") MultipartFile imagem
    ) {
        return service.salvarImagem(id, imagem);
    }

}
