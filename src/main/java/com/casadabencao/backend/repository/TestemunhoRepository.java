package com.casadabencao.backend.repository;

import com.casadabencao.backend.model.Testemunho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestemunhoRepository extends JpaRepository<Testemunho, Long> {

    // Listar testemunhos por email do usuário
    List<Testemunho> findByUsuarioEmail(String email);

    // Listar apenas testemunhos com responded = true (ex: vindo de oração respondida)
    List<Testemunho> findByRespondedTrue();

    // Listar testemunhos não vinculados a orações
    List<Testemunho> findByOracaoOriginalIsNull();
}
