package com.casadabencao.backend.repository;

import com.casadabencao.backend.model.Contribuicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContribuicaoRepository extends JpaRepository<Contribuicao, Long> {
}
