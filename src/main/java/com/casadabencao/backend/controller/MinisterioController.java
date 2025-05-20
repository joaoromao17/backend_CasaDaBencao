package com.casadabencao.backend.controller;

import com.casadabencao.backend.model.Ministerio;
import com.casadabencao.backend.service.MinisterioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ministerios")
@CrossOrigin(origins = "http://localhost:3000")
public class MinisterioController {

    @Autowired
    private MinisterioService ministerioService;

    @GetMapping
    public List<Ministerio> getAll() {
        return ministerioService.findAll();
    }

    @GetMapping("/{id}")
    public Ministerio getById(@PathVariable Long id) {
        return ministerioService.findById(id).orElse(null);
    }

    @PostMapping
    public Ministerio create(@RequestBody Ministerio ministerio) {
        return ministerioService.save(ministerio);
    }

    @PutMapping("/{id}")
    public Ministerio update(@PathVariable Long id, @RequestBody Ministerio atualizado) {
        return ministerioService.update(id, atualizado);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ministerioService.delete(id);
    }
}
