package com.casadabencao.backend.controller;

import com.casadabencao.backend.model.Versiculo;
import com.casadabencao.backend.service.VersiculoService;
import com.casadabencao.backend.repository.VersiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/versiculos")
@CrossOrigin(origins = "http://localhost:3000")
public class VersiculoController {

    @Autowired
    private VersiculoRepository versiculoRepository;

    @Autowired
    private VersiculoService versiculoService;

    @GetMapping("/random")
    public Versiculo getVersiculoDoDia() {
        return versiculoService.getVersiculoDoDia();
    }

    // Opcional: endpoints de listagem e adição manual
    @GetMapping
    public List<Versiculo> listarTodos() {
        return versiculoRepository.findAll();
    }

    @PostMapping
    public Versiculo criar(@RequestBody Versiculo versiculo) {
        return versiculoRepository.save(versiculo);
    }
}
