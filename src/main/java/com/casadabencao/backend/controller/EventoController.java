package com.casadabencao.backend.controller;

import com.casadabencao.backend.dto.EventoDto;
import com.casadabencao.backend.model.Evento;
import com.casadabencao.backend.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.casadabencao.backend.service.CloudinaryService;

import java.util.List;
import java.io.IOException;


@RestController
@RequestMapping("/api/eventos")
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
    public ResponseEntity<Evento> getById(@PathVariable Long id) {
        return eventoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


@PostMapping
public ResponseEntity<Evento> create(
        @RequestPart("evento") EventoDto eventoDto,
        @RequestPart(value = "image", required = false) MultipartFile image
) throws IOException {
    Evento salvo = eventoService.saveFromDto(eventoDto, image);
    return ResponseEntity.ok(salvo);
}


@PutMapping("/{id}")
public ResponseEntity<Evento> update(
        @PathVariable Long id,
        @RequestPart("evento") EventoDto eventoDto,
        @RequestPart(value = "image", required = false) MultipartFile image
) throws IOException {
    Evento atualizado = eventoService.updateFromDto(id, eventoDto, image);
    if (atualizado == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(atualizado);
}


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        eventoService.delete(id);
    }
}
