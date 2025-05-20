package com.casadabencao.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Oracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String message;
    private LocalDate date;

    @Column(name = "approved", nullable = false)
    private Boolean approved = false;

    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous = false;

    private String category; // ⬅️ Novo campo
}