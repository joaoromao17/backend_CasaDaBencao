package com.casadabencao.backend.controller;

import com.casadabencao.backend.model.Testemunho;
import com.casadabencao.backend.service.TestemunhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testemunhos")
@CrossOrigin(origins = "http://localhost:3000")
public class TestemunhoController {

    @Autowired
    private TestemunhoService testemunhoService;

    @GetMapping
    public List<Testemunho> getAll() {
        return testemunhoService.findAll();
    }

    @GetMapping("/{id}")
    public Testemunho getById(@PathVariable Long id) {
        return testemunhoService.findById(id).orElse(null);
    }

    @PostMapping
    public Testemunho create(@RequestBody Testemunho testemunho) {
        return testemunhoService.save(testemunho);
    }

    @PutMapping("/{id}")
    public Testemunho update(@PathVariable Long id, @RequestBody Testemunho atualizado) {
        return testemunhoService.update(id, atualizado);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        testemunhoService.delete(id);
    }
}
