package com.casadabencao.backend.controller;

import com.casadabencao.backend.model.Evento;
import com.casadabencao.backend.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "http://localhost:3000")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    // Alterado para retornar apenas eventos futuros
    @GetMapping
    public List<Evento> getAll() {
        return eventoService.findUpcoming();
    }

    @GetMapping("/futuros")
    public List<Evento> getEventosFuturos() {
        return eventoService.findUpcoming();
    }

    @GetMapping("/{id}")
    public Evento getById(@PathVariable Long id) {
        return eventoService.findById(id).orElse(null);
    }

    @PostMapping
    public Evento create(@RequestBody Evento evento) {
        return eventoService.save(evento);
    }

    @PutMapping("/{id}")
    public Evento update(@PathVariable Long id, @RequestBody Evento eventoAtualizado) {
        return eventoService.update(id, eventoAtualizado);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        eventoService.delete(id);
    }
}
