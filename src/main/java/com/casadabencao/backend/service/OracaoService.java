package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Oracao;
import com.casadabencao.backend.repository.OracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OracaoService {

    @Autowired
    private OracaoRepository oracaoRepository;

    public List<Oracao> findAll() {
        return oracaoRepository.findAll();
    }

    public Optional<Oracao> findById(Long id) {
        return oracaoRepository.findById(id);
    }

    public Oracao save(Oracao oracao) {
        return oracaoRepository.save(oracao);
    }

    public Oracao update(Long id, Oracao novaOracao) {
        return oracaoRepository.findById(id).map(oracao -> {
            oracao.setName(novaOracao.getName());
            oracao.setMessage(novaOracao.getMessage());
            oracao.setDate(novaOracao.getDate());
            oracao.setApproved(novaOracao.getApproved());
            oracao.setCategory(novaOracao.getCategory());
            return oracaoRepository.save(oracao);
        }).orElse(null);
    }

    public void delete(Long id) {
        oracaoRepository.deleteById(id);
    }

    public List<Oracao> findByApproved(boolean approved) {
        return oracaoRepository.findByApproved(approved);
    }

    public Oracao marcarComoRespondido(Long id) {
        return oracaoRepository.findById(id).map(oracao -> {
            oracao.setApproved(true);
            return oracaoRepository.save(oracao);
        }).orElse(null);
    }

}