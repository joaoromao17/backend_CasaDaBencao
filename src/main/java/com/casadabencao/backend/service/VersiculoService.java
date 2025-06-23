package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Versiculo;
import com.casadabencao.backend.repository.VersiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.io.IOException;

@Service
public class VersiculoService {

    private final VersiculoRepository versiculoRepository;
    private Versiculo versiculoDoDia;
    private LocalDate dataUltimaAtualizacao;

    @Autowired
    public VersiculoService(VersiculoRepository versiculoRepository) {
        this.versiculoRepository = versiculoRepository;
        this.dataUltimaAtualizacao = null;
    }

    public Versiculo getVersiculoDoDia() {
        LocalDate hoje = LocalDate.now();

        // Se for um novo dia ou ainda não foi carregado
        if (dataUltimaAtualizacao == null || !hoje.equals(dataUltimaAtualizacao)) {
            List<Versiculo> todos = versiculoRepository.findAll();
            if (todos.isEmpty()) {
                throw new RuntimeException("Nenhum versículo disponível no banco de dados.");
            }

            versiculoDoDia = todos.get(new Random().nextInt(todos.size()));
            dataUltimaAtualizacao = hoje;
        }
        return versiculoDoDia;
    }
}
