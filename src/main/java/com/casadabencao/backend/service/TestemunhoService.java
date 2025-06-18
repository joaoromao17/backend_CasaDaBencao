package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Oracao;
import com.casadabencao.backend.model.Testemunho;
import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.repository.TestemunhoRepository;
import com.casadabencao.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestemunhoService {

    @Autowired
    private TestemunhoRepository testemunhoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Testemunho> findAll() {
        return testemunhoRepository.findAll();
    }

    public Optional<Testemunho> findById(Long id) {
        return testemunhoRepository.findById(id);
    }

    public Testemunho save(Testemunho testemunho) {
        return testemunhoRepository.save(testemunho);
    }

    public Testemunho update(Long id, Testemunho atualizado, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

        return testemunhoRepository.findById(id).map(t -> {
            if ("Anônimo".equalsIgnoreCase(atualizado.getName())) {
                t.setName("Anônimo");
            } else if (atualizado.getName() == null || atualizado.getName().isBlank()) {
                t.setName(usuario.getName());
            }

            t.setMessage(atualizado.getMessage());
            t.setCategory(atualizado.getCategory());
            return testemunhoRepository.save(t);
        }).orElse(null);
    }

    public Testemunho fromOracao(Oracao oracao) {
        Testemunho testemunho = new Testemunho();
        testemunho.setName(oracao.getName());
        testemunho.setMessage(oracao.getMessage());
        testemunho.setCategory(oracao.getCategory());
        testemunho.setUsuario(oracao.getUsuario());
        testemunho.setOracaoOriginal(oracao);
        testemunho.setResponded(true); // ✅ indica que veio de uma oração respondida
        return testemunhoRepository.save(testemunho);
    }

    public void delete(Long id) {
        testemunhoRepository.deleteById(id);
    }

    public Testemunho saveWithUser(Testemunho testemunho, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

        testemunho.setUsuario(usuario);

        if (!"Anônimo".equalsIgnoreCase(testemunho.getName())) {
            testemunho.setName(usuario.getName());
        }

        return testemunhoRepository.save(testemunho);
    }

    public List<Testemunho> findByUsuarioEmail(String email) {
        return testemunhoRepository.findByUsuarioEmail(email);
    }

    public List<Testemunho> findAllFiltered(String search, String category) {
        List<Testemunho> testemunhos = testemunhoRepository.findAll();

        if (search != null && !search.isBlank()) {
            testemunhos = testemunhos.stream()
                    .filter(t ->
                            (t.getMessage() != null && t.getMessage().toLowerCase().contains(search.toLowerCase())) ||
                                    (t.getName() != null && t.getName().toLowerCase().contains(search.toLowerCase()))
                    )
                    .toList();
        }

        if (category != null && !category.equalsIgnoreCase("todos")) {
            testemunhos = testemunhos.stream()
                    .filter(t -> t.getCategory() != null &&
                            t.getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        return testemunhos;
    }

}
