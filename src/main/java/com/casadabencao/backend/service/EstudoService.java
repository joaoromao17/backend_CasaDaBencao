package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Estudo;
import com.casadabencao.backend.repository.EstudoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.io.IOException;

@Service
public class EstudoService {

    @Autowired
    private EstudoRepository estudoRepository;

    public List<Estudo> findAll() {
        return estudoRepository.findAll();
    }

    public Optional<Estudo> findById(Long id) {
        return estudoRepository.findById(id);
    }

    public Estudo save(Estudo estudo) {
        return estudoRepository.save(estudo);
    }

    public void deleteById(Long id) {
        estudoRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return estudoRepository.existsById(id);
    }
}
