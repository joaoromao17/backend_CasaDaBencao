package com.casadabencao.backend.controller;

import com.casadabencao.backend.model.Oracao;
import com.casadabencao.backend.service.OracaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/oracoes")
@CrossOrigin(origins = "http://localhost:3000")
public class OracaoController {

    @Autowired
    private OracaoService oracaoService;

    @GetMapping
    public List<Oracao> getAll() {
        return oracaoService.findAll();
    }

    @GetMapping("/{id}")
    public Oracao getById(@PathVariable Long id) {
        return oracaoService.findById(id).orElse(null);
    }

    @PostMapping
    public Oracao create(@RequestBody Oracao oracao) {
        return oracaoService.save(oracao);
    }

    @PutMapping("/{id}")
    public Oracao update(@PathVariable Long id, @RequestBody Oracao novaOracao) {
        return oracaoService.update(id, novaOracao);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        oracaoService.delete(id);
    }

    // Lista apenas pedidos de oração ainda não respondidos
    @GetMapping("/pedidos")
    public List<Oracao> getPedidosNaoRespondidos() {
        return oracaoService.findByApproved(false);
    }

    // Marca um pedido de oração como respondido
    @PutMapping("/{id}/responder")
    public Oracao marcarComoRespondido(@PathVariable Long id) {
        return oracaoService.marcarComoRespondido(id);
    }

}