package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Contribuicao;
import com.casadabencao.backend.repository.ContribuicaoRepository;
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

    public Contribuicao salvarImagem(Long id, MultipartFile imagem) {
        return repository.findById(id).map(c -> {
            String filename = System.currentTimeMillis() + "_" + StringUtils.cleanPath(imagem.getOriginalFilename());
            String pastaDestino = "uploads/contribuicoes";
            File pasta = new File(pastaDestino);
            if (!pasta.exists()) pasta.mkdirs();

            if (!imagem.getContentType().startsWith("image/")) {
                throw new RuntimeException("Arquivo enviado não é uma imagem.");
            }


            Path caminho = Paths.get(pastaDestino, filename);
            try {
                Files.copy(imagem.getInputStream(), caminho, StandardCopyOption.REPLACE_EXISTING);
                c.setImageUrl("/uploads/contribuicoes/" + filename);
                return repository.save(c);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar imagem", e);
            }
        }).orElseThrow(() -> new RuntimeException("Contribuição não encontrada com id " + id));
    }
}
