package com.casadabencao.backend.repository;

import com.casadabencao.backend.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByDateGreaterThanEqualOrderByDateAsc(LocalDate date);
}
