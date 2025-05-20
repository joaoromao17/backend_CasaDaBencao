package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Estudo;
import com.casadabencao.backend.repository.EstudoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstudoService {

    @Autowired
    private EstudoRepository estudoRepository;

    public List<Estudo> listarTodos() {
        return estudoRepository.findAll();
    }

    public Optional<Estudo> buscarPorId(Long id) {
        return estudoRepository.findById(id);
    }

    public Estudo salvar(Estudo estudo) {
        return estudoRepository.save(estudo);
    }

    public void deletar(Long id) {
        estudoRepository.deleteById(id);
    }
}
