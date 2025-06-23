package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Oracao;
import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.repository.OracaoRepository;
import com.casadabencao.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.io.IOException;

@Service
public class OracaoService {

    @Autowired
    private OracaoRepository oracaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Oracao> findAll() {
        return oracaoRepository.findAll();
    }

    public Optional<Oracao> findById(Long id) {
        return oracaoRepository.findById(id);
    }

    public Oracao update(Long id, Oracao novaOracao, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

        return oracaoRepository.findById(id).map(oracao -> {
            // Se vier com nome "Anônimo", deixa como está
            // Se vier null, volta o nome real do usuário
            if ("Anônimo".equalsIgnoreCase(novaOracao.getName())) {
                oracao.setName("Anônimo");
            } else if (novaOracao.getName() == null || novaOracao.getName().isBlank()) {
                oracao.setName(usuario.getName());
            }

            oracao.setMessage(novaOracao.getMessage());
            oracao.setResponded(novaOracao.getResponded());
            oracao.setCategory(novaOracao.getCategory());

            return oracaoRepository.save(oracao);
        }).orElse(null);
    }


    public void delete(Long id) {
        oracaoRepository.deleteById(id);
    }

    public List<Oracao> findNaoRespondidas() {
        return oracaoRepository.findByRespondedFalse();
    }

    public Oracao marcarComoRespondido(Long id) {
        return oracaoRepository.findById(id).map(oracao -> {
            oracao.setResponded(true);
            return oracaoRepository.save(oracao);
        }).orElse(null);
    }

    public Oracao saveWithUser(Oracao oracao, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

        oracao.setUsuario(usuario);
        oracao.setDate(LocalDate.now());

        if (!"Anônimo".equalsIgnoreCase(oracao.getName())) {
            oracao.setName(usuario.getName());
        }

        return oracaoRepository.save(oracao);
    }

    public List<Oracao> findByUsuarioEmail(String email) {
        return oracaoRepository.findByUsuarioEmail(email);
    }

    public List<Oracao> findAllFiltered(String search, String category) {
        List<Oracao> oracoes = oracaoRepository.findAll();

        if (search != null && !search.isBlank()) {
            oracoes = oracoes.stream()
                    .filter(o ->
                            (o.getMessage() != null && o.getMessage().toLowerCase().contains(search.toLowerCase())) ||
                                    (o.getName() != null && o.getName().toLowerCase().contains(search.toLowerCase()))
                    )
                    .toList();
        }

        if (category != null && !category.equalsIgnoreCase("todos")) {
            oracoes = oracoes.stream()
                    .filter(o -> o.getCategory() != null &&
                            o.getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        return oracoes;
    }


}