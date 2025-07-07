package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Contribuicao;
import com.casadabencao.backend.repository.ContribuicaoRepository;
import com.casadabencao.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class ContribuicaoService {

    @Autowired
    private ContribuicaoRepository repository;

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Contribuicao> findAll() {
        return repository.findAll();
    }

    public Optional<Contribuicao> findById(Long id) {
        return repository.findById(id);
    }

    public Contribuicao save(Contribuicao contrib) {
        Contribuicao salva = repository.save(contrib);

        // 🔔 Enviar notificação para todos com token FCM
        usuarioRepository.findAll().forEach(usuario -> {
            String fcm = usuario.getFcmToken();
            if (fcm != null && !fcm.isBlank()) {
                firebaseService.enviarNotificacaoComLink(
                        "💖 Nova caixinha de contribuição!",
                        "Apoie: " + salva.getTitle(),
                        fcm,
                        "https://localhost/contribuicoes/" + salva.getId()
                );
            }
        });

        return salva;
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Contribuicao update(Long id, Contribuicao nova) {
        return repository.findById(id).map(c -> {
            c.setTitle(nova.getTitle());
            c.setDescription(nova.getDescription());
            if (nova.getImageUrl() != null && !nova.getImageUrl().isBlank()) {
                c.setImageUrl(nova.getImageUrl());
            }
            c.setTargetValue(nova.getTargetValue());
            c.setGoalVisible(nova.isGoalVisible());
            c.setCollectedValue(nova.getCollectedValue());
            c.setStatus(nova.getStatus());
            c.setStartDate(nova.getStartDate());
            c.setEndDate(nova.getEndDate());
            c.setCreatedBy(nova.getCreatedBy());
            c.setPixKey(nova.getPixKey());
            return repository.save(c);
        }).orElseThrow(() -> new RuntimeException("Contribuição não encontrada com id " + id));
    }

    public Contribuicao adicionarContribuicao(Long id, BigDecimal valor) {
        return repository.findById(id).map(c -> {
            c.setCollectedValue(c.getCollectedValue().add(valor));
            return repository.save(c);
        }).orElseThrow(() -> new RuntimeException("Contribuição não encontrada com id " + id));
    }

public Contribuicao salvarImagemUrl(Long id, String imageUrl) {
    Contribuicao c = findById(id).orElseThrow();
    c.setImageUrl(imageUrl); 
    return repository.save(c);
}
}
