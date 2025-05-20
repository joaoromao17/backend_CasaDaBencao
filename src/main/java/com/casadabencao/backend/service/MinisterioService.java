package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Ministerio;
import com.casadabencao.backend.repository.MinisterioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MinisterioService {

    @Autowired
    private MinisterioRepository ministerioRepository;

    public List<Ministerio> findAll() {
        return ministerioRepository.findAll();
    }

    public Optional<Ministerio> findById(Long id) {
        return ministerioRepository.findById(id);
    }

    public Ministerio save(Ministerio ministerio) {
        return ministerioRepository.save(ministerio);
    }

    public Ministerio update(Long id, Ministerio atualizado) {
        return ministerioRepository.findById(id).map(ministerio -> {
            ministerio.setName(atualizado.getName());
            ministerio.setDescription(atualizado.getDescription());
            ministerio.setLeader(atualizado.getLeader());
            ministerio.setImageUrl(atualizado.getImageUrl());
            ministerio.setMeetingDay(atualizado.getMeetingDay());
            ministerio.setAtividades(atualizado.getAtividades()); // <- Aqui
            return ministerioRepository.save(ministerio);
        }).orElse(null);
    }


    public void delete(Long id) {
        ministerioRepository.deleteById(id);
    }
}
