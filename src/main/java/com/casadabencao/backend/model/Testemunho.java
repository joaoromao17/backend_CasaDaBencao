package com.casadabencao.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Testemunho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String message;
    private LocalDate date;

    @Column(name = "approved", nullable = true)
    private Boolean approved = true;

    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous;

    private String category; // ⬅️ Novo campo
}

