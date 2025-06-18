package com.casadabencao.backend.repository;

import com.casadabencao.backend.model.Oracao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OracaoRepository extends JpaRepository<Oracao, Long> {
    List<Oracao> findByRespondedFalse();
    List<Oracao> findByUsuarioEmail(String email);
}
