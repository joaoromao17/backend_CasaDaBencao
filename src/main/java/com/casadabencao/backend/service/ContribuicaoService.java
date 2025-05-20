package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Contribuicao;
import com.casadabencao.backend.repository.ContribuicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ContribuicaoService {

    @Autowired
    private ContribuicaoRepository repository;

    public List<Contribuicao> findAll() {
        return repository.findAll();
    }

    public Optional<Contribuicao> findById(Long id) {
        return repository.findById(id);
    }

    public Contribuicao save(Contribuicao contrib) {
        return repository.save(contrib);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Contribuicao update(Long id, Contribuicao nova) {
        return repository.findById(id).map(c -> {
            c.setTitle(nova.getTitle());
            c.setDescription(nova.getDescription());
            c.setImageUrl(nova.getImageUrl());
            c.setTargetValue(nova.getTargetValue());
            c.setGoalVisible(nova.isGoalVisible());
            c.setStatus(nova.getStatus());
            c.setStartDate(nova.getStartDate());
            c.setEndDate(nova.getEndDate());
            c.setCreatedBy(nova.getCreatedBy());
            c.setShortDescription(nova.getShortDescription());
            c.setPixKey(nova.getPixKey());
            c.setFullDescription(nova.getFullDescription());
            return repository.save(c);
        }).orElseThrow(() -> new RuntimeException("Contribuição não encontrada com id " + id));
    }

    public Contribuicao adicionarContribuicao(Long id, BigDecimal valor) {
        return repository.findById(id).map(c -> {
            c.setCollectedValue(c.getCollectedValue().add(valor));
            return repository.save(c);
        }).orElseThrow(() -> new RuntimeException("Contribuição não encontrada com id " + id));
    }
}
