package com.casadabencao.backend.controller;

import com.casadabencao.backend.model.Oracao;
import com.casadabencao.backend.service.OracaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;


import java.util.List;

@RestController
@RequestMapping("/api/oracoes")
public class OracaoController {

    @Autowired
    private OracaoService oracaoService;

    @GetMapping
    public List<Oracao> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category
    ) {
        return oracaoService.findAllFiltered(search, category);
    }


    @GetMapping("/{id}")
    public Oracao getById(@PathVariable Long id) {
        return oracaoService.findById(id).orElse(null);
    }

    @PostMapping
    public Oracao create(@RequestBody Oracao oracao, Principal principal) {
        String email = principal.getName(); // usa o email como identificador
        return oracaoService.saveWithUser(oracao, email);
    }

    @PutMapping("/{id}")
    public Oracao update(@PathVariable Long id, @RequestBody Oracao novaOracao, Principal principal) {
        String email = principal.getName();
        return oracaoService.update(id, novaOracao, email);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        oracaoService.delete(id);
    }

    // Lista apenas pedidos de oração ainda não respondidos
    @GetMapping("/pedidos")
    public List<Oracao> getPedidosNaoRespondidos() {
        return oracaoService.findNaoRespondidas();
    }

    // Marca um pedido de oração como respondido
    @PutMapping("/{id}/responder")
    public Oracao marcarComoRespondido(@PathVariable Long id) {
        return oracaoService.marcarComoRespondido(id);
    }

    @GetMapping("/minhas")
    public List<Oracao> getMinhasOracoes(Principal principal) {
        String email = principal.getName();
        return oracaoService.findByUsuarioEmail(email);
    }


}