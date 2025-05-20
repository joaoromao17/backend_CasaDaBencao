package com.casadabencao.backend.repository;

import com.casadabencao.backend.model.Estudo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstudoRepository extends JpaRepository<Estudo, Long> {
}