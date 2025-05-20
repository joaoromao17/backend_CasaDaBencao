package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Testemunho;
import com.casadabencao.backend.repository.TestemunhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestemunhoService {

    @Autowired
    private TestemunhoRepository testemunhoRepository;

    public List<Testemunho> findAll() {
        return testemunhoRepository.findAll();
    }

    public Optional<Testemunho> findById(Long id) {
        return testemunhoRepository.findById(id);
    }

    public Testemunho save(Testemunho testemunho) {
        return testemunhoRepository.save(testemunho);
    }

    public Testemunho update(Long id, Testemunho atualizado) {
        return testemunhoRepository.findById(id).map(t -> {
            t.setName(atualizado.getName());
            t.setMessage(atualizado.getMessage());
            t.setDate(atualizado.getDate());
            t.setApproved(atualizado.getApproved());
            t.setCategory(atualizado.getCategory()); // ⬅️ Adicionado
            return testemunhoRepository.save(t);
        }).orElse(null);
    }


    public void delete(Long id) {
        testemunhoRepository.deleteById(id);
    }
}
