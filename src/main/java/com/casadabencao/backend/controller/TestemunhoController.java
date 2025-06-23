package com.casadabencao.backend.controller;

import com.casadabencao.backend.model.Oracao;
import com.casadabencao.backend.model.Testemunho;
import com.casadabencao.backend.service.OracaoService;
import com.casadabencao.backend.service.TestemunhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.io.IOException;

@RestController
@RequestMapping("/api/testemunhos")
public class TestemunhoController {

    @Autowired
    private TestemunhoService testemunhoService;

    @Autowired
    private OracaoService oracaoService;

    @GetMapping
    public List<Testemunho> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category
    ) {
        return testemunhoService.findAllFiltered(search, category);
    }


    @GetMapping("/{id}")
    public Testemunho getById(@PathVariable Long id) {
        return testemunhoService.findById(id).orElse(null);
    }

    @PostMapping
    public Testemunho create(@RequestBody Testemunho testemunho, Principal principal) {
        String email = principal.getName();
        return testemunhoService.saveWithUser(testemunho, email);
    }

    @PutMapping("/{id}")
    public Testemunho update(@PathVariable Long id, @RequestBody Testemunho atualizado, Principal principal) {
        String email = principal.getName();
        return testemunhoService.update(id, atualizado, email);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        testemunhoService.delete(id);
    }

    @GetMapping("/public")
    public List<Testemunho> getTestemunhosPublicos() {
        return testemunhoService.findAll();
    }

    @GetMapping("/minhas")
    public List<Testemunho> getMeusTestemunhos(Principal principal) {
        String email = principal.getName();
        return testemunhoService.findByUsuarioEmail(email);
    }

    @PostMapping("/from-oracao/{oracaoId}")
    public Testemunho createFromOracao(@PathVariable Long oracaoId, Principal principal) {
        String email = principal.getName();

        Oracao oracao = oracaoService.findById(oracaoId).orElseThrow();

        // Verifica se o usuário atual é o dono da oração
        if (!oracao.getUsuario().getEmail().equals(email)) {
            throw new RuntimeException("Você não tem permissão para usar essa oração.");
        }

        return testemunhoService.fromOracao(oracao);
    }

}
